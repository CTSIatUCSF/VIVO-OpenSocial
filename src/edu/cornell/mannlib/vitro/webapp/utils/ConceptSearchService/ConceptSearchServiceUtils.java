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

package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
/**
 * Utilities for search    
 */
public class ConceptSearchServiceUtils {
    private static final Log log = LogFactory.getLog(ConceptSearchServiceUtils.class);
    //Get the appropriate search service class
    //TODO: Change this so retrieved from the system instead using a query
    private static final String UMLSVocabSource = "http://link.informatics.stonybrook.edu/umls";
    private static final String AgrovocVocabSource = "http://aims.fao.org/aos/agrovoc/agrovocScheme";
    private static final String GemetVocabSource = "http://www.eionet.europa.eu/gemet/gemetThesaurus";

    //Get the class that corresponds to the appropriate search
	public static String getConceptSearchServiceClassName(String searchServiceName) {
		HashMap<String, String> map = getMapping();
		if(map.containsKey(searchServiceName)) {
			return map.get(searchServiceName);
		}
		return null;
	}
	
	//Get the URLS for the different services
	//URL to label
	public static HashMap<String, VocabSourceDescription> getVocabSources() {
		HashMap<String, VocabSourceDescription> map = new HashMap<String, VocabSourceDescription>();
    	map.put(UMLSVocabSource, new VocabSourceDescription("UMLS", UMLSVocabSource, "http://www.nlm.nih.gov/research/umls/", "Unified Medical Language System"));
    	//Commenting out agrovoc for now until implementation is updated
    	//	map.put(AgrovocVocabSource, new VocabSourceDescription("AGROVOC", AgrovocVocabSource, "http://www.fao.org/agrovoc/", "Agricultural Vocabulary"));
    	map.put(GemetVocabSource, new VocabSourceDescription("GEMET", GemetVocabSource, "http://www.eionet.europa.eu/gemet", "GEneral Multilingual Environmental Thesaurus"));
    	return map;
	}
	
	//Get additional vocab source info
	
    
    //Get the hashmap mapping service name to Service class
    private static HashMap<String, String> getMapping() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	map.put(UMLSVocabSource, "edu.cornell.mannlib.semservices.service.impl.UMLSService");
    	map.put(AgrovocVocabSource, "edu.cornell.mannlib.semservices.service.impl.AgrovocService");
    	map.put(GemetVocabSource, "edu.cornell.mannlib.semservices.service.impl.GemetService");
    	return map;
    }
    
    public static List<Concept> getSearchResults(ServletContext context, VitroRequest vreq) throws Exception {
    	String searchServiceName = getSearchServiceUri(vreq);
    	String searchServiceClassName = getConceptSearchServiceClassName(searchServiceName);
    
    	ExternalConceptService conceptServiceClass = null;
	
	    Object object = null;
	    try {
	        Class classDefinition = Class.forName(searchServiceClassName);
	        object = classDefinition.newInstance();
	        conceptServiceClass = (ExternalConceptService) object;
	    } catch (InstantiationException e) {
	        System.out.println(e);
	    } catch (IllegalAccessException e) {
	        System.out.println(e);
	    } catch (ClassNotFoundException e) {
	        System.out.println(e);
	    }    	
    
	    if(conceptServiceClass == null){
	    	log.error("could not find Concept Search Class for " + searchServiceName);
	    	return null;
	    } 
	    
	    //Get search
	    String searchTerm = getSearchTerm(vreq);
	    List<Concept> conceptResults =  conceptServiceClass.processResults(searchTerm);
	    return conceptResults;
    }


	private static String getSearchServiceUri(VitroRequest vreq) {
		return vreq.getParameter("source");
	}

	private static String getSearchTerm(VitroRequest vreq) {
		return vreq.getParameter("searchTerm");
	}
	

}

