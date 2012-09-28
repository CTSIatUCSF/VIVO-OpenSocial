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
package edu.cornell.mannlib.vitro.webapp.utils.dataGetter;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stubs.javax.servlet.http.HttpServletRequestStub;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class DataGetterUtilsTest extends AbstractTestClass{
    
    OntModel displayModel;
    VitroRequest vreq;
    String testDataGetterURI_1 = "http://vitro.mannlib.cornell.edu/ontologies/display/1.1#query1data";
    String pageURI_1 = "http://vitro.mannlib.cornell.edu/ontologies/display/1.1#SPARQLPage";
    String pageX = "http://vitro.mannlib.cornell.edu/ontologies/display/1.1#pageX";
    String dataGetterX = "http://vitro.mannlib.cornell.edu/ontologies/display/1.1#pageDataGetterX";
    
    @Before
    public void setUp() throws Exception {    
        // Suppress error logging.
        setLoggerLevel(RDFDefaultErrorHandler.class, Level.OFF);
        
        Model model = ModelFactory.createDefaultModel();        
        InputStream in = DataGetterUtilsTest.class.getResourceAsStream("resources/dataGetterTest.n3");
        model.read(in,"","N3");        
        displayModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,model);
        
        vreq = new VitroRequest(new HttpServletRequestStub());
    }

    @Test
    public void testGetJClassForDataGetterURI() throws IllegalAccessException {
        String fullJavaClassName = DataGetterUtils.getJClassForDataGetterURI(displayModel, testDataGetterURI_1);
        Assert.assertNotNull(fullJavaClassName);
        Assert.assertTrue("java class name should not be empty", ! StringUtils.isEmpty(fullJavaClassName));
        Assert.assertEquals(SparqlQueryDataGetter.class.getName(), fullJavaClassName);
    }

    @Test
    public void testDataGetterForURI() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        DataGetter dg = DataGetterUtils.dataGetterForURI(vreq, displayModel, testDataGetterURI_1);
        Assert.assertNotNull(dg);
    }
    
    @Test
    public void testGetDataGettersForPage() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        List<DataGetter> dgList = 
            DataGetterUtils.getDataGettersForPage(vreq, displayModel, pageURI_1);
        Assert.assertNotNull(dgList);
        Assert.assertTrue("List of DataGetters was empty, it should not be.", dgList.size() > 0);
    }


    @Test
    public void testNonPageDataGetter() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException{        
        DataGetter dg = DataGetterUtils.dataGetterForURI(vreq, displayModel,dataGetterX);
        Assert.assertNull(dg);        
        
        List<DataGetter> dgList = 
            DataGetterUtils.getDataGettersForPage(vreq, displayModel, pageX);
        Assert.assertNotNull(dgList);
        Assert.assertTrue("List should be, it was not", dgList.size() == 0);                
    }

}
