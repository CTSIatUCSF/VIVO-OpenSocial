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
package edu.cornell.mannlib.vitro.webapp.search.indexing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * @author bdc34
 *
 */
public class AdditionalURIsForTypeStatementsTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for {@link edu.cornell.mannlib.vitro.webapp.search.indexing.AdditionalURIsForTypeStatements#findAdditionalURIsToIndex(com.hp.hpl.jena.rdf.model.Statement)}.
     */
    @Test
    public void testFindAdditionalURIsToIndex() {
        AdditionalURIsForTypeStatements aufts = new AdditionalURIsForTypeStatements();

        String subject = "http://caruso-laptop.mannlib.cornell.edu:8090/vivo/individual/n3270";
        Statement typeChangeStatement =  ResourceFactory.createStatement(                
                ResourceFactory.createResource(subject),
                ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
                ResourceFactory.createResource( "http://caruso-laptop.mannlib.cornell.edu:8090/vivo/ontology/localOnt#LocalInternalClass"));
                  
        
        List<String> uris = aufts.findAdditionalURIsToIndex( typeChangeStatement );
        
        Assert.assertNotNull(uris);
        Assert.assertTrue("Did not contain subject of type change statement", uris.contains(subject));
    }

}
