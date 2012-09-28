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

package edu.cornell.mannlib.vitro.webapp.auth.policy.setup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.ActiveIdentifierBundleFactories;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.factory.HasPermissionFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.factory.HasPermissionSetFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.factory.HasProfileOrIsBlacklistedFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.factory.HasProxyEditingRightsFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.factory.IsRootUserFactory;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.factory.IsUserFactory;
import edu.cornell.mannlib.vitro.webapp.auth.policy.DisplayRestrictedDataToSelfPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PermissionsPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.SelfEditingPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Set up the common policy family, with Identifier factories.
 */
public class CommonPolicyFamilySetup implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		StartupStatus ss = StartupStatus.getBean(ctx);

		try {
			policy(ctx, new PermissionsPolicy());
			policy(ctx, new DisplayRestrictedDataToSelfPolicy(ctx));
			policy(ctx, new SelfEditingPolicy(ctx));

			factory(ctx, new IsUserFactory(ctx));
			factory(ctx, new IsRootUserFactory(ctx));
			factory(ctx, new HasProfileOrIsBlacklistedFactory(ctx));
			factory(ctx, new HasPermissionSetFactory(ctx));
			factory(ctx, new HasPermissionFactory(ctx));
			factory(ctx, new HasProxyEditingRightsFactory(ctx));
		} catch (Exception e) {
			ss.fatal(this, "could not run CommonPolicyFamilySetup", e);
		}
	}

	private void policy(ServletContext ctx, PolicyIface policy) {
		ServletPolicyList.addPolicy(ctx, policy);
	}

	private void factory(ServletContext ctx, IdentifierBundleFactory factory) {
		ActiveIdentifierBundleFactories.addFactory(ctx, factory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) { /* nothing */
	}

}
