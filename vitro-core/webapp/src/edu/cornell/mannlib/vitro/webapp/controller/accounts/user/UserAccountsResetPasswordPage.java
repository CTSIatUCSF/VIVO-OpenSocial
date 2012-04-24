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

import static javax.mail.Message.RecipientType.TO;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.UserAccount.Status;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailFactory;
import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailMessage;

/**
 * When the user clicks on the link in their notification email, handle their
 * request to reset their password.
 */
public class UserAccountsResetPasswordPage extends UserAccountsPasswordBasePage {
	private static final Log log = LogFactory
			.getLog(UserAccountsResetPasswordPage.class);

	private static final String TEMPLATE_NAME = "userAccounts-resetPassword.ftl";

	private static final String EMAIL_TEMPLATE = "userAccounts-passwordResetCompleteEmail.ftl";

	protected UserAccountsResetPasswordPage(VitroRequest vreq) {
		super(vreq);
	}

	public void resetPassword() {
		userAccount.setMd5Password(Authenticator.applyMd5Encoding(newPassword));
		userAccount.setPasswordLinkExpires(0L);
		userAccount.setPasswordChangeRequired(false);
		userAccount.setStatus(Status.ACTIVE);
		userAccountsDao.updateUserAccount(userAccount);
		log.debug("Set password on '" + userAccount.getEmailAddress()
				+ "' to '" + newPassword + "'");

		notifyUser();
	}

	@Override
	protected String alreadyLoggedInMessage(String currentUserEmail) {
		return "You may not reset the password for " + userEmail
				+ " while you are logged in as " + currentUserEmail
				+ ". Please log out and try again.";
	}

	@Override
	protected String passwordChangeNotPendingMessage() {
		return "The password for " + userEmail + " has already been reset.";
	}

	@Override
	protected String templateName() {
		return TEMPLATE_NAME;
	}

	private void notifyUser() {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("userAccount", userAccount);
		body.put("siteName", getSiteName());

		FreemarkerEmailMessage email = FreemarkerEmailFactory
				.createNewMessage(vreq);
		email.addRecipient(TO, userAccount.getEmailAddress());
		email.setSubject("Password changed.");
		email.setTemplate(EMAIL_TEMPLATE);
		email.setBodyMap(body);
		email.processTemplate();
		email.send();
	}

}
