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
/**
 * 
 */
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.SkipIndividualException;
import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.ThumbnailImageURL;

/**
 * @author bdc34
 *
 */
public class ThumbnailImageURLTest extends AbstractTestClass{
    OntModel testModel;
    String personsURI = "http://vivo.cornell.edu/individual/individual8803";
    
    static VitroSearchTermNames term = new VitroSearchTermNames();
    String fieldForThumbnailURL = term.THUMBNAIL_URL;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        setLoggerLevel(RDFDefaultErrorHandler.class, Level.OFF);

        Model model = ModelFactory.createDefaultModel();        
        InputStream in = ThumbnailImageURLTest.class.getResourceAsStream("testPerson.n3");
        model.read(in,"","N3");        
        testModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
    }

    /**
     * Test method for {@link edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.ThumbnailImageURL#modifyDocument(edu.cornell.mannlib.vitro.webapp.beans.Individual, org.apache.solr.common.SolrInputDocument, java.lang.StringBuffer)}.
     */
    @Test
    public void testModifyDocument() {
        SolrInputDocument doc = new SolrInputDocument();
        ThumbnailImageURL testMe = new ThumbnailImageURL( testModel );
        Individual ind = new IndividualImpl();
        ind.setURI(personsURI);
        try {
            testMe.modifyDocument(ind, doc, null);
        } catch (SkipIndividualException e) {
                Assert.fail("person was skipped: " + e.getMessage());
        }
                
        SolrInputField thumbnailField = doc.getField(fieldForThumbnailURL);
        Assert.assertNotNull(thumbnailField);

        Assert.assertNotNull( thumbnailField.getValues() );
        Assert.assertEquals(1, thumbnailField.getValueCount());
        
        Assert.assertEquals("http://vivo.cornell.edu/individual/n54945", thumbnailField.getFirstValue());
    }

}
