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

package edu.cornell.mannlib.vitro.webapp.controller.authenticate;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean.AuthenticationSource;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ActiveIdentifierBundleFactories;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ArrayIdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;

/**
 * A "restricted" authenticator, that will not allow logins except for root and
 * for users that are authorized to maintain the system.
 */
public class RestrictedAuthenticator extends Authenticator {
	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	public static class Factory implements AuthenticatorFactory {
		@Override
		public Authenticator getInstance(HttpServletRequest req) {
			return new RestrictedAuthenticator(req, new BasicAuthenticator(req));
		}
	}

	// ----------------------------------------------------------------------
	// The authenticator
	// ----------------------------------------------------------------------

	private final HttpServletRequest req;
	private final Authenticator auth;

	public RestrictedAuthenticator(HttpServletRequest req, Authenticator auth) {
		this.req = req;
		this.auth = auth;
	}

	@Override
	public boolean isUserPermittedToLogin(UserAccount userAccount) {
		if (userAccount == null) {
			return false;
		}
		
		ArrayIdentifierBundle ids = new ArrayIdentifierBundle();
		ids.addAll(getIdsForUserAccount(req, userAccount));
		ids.addAll(RequestIdentifiers.getIdBundleForRequest(req));

		return PolicyHelper.isAuthorizedForActions(ids,
				ServletPolicyList.getPolicies(req),
				SimplePermission.LOGIN_DURING_MAINTENANCE.ACTIONS);
	}

	@Override
	public void recordLoginAgainstUserAccount(UserAccount userAccount,
			AuthenticationSource authSource) throws LoginNotPermitted {
		if (!isUserPermittedToLogin(userAccount)) {
			throw new LoginNotPermitted();
		}
		auth.recordLoginAgainstUserAccount(userAccount, authSource);
	}

	@Override
	public UserAccount getAccountForExternalAuth(String externalAuthId) {
		return auth.getAccountForExternalAuth(externalAuthId);
	}

	@Override
	public UserAccount getAccountForInternalAuth(String emailAddress) {
		return auth.getAccountForInternalAuth(emailAddress);
	}

	@Override
	public boolean isCurrentPassword(UserAccount userAccount,
			String clearTextPassword) {
		return auth.isCurrentPassword(userAccount, clearTextPassword);
	}

	@Override
	public void recordNewPassword(UserAccount userAccount,
			String newClearTextPassword) {
		auth.recordNewPassword(userAccount, newClearTextPassword);
	}

	@Override
	public boolean accountRequiresEditing(UserAccount userAccount) {
		return auth.accountRequiresEditing(userAccount);
	}

	@Override
	public List<String> getAssociatedIndividualUris(UserAccount userAccount) {
		return auth.getAssociatedIndividualUris(userAccount);
	}

	@Override
	public void recordUserIsLoggedOut() {
		auth.recordUserIsLoggedOut();
	}

	@Override
	public String toString() {
		return "RestrictedAuthenticator[" + auth + "]";
	}

}
