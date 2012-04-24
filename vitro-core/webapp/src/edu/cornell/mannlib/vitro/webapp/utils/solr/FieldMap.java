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

package edu.cornell.mannlib.vitro.webapp.utils.solr;

import java.util.HashMap;
import java.util.Map;

/**
 * A builder object that can assemble a map of Solr field names to JSON field
 * names.
 * 
 * Use like this:
 * 
 * m = SolrQueryUtils.fieldMap().row("this", "that").row("2nd", "row").map();
 * 
 */
public class FieldMap {
	private final Map<String, String> m = new HashMap<String, String>();

	/**
	 * Add a row to the map
	 */
	public FieldMap put(String solrFieldName, String jsonFieldName) {
		if (solrFieldName == null) {
			throw new NullPointerException("solrFieldName may not be null.");
		}
		if (jsonFieldName == null) {
			throw new NullPointerException("jsonFieldName may not be null.");
		}
		m.put(solrFieldName, jsonFieldName);

		return this;
	}

	/**
	 * Release the map for use.
	 */
	public Map<String, String> map() {
		return new HashMap<String, String>(m);
	}
}
