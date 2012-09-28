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

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasAssociatedIndividual;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.AbstractRelationshipPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;

/**
 * Permit self-editors to edit the properties of classes with which they share a
 * special relationship. So for example:
 * 
 * A self-editor may edit properties of an InformationResource for which he is
 * an author, an editor, or in which he is featured.
 * 
 * A self-editor may edit properties of a Project in which he plays a clinical
 * role.
 * 
 * Etc.
 * 
 * NOTE: properties or resources which are restricted by namespace or by access
 * setting will still not be editable, even if this special relationship
 * applies.
 * 
 * NOTE: This could be further generalized by building a list of authorizing
 * relationships, where each relationship may specify a type of object, a
 * relating property (or chain of properties), and a text message describing the
 * relationship (to be used in the decision).
 */
public class SelfEditorRelationshipPolicy extends AbstractRelationshipPolicy
		implements PolicyIface {
	private static final Log log = LogFactory
			.getLog(SelfEditorRelationshipPolicy.class);

	public SelfEditorRelationshipPolicy(ServletContext ctx) {
		super(ctx);
	}

	@Override
	public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
			RequestedAction whatToAuth) {
		if (whatToAuth == null) {
			return inconclusiveDecision("whatToAuth was null");
		}

		if (!(whatToAuth instanceof AbstractPropertyStatementAction)) {
			return inconclusiveDecision("Does not authorize "
					+ whatToAuth.getClass().getSimpleName() + " actions");
		}
		AbstractPropertyStatementAction action = (AbstractPropertyStatementAction) whatToAuth;

		List<String> userUris = new ArrayList<String>(
				HasAssociatedIndividual.getIndividualUris(whoToAuth));
		if (userUris.isEmpty()) {
			return inconclusiveDecision("Not self-editing.");
		}

		if (!canModifyPredicate(action.getPredicateUri())) {
			return cantModifyPredicate(action.getPredicateUri());
		}

		for (String resourceUri : action.getResourceUris()) {
			if (!canModifyResource(resourceUri)) {
				return cantModifyResource(resourceUri);
			}
		}

		return checkRelationships(userUris, action);
	}

	private PolicyDecision checkRelationships(List<String> userUris,
			AbstractPropertyStatementAction action) {

		PolicyDecision decision = new InformationResourceChecker(action)
				.isAuthorized(userUris);
		if (decision == null) {
			decision = new GrantChecker(action).isAuthorized(userUris);
		}
		if (decision == null) {
			decision = new ProjectOrServiceChecker(action)
					.isAuthorized(userUris);
		}
		if (decision == null) {
			decision = new PresentationChecker(action).isAuthorized(userUris);
		}
		if (decision == null) {
			decision = new CourseChecker(action).isAuthorized(userUris);
		}
		if (decision == null) {
			decision = userNotAuthorizedToStatement();
		}
		return decision;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName()
				+ ": information resources, grants, projects, etc. - "
				+ hashCode();
	}

	// ----------------------------------------------------------------------
	// helper classes
	// ----------------------------------------------------------------------

	/**
	 * When the system starts up, install the policy. This class must be a
	 * listener in web.xml
	 * 
	 * The CommonIdentifierBundleFactory already creates the IDs we need.
	 */
	public static class Setup implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			ServletContext ctx = sce.getServletContext();

			ServletPolicyList.addPolicy(ctx, new SelfEditorRelationshipPolicy(
					ctx));
		}

		@Override
		public void contextDestroyed(ServletContextEvent sce) { /* nothing */
		}
	}
}
