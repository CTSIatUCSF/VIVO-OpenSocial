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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.manageproxies;

/**
 * An immutable collection of fields that will be displayed in a
 * ProxyRelationship.
 */
public class ProxyItemInfo {
	private final String uri;
	private final String label;
	private final String classLabel;
	private final String imageUrl;

	public ProxyItemInfo(String uri, String label, String classLabel,
			String imageUrl) {
		this.uri = uri;
		this.label = label;
		this.classLabel = classLabel;
		this.imageUrl = imageUrl;
	}

	public String getUri() {
		return uri;
	}

	public String getLabel() {
		return label;
	}

	public String getClassLabel() {
		return classLabel;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		ProxyItemInfo that = (ProxyItemInfo) o;
		return equivalent(this.uri, that.uri)
				&& equivalent(this.label, that.label)
				&& equivalent(this.classLabel, that.classLabel)
				&& equivalent(this.imageUrl, that.imageUrl);
	}

	private boolean equivalent(Object o1, Object o2) {
		return (o1 == null) ? (o2 == null) : o1.equals(o2);
	}

	@Override
	public int hashCode() {
		return hash(this.uri) ^ hash(this.label) ^ hash(this.classLabel)
				^ hash(this.imageUrl);
	}

	private int hash(Object o) {
		return (o == null) ? 0 : o.hashCode();
	}

	@Override
	public String toString() {
		return "ProxyItemInfo[uri=" + uri + ", label=" + label
				+ ", classLabel=" + classLabel + ", imageUrl=" + imageUrl + "]";
	}

}
