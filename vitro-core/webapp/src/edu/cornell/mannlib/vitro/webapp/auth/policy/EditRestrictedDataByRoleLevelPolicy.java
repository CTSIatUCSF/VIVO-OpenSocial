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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasRoleLevel;
import edu.cornell.mannlib.vitro.webapp.auth.policy.bean.PropertyRestrictionPolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractDataPropertyAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractObjectPropertyAction;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;

/**
 * Permit adding, editing, or deleting of various data based on the user's Role
 * level and the restrictions in the ontology.
 * 
 * This policy only authorizes users who are Editors, Curators or DBAs.
 * Self-editors and users who are not logged in must look elsewhere for
 * authorization.
 */
public class EditRestrictedDataByRoleLevelPolicy implements PolicyIface {
	private static final Log log = LogFactory
			.getLog(EditRestrictedDataByRoleLevelPolicy.class);

	private final ServletContext ctx;

	public EditRestrictedDataByRoleLevelPolicy(ServletContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * If the requested action is to edit a property statement, we might
	 * authorize it based on their role level.
	 */
	@Override
	public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
			RequestedAction whatToAuth) {
		if (whoToAuth == null) {
			return defaultDecision("whomToAuth was null");
		}
		if (whatToAuth == null) {
			return defaultDecision("whatToAuth was null");
		}

		RoleLevel userRole = HasRoleLevel.getUsersRoleLevel(whoToAuth);
		if (!userRoleIsHighEnough(userRole)) {
			return defaultDecision("insufficient role level: " + userRole);
		}

		PolicyDecision result;
		if (whatToAuth instanceof AbstractDataPropertyAction) {
			result = isAuthorized((AbstractDataPropertyAction) whatToAuth,
					userRole);
		} else if (whatToAuth instanceof AbstractObjectPropertyAction) {
			result = isAuthorized((AbstractObjectPropertyAction) whatToAuth,
					userRole);
		} else {
			result = defaultDecision("Unrecognized action");
		}

		log.debug("whoToAuth: " + whoToAuth);
		log.debug("decision for '" + whatToAuth + "' is " + result);
		return result;
	}

	/**
	 * We only consider Editors, Curators and DBAs.
	 */
	private boolean userRoleIsHighEnough(RoleLevel userRole) {
		return (userRole == RoleLevel.EDITOR)
				|| (userRole == RoleLevel.CURATOR)
				|| (userRole == RoleLevel.DB_ADMIN);
	}

	/**
	 * The user may add, edit, or delete this data property if they are allowed
	 * to modify its subject and its predicate.
	 */
	private PolicyDecision isAuthorized(AbstractDataPropertyAction action,
			RoleLevel userRole) {
		String subjectUri = action.getSubjectUri();
		String predicateUri = action.getPredicateUri();
		if (canModifyResource(subjectUri, userRole)
				&& canModifyPredicate(predicateUri, userRole)) {
			return authorized("user may modify DataPropertyStatement "
					+ subjectUri + " ==> " + predicateUri);
		} else {
			return defaultDecision("user may not modify DataPropertyStatement "
					+ subjectUri + " ==> " + predicateUri);
		}
	}

	/**
	 * The user may add, edit, or delete this data property if they are allowed
	 * to modify its subject, its predicate, and its object.
	 */
	private PolicyDecision isAuthorized(AbstractObjectPropertyAction action,
			RoleLevel userRole) {
		String subjectUri = action.getUriOfSubject();
		String predicateUri = action.getUriOfPredicate();
		String objectUri = action.getUriOfObject();
		if (canModifyResource(subjectUri, userRole)
				&& canModifyPredicate(predicateUri, userRole)
				&& canModifyResource(objectUri, userRole)) {
			return authorized("user may modify ObjectPropertyStatement "
					+ subjectUri + " ==> " + predicateUri + " ==> " + objectUri);
		} else {
			return defaultDecision("user may not modify ObjectPropertyStatement "
					+ subjectUri + " ==> " + predicateUri + " ==> " + objectUri);
		}
	}

	/** If the user is explicitly authorized, return this. */
	private PolicyDecision authorized(String message) {
		String className = this.getClass().getSimpleName();
		return new BasicPolicyDecision(Authorization.AUTHORIZED, className
				+ ": " + message);
	}

	/** If the user isn't explicitly authorized, return this. */
	private PolicyDecision defaultDecision(String message) {
		return new BasicPolicyDecision(Authorization.INCONCLUSIVE, message);
	}

	private boolean canModifyResource(String uri, RoleLevel userRole) {
		return PropertyRestrictionPolicyHelper.getBean(ctx).canModifyResource(
				uri, userRole);
	}

	private boolean canModifyPredicate(String uri, RoleLevel userRole) {
		return PropertyRestrictionPolicyHelper.getBean(ctx).canModifyPredicate(
				uri, userRole);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + hashCode();
	}
}
