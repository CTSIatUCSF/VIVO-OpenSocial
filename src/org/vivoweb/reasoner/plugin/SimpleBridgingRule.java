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

package org.vivoweb.reasoner.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.reasoner.ReasonerPlugin;
import edu.cornell.mannlib.vitro.webapp.reasoner.SimpleReasoner;

/**
 * handles rules of the form
 * assertedProp1(?x, ?y) ^ assertedProp2(?y, ?z) -> inferredProp(?x, ?z)
 *	
 * @author bjl23
 *
 */
public abstract class SimpleBridgingRule implements ReasonerPlugin {

	private static final Log log = LogFactory.getLog(SimpleBridgingRule.class);
	
	private Property assertedProp1;
	private Property assertedProp2;
	private String   queryStr;
	private SimpleReasoner simpleReasoner;
	
	protected SimpleBridgingRule(String assertedProp1, String assertedProp2, String inferredProp) {
		this.assertedProp1 = ResourceFactory.createProperty(assertedProp1);
        this.assertedProp2 = ResourceFactory.createProperty(assertedProp2);
        
        this.queryStr = "CONSTRUCT { \n" +
                        "  ?x <" + inferredProp + "> ?z \n" +
                        "} WHERE { \n" +
                        "  ?x <" + assertedProp1 + "> ?y . \n" +
                        "  ?y <" + assertedProp2 + "> ?z \n" +
                        "}";
	}
	
	public boolean isInterestedInAddedStatement(Statement stmt) {
		return isRelevantPredicate(stmt);
	}
	
	public boolean isInterestedInRemovedStatement(Statement stmt) {
		return isRelevantPredicate(stmt);
	}
	
	public void addedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
		if (ignore(stmt)) {
			return;
		}
        Model inf = constructInferences(stmt, aboxAssertionsModel);
        StmtIterator sit = inf.listStatements();
        while(sit.hasNext()) {
        	Statement s = sit.nextStatement();
        	if (simpleReasoner != null) simpleReasoner.addInference(s,aboxInferencesModel);
        }     
	}
	
	private boolean ignore(Statement stmt) {
		return (
				(stmt.getSubject().isAnon() || stmt.getObject().isAnon())
			    // can't deal with blank nodes
		||
		        (!stmt.getObject().isResource()) 
			    // don't deal with literal values
	    );
	}

	private Model constructInferences(Statement stmt, Model aboxAssertionsModel) {
		String queryStr = new String(this.queryStr);
		if (stmt.getPredicate().equals(assertedProp1)) {
			queryStr = queryStr.replace(
					"?x", "<" + stmt.getSubject().getURI() + ">");
			queryStr = queryStr.replace(
					"?y", "<" + ((Resource) stmt.getObject()).getURI() + ">");
		} else if (stmt.getPredicate().equals(assertedProp2)) {
			queryStr = queryStr.replace(
					"?y", "<" + stmt.getSubject().getURI() + ">");
			queryStr = queryStr.replace(
					"?z", "<" + ((Resource) stmt.getObject()).getURI() + ">");			
		} else {
			// should never be here
			return ModelFactory.createDefaultModel();
		}
		Query query = QueryFactory.create(queryStr);
		QueryExecution qe = QueryExecutionFactory.create(query, aboxAssertionsModel);
		try {
			return qe.execConstruct();
		} finally {
			qe.close();
		}
		
	}
	
    public void removedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
		if (ignore(stmt)) {
			return;
		}
		// The following should probably be improved, as it is likely to be 
		// inefficient.
		// The SPARQL query will currently depend on the existence of the triple
		// that has just been removed, so we'll union it in temporarily.
		// TODO: make the SPARQL query construction smarter.
		Model m = ModelFactory.createDefaultModel();
		m.add(stmt);
		Model union = ModelFactory.createUnion(m, aboxAssertionsModel);
		Model inf = constructInferences(stmt, union);
        StmtIterator sit = inf.listStatements();
        while(sit.hasNext()) {
        	Statement s = sit.nextStatement();
        	if (simpleReasoner != null) simpleReasoner.removeInference(s,aboxInferencesModel);
        }     
		
        aboxInferencesModel.remove(constructInferences(stmt, union));  
    }
	
    private boolean isRelevantPredicate(Statement stmt) {
		return (assertedProp1.equals(stmt.getPredicate())
				|| assertedProp2.equals(stmt.getPredicate()));
    }
    
	public void setSimpleReasoner(SimpleReasoner simpleReasoner) {
		this.simpleReasoner = simpleReasoner;
	}
	
	public SimpleReasoner getSimpleReasoner() {
		return this.simpleReasoner;		
	}
}

