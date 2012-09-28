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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.admin.ajax;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.VitroAjaxController;

/**
 * Handle the AJAX functions that are specific to the UserAccounts pages.
 */
public class UserAccountsAjaxController extends VitroAjaxController {
	private static final Log log = LogFactory
			.getLog(UserAccountsAjaxController.class);

	private static final String PARAMETER_ACTION = "action";

	@Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.MANAGE_USER_ACCOUNTS.ACTIONS;
	}

	@Override
	protected void doRequest(VitroRequest vreq, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String function = vreq.getParameter(PARAMETER_ACTION);
			if ("checkExternalAuth".equals(function)) {
				new ExternalAuthChecker(this, vreq, resp).processRequest();
			} else if ("autoCompleteProfile".equals(function)) {
				new ProfileAutoCompleter(this, vreq, resp).processRequest();
			} else {
				resp.getWriter().write("[]");
			}
		} catch (Exception e) {
			log.error(e, e);
			resp.getWriter().write("[]");
		}
	}

}
