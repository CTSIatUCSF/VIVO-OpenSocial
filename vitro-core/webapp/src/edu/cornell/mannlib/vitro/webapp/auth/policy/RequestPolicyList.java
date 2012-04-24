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

package edu.cornell.mannlib.vitro.webapp.auth.policy;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;

/**
 * Allow us to store policies in a Request, in addition to those in the
 * ServletContext
 */
public class RequestPolicyList extends PolicyList {
	private static final String ATTRIBUTE_POLICY_ADDITIONS = RequestPolicyList.class
			.getName();
	private static final Log log = LogFactory.getLog(RequestPolicyList.class);

	/**
	 * Get a copy of the current list of policies. This includes the policies in
	 * the ServletContext, followed by any stored in the request. This method may
	 * return an empty list, but it never returns null.
	 */
	public static PolicyList getPolicies(HttpServletRequest request) {
		ServletContext ctx = request.getSession().getServletContext();

		PolicyList list = ServletPolicyList.getPolicies(ctx);
		list.addAll(getPoliciesFromRequest(request));
		return list;
	}

	public static void addPolicy(ServletRequest request, PolicyIface policy) {
		PolicyList policies = getPoliciesFromRequest(request);
		if (!policies.contains(policy)) {
			policies.add(policy);
			log.debug("Added policy: " + policy.toString());
		} else {
			log.warn("Ignored attempt to add redundent policy.");
		}
	}

	/**
	 * Get the current list of policy additions from the request, or create one
	 * if there is none. This method may return an empty list, but it never
	 * returns null.
	 */
	private static PolicyList getPoliciesFromRequest(ServletRequest request) {
		if (request == null) {
			throw new NullPointerException("request may not be null.");
		}
		
		Object obj = request.getAttribute(ATTRIBUTE_POLICY_ADDITIONS);
		if (obj == null) {
			obj = new PolicyList();
			request.setAttribute(ATTRIBUTE_POLICY_ADDITIONS, obj);
		}
		
		if (!(obj instanceof PolicyList)) {
			throw new IllegalStateException("Expected to find an instance of "
					+ PolicyList.class.getName()
					+ " in the context, but found an instance of "
					+ obj.getClass().getName() + " instead.");
		}

		return (PolicyList) obj;
	}
}
