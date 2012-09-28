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

package edu.cornell.mannlib.vitro.webapp.auth.policy;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropObjectPropertyStatement;

/**
 * A collection of static methods to help determine whether requested actions
 * are authorized by current policy.
 */
public class PolicyHelper {
	private static final Log log = LogFactory.getLog(PolicyHelper.class);

	/**
	 * Are these actions authorized for the current user by the current
	 * policies?
	 */
	public static boolean isAuthorizedForActions(HttpServletRequest req,
			RequestedAction... actions) {
		return isAuthorizedForActions(req, new Actions(actions));
	}

	/**
	 * Are these actions authorized for the current user by the current
	 * policies?
	 */
	public static boolean isAuthorizedForActions(HttpServletRequest req,
			Actions actions) {
		PolicyIface policy = ServletPolicyList.getPolicies(req);
		IdentifierBundle ids = RequestIdentifiers.getIdBundleForRequest(req);
		return isAuthorizedForActions(ids, policy, actions);
	}

	/**
	 * Are these actions authorized for these identifiers by these policies?
	 */
	public static boolean isAuthorizedForActions(IdentifierBundle ids,
			PolicyIface policy, Actions actions) {
		return Actions.notNull(actions).isAuthorized(policy, ids);
	}

	/**
	 * Do the current policies authorize the current user to add this statement
	 * to this model?
	 * 
	 * The statement is expected to be fully-populated, with no null fields.
	 */
	public static boolean isAuthorizedToAdd(HttpServletRequest req,
			Statement stmt, OntModel modelToBeModified) {
		if ((req == null) || (stmt == null) || (modelToBeModified == null)) {
			return false;
		}

		Resource subject = stmt.getSubject();
		Property predicate = stmt.getPredicate();
		RDFNode objectNode = stmt.getObject();
		if ((subject == null) || (predicate == null) || (objectNode == null)) {
			return false;
		}

		RequestedAction action;
		if (objectNode.isResource()) {
			action = new AddObjectPropertyStatement(modelToBeModified,
					subject.getURI(), predicate.getURI(), objectNode
							.asResource().getURI());
		} else {
			action = new AddDataPropertyStatement(modelToBeModified,
					subject.getURI(), predicate.getURI());
		}
		return isAuthorizedForActions(req, action);
	}

	/**
	 * Do the current policies authorize the current user to drop this statement
	 * from this model?
	 * 
	 * The statement is expected to be fully-populated, with no null fields.
	 */
	public static boolean isAuthorizedToDrop(HttpServletRequest req,
			Statement stmt, OntModel modelToBeModified) {
		if ((req == null) || (stmt == null) || (modelToBeModified == null)) {
			return false;
		}

		Resource subject = stmt.getSubject();
		Property predicate = stmt.getPredicate();
		RDFNode objectNode = stmt.getObject();
		if ((subject == null) || (predicate == null) || (objectNode == null)) {
			return false;
		}

		RequestedAction action;
		if (objectNode.isResource()) {
			action = new DropObjectPropertyStatement(modelToBeModified,
					subject.getURI(), predicate.getURI(), objectNode
							.asResource().getURI());
		} else {
			action = new DropDataPropertyStatement(modelToBeModified,
					subject.getURI(), predicate.getURI());
		}
		return isAuthorizedForActions(req, action);
	}

	/**
	 * Do the current policies authorize the current user to modify this model
	 * by adding all of the statments in the additions model and dropping all of
	 * the statements in the retractions model?
	 * 
	 * This differs from the other calls to "isAuthorized..." because we always
	 * expect the answer to be true. If the answer is false, it should be logged
	 * as an error.
	 * 
	 * Even if a statement fails the test, continue to test the others, so the
	 * log will contain a full record of all failures. This is no more expensive
	 * than if all statements succeeded.
	 */
	public static boolean isAuthorizedAsExpected(HttpServletRequest req,
			Model additions, Model retractions, OntModel modelBeingModified) {
		if (req == null) {
			log.warn("Can't evaluate authorization if req is null");
			return false;
		}
		if (additions == null) {
			log.warn("Can't evaluate authorization if additions model is null");
			return false;
		}
		if (retractions == null) {
			log.warn("Can't evaluate authorization if retractions model is null");
			return false;
		}
		if (modelBeingModified == null) {
			log.warn("Can't evaluate authorization if model being modified is null");
			return false;
		}

		/*
		 * The naive way to test the additions is to test each statement against
		 * the JenaOntModel. However, some of the statements may not be
		 * authorized unless others are added first. The client code should not
		 * need to know which sequence will be successful. The client code only
		 * cares that such a sequence does exist.
		 * 
		 * There are 3 obvious ways to test this, ranging from the most rigorous
		 * (and most costly) to the least costly (and least rigorous).
		 * 
		 * 1. Try all sequences to find one that works. First, try to add each
		 * statement to the modelBeingModified. If any statement succeeds,
		 * construct a temporary model that joins that statement to the
		 * modelBeingModified. Now try the remaining statements against that
		 * temporary model, adding the statement each time we are successful. If
		 * we eventually find all of the statements authorized, declare success.
		 * This is logically rigorous, but could become geometrically expensive
		 * as statements are repeatedly tried against incremented models. O(n!).
		 * 
		 * 2. Try each statement on the assumption that all of the others have
		 * already been added. So for each statement we create a temporary
		 * modeol that joins the other additions to the JenaOntModel. If all
		 * statements pass this test, declare success. This is logically flawed
		 * since it is possible that two statements would circularly authorize
		 * each other, but that neither statement could be added first. However,
		 * that seems like a small risk, and the algorithm is considerably less
		 * expensive. O(n).
		 * 
		 * 3. Try each statement on the assumption that all of the statements
		 * (including itself) have already been added. If all statements pass
		 * this test, declare success. This has the additional minor flaw of
		 * allowing a statement to authorize its own addition, but this seems
		 * very unlikely. This is about as expensive as choice 2., but much
		 * simpler to code.
		 * 
		 * For now, I am going with choice 3.
		 */

		boolean result = true;

		OntModel modelToTestAgainst = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		modelToTestAgainst.addSubModel(additions);
		modelToTestAgainst.addSubModel(modelBeingModified);

		StmtIterator addStmts = additions.listStatements();
		try {
			while (addStmts.hasNext()) {
				Statement stmt = addStmts.next();
				if (isAuthorizedToAdd(req, stmt, modelToTestAgainst)) {
					if (log.isDebugEnabled()) {
						log.debug("Last-chance authorization check: "
								+ "authorized to add statement: "
								+ formatStatement(stmt));
					}
				} else {
					log.warn("Last-chance authorization check reveals not "
							+ "authorized to add statement: "
							+ formatStatement(stmt));
					result = false;
				}
			}
		} finally {
			addStmts.close();
		}

		/*
		 * For retractions, there is no such conundrum. Assume that all of the
		 * additions have been added, and check the authorization of each
		 * retraction.
		 */

		StmtIterator dropStmts = retractions.listStatements();
		try {
			while (dropStmts.hasNext()) {
				Statement stmt = dropStmts.next();
				if (isAuthorizedToDrop(req, stmt, modelToTestAgainst)) {
					if (log.isDebugEnabled()) {
						log.debug("Last-chance authorization check: "
								+ "authorized to drop statement: "
								+ formatStatement(stmt));
					}
				} else {
					log.warn("Last-chance authorization check reveals not "
							+ "authorized to drop statement: "
							+ formatStatement(stmt));
					result = false;
				}
			}
		} finally {
			dropStmts.close();
		}

		return result;
	}

	private static String formatStatement(Statement stmt) {
		if (stmt == null) {
			return "null statement";
		}
		return "<" + stmt.getSubject() + "> <" + stmt.getPredicate() + "> <"
				+ stmt.getObject() + ">";
	}

	/**
	 * No need to instantiate this helper class - all methods are static.
	 */
	private PolicyHelper() {
		// nothing to do.
	}

}
