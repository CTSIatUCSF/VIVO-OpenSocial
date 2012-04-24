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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.ManageRootAccount;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.UserAccountsPage;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.user.UserAccountsUserController;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
 * Process a request to delete User Accounts.
 */
public class UserAccountsDeleter extends UserAccountsPage {
	private static final Log log = LogFactory.getLog(UserAccountsDeleter.class);

	private static final String PARAMETER_DELETE_ACCOUNT = "deleteAccount";

	/** Might be empty, but never null. */
	private final String[] uris;

	/** The result of checking whether this request is even appropriate. */
	private String bogusMessage = "";

	public UserAccountsDeleter(VitroRequest vreq) {
		super(vreq);

		String[] values = vreq.getParameterValues(PARAMETER_DELETE_ACCOUNT);
		if (values == null) {
			this.uris = new String[0];
		} else {
			this.uris = values;
		}

		WebappDaoFactory wadf = vreq.getWebappDaoFactory();

		validateInputUris();
	}

	private void validateInputUris() {
		UserAccount loggedInAccount = LoginStatusBean.getCurrentUser(vreq);
		if (loggedInAccount == null) {
			log.warn("Trying to delete accounts while not logged in!");
			bogusMessage = UserAccountsUserController.BOGUS_STANDARD_MESSAGE;
			return;
		}

		for (String uri : this.uris) {
			UserAccount u = userAccountsDao.getUserAccountByUri(uri);

			if (u == null) {
				log.warn("Delete account for '" + uri
						+ "' is bogus: no such user");
				bogusMessage = UserAccountsUserController.BOGUS_STANDARD_MESSAGE;
				return;
			}

			if (u.getUri().equals(loggedInAccount.getUri())) {
				log.warn("'" + u.getUri()
						+ "' is trying to delete his own account.");
				bogusMessage = UserAccountsUserController.BOGUS_STANDARD_MESSAGE;
				return;
			}

			if (u.isRootUser()
					&& (!PolicyHelper.isAuthorizedForActions(vreq,
							new ManageRootAccount()))) {
				log.warn("Attempting to delete the root account, "
						+ "but not authorized. Logged in as "
						+ LoginStatusBean.getCurrentUser(vreq));
				bogusMessage = UserAccountsUserController.BOGUS_STANDARD_MESSAGE;
				return;
			}
		}
	}

	public Collection<String> delete() {
		List<String> deletedUris = new ArrayList<String>();

		for (String uri : uris) {
			UserAccount u = userAccountsDao.getUserAccountByUri(uri);
			if (u != null) {
				userAccountsDao.deleteUserAccount(uri);
				deletedUris.add(uri);
			}
		}

		return deletedUris;
	}

	public boolean isBogus() {
		return !bogusMessage.isEmpty();
	}

	public String getBogusMessage() {
		return bogusMessage;
	}

}
