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

package edu.cornell.mannlib.vitro.webapp.auth.identifier;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Build a list of Identifiers that apply to the current request and cache them
 * in the request.
 */
public class RequestIdentifiers {
	private static final String ATTRIBUTE_ID_BUNDLE = RequestIdentifiers.class
			.getName();

	// ----------------------------------------------------------------------
	// static methods
	// ----------------------------------------------------------------------

	/**
	 * If the currently applicable Identifiers have been cached in the request,
	 * get them. If not, assemble them from the active factories, and cache them
	 * in the request.
	 * 
	 * This method might return an empty bundle, but it never returns null.
	 */
	public static IdentifierBundle getIdBundleForRequest(ServletRequest request) {
		if (!(request instanceof HttpServletRequest)) {
			return new ArrayIdentifierBundle();
		}
		HttpServletRequest hreq = (HttpServletRequest) request;

		Object obj = hreq.getAttribute(ATTRIBUTE_ID_BUNDLE);
		if (obj == null) {
			obj = ActiveIdentifierBundleFactories.getIdentifierBundle(hreq);
			hreq.setAttribute(ATTRIBUTE_ID_BUNDLE, obj);
		}

		if (!(obj instanceof IdentifierBundle)) {
			throw new IllegalStateException("Expected to find an instance of "
					+ IdentifierBundle.class.getName()
					+ " in the request, but found an instance of "
					+ obj.getClass().getName() + " instead.");
		}

		return (IdentifierBundle) obj;
	}

	/**
	 * The login status has changed, so discard the cached Identifiers.
	 */
	public static void resetIdentifiers(ServletRequest request) {
		request.removeAttribute(ATTRIBUTE_ID_BUNDLE);
	}

}
