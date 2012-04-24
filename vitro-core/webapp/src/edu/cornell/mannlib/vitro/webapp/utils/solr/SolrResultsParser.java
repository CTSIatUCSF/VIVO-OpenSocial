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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * Parse this Solr response, creating a map of values for each document.
 * 
 * The Solr field names in the document are replaced by json field names in the
 * parsed results, according to the fieldMap.
 */
public class SolrResultsParser {
	private static final Log log = LogFactory.getLog(SolrResultsParser.class);

	private final QueryResponse queryResponse;
	private final Map<String, String> fieldNameMapping;

	public SolrResultsParser(QueryResponse queryResponse, FieldMap fieldMap) {
		this.queryResponse = queryResponse;
		this.fieldNameMapping = fieldMap.map();
	}

	/**
	 * Parse the entire response into a list of maps.
	 */
	public List<Map<String, String>> parse() {
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();

		if (queryResponse == null) {
			log.error("Query response for a search was null");
			return maps;
		}

		SolrDocumentList docs = queryResponse.getResults();
		if (docs == null) {
			log.error("Docs for a search was null");
			return maps;
		}
		log.debug("Total number of hits = " + docs.getNumFound());

		for (SolrDocument doc : docs) {
			maps.add(parseSingleDocument(doc));
		}

		return maps;
	}

	/**
	 * Parse the response, accepting only those maps that are acceptable to the
	 * filter, until we reach the maximum desired number of results (or until we
	 * have parsed the entire response).
	 */
	public List<Map<String, String>> parseAndFilterResponse(
			SolrResponseFilter filter, int maxNumberOfResults) {
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();

		if (queryResponse == null) {
			log.error("Query response for a search was null");
			return maps;
		}

		SolrDocumentList docs = queryResponse.getResults();
		if (docs == null) {
			log.error("Docs for a search was null");
			return maps;
		}
		log.debug("Total number of hits = " + docs.getNumFound());

		for (SolrDocument doc : docs) {
			Map<String, String> map = parseSingleDocument(doc);
			if (filter.accept(map)) {
				maps.add(map);
			}
			if (maps.size() >= maxNumberOfResults) {
				break;
			}
		}

		return maps;
	}

	/**
	 * Create a map from this document, applying translation on the field names.
	 */
	private Map<String, String> parseSingleDocument(SolrDocument doc) {
		Map<String, String> result = new HashMap<String, String>();
		for (String solrFieldName : fieldNameMapping.keySet()) {
			String jsonFieldName = fieldNameMapping.get(solrFieldName);

			result.put(jsonFieldName, parseSingleValue(doc, solrFieldName));
		}

		return result;
	}

	/**
	 * Find a single value in the document
	 */
	private String parseSingleValue(SolrDocument doc, String key) {
		Object rawValue = getFirstValue(doc.get(key));

		if (rawValue == null) {
			return "";
		}
		if (rawValue instanceof String) {
			return (String) rawValue;
		}
		return String.valueOf(rawValue);
	}

	/**
	 * The result might be a list. If so, get the first element.
	 */
	private Object getFirstValue(Object rawValue) {
		if (rawValue instanceof List<?>) {
			List<?> list = (List<?>) rawValue;
			if (list.isEmpty()) {
				return null;
			} else {
				return list.get(0);
			}
		} else {
			return rawValue;
		}
	}

}
