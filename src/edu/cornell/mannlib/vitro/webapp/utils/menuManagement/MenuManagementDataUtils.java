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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;

/*
 * This class includes methods that help in selecting a data getter based on 
 * parameters, and VIVO will have its own version or extend this
 */
public class MenuManagementDataUtils {
    private static final Log log = LogFactory.getLog(MenuManagementDataUtils.class);

    //Data that is to be returned to template that does not involve data getters
    //e.g. what are the current class groups, etc.
    public static void includeRequiredSystemData(ServletContext context, Map<String, Object> templateData) {
    	
    	checkInstitutionalInternalClass(context, templateData);
    }
    
	//Check whether any classes exist with internal class restrictions
	private static void checkInstitutionalInternalClass(ServletContext context, Map<String, Object> templateData) {
		//TODO: replace with more generic ModelContext retrieval method
		String internalClass = retrieveInternalClass(context);
		if(internalClass != null) {			
			templateData.put("internalClass", internalClass);
			templateData.put("internalClassUri", internalClass);
		} else {
			//need to initialize to empty string anyway
			templateData.put("internalClassUri", "");
		}
		
	}
	
	private static String retrieveInternalClass(ServletContext context) {
		OntModel mainModel = ModelContext.getBaseOntModelSelector(context).getTBoxModel();
		StmtIterator internalIt = mainModel.listStatements(null, ResourceFactory.createProperty(VitroVocabulary.IS_INTERNAL_CLASSANNOT), (RDFNode) null);
		if(internalIt.hasNext()) {			
			String internalClass = internalIt.nextStatement().getSubject().getURI();
			return internalClass;
		}
		return null;
	}

    
}