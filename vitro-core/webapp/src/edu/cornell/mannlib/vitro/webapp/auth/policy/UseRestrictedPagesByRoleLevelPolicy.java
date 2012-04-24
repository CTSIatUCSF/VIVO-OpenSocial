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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasRoleLevel;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.admin.RebuildVClassGroupCache;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.querymodel.QueryFullModel;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.querymodel.QueryUserAccountsModel;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.AccessSpecialDataModels;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.DoFrontEndEditing;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.DoBackEndEditing;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.EditOntology;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.EditOwnAccount;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.EditSiteInformation;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageMenus;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageOwnProxies;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManagePortals;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageProxies;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageSearchIndex;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageTabs;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageUserAccounts;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.RefreshVisualizationCacheAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.SeeIndividualEditingPanel;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.SeeRevisionInfo;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.SeeSiteAdminPage;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.SeeStartupStatus;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.SeeVerbosePropertyInformation;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseAdvancedDataToolsPages;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseBasicAjaxControllers;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseMiscellaneousAdminPages;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseMiscellaneousCuratorPages;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseMiscellaneousEditorPages;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseMiscellaneousPages;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;

/**
 * Check the users role level to determine whether they are allowed to use
 * restricted pages.
 */
public class UseRestrictedPagesByRoleLevelPolicy implements PolicyIface {
	private static final Log log = LogFactory
			.getLog(UseRestrictedPagesByRoleLevelPolicy.class);

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

		PolicyDecision result;
		if (whatToAuth instanceof UseAdvancedDataToolsPages) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);

		} else if (whatToAuth instanceof ManageUserAccounts) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);

		} else if (whatToAuth instanceof ManageMenus) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);

		} else if (whatToAuth instanceof ManageSearchIndex) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);
			
		} else if (whatToAuth instanceof UseMiscellaneousAdminPages) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);

		} else if (whatToAuth instanceof AccessSpecialDataModels) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);
			
		} else if (whatToAuth instanceof RebuildVClassGroupCache) {
            result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);

		} else if (whatToAuth instanceof RefreshVisualizationCacheAction) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);
		
		} else if (whatToAuth instanceof SeeStartupStatus) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);
			
		} else if (whatToAuth instanceof ManageProxies) {
			result = isAuthorized(whatToAuth, RoleLevel.DB_ADMIN, userRole);
			
		} else if (whatToAuth instanceof EditOntology) {
			result = isAuthorized(whatToAuth, RoleLevel.CURATOR, userRole);

		} else if (whatToAuth instanceof ManagePortals) {
			result = isAuthorized(whatToAuth, RoleLevel.CURATOR, userRole);

		} else if (whatToAuth instanceof ManageTabs) {
			result = isAuthorized(whatToAuth, RoleLevel.CURATOR, userRole);

		} else if (whatToAuth instanceof EditSiteInformation) {
			result = isAuthorized(whatToAuth, RoleLevel.CURATOR, userRole);
			
		} else if (whatToAuth instanceof SeeVerbosePropertyInformation) {
			result = isAuthorized(whatToAuth, RoleLevel.CURATOR, userRole);
			
		} else if (whatToAuth instanceof UseMiscellaneousCuratorPages) {
			result = isAuthorized(whatToAuth, RoleLevel.CURATOR, userRole);

		} else if (whatToAuth instanceof DoBackEndEditing) {
			result = isAuthorized(whatToAuth, RoleLevel.EDITOR, userRole);

		} else if (whatToAuth instanceof SeeSiteAdminPage) {
			result = isAuthorized(whatToAuth, RoleLevel.EDITOR, userRole);

		} else if (whatToAuth instanceof SeeRevisionInfo) {
			result = isAuthorized(whatToAuth, RoleLevel.EDITOR, userRole);

		} else if (whatToAuth instanceof SeeIndividualEditingPanel) {
			result = isAuthorized(whatToAuth, RoleLevel.EDITOR, userRole);
			
		} else if (whatToAuth instanceof UseMiscellaneousEditorPages) {
			result = isAuthorized(whatToAuth, RoleLevel.EDITOR, userRole);
			
		} else if (whatToAuth instanceof UseBasicAjaxControllers) {
			result = isAuthorized(whatToAuth, RoleLevel.SELF, userRole);
			
		} else if (whatToAuth instanceof UseMiscellaneousPages) {
			result = isAuthorized(whatToAuth, RoleLevel.SELF, userRole);
			
		} else if (whatToAuth instanceof EditOwnAccount) {
			result = isAuthorized(whatToAuth, RoleLevel.SELF, userRole);

		} else if (whatToAuth instanceof ManageOwnProxies) {
			result = isAuthorized(whatToAuth, RoleLevel.SELF, userRole);
			
		} else if (whatToAuth instanceof QueryUserAccountsModel) {
			result = isAuthorized(whatToAuth, RoleLevel.SELF, userRole);
			
		} else if (whatToAuth instanceof DoFrontEndEditing) {
			result = isAuthorized(whatToAuth, RoleLevel.SELF, userRole);
			
		} else if (whatToAuth instanceof QueryFullModel) {
			result = isAuthorized(whatToAuth, RoleLevel.PUBLIC, userRole);
			
		} else {
			result = defaultDecision("Unrecognized action");
		}

		log.debug("decision for '" + whatToAuth + "' is " + result);
		return result;
	}

	/** Authorize if user's role is at least as high as the required role. */
	private PolicyDecision isAuthorized(RequestedAction whatToAuth,
			RoleLevel requiredRole, RoleLevel currentRole) {
		if (isRoleAtLeast(requiredRole, currentRole)) {
			return authorized("User may view page: " + whatToAuth
					+ ", requiredRole=" + requiredRole + ", currentRole="
					+ currentRole);
		} else {
			return defaultDecision("User may not view page: " + whatToAuth
					+ ", requiredRole=" + requiredRole + ", currentRole="
					+ currentRole);
		}
	}

	private boolean isRoleAtLeast(RoleLevel required, RoleLevel current) {
		return (current.compareTo(required) >= 0);
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

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + hashCode();
	}
}
