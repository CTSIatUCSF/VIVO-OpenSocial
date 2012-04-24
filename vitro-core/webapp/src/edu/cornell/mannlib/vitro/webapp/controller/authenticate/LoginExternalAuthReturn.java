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

import static edu.cornell.mannlib.vitro.webapp.controller.authenticate.LoginExternalAuthSetup.ATTRIBUTE_REFERRER;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean.AuthenticationSource;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.user.UserAccountsFirstTimeExternalPage;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.login.LoginProcessBean;

/**
 * Handle the return from the external authorization login server. If we are
 * successful, record the login. Otherwise, display the failure.
 */
public class LoginExternalAuthReturn extends BaseLoginServlet {
	private static final Log log = LogFactory
			.getLog(LoginExternalAuthReturn.class);

	/**
	 * <pre>
	 * Returning from the external authorization server. If we were successful,
	 * the header will contain the name of the user who just logged in.
	 * 
	 * Deal with these possibilities: 
	 * - The header name was not configured in deploy.properties. Complain.
	 * - No username: the login failed. Complain 
	 * - User corresponds to a User acocunt. Record the login. 
	 * - User corresponds to an Individual (self-editor). 
	 * - User is not recognized.
	 * 
	 * On entry, we expect to find:
	 * - A LoginProcessBean, which will give us the afterLoginUrl if the login
	 *      succeeds.
	 * - A referrer URL, to which we will redirect if the login fails.
	 *      TODO: is this equal to LoginProcessBean.getLoginPageUrl()?
	 * These are removed on exit.
	 * </pre>
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String externalAuthId = ExternalAuthHelper.getHelper(req)
				.getExternalAuthId(req);
		if (externalAuthId == null) {
			log.debug("No externalAuthId.");
			complainAndReturnToReferrer(req, resp, ATTRIBUTE_REFERRER,
					MESSAGE_LOGIN_FAILED);
			return;
		}

		String afterLoginUrl = LoginProcessBean.getBean(req).getAfterLoginUrl();
		removeLoginProcessArtifacts(req);

		UserAccount userAccount = getAuthenticator(req)
				.getAccountForExternalAuth(externalAuthId);
		if (userAccount == null) {
			log.debug("Creating new account for " + externalAuthId
					+ ", return to '" + afterLoginUrl + "'");
			UserAccountsFirstTimeExternalPage.setExternalLoginInfo(req,
					externalAuthId, afterLoginUrl);
			resp.sendRedirect(UrlBuilder.getUrl("/accounts/firstTimeExternal"));
			return;
		} else {
			log.debug("Logging in as " + userAccount.getUri());
			getAuthenticator(req).recordLoginAgainstUserAccount(userAccount,
					AuthenticationSource.EXTERNAL);
			new LoginRedirector(req, afterLoginUrl).redirectLoggedInUser(resp);
			return;
		}
	}

	private void removeLoginProcessArtifacts(HttpServletRequest req) {
		req.getSession().removeAttribute(ATTRIBUTE_REFERRER);
		LoginProcessBean.removeBean(req);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
