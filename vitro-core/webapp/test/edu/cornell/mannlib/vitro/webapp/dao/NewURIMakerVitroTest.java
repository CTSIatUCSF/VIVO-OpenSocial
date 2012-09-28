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
package edu.cornell.mannlib.vitro.webapp.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.SimpleOntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;

public class NewURIMakerVitroTest extends AbstractTestClass{

	@Test
	public void testMultipleNewURIs() {
		//Three items needs new URIs assigned in the default namespace
		//Var name to namespace, in this case null to denote default namespace
		Map<String,String> newResources = new HashMap<String, String>();
		newResources.put("page", null);
		newResources.put("menuItem", null);
		newResources.put("dataGetter", null);
		
		//Setup webappdaofactory
		WebappDaoFactoryJena wadf = this.setupWebappDaoFactory();
		NewURIMakerVitro nv = new NewURIMakerVitro(wadf);
		
		//Now test for new URI
		HashMap<String,String> varToNewURIs = new HashMap<String,String>();       
		try {
			for (String key : newResources.keySet()) {
	           String prefix = newResources.get(key);
	           String uri = nv.getUnusedNewURI(prefix);                        
	           varToNewURIs.put(key, uri);  
			}  
		} catch(Exception ex) {
			System.out.println("Error occurred " + ex);
		}
		
		//Ensure that URIs are not included more than once
		List<String> values = new ArrayList<String>(varToNewURIs.values());
		Set<String> valuesSet = new HashSet<String>(varToNewURIs.values());
		assertTrue(valuesSet.size() == values.size());

	}
	
	@Test
	public void testNonNullNamespace() {
		//Three items needs new URIs assigned in the default namespace
		//Var name to namespace, in this case null to denote default namespace
		Map<String,String> newResources = new HashMap<String, String>();
		newResources.put("page", "http://displayOntology/test/n12");
	
		
		//Setup webappdaofactory
		WebappDaoFactoryJena wadf = this.setupWebappDaoFactory();
		NewURIMakerVitro nv = new NewURIMakerVitro(wadf);
		
		//Now test for new URI
		HashMap<String,String> varToNewURIs = new HashMap<String,String>();       
		try {
			for (String key : newResources.keySet()) {
	           String prefix = newResources.get(key);
	           String uri = nv.getUnusedNewURI(prefix);                        
	           varToNewURIs.put(key, uri);  
			}  
		} catch(Exception ex) {
			System.out.println("Error occurred " + ex);
		}
		
		//Ensure that URIs are not included more than once
		List<String> values = new ArrayList<String>(varToNewURIs.values());
		Set<String> valuesSet = new HashSet<String>(varToNewURIs.values());
		assertTrue(valuesSet.size() == values.size());
	}
	
	private WebappDaoFactoryJena setupWebappDaoFactory() {
		String defaultNamespace= "http://vivo.mannlib.cornell.edu/individual/";
		String testNamespace = "http://displayOntology/test/";
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM); 
		ontModel.add(
				ontModel.createResource(defaultNamespace + "n234"),
				RDF.type, 
				OWL.Thing);
		ontModel.add(
				ontModel.createResource(testNamespace + "n234"),
				RDF.type, 
				OWL.Thing);
		OntModelSelector selector = new SimpleOntModelSelector(ontModel);
		//Set up default namespace somewhere?
		WebappDaoFactoryConfig config = new WebappDaoFactoryConfig();
        config.setDefaultNamespace(defaultNamespace);
		//Set up some test uris
		WebappDaoFactoryJena wadf = new WebappDaoFactoryJena(selector, config);
		return wadf;
	}

}
