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

package edu.cornell.mannlib.vitro.webapp.reasoner;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;

public class SimpleReasonerPluginTest extends AbstractTestClass {
	long delay = 50;
	
	private final static String DEFAULT_NS = "http://vivoweb.org/individual/";
	
	private final static String DCTERMS_NS = "http://purl.org/dc/terms/";
	private final static String VIVOCORE_NS = "http://vivoweb.org/ontology/core#";
	
	private final static String creator_URI = DCTERMS_NS + "creator";
	private final static String authorInAuthorship_URI = VIVOCORE_NS + "authorInAuthorship";
	private final static String linkedAuthor_URI = VIVOCORE_NS + "linkedAuthor";
	private final static String informationResourceInAuthorship_URI = VIVOCORE_NS + "informationResourceInAuthorship";
	private final static String linkedInformationResource_URI = VIVOCORE_NS + "linkedInformationResource";
		
	@Before
	public void suppressErrorOutput() {
		//suppressSyserr();
        //Turn off log messages to console
		setLoggerLevel(SimpleReasoner.class, Level.DEBUG);
		setLoggerLevel(SimpleReasonerTBoxListener.class, Level.DEBUG);
	}
	
	/*
	* testing samplePlugin - based on dcterms:creator plugin
	* 
	*/
	@Test
	public void test1()  {
		OntModel tBox = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC); 
		
		OntProperty authorInAuthorship = tBox.createObjectProperty(authorInAuthorship_URI);
		OntProperty linkedAuthor = tBox.createObjectProperty(linkedAuthor_URI);
		OntProperty informationResourceInAuthorship = tBox.createObjectProperty(informationResourceInAuthorship_URI);
		OntProperty linkedInformationResource = tBox.createObjectProperty(linkedInformationResource_URI);
		
		authorInAuthorship.addInverseOf(linkedAuthor);
		informationResourceInAuthorship.addInverseOf(linkedInformationResource);
				
		Literal title1 = tBox.createLiteral("My Findings");
		Literal name1 = tBox.createLiteral("Priscilla Powers");
		
        // this is the model to receive inferences
        Model inf = ModelFactory.createDefaultModel();
        
		// create an ABox and register the SimpleReasoner listener with it
		OntModel aBox = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM); 
		SimpleReasoner simpleReasoner = new SimpleReasoner(tBox, aBox, inf);
		aBox.register(simpleReasoner);
	
		// register plugin with SimpleReasoner
		List<ReasonerPlugin> pluginList = new ArrayList<ReasonerPlugin>();	
		String pluginClassName = "edu.cornell.mannlib.vitro.webapp.reasoner.plugin.SamplePlugin";
		
		try {
		    ReasonerPlugin plugin = (ReasonerPlugin) Class.forName(pluginClassName).getConstructors()[0].newInstance();
		    plugin.setSimpleReasoner(simpleReasoner);
		    pluginList.add(plugin);
	        simpleReasoner.setPluginList(pluginList);		
		} catch (Exception e) {
			System.out.println("Exception trying to instantiate plugin: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		Property dctermsCreator = ResourceFactory.createProperty(creator_URI);
		
        // add abox data for person, authorship and article.	
		// note, they aren't actually typed in this test tbox
		Resource prissy = aBox.createResource(DEFAULT_NS + "prissy");
	
		// assert same as
		
		Resource authorship1 = aBox.createResource(DEFAULT_NS + "authorship1");
		Resource article1 = aBox.createResource(DEFAULT_NS + "article1");
		Resource article100 = aBox.createResource(DEFAULT_NS + "article100");		
		
		aBox.add(prissy,RDFS.label,name1);
		aBox.add(prissy,authorInAuthorship,authorship1);
		
		aBox.add(authorship1,linkedAuthor,prissy);
		aBox.add(authorship1,linkedInformationResource,article1);
		
		aBox.add(article1,RDFS.label,title1);
		aBox.add(article1,informationResourceInAuthorship,authorship1);
        aBox.add(article1, OWL.sameAs, article100);

		Assert.assertTrue(inf.contains(article1,dctermsCreator,prissy));
		Assert.assertTrue(inf.contains(article100,dctermsCreator,prissy));	
		
		aBox.remove(authorship1,linkedAuthor,prissy);
		
		Assert.assertFalse(inf.contains(article1,dctermsCreator,prissy));
		Assert.assertFalse(inf.contains(article100,dctermsCreator,prissy));				
	}
			
	
	//==================================== Utility methods ====================
	SimpleReasonerTBoxListener getTBoxListener(SimpleReasoner simpleReasoner) {
	    return new SimpleReasonerTBoxListener(simpleReasoner, new Exception().getStackTrace()[1].getMethodName());
	}
		
	// To help in debugging the unit test
	void printModel(Model model, String modelName) {
	    
		System.out.println("\nThe " + modelName + " model has " + model.size() + " statements:");
		System.out.println("---------------------------------------------------------------------");
		model.write(System.out);		
	}
	
	// To help in debugging the unit test
	void printModel(OntModel ontModel, String modelName) {
	    
		System.out.println("\nThe " + modelName + " model has " + ontModel.size() + " statements:");
		System.out.println("---------------------------------------------------------------------");
		ontModel.writeAll(System.out,"N3",null);
		
	}
}