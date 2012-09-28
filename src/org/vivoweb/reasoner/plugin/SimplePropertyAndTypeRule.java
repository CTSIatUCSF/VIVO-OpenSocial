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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.reasoner.ReasonerPlugin;
import edu.cornell.mannlib.vitro.webapp.reasoner.SimpleReasoner;

/**
 * handles rules of the form
 * assertedProp(?x, ?y) ^ type(?x) -> inferredProp(?x, ?y)
 *	
 * @author bjl23
 *
 */
public abstract class SimplePropertyAndTypeRule implements ReasonerPlugin {

	private Property ASSERTED_PROP;
	private Resource TYPE;
	private Property INFERRED_PROP;
	private SimpleReasoner simpleReasoner;
	
	protected SimplePropertyAndTypeRule(String assertedProp, String type, String inferredProp) {
		TYPE = ResourceFactory.createResource(type);
        ASSERTED_PROP = ResourceFactory.createProperty(assertedProp);
        INFERRED_PROP = ResourceFactory.createProperty(inferredProp);
	}
	
	public boolean isInterestedInAddedStatement(Statement stmt) {
		return (RDF.type.equals(stmt.getPredicate()) || isRelevantPredicate(stmt));
	}
	
	public boolean isInterestedInRemovedStatement(Statement stmt) {
		return (RDF.type.equals(stmt.getPredicate()) || isRelevantPredicate(stmt));
	}
	
	public void addedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
		boolean relevantType = isRelevantType(stmt, TBoxInferencesModel);
		boolean relevantPredicate = isRelevantPredicate(stmt);

		if (relevantType) {
			StmtIterator stmtIt = aboxAssertionsModel.listStatements(
					stmt.getSubject(), ASSERTED_PROP, (RDFNode)null);
			while (stmtIt.hasNext()) {
				Statement s = stmtIt.nextStatement();
				tryToInfer(stmt.getSubject(), 
						   INFERRED_PROP, 
						   s.getObject(), 
						   aboxAssertionsModel, 
						   aboxInferencesModel);
			}
		} else if (relevantPredicate) {
			if(aboxAssertionsModel.contains(
					stmt.getSubject(), RDF.type, TYPE) 
			  || aboxInferencesModel.contains(
					  stmt.getSubject(), RDF.type, TYPE)) {
				tryToInfer(stmt.getSubject(), 
						   INFERRED_PROP, 
						   stmt.getObject(), 
						   aboxAssertionsModel, 
						   aboxInferencesModel);
			}
		}
	}
	
	private void tryToInfer(Resource subject, 
			                Property predicate, 
			                RDFNode object, 
			                Model aboxAssertionsModel, 
			                Model aboxInferencesModel) {
		// this should be part of a superclass or some class that provides
		// reasoning framework functions
		Statement s = ResourceFactory.createStatement(subject, predicate, object);
		if (simpleReasoner != null) {
			simpleReasoner.addInference(s,aboxInferencesModel);
		}
	}

    public void removedABoxStatement(Statement stmt, 
            Model aboxAssertionsModel, 
            Model aboxInferencesModel, 
            OntModel TBoxInferencesModel) {
    	
    	if (isRelevantPredicate(stmt)) {
//    		if (aboxAssertionsModel.contains(
//    				stmt.getSubject(), RDF.type, BIBO_DOCUMENT)
//    			        || aboxInferencesModel.contains(
//    						stmt.getSubject(), RDF.type, BIBO_DOCUMENT)) {
    		    if (simpleReasoner != null) {
    		       simpleReasoner.removeInference(ResourceFactory.createStatement(stmt.getSubject(), INFERRED_PROP, stmt.getObject()), aboxInferencesModel);
    		    }    
//    		}
    	} else if (isRelevantType(stmt, TBoxInferencesModel)) {
    		if(!aboxInferencesModel.contains(
    				stmt.getSubject(), RDF.type, TYPE)) {
    			StmtIterator groundIt = aboxAssertionsModel.listStatements(
    					stmt.getSubject(), ASSERTED_PROP, (RDFNode) null);
    			while (groundIt.hasNext()) {
    				Statement groundStmt = groundIt.nextStatement();
        		    simpleReasoner.removeInference(ResourceFactory.createStatement(groundStmt.getSubject(), INFERRED_PROP, groundStmt.getObject()), aboxInferencesModel);
    			}
    		}
    	}
    }
    
    private boolean isRelevantType(Statement stmt, Model TBoxInferencesModel) {
		return (RDF.type.equals(stmt.getPredicate()) 
				&& (TYPE.equals(stmt.getObject()) 
						|| TBoxInferencesModel.contains(
								(Resource) stmt.getObject(), RDFS.subClassOf, TYPE)));
    }	
	
    private boolean isRelevantPredicate(Statement stmt) {
		return (ASSERTED_PROP.equals(stmt.getPredicate()));
    }
    
	public void setSimpleReasoner(SimpleReasoner simpleReasoner) {
		this.simpleReasoner = simpleReasoner;
	}
	
	public SimpleReasoner getSimpleReasoner() {
		return this.simpleReasoner;		
	}
}
