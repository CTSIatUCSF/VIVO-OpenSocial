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
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayDataProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayObjectProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;

/**
 * Permit display of various data based on the user's Role level and the
 * restrictions in the onotology.
 */
public class DisplayRestrictedDataByRoleLevelPolicy implements PolicyIface {
	private static final Log log = LogFactory
			.getLog(DisplayRestrictedDataByRoleLevelPolicy.class);

	private final ServletContext ctx;

	public DisplayRestrictedDataByRoleLevelPolicy(ServletContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * If the requested action is to display a property or a property statement,
	 * we might authorize it based on their role level.
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
		/*
		 * This policy treats a self-editor as no better than public. If you
		 * want self-editors to see their own properties, some other policy must
		 * grant that.
		 */
		if (userRole == RoleLevel.SELF) {
			userRole = RoleLevel.PUBLIC;
		}

		PolicyDecision result;
		if (whatToAuth instanceof DisplayDataProperty) {
			result = isAuthorized((DisplayDataProperty) whatToAuth, userRole);
		} else if (whatToAuth instanceof DisplayObjectProperty) {
			result = isAuthorized((DisplayObjectProperty) whatToAuth, userRole);
		} else if (whatToAuth instanceof DisplayDataPropertyStatement) {
			result = isAuthorized((DisplayDataPropertyStatement) whatToAuth,
					userRole);
		} else if (whatToAuth instanceof DisplayObjectPropertyStatement) {
			result = isAuthorized((DisplayObjectPropertyStatement) whatToAuth,
					userRole);
		} else {
			result = defaultDecision("Unrecognized action");
		}

		log.debug("decision for '" + whatToAuth + "' is " + result);
		return result;
	}

	/**
	 * The user may see this data property if they are allowed to see its
	 * predicate.
	 */
	private PolicyDecision isAuthorized(DisplayDataProperty action,
			RoleLevel userRole) {
		String predicateUri = action.getDataProperty().getURI();
		if (canDisplayPredicate(predicateUri, userRole)) {
			return authorized("user may view DataProperty " + predicateUri);
		} else {
			return defaultDecision("user may not view DataProperty "
					+ predicateUri);
		}
	}

	/**
	 * The user may see this object property if they are allowed to see its
	 * predicate.
	 */
	private PolicyDecision isAuthorized(DisplayObjectProperty action,
			RoleLevel userRole) {
		String predicateUri = action.getObjectProperty().getURI();
		if (canDisplayPredicate(predicateUri, userRole)) {
			return authorized("user may view ObjectProperty " + predicateUri);
		} else {
			return defaultDecision("user may not view ObjectProperty "
					+ predicateUri);
		}
	}

	/**
	 * The user may see this data property if they are allowed to see its
	 * subject and its predicate.
	 */
	private PolicyDecision isAuthorized(DisplayDataPropertyStatement action,
			RoleLevel userRole) {
		DataPropertyStatement stmt = action.getDataPropertyStatement();
		String subjectUri = stmt.getIndividualURI();
		String predicateUri = stmt.getDatapropURI();
		if (canDisplayResource(subjectUri, userRole)
				&& canDisplayPredicate(predicateUri, userRole)) {
			return authorized("user may view DataPropertyStatement "
					+ subjectUri + " ==> " + predicateUri);
		} else {
			return defaultDecision("user may not view DataPropertyStatement "
					+ subjectUri + " ==> " + predicateUri);
		}
	}

	/**
	 * The user may see this data property if they are allowed to see its
	 * subject, its predicate, and its object.
	 */
	private PolicyDecision isAuthorized(DisplayObjectPropertyStatement action,
			RoleLevel userRole) {
		ObjectPropertyStatement stmt = action.getObjectPropertyStatement();
		String subjectUri = stmt.getSubjectURI();
		String predicateUri = stmt.getPropertyURI();
		String objectUri = stmt.getObjectURI();
		if (canDisplayResource(subjectUri, userRole)
				&& canDisplayPredicate(predicateUri, userRole)
				&& canDisplayResource(objectUri, userRole)) {
			return authorized("user may view ObjectPropertyStatement "
					+ subjectUri + " ==> " + predicateUri + " ==> " + objectUri);
		} else {
			return defaultDecision("user may not view ObjectPropertyStatement "
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

	private boolean canDisplayResource(String uri, RoleLevel userRole) {
		return PropertyRestrictionPolicyHelper.getBean(ctx).canDisplayResource(
				uri, userRole);
	}

	private boolean canDisplayPredicate(String uri, RoleLevel userRole) {
		return PropertyRestrictionPolicyHelper.getBean(ctx)
				.canDisplayPredicate(uri, userRole);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + hashCode();
	}

}
