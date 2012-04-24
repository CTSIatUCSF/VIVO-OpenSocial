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
import java.util.List;

/**
 * A mutable version of ProxyRelationshipSelection, that can be assembled
 * piecemeal as the info becomes available and then translated to an immutable
 * ProxyRelationshipSelection.
 * 
 * Uses mutable subclasses Relationship and ItemInfo.
 * 
 * ItemInfo contains a field for externalAuthId only because it is useful when
 * gathering the classLabel and imageUrl.
 */
public class ProxyRelationshipSelectionBuilder {
	final ProxyRelationshipSelectionCriteria criteria;

	final List<Relationship> relationships = new ArrayList<Relationship>();
	int count;

	public ProxyRelationshipSelectionBuilder(
			ProxyRelationshipSelectionCriteria criteria) {
		this.criteria = criteria;
	}

	public ProxyRelationshipSelection build() {
		List<ProxyRelationship> proxyRelationships = new ArrayList<ProxyRelationship>();
		for (Relationship r : relationships) {
			proxyRelationships.add(buildProxyRelationship(r));
		}
		return new ProxyRelationshipSelection(criteria, proxyRelationships,
				count);
	}

	private ProxyRelationship buildProxyRelationship(Relationship r) {
		List<ProxyItemInfo> proxyInfos = buildInfos(r.proxyInfos);
		List<ProxyItemInfo> profileInfos = buildInfos(r.profileInfos);
		return new ProxyRelationship(proxyInfos, profileInfos);
	}

	private List<ProxyItemInfo> buildInfos(List<ItemInfo> infos) {
		List<ProxyItemInfo> result = new ArrayList<ProxyItemInfo>();
		for (ItemInfo info : infos) {
			result.add(new ProxyItemInfo(info.uri, info.label, info.classLabel,
					info.imageUrl));
		}
		return result;
	}

	public static class Relationship {
		final List<ItemInfo> proxyInfos = new ArrayList<ItemInfo>();
		final List<ItemInfo> profileInfos = new ArrayList<ItemInfo>();
	}

	public static class ItemInfo {
		String uri = "";
		String label = "";
		String externalAuthId = "";
		String classLabel = "";
		String imageUrl = "";

		public ItemInfo() {
			// leave fields at default values.
		}

		public ItemInfo(String uri, String label, String externalAuthId,
				String classLabel, String imageUrl) {
			this.uri = uri;
			this.label = label;
			this.externalAuthId = externalAuthId;
			this.classLabel = classLabel;
			this.imageUrl = imageUrl;
		}

	}
}
