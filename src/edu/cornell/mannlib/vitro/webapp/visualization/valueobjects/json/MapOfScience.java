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
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapOfScience {
	
	private String uri;
	private String label;
	private String type;
	private int pubsMapped;
	private int pubsWithNoJournals;
	private int pubsWithInvalidJournals;
	private String lastCachedAtDateTime;
	private Map<Integer, Float> subdisciplineActivity = new HashMap<Integer, Float>();
	private Set<SubEntityInfo> subEntities = new HashSet<SubEntityInfo>();

	public MapOfScience(String uri) {
		this.uri = uri;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUri() {
		return uri;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setPubsMapped(int pubsMapped) {
		this.pubsMapped = pubsMapped;
	}
	public int getPubsMapped() {
		return pubsMapped;
	}
	public void setPubsWithNoJournals(int pubsUnmapped) {
		this.pubsWithNoJournals = pubsUnmapped;
	}
	public int getPubsWithNoJournals() {
		return pubsWithNoJournals;
	}
	public void setPubsWithInvalidJournals(int pubsWithInvalidJournals) {
		this.pubsWithInvalidJournals = pubsWithInvalidJournals;
	}

	public int getPubsWithInvalidJournals() {
		return pubsWithInvalidJournals;
	}

	public void setSubdisciplineActivity(Map<Integer, Float> subdisciplineActivity) {
		this.subdisciplineActivity = subdisciplineActivity;
	}
	public Map<Integer, Float> getSubdisciplineActivity() {
		return subdisciplineActivity;
	}

	public void setLastCachedAtDateTime(String lastCachedAtDateTime) {
		this.lastCachedAtDateTime = lastCachedAtDateTime;
	}

	public String getLastCachedAtDateTime() {
		return lastCachedAtDateTime;
	}

	public void addSubEntity(String uri, String label, String type, int pubs) {
		this.subEntities.add(new SubEntityInfo(uri, label, type, pubs));
	}

	public Set<SubEntityInfo> getSubEntities() {
		return subEntities;
	}

	private class SubEntityInfo {
		
		private String uri;
		private String label;
		private String type;
		private int pubs;
		
		public SubEntityInfo(String uri, String label, String type, int pubs) {
			this.uri = uri;
			this.label = label;
			this.type = type;
			this.pubs = pubs;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getPubs() {
			return pubs;
		}

		public void setPubs(int pubs) {
			this.pubs = pubs;
		}
		
		
	}
}
