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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable group of relationships (might be empty), with the criteria that
 * were used to select them.
 */

public class ProxyRelationshipSelection {
	private final ProxyRelationshipSelectionCriteria criteria;
	private final List<ProxyRelationship> proxyRelationships;
	private final int totalResultCount;

	public ProxyRelationshipSelection(
			ProxyRelationshipSelectionCriteria criteria,
			List<ProxyRelationship> proxyRelationships, int totalResultCount) {
		this.criteria = criteria;
		this.proxyRelationships = Collections
				.unmodifiableList(new ArrayList<ProxyRelationship>(
						proxyRelationships));
		this.totalResultCount = totalResultCount;
	}

	public ProxyRelationshipSelectionCriteria getCriteria() {
		return criteria;
	}

	public List<ProxyRelationship> getProxyRelationships() {
		return proxyRelationships;
	}

	public int getTotalResultCount() {
		return totalResultCount;
	}

	@Override
	public String toString() {
		return "ProxyRelationshipSelection[count=" + totalResultCount
				+ ", relationships=" + proxyRelationships + ", criteria="
				+ criteria + "]";
	}

}
