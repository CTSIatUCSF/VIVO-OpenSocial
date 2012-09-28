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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.DisplayMessage;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;

/**
 * Parcel out the different actions required of the Administrators portion of
 * the UserAccounts GUI.
 */
public class UserAccountsAdminController extends FreemarkerHttpServlet {
	private static final Log log = LogFactory
			.getLog(UserAccountsAdminController.class);

	private static final String ACTION_ADD = "/add";
	private static final String ACTION_DELETE = "/delete";
	private static final String ACTION_EDIT = "/edit";

	@Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.MANAGE_USER_ACCOUNTS.ACTIONS;
	}

	@Override
	protected ResponseValues processRequest(VitroRequest vreq) {
		if (log.isDebugEnabled()) {
			dumpRequestParameters(vreq);
		}

		String action = vreq.getPathInfo();
		log.debug("action = '" + action + "'");

		if (ACTION_ADD.equals(action)) {
			return handleAddRequest(vreq);
		} else if (ACTION_EDIT.equals(action)) {
			return handleEditRequest(vreq);
		} else if (ACTION_DELETE.equals(action)) {
			return handleDeleteRequest(vreq);
		} else {
			return handleListRequest(vreq);
		}
	}

	private ResponseValues handleAddRequest(VitroRequest vreq) {
		UserAccountsAddPage page = new UserAccountsAddPage(vreq);
		if (page.isSubmit() && page.isValid()) {
			page.createNewAccount();

			UserAccountsListPage.Message.showNewAccount(vreq,
					page.getAddedAccount(), page.wasPasswordEmailSent());
			return redirectToList();
		} else {
			return page.showPage();
		}
	}

	private ResponseValues handleEditRequest(VitroRequest vreq) {
		UserAccountsEditPage page = new UserAccountsEditPage(vreq);
		if (page.isBogus()) {
			return showHomePage(vreq, page.getBogusMessage());
		} else if (page.isSubmit() && page.isValid()) {
			page.updateAccount();

			UserAccountsListPage.Message.showUpdatedAccount(vreq,
					page.getUpdatedAccount(), page.wasPasswordEmailSent());
			return redirectToList();
		} else {
			return page.showPage();
		}
	}

	private ResponseValues handleDeleteRequest(VitroRequest vreq) {
		UserAccountsDeleter deleter = new UserAccountsDeleter(vreq);
		if (deleter.isBogus()) {
			return showHomePage(vreq, deleter.getBogusMessage());
		} else {
			Collection<String> deletedUris = deleter.delete();

			UserAccountsListPage.Message.showDeletions(vreq, deletedUris);
			return redirectToList();
		}
	}

	private ResponseValues handleListRequest(VitroRequest vreq) {
		UserAccountsListPage page = new UserAccountsListPage(vreq);
		return page.showPage();
	}

	/**
	 * After an successful change, redirect to the list instead of forwarding.
	 * That way, a browser "refresh" won't try to repeat the operation.
	 */
	private ResponseValues redirectToList() {
		return new RedirectResponseValues("/accountsAdmin/list");
	}

	private ResponseValues showHomePage(VitroRequest vreq, String message) {
		DisplayMessage.setMessage(vreq, message);
		return new RedirectResponseValues("/");
	}

}
