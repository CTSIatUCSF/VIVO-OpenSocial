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
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.CommonIdentifierBundleFactory;
import edu.cornell.mannlib.vitro.webapp.auth.policy.DisplayRestrictedDataByRoleLevelPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.DisplayRestrictedDataToSelfPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.EditRestrictedDataByRoleLevelPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.SelfEditingPolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.UseRestrictedPagesByRoleLevelPolicy;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Set up the common policy family, with Identifier factory.
 */
public class CommonPolicyFamilySetup implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		StartupStatus ss = StartupStatus.getBean(ctx);

		try {
			ServletPolicyList.addPolicy(ctx,
					new DisplayRestrictedDataByRoleLevelPolicy(ctx));
			ServletPolicyList.addPolicy(ctx,
					new DisplayRestrictedDataToSelfPolicy(ctx));
			ServletPolicyList.addPolicy(ctx,
					new EditRestrictedDataByRoleLevelPolicy(ctx));
			ServletPolicyList.addPolicy(ctx,
					new UseRestrictedPagesByRoleLevelPolicy());

			ServletPolicyList.addPolicy(ctx, new SelfEditingPolicy(ctx));

			// This factory creates Identifiers for all of the above policies.
			CommonIdentifierBundleFactory factory = new CommonIdentifierBundleFactory(
					ctx);

			ActiveIdentifierBundleFactories.addFactory(sce, factory);
		} catch (Exception e) {
			ss.fatal(this, "could not run CommonPolicyFamilySetup", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) { /* nothing */
	}

}
