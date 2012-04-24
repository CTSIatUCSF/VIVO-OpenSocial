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
import java.util.Collections;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.search.beans.StatementToURIsToUpdate;

/**
 * If a class changes classgroups, then all members of that class
 * will have to be update in the search since the serach include
 * the clasgroup membership of all individuals.
 * 
 * Ex. when a statement like: 
 * sub='http://vivoweb.org/ontology/core#Summer&#39; 
 * pred='http://vitro.mannlib.cornell.edu/ns/vitro/0.7#inClassGroup&#39; 
 * obj='http://vivoweb.org/ontology#vitroClassGrouppeople&#39; 
 * changes, all members of the class core:Summer need to be update so they get the new classgroup values. 
 */
public class AdditionalURIsForClassGroupChanges implements
        StatementToURIsToUpdate {

    private OntModel model;

    public AdditionalURIsForClassGroupChanges(OntModel model) {
        this.model = model;                
    }

    @Override
    public List<String> findAdditionalURIsToIndex(Statement stmt) {
        if( stmt != null 
            && VitroVocabulary.IN_CLASSGROUP.equals( stmt.getPredicate().getURI() ) 
            && stmt.getSubject() != null ){
            // its a classgroup membership change for a class, 
            // update all individuals from the class.
            List<String> uris = new ArrayList<String>();
            model.enterCriticalSection(Lock.READ);
            try{
                StmtIterator iter = model.listStatements(null, RDF.type, stmt.getSubject());
                while( iter.hasNext() ){
                    Statement typeStmt = iter.nextStatement();
                    if( typeStmt != null && typeStmt.getSubject().isURIResource() ){
                        uris.add(typeStmt.getSubject().getURI());
                    }
                }
            }finally{
                model.leaveCriticalSection();
            }
            return uris;
        }else{
            return Collections.emptyList();
        }
    }
    
    @Override
    public void startIndexing() { /* nothing to prepare */ }

    @Override
    public void endIndxing() { /* nothing to do */ }
}
