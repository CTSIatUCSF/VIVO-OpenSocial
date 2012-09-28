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

package edu.cornell.mannlib.vitro.webapp.controller.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerConfigurationLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * A base class for servlets that handle AJAX requests.
 */
public abstract class VitroAjaxController extends HttpServlet {

	private static final Log log = LogFactory.getLog(VitroAjaxController.class);

	/**
	 * Sub-classes must implement this method to handle both GET and POST
	 * requests.
	 */
	protected abstract void doRequest(VitroRequest vreq,
			HttpServletResponse resp) throws ServletException, IOException;

	/**
	 * Sub-classes should not override this. Instead, implement doRequest().
	 */
	@Override
	protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		VitroRequest vreq = new VitroRequest(req);
		if (PolicyHelper.isAuthorizedForActions(vreq, requiredActions(vreq))) {
			doRequest(vreq, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
		}
	}

	/**
	 * Sub-classes should not override this. Instead, implement doRequest().
	 */
	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
    /**
     * By default, a controller requires authorization for no actions.
     * Subclasses that require authorization to process their page will override 
	 *    to return the actions that require authorization.
	 * In some cases, the choice of actions will depend on the contents of the request.
     */
    @SuppressWarnings("unused")
	protected Actions requiredActions(VitroRequest vreq) {
		return Actions.AUTHORIZED;
	}

	/**
	 * Process data through a Freemarker template and output the result.
	 */
	protected void writeTemplate(String templateName, Map<String, Object> map,
			VitroRequest vreq, HttpServletResponse response) {
		Configuration config = FreemarkerConfigurationLoader.getConfig(vreq);
		try {
			Template template = config.getTemplate(templateName);
			PrintWriter out = response.getWriter();
			template.process(map, out);
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	protected void doError(HttpServletResponse response, String errorMsg,
			int httpstatus) {
		response.setStatus(httpstatus);
		try {
			response.getWriter().write(errorMsg);
		} catch (IOException e) {
			log.debug("IO exception during output", e);
		}
	}
}
