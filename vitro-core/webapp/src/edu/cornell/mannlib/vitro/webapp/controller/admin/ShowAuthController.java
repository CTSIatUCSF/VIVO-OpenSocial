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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ActiveIdentifierBundleFactories;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.Identifier;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasAssociatedIndividual;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestActionConstants;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.authenticate.Authenticator;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;

/**
 * Show a summary of who is logged in and how they are to be treated by the
 * authorization system.
 */
public class ShowAuthController extends FreemarkerHttpServlet {

	@Override
	protected Actions requiredActions(VitroRequest vreq) {
		return Actions.AUTHORIZED;
	}

	@Override
	protected ResponseValues processRequest(VitroRequest vreq) {

		Map<String, Object> body = new HashMap<String, Object>();

		body.put("identifiers", getSortedIdentifiers(vreq));
		body.put("currentUser", LoginStatusBean.getCurrentUser(vreq));
		body.put("associatedIndividuals", getAssociatedIndividuals(vreq));
		body.put("factories", getIdentifierFactoryNames(vreq));
		body.put("policies", ServletPolicyList.getPolicies(vreq));
		body.put("matchingProperty", getMatchingProperty(vreq));
		body.put("authenticator", Authenticator.getInstance(vreq));

		return new TemplateResponseValues("admin-showAuth.ftl", body);
	}

	private List<Identifier> getSortedIdentifiers(VitroRequest vreq) {
		Map<String, Identifier> idMap = new TreeMap<String, Identifier>();
		for (Identifier id : RequestIdentifiers.getIdBundleForRequest(vreq)) {
			idMap.put(id.toString(), id);
		}
		return new ArrayList<Identifier>(idMap.values());
	}

	private List<String> getIdentifierFactoryNames(VitroRequest vreq) {
		ServletContext ctx = vreq.getSession().getServletContext();
		return ActiveIdentifierBundleFactories.getFactoryNames(ctx);
	}

	private String getMatchingProperty(VitroRequest vreq) {
		return ConfigurationProperties.getBean(vreq).getProperty(
				"selfEditing.idMatchingProperty", "");
	}

	private List<AssociatedIndividual> getAssociatedIndividuals(
			VitroRequest vreq) {
		List<AssociatedIndividual> list = new ArrayList<AssociatedIndividual>();
		IdentifierBundle ids = RequestIdentifiers.getIdBundleForRequest(vreq);
		for (String uri : HasAssociatedIndividual.getIndividualUris(ids)) {
			list.add(new AssociatedIndividual(uri, mayEditIndividual(vreq, uri)));
		}
		return list;
	}

	/**
	 * Is the current user authorized to edit an arbitrary object property on
	 * this individual?
	 */
	private boolean mayEditIndividual(VitroRequest vreq, String individualUri) {
		RequestedAction action = new EditObjectPropertyStatement(
				vreq.getJenaOntModel(), individualUri,
				RequestActionConstants.SOME_URI,
				RequestActionConstants.SOME_URI);
		return PolicyHelper.isAuthorizedForActions(vreq, action);
	}

	public class AssociatedIndividual {
		private final String uri;
		private final boolean editable;

		public AssociatedIndividual(String uri, boolean editable) {
			this.uri = uri;
			this.editable = editable;
		}

		public String getUri() {
			return uri;
		}

		public boolean isEditable() {
			return editable;
		}

	}
}
