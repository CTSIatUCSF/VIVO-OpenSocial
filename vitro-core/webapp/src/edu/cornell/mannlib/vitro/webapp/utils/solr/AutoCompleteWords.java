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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A helper class for use with an Auto-complete query.
 * 
 * Any word that is followed by a delimiter is considered to be complete, and
 * should be matched exactly in the query. If there is a word on the end that is
 * not followed by a delimiter, it is incomplete, and should act like a
 * "starts-with" query.
 */
public class AutoCompleteWords {
	private static final Log log = LogFactory.getLog(AutoCompleteWords.class);

	private final String searchTerm;
	private final String delimiterPattern;
	private final List<String> completeWords;
	private final String partialWord;

	/**
	 * Package-access. Use SolrQueryUtils.parseForAutoComplete() to create an
	 * instance.
	 */
	AutoCompleteWords(String searchTerm, String delimiterPattern) {
		this.searchTerm = searchTerm;
		this.delimiterPattern = delimiterPattern;

		List<String> termWords = figureTermWords();
		if (termWords.isEmpty() || this.searchTerm.endsWith(" ")) {
			this.completeWords = termWords;
			this.partialWord = null;
		} else {
			this.completeWords = termWords.subList(0, termWords.size() - 1);
			this.partialWord = termWords.get(termWords.size() - 1);
		}

	}

	private List<String> figureTermWords() {
		List<String> list = new ArrayList<String>();
		String[] array = this.searchTerm.split(this.delimiterPattern);
		for (String word : array) {
			String trimmed = word.trim();
			if (!trimmed.isEmpty()) {
				list.add(trimmed);
			}
		}
		return Collections.unmodifiableList(list);
	}

	public String assembleQuery(String fieldNameForCompleteWords,
			String fieldNameForPartialWord) {
		List<String> terms = new ArrayList<String>();
		for (String word : this.completeWords) {
			terms.add(buildTerm(fieldNameForCompleteWords, word));
		}
		if (partialWord != null) {
			terms.add(buildTerm(fieldNameForPartialWord, partialWord));
		}

		String q = StringUtils.join(terms, " AND ");
		log.debug("Query string is '" + q + "'");
		return q;
	}

	private String buildTerm(String fieldName, String word) {
		return fieldName + ":\"" + word + "\"";
	}

	@Override
	public String toString() {
		return "AutoCompleteWords[searchTerm='" + searchTerm
				+ "', delimiterPattern='" + delimiterPattern
				+ "', completeWords=" + completeWords + ", partialWord="
				+ partialWord + "]";
	}

}
