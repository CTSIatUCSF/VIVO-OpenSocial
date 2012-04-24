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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * Some static method to help in constructing Solr queries and parsing the
 * results.
 */
public class SolrQueryUtils {
	public enum Conjunction {
		AND, OR;

		public String joiner() {
			return " " + this.name() + " ";
		}
	}

	/**
	 * Create an AutoCompleteWords object that can be used to build an
	 * auto-complete query.
	 */
	public static AutoCompleteWords parseForAutoComplete(String searchTerm,
			String delimiterPattern) {
		return new AutoCompleteWords(searchTerm, delimiterPattern);
	}

	/**
	 * Create a builder object that can assemble a map of Solr field names to
	 * JSON field names.
	 */
	public static FieldMap fieldMap() {
		return new FieldMap();
	}

	/**
	 * Parse a response into a list of maps, one map for each document.
	 * 
	 * The Solr field names in the document are replaced by json field names in
	 * the result, according to the fieldMap.
	 */
	public static List<Map<String, String>> parseResponse(
			QueryResponse queryResponse, FieldMap fieldMap) {
		return new SolrResultsParser(queryResponse, fieldMap).parse();
	}

	/**
	 * Parse a response into a list of maps, accepting only those maps that pass
	 * a filter, and only up to a maximum number of records.
	 * 
	 * The Solr field names in the document are replaced by json field names in
	 * the result, according to the fieldMap.
	 */
	public static List<Map<String, String>> parseAndFilterResponse(
			QueryResponse queryResponse, FieldMap fieldMap,
			SolrResponseFilter filter, int maxNumberOfResults) {
		return new SolrResultsParser(queryResponse, fieldMap)
				.parseAndFilterResponse(filter, maxNumberOfResults);
	}

	/**
	 * Break a string into a list of words, according to a RegEx delimiter. Trim
	 * leading and trailing white space from each word.
	 */
	public static List<String> parseWords(String typesString,
			String wordDelimiter) {
		List<String> list = new ArrayList<String>();
		String[] array = typesString.split(wordDelimiter);
		for (String word : array) {
			String trimmed = word.trim();
			if (!trimmed.isEmpty()) {
				list.add(trimmed);
			}
		}
		return list;
	}

	/**
	 * Glue these words together into a query on a given field, joined by either
	 * AND or OR.
	 */
	public static String assembleConjunctiveQuery(String fieldName,
			Collection<String> words, Conjunction c) {
		List<String> terms = new ArrayList<String>();
		for (String word : words) {
			terms.add(buildTerm(fieldName, word));
		}
		String q = StringUtils.join(terms, c.joiner());
		return q;
	}

	private static String buildTerm(String fieldName, String word) {
		return fieldName + ":\"" + word + "\"";
	}

}
