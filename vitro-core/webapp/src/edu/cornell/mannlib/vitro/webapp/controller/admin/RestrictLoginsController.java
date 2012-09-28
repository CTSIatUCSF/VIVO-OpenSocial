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

package edu.cornell.mannlib.vitro.webapp.controller.admin;

import java.util.HashMap;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.AbstractPageHandler;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.BasicAuthenticator;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.RestrictedAuthenticator;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;

/**
 * Offer the user the ability to apply a RestrictedAuthenticator or revert to a
 * BasicAuthenticator.
 */
public class RestrictLoginsController extends FreemarkerHttpServlet {
	public static final String PARAMETER_RESTRICT = "restrict";
	public static final String PARAMETER_OPEN = "open";
	public static final String MESSAGE_NO_MESSAGE = "message";
	public static final String MESSAGE_RESTRICTING = "messageRestricting";
	public static final String MESSAGE_OPENING = "messageOpening";
	public static final String MESSAGE_ALREADY_RESTRICTED = "messageAlreadyRestricted";
	public static final String MESSAGE_ALREADY_OPEN = "messageAlreadyOpen";

	@Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.LOGIN_DURING_MAINTENANCE.ACTIONS;
	}

	@Override
	protected ResponseValues processRequest(VitroRequest vreq) {
		Core pageHandler = new Core(vreq);
		pageHandler.processInput();
		return pageHandler.prepareOutput();
	}

	private static class Core extends AbstractPageHandler {
		private enum State {
			OPEN, RESTRICTED
		}

		private String messageCode;

		Core(VitroRequest vreq) {
			super(vreq);
		}

		void processInput() {
			State desired = figureDesiredState();
			State current = figureCurrentlyState();

			if (desired == null) {
				messageCode = MESSAGE_NO_MESSAGE;
			} else if (desired == State.OPEN) {
				if (current == State.OPEN) {
					messageCode = MESSAGE_ALREADY_OPEN;
				} else {
					openLogins();
					messageCode = MESSAGE_OPENING;
				}
			} else if (desired == State.RESTRICTED) {
				if (current == State.RESTRICTED) {
					messageCode = MESSAGE_ALREADY_RESTRICTED;
				} else {
					restrictLogins();
					messageCode = MESSAGE_RESTRICTING;
				}
			}
		}

		ResponseValues prepareOutput() {
			boolean restricted = figureCurrentlyState() == State.RESTRICTED;

			Map<String, Object> body = new HashMap<String, Object>();
			body.put("title", "Restrict Logins");
			body.put("restricted", restricted);
			if (!MESSAGE_NO_MESSAGE.equals(messageCode)) {
				body.put(messageCode, Boolean.TRUE);
			}
			body.put("restrictUrl", UrlBuilder.getUrl("/admin/restrictLogins",
					PARAMETER_RESTRICT, "true"));
			body.put("openUrl", UrlBuilder.getUrl("/admin/restrictLogins",
					PARAMETER_OPEN, "true"));
			
			return new TemplateResponseValues("admin-restrictLogins.ftl", body);
		}

		private State figureDesiredState() {
			if (isFlagOnRequest(PARAMETER_RESTRICT)) {
				return State.RESTRICTED;
			} else if (isFlagOnRequest(PARAMETER_OPEN)) {
				return State.OPEN;
			} else {
				return null;
			}
		}

		private State figureCurrentlyState() {
			Authenticator auth = Authenticator.getInstance(vreq);
			if (auth instanceof RestrictedAuthenticator) {
				return State.RESTRICTED;
			} else {
				return State.OPEN;
			}
		}

		private void openLogins() {
			Authenticator.setAuthenticatorFactory(
					new BasicAuthenticator.Factory(), ctx);
		}

		private void restrictLogins() {
			Authenticator.setAuthenticatorFactory(
					new RestrictedAuthenticator.Factory(), ctx);
		}
	}
}
