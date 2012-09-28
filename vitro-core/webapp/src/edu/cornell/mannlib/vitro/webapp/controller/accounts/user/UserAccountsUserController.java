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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.user;

import static edu.cornell.mannlib.vedit.beans.LoginStatusBean.AuthenticationSource.EXTERNAL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.DisplayMessage;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator.LoginNotPermitted;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.LoginRedirector;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;

/**
 * Parcel out the different actions required of the UserAccounts GUI.
 */
public class UserAccountsUserController extends FreemarkerHttpServlet {
	private static final Log log = LogFactory
			.getLog(UserAccountsUserController.class);

	public static final String BOGUS_STANDARD_MESSAGE = "Request failed. Please contact your system administrator.";

	private static final String ACTION_CREATE_PASSWORD = "/createPassword";
	private static final String ACTION_RESET_PASSWORD = "/resetPassword";
	private static final String ACTION_MY_ACCOUNT = "/myAccount";
	private static final String ACTION_FIRST_TIME_EXTERNAL = "/firstTimeExternal";

	@Override
	protected Actions requiredActions(VitroRequest vreq) {
		String action = vreq.getPathInfo();

		if (ACTION_MY_ACCOUNT.equals(action)) {
			return SimplePermission.EDIT_OWN_ACCOUNT.ACTIONS;
		} else {
			return Actions.AUTHORIZED;
		}
	}

	@Override
	protected ResponseValues processRequest(VitroRequest vreq) {
		if (log.isDebugEnabled()) {
			dumpRequestParameters(vreq);
		}

		String action = vreq.getPathInfo();
		log.debug("action = '" + action + "'");

		if (ACTION_MY_ACCOUNT.equals(action)) {
			return handleMyAccountRequest(vreq);
		} else if (ACTION_CREATE_PASSWORD.equals(action)) {
			return handleCreatePasswordRequest(vreq);
		} else if (ACTION_RESET_PASSWORD.equals(action)) {
			return handleResetPasswordRequest(vreq);
		} else if (ACTION_FIRST_TIME_EXTERNAL.equals(action)) {
			return handleFirstTimeLoginFromExternalAccount(vreq);
		} else {
			return handleInvalidRequest(vreq);
		}
	}

	private ResponseValues handleMyAccountRequest(VitroRequest vreq) {
		UserAccountsMyAccountPage page = new UserAccountsMyAccountPage(vreq);
		if (page.isSubmit() && page.isValid()) {
			page.updateAccount();
		}
		return page.showPage();
	}

	private ResponseValues handleCreatePasswordRequest(VitroRequest vreq) {
		UserAccountsCreatePasswordPage page = new UserAccountsCreatePasswordPage(
				vreq);
		if (page.isBogus()) {
			return showHomePage(vreq, page.getBogusMessage());
		} else if (page.isSubmit() && page.isValid()) {
			page.createPassword();
			return showHomePage(vreq, page.getSuccessMessage());
		} else {
			return page.showPage();
		}

	}

	private ResponseValues handleResetPasswordRequest(VitroRequest vreq) {
		UserAccountsResetPasswordPage page = new UserAccountsResetPasswordPage(
				vreq);
		if (page.isBogus()) {
			return showHomePage(vreq, page.getBogusMessage());
		} else if (page.isSubmit() && page.isValid()) {
			page.resetPassword();
			return showHomePage(vreq, page.getSuccessMessage());
		} else {
			return page.showPage();
		}

	}

	private ResponseValues handleFirstTimeLoginFromExternalAccount(
			VitroRequest vreq) {
		UserAccountsFirstTimeExternalPage page = new UserAccountsFirstTimeExternalPage(
				vreq);
		if (page.isBogus()) {
			return showHomePage(vreq, page.getBogusMessage());
		} else if (page.isSubmit() && page.isValid()) {
			try {
				UserAccount userAccount = page.createAccount();
				Authenticator auth = Authenticator.getInstance(vreq);
				auth.recordLoginAgainstUserAccount(userAccount, EXTERNAL);
				return showLoginRedirection(vreq, page.getAfterLoginUrl());
			} catch (LoginNotPermitted e) {
				// This should have been anticipated by the page.
				return showHomePage(vreq, BOGUS_STANDARD_MESSAGE);
			}
		} else {
			return page.showPage();
		}
	}

	private ResponseValues handleInvalidRequest(VitroRequest vreq) {
		return showHomePage(vreq, BOGUS_STANDARD_MESSAGE);
	}

	private ResponseValues showHomePage(VitroRequest vreq, String message) {
		DisplayMessage.setMessage(vreq, message);
		return new RedirectResponseValues("/");
	}

	private ResponseValues showLoginRedirection(VitroRequest vreq,
			String afterLoginUrl) {
		LoginRedirector lr = new LoginRedirector(vreq, afterLoginUrl);
		DisplayMessage.setMessage(vreq, lr.assembleWelcomeMessage());
		String uri = lr.getRedirectionUriForLoggedInUser();
		return new RedirectResponseValues(stripContextPath(vreq, uri));
	}

	/**
	 * TODO The LoginRedirector gives a URI that includes the context path. But
	 * the RedirectResponseValues wants a URI that does not include the context
	 * path.
	 * 
	 * Bridge the gap.
	 */
	private String stripContextPath(VitroRequest vreq, String uri) {
		if ((uri == null) || uri.isEmpty() || uri.equals(vreq.getContextPath())) {
			return "/";
		}
		if (uri.contains("://")) {
			return uri;
		}
		if (uri.startsWith(vreq.getContextPath() + '/')) {
			return uri.substring(vreq.getContextPath().length());
		}
		return uri;
	}
}
