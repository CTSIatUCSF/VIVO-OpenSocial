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

package edu.cornell.mannlib.vitro.webapp.auth.permissions;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;

/**
 * Interface that describes a unit of authorization, or permission to perform
 * requested actions.
 */
public abstract class Permission implements Comparable<Permission> {
	protected final String uri;

	protected Permission(String uri) {
		if (uri == null) {
			throw new NullPointerException("uri may not be null.");
		}
		this.uri = uri;
	}

	/**
	 * Get the URI that identifies this Permission object.
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Is a user with this Permission authorized to perform this
	 * RequestedAction?
	 */
	public abstract boolean isAuthorized(RequestedAction whatToAuth);

	@Override
	public int compareTo(Permission that) {
		return this.uri.compareTo(that.uri);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		Permission that = (Permission) obj;
		return this.uri.equals(that.uri);
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "['" + uri + "']";
	}

	/**
	 * A concrete Permission instance that authorizes nothing.
	 */
	static Permission NOT_AUTHORIZED = new Permission("java:"
			+ Permission.class.getName() + "#NOT_AUTHORIZED") {

		@Override
		public boolean isAuthorized(RequestedAction whatToAuth) {
			return false;
		}

	};
}
