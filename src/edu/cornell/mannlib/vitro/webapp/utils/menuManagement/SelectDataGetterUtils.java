/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.utils.menuManagement;

import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter;


/*
 * This class includes methods that help in selecting a data getter based on 
 * parameters, and VIVO will have its own version or extend this
 */
public class SelectDataGetterUtils {
    private static final Log log = LogFactory.getLog(SelectDataGetterUtils.class);

    /**Get data for an existing page and set variables for the template accordingly**/
   
    
	public static void processAndRetrieveData(VitroRequest vreq, ServletContext context, Map<String, Object> pageData, String dataGetterClass, Map<String, Object> templateData) {
		//The type of the data getter will show how to process the data from the data getter
		ProcessDataGetter processor = selectProcessor(dataGetterClass);
		processor.populateTemplate(context, pageData, templateData);
	}
	
	//This will be different in VIVO than in VITRO
	private static ProcessDataGetter selectProcessor(String dataGetterClass) {
		if(dataGetterClass.equals(ClassGroupPageData.class.getName())) {
			return new ProcessClassGroup();
		} else if(dataGetterClass.equals(InternalClassesDataGetter.class.getName())) {
			return new ProcessInternalClasses();
			//below should be for vitro specific version
			//return new ProcessIndividualsForClasses();
		} 
		return null;
	}
    

    /**Process parameters from form and select appropriate data getter on this basis **/   
    public static Model createDataGetterModel(VitroRequest vreq, Resource dataGetterResource) {
		Model dataGetterModel = null;
    	if(dataGetterResource != null) {
    		//If "All selected" then use class group else use individuals for classes
    		dataGetterModel = ModelFactory.createDefaultModel();
    		
    		ProcessInternalClasses individualsProcess = new ProcessInternalClasses();

    		ProcessClassGroup classGroupProcess = new ProcessClassGroup();
    		if(individualsProcess.useProcessor(vreq)) {
    			dataGetterModel = individualsProcess.processSubmission(vreq, dataGetterResource);
    		} else {
    			dataGetterModel = classGroupProcess.processSubmission(vreq, dataGetterResource);
    		}
    		
    		
    	} else {
    		log.error("Data getter is null ");
    	}
    	return dataGetterModel;
		
	}
    
}