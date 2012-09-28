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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractObjectPropertyStatementAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;

/**
 * Don't allow user to edit or drop the HomeMenuItem statement.
 */
public class RestrictHomeMenuItemEditingPolicy implements PolicyIface {

	@Override
	public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
			RequestedAction whatToAuth) {
		if (whatToAuth instanceof EditObjectPropertyStatement) {
			return isAuthorized((EditObjectPropertyStatement) whatToAuth);
		} else if (whatToAuth instanceof DropObjectPropertyStatement) {
			return isAuthorized((DropObjectPropertyStatement) whatToAuth);
		} else {
			return notHandled();
		}
	}

	private PolicyDecision isAuthorized(
			AbstractObjectPropertyStatementAction whatToAuth) {
		if (whatToAuth.getPredicateUri()
				.equals(DisplayVocabulary.HAS_ELEMENT)
				&& whatToAuth.getObjectUri().equals(
						DisplayVocabulary.HOME_MENU_ITEM)) {
			return notAuthorized();
		} else {
			return notHandled();
		}
	}

	private BasicPolicyDecision notHandled() {
		return new BasicPolicyDecision(Authorization.INCONCLUSIVE,
				"Doesn't handle this type of request");
	}

	private BasicPolicyDecision notAuthorized() {
		return new BasicPolicyDecision(Authorization.UNAUTHORIZED,
				"Can't edit home menu item.");
	}

	public static class Setup implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent sce) {
			ServletPolicyList.addPolicyAtFront(sce.getServletContext(),
					new RestrictHomeMenuItemEditingPolicy());
		}

		@Override
		public void contextDestroyed(ServletContextEvent ctx) {
			// Nothing to do here.
		}

	}
}
