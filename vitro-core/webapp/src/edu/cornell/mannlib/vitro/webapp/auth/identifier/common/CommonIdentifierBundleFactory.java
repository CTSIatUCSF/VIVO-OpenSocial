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

package edu.cornell.mannlib.vitro.webapp.auth.identifier.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.ArrayIdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.Identifier;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
 * Create Identifiers that are recognized by the common policy family.
 */
public class CommonIdentifierBundleFactory implements IdentifierBundleFactory {
	private static final Log log = LogFactory
			.getLog(CommonIdentifierBundleFactory.class);

	private final ServletContext context;

	public CommonIdentifierBundleFactory(ServletContext context) {
		this.context = context;
	}

	@Override
	public IdentifierBundle getIdentifierBundle(ServletRequest request,
			HttpSession session, ServletContext unusedContext) {

		// If this is not an HttpServletRequest, we might as well fail now.
		HttpServletRequest req = (HttpServletRequest) request;

		ArrayIdentifierBundle bundle = new ArrayIdentifierBundle();

		bundle.addAll(createUserIdentifiers(req));
		bundle.addAll(createRootUserIdentifiers(req));
		bundle.addAll(createRoleLevelIdentifiers(req));
		bundle.addAll(createBlacklistOrAssociatedIndividualIdentifiers(req));
		bundle.addAll(createExplicitProxyEditingIdentifiers(req));

		return bundle;
	}

	/**
	 * If the user is logged in, create an identifier that shows his URI.
	 */
	private Collection<? extends Identifier> createUserIdentifiers(
			HttpServletRequest req) {
		LoginStatusBean bean = LoginStatusBean.getBean(req);
		if (bean.isLoggedIn()) {
			return Collections.singleton(new IsUser(bean.getUserURI()));
		} else {
			return Collections.emptySet();
		}
	}

	private Collection<? extends Identifier> createRootUserIdentifiers(
			HttpServletRequest req) {
		UserAccount user = LoginStatusBean.getCurrentUser(req);
		if ((user != null) && user.isRootUser()) {
			return Collections.singleton(new IsRootUser());
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * Create an identifier that shows the role level of the current user, or
	 * PUBLIC if the user is not logged in.
	 */
	private Collection<? extends Identifier> createRoleLevelIdentifiers(
			HttpServletRequest req) {
		RoleLevel roleLevel = RoleLevel.getRoleFromLoginStatus(req);
		return Collections.singleton(new HasRoleLevel(roleLevel));
	}

	/**
	 * Find all of the individuals that are associated with the current user,
	 * and create either an IsBlacklisted or HasAssociatedIndividual for each
	 * one.
	 */
	private Collection<? extends Identifier> createBlacklistOrAssociatedIndividualIdentifiers(
			HttpServletRequest req) {
		Collection<Identifier> ids = new ArrayList<Identifier>();

		for (Individual ind : getAssociatedIndividuals(req)) {
			// If they are blacklisted, this factory will return an identifier
			Identifier id = IsBlacklisted.getInstance(ind, context);
			if (id != null) {
				ids.add(id);
			} else {
				ids.add(new HasProfile(ind.getURI()));
			}
		}

		return ids;
	}

	/**
	 * Get all Individuals associated with the current user as SELF.
	 */
	private Collection<Individual> getAssociatedIndividuals(
			HttpServletRequest req) {
		Collection<Individual> individuals = new ArrayList<Individual>();

		UserAccount user = LoginStatusBean.getCurrentUser(req);
		if (user == null) {
			log.debug("No Associated Individuals: not logged in.");
			return individuals;
		}

		WebappDaoFactory wdf = (WebappDaoFactory) context
				.getAttribute("webappDaoFactory");
		if (wdf == null) {
			log.error("Could not get a WebappDaoFactory from the ServletContext");
			return individuals;
		}

		IndividualDao indDao = wdf.getIndividualDao();

		SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(req);
		individuals.addAll(sec.getAssociatedIndividuals(indDao, user));

		return individuals;
	}

	/**
	 * Get all Individuals associated with the current user by explicit proxy relationship.
	 */
	private Collection<? extends Identifier> createExplicitProxyEditingIdentifiers(
			HttpServletRequest req) {
		Collection<Identifier> ids = new ArrayList<Identifier>();

		UserAccount user = LoginStatusBean.getCurrentUser(req);
		if (user != null) {
			for(String proxiedUri: user.getProxiedIndividualUris()) {
				ids.add(new HasProxyEditingRights(proxiedUri));
			}
		}

		return ids;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " - " + hashCode();
	}

}
