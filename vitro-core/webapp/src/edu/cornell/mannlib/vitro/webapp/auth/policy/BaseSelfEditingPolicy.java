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

import javax.servlet.ServletContext;

import edu.cornell.mannlib.vitro.webapp.auth.policy.bean.PropertyRestrictionPolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;

/**
 * A base class with utility methods for policies involving self-editing.
 */
public abstract class BaseSelfEditingPolicy {
	protected final ServletContext ctx;
	protected final RoleLevel roleLevel;

	public BaseSelfEditingPolicy(ServletContext ctx, RoleLevel roleLevel) {
		this.ctx = ctx;
		this.roleLevel = roleLevel;
	}

	protected boolean canModifyResource(String uri) {
		return PropertyRestrictionPolicyHelper.getBean(ctx).canModifyResource(
				uri, roleLevel);
	}

	protected boolean canModifyPredicate(String uri) {
		return PropertyRestrictionPolicyHelper.getBean(ctx).canModifyPredicate(
				uri, roleLevel);
	}

	protected PolicyDecision cantModifyResource(String uri) {
		return inconclusiveDecision("No access to admin resources; cannot modify "
				+ uri);
	}

	protected PolicyDecision cantModifyPredicate(String uri) {
		return inconclusiveDecision("No access to admin predicates; cannot modify "
				+ uri);
	}

	protected PolicyDecision userNotAuthorizedToStatement() {
		return inconclusiveDecision("User has no access to this statement.");
	}

	/** An INCONCLUSIVE decision with a message like "PolicyClass: message". */
	protected PolicyDecision inconclusiveDecision(String message) {
		return new BasicPolicyDecision(Authorization.INCONCLUSIVE, getClass()
				.getSimpleName() + ": " + message);
	}

	/** An AUTHORIZED decision with a message like "PolicyClass: message". */
	protected PolicyDecision authorizedDecision(String message) {
		return new BasicPolicyDecision(Authorization.AUTHORIZED, getClass()
				.getSimpleName() + ": " + message);
	}

}
