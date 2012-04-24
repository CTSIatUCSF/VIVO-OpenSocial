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

import com.hp.hpl.jena.rdf.model.Model;
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
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddDataPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddObjectPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropDataPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropObjectPropStmt;

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
		return Actions.notNull(actions).isAuthorized(policy, ids);
	}

	/**
	 * Do the current policies authorize the current user to add all of the
	 * statements in this model?
	 */
	public static boolean isAuthorizedToAdd(HttpServletRequest req, Model model) {
		if ((req == null) || (model == null)) {
			return false;
		}

		StmtIterator stmts = model.listStatements();
		try {
			while (stmts.hasNext()) {
				if (!isAuthorizedToAdd(req, stmts.next())) {
					return false;
				}
			}
			return true;
		} finally {
			stmts.close();
		}
	}

	/**
	 * Do the current policies authorize the current user to add this statement?
	 * 
	 * The statement is expected to be fully-populated, with no null fields.
	 */
	public static boolean isAuthorizedToAdd(HttpServletRequest req,
			Statement stmt) {
		if ((req == null) || (stmt == null)) {
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
			action = new AddObjectPropStmt(subject.getURI(),
					predicate.getURI(), objectNode.asResource().getURI());
		} else {
			action = new AddDataPropStmt(subject.getURI(), predicate.getURI(),
					objectNode.asLiteral());
		}
		return isAuthorizedForActions(req, action);
	}

	/**
	 * Do the current policies authorize the current user to drop all of the
	 * statements in this model?
	 */
	public static boolean isAuthorizedToDrop(HttpServletRequest req, Model model) {
		if ((req == null) || (model == null)) {
			return false;
		}

		StmtIterator stmts = model.listStatements();
		try {
			while (stmts.hasNext()) {
				if (!isAuthorizedToDrop(req, stmts.next())) {
					return false;
				}
			}
			return true;
		} finally {
			stmts.close();
		}
	}

	/**
	 * Do the current policies authorize the current user to drop this
	 * statement?
	 * 
	 * The statement is expected to be fully-populated, with no null fields.
	 */
	public static boolean isAuthorizedToDrop(HttpServletRequest req,
			Statement stmt) {
		if ((req == null) || (stmt == null)) {
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
			action = new DropObjectPropStmt(subject.getURI(),
					predicate.getURI(), objectNode.asResource().getURI());
		} else {
			action = new DropDataPropStmt(subject.getURI(), predicate.getURI(),
					objectNode.asLiteral());
		}
		return isAuthorizedForActions(req, action);
	}

	/**
	 * No need to instantiate this helper class - all methods are static.
	 */
	private PolicyHelper() {
		// nothing to do.
	}

}
