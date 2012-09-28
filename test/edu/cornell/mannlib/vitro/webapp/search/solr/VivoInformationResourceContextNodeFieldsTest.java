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
package edu.cornell.mannlib.vitro.webapp.search.solr;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceFactorySingle;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.jena.model.RDFServiceModel;

public class VivoInformationResourceContextNodeFieldsTest extends AbstractTestClass {

    String TEST_NO_LABLE_N3_FILE = "VivoInformationResourceContextNodeFieldsTest.n3";
    String RDFS_LABEL_VALUE = "Test Document X";
    String DOCUMENT_URI = "http://example.com/vivo/individual/n7474";
    
    @Test
    public void testNoLabel() throws IOException{
    	
        //Test that rdfs:label is NOT added by the VivoInformationResourceContextNodeFields
        
        //setup a model & wdf with test RDF file
        InputStream stream = VivoInformationResourceContextNodeFieldsTest.class.getResourceAsStream(TEST_NO_LABLE_N3_FILE);
        Model model = ModelFactory.createDefaultModel();
        model.read(stream, null, "N3");
        stream.close();

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
        ontModel.prepare();
        Assert.assertTrue("ontModel had no statements" , ontModel.size() > 0 );
         
        WebappDaoFactory wadf = new WebappDaoFactoryJena(ontModel);        
        Individual ind = wadf.getIndividualDao().getIndividualByURI(DOCUMENT_URI);
        Assert.assertNotNull(ind);
        
        RDFService rdfService = new RDFServiceModel(ontModel);
        RDFServiceFactory rdfServiceFactory = new RDFServiceFactorySingle(rdfService);
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("ALLTEXT", "");                
        
        VivoInformationResourceContextNodeFields vircnf = new VivoInformationResourceContextNodeFields(rdfServiceFactory);
        vircnf.modifyDocument(ind, doc, new StringBuffer());                
        
        Collection values = doc.getFieldValues("ALLTEXT");
        for( Object value : values){
            Assert.assertFalse("rdf:label erroneously added by document modifier:", value.toString().contains(RDFS_LABEL_VALUE));
        }
        
        VivoAgentContextNodeFields vacnf = new VivoAgentContextNodeFields(rdfServiceFactory);
        vacnf.modifyDocument(ind, doc, new StringBuffer());
        
     }
       
     
}
