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
package edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stubs.javax.servlet.http.HttpServletRequestStub;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.SimpleOntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;

public class PageDataGetterUtilsTest extends AbstractTestClass{
    OntModel displayModel;
    WebappDaoFactory wdf;
    
    String pageURI = "http://vitro.mannlib.cornell.edu/ontologies/display/1.1#pageX";
    String pageURI_2 = "http://vitro.mannlib.cornell.edu/ontologies/display/1.1#SPARQLPage";
    
    @Before
    public void setUp() throws Exception {
        // Suppress error logging.
        setLoggerLevel(RDFDefaultErrorHandler.class, Level.OFF);

        OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);        
        InputStream in = PageDataGetterUtilsTest.class.getResourceAsStream("resources/pageDataGetter.n3");
        model.read(in,"","N3");        
        displayModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
        
        SimpleOntModelSelector sos = new SimpleOntModelSelector( ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM));
        sos.setDisplayModel(displayModel);
        
        wdf = new WebappDaoFactoryJena(sos);
    }

    @Test
    public void testGetPageDataGetterObjects() throws Exception{
        VitroRequest vreq = new VitroRequest( new HttpServletRequestStub() );
        vreq.setWebappDaoFactory(wdf);
        
        List<PageDataGetter> pdgList = PageDataGetterUtils.getPageDataGetterObjects(vreq, pageURI);
        Assert.assertNotNull(pdgList);
        Assert.assertTrue("should have one PageDataGetter", pdgList.size() == 1);
    }

    @Test
    public void testGetNonPageDataGetterObjects() throws Exception{
        VitroRequest vreq = new VitroRequest( new HttpServletRequestStub() );
        vreq.setWebappDaoFactory(wdf);
        
        List<PageDataGetter> pdgList = PageDataGetterUtils.getPageDataGetterObjects(vreq, pageURI_2);
        Assert.assertNotNull(pdgList);
        Assert.assertTrue("should have no PageDataGetters", pdgList.size() == 0);
    }
}
