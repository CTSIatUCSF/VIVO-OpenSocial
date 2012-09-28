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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils.ProcessDataGetterN3;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.DataGetter;

/*
 * This class determines what n3 should be returned for a particular data getter and can be overwritten or extended in VIVO. 
 */
public class ProcessDataGetterN3Utils {
    private static final Log log = LogFactory.getLog(ProcessDataGetterN3Utils.class);
    
    public static ProcessDataGetterN3 getDataGetterProcessorN3(String dataGetterClass, JSONObject jsonObject) {
    	HashMap<String, String> map = ProcessDataGetterN3Map.getDataGetterTypeToProcessorMap();
    	//
    	if(map.containsKey(dataGetterClass)) {
    		String processorClass = map.get(dataGetterClass);
    		try {
    			ProcessDataGetterN3 pn = instantiateClass(processorClass, jsonObject);
    			return pn;
    		} catch(Exception ex) {
    			log.error("Exception occurred in trying to get processor class for n3 for " + dataGetterClass, ex);
    			return null;
    		}
    	}
    	return null;
    }
    
    private static ProcessDataGetterN3 instantiateClass(String processorClass, JSONObject jsonObject) {
    	ProcessDataGetterN3 pn = null;
    	try {
	    	Class<?> clz = Class.forName(processorClass);
	    	Constructor<?>[] ctList = clz.getConstructors();
	    	for (Constructor<?> ct: ctList) {
		    	Class<?>[] parameterTypes =ct.getParameterTypes();
				if(parameterTypes.length > 0 && parameterTypes[0].isAssignableFrom(jsonObject.getClass())) {
						 pn = (ProcessDataGetterN3) ct.newInstance(jsonObject);
				} 	else {
						pn = (ProcessDataGetterN3) ct.newInstance();
				} 
	    	}
		
    	} catch(Exception ex) {
			log.error("Error occurred instantiating " + processorClass, ex);
		}
    	return pn;
        		
    }
    
}