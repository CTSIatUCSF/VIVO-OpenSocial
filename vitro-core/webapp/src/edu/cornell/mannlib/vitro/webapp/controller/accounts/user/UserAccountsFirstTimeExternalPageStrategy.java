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

import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.UserAccountsPage;
import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailFactory;
import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailMessage;

/**
 * Handle the variations in the UserAccountsFirstTimeExternal page. If email is
 * available, inform the template, and send a notification to the user.
 * 
 * If not, then don't.
 */
public abstract class UserAccountsFirstTimeExternalPageStrategy extends
		UserAccountsPage {

	public static UserAccountsFirstTimeExternalPageStrategy getInstance(
			VitroRequest vreq, UserAccountsFirstTimeExternalPage page,
			boolean emailEnabled) {
		if (emailEnabled) {
			return new EmailStrategy(vreq, page);
		} else {
			return new NoEmailStrategy(vreq, page);
		}
	}

	@SuppressWarnings("unused")
	private UserAccountsFirstTimeExternalPage page;

	public UserAccountsFirstTimeExternalPageStrategy(VitroRequest vreq,
			UserAccountsFirstTimeExternalPage page) {
		super(vreq);
		this.page = page;
	}

	public abstract void addMoreBodyValues(Map<String, Object> body);

	public abstract void notifyUser(UserAccount ua);

	// ----------------------------------------------------------------------
	// Strategy to use if email is enabled.
	// ----------------------------------------------------------------------

	public static class EmailStrategy extends
			UserAccountsFirstTimeExternalPageStrategy {

		private static final String EMAIL_TEMPLATE = "userAccounts-firstTimeExternalEmail.ftl";

		public EmailStrategy(VitroRequest vreq,
				UserAccountsFirstTimeExternalPage page) {
			super(vreq, page);
		}

		@Override
		public void addMoreBodyValues(Map<String, Object> body) {
			body.put("emailIsEnabled", Boolean.TRUE);
		}

		@Override
		public void notifyUser(UserAccount ua) {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put("userAccount", ua);
			body.put("siteName", getSiteName());

			FreemarkerEmailMessage email = FreemarkerEmailFactory
					.createNewMessage(vreq);
			email.addRecipient(TO, ua.getEmailAddress());
			email.setSubject("Your VIVO account has been created.");
			email.setTemplate(EMAIL_TEMPLATE);
			email.setBodyMap(body);
			email.processTemplate();
			email.send();
		}

	}

	// ----------------------------------------------------------------------
	// Strategy to use if email is disabled.
	// ----------------------------------------------------------------------

	public static class NoEmailStrategy extends
			UserAccountsFirstTimeExternalPageStrategy {

		public NoEmailStrategy(VitroRequest vreq,
				UserAccountsFirstTimeExternalPage page) {
			super(vreq, page);
		}

		@Override
		public void addMoreBodyValues(Map<String, Object> body) {
			// Nothing to add.
		}

		@Override
		public void notifyUser(UserAccount ua) {
			// No way to notify.
		}

	}

}
