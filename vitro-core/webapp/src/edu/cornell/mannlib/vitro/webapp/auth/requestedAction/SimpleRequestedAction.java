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

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;

/**
 * A RequestedAction that can be recognized by a SimplePermission.
 */
public class SimpleRequestedAction extends RequestedAction {
	private final String localName;

	public SimpleRequestedAction(String localName) {
		if (localName == null) {
			throw new NullPointerException("localName may not be null.");
		}

		this.localName = localName;
	}

	@Override
	public String getURI() {
		return "java:" + this.getClass().getName() + "#" + localName;
	}

	@Override
	public int hashCode() {
		return (localName == null) ? 0 : localName.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SimpleRequestedAction) {
			SimpleRequestedAction that = (SimpleRequestedAction) o;
			return equivalent(this.localName, that.localName);
		}
		return false;
	}
	
	private boolean equivalent(Object o1, Object o2) {
		return (o1 == null) ? (o2 == null) : o1.equals(o2);
	}

	@Override
	public String toString() {
		return "SimpleRequestedAction['" + localName + "']";
	}

}
