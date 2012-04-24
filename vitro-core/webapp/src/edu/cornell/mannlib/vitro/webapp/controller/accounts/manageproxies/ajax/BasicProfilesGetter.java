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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.manageproxies.ajax;

import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.AC_NAME_STEMMED;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.NAME_RAW;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.NAME_UNSTEMMED;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.RDFTYPE;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.URI;
import static edu.cornell.mannlib.vitro.webapp.utils.solr.SolrQueryUtils.Conjunction.OR;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.json.JSONException;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.AbstractAjaxResponder;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;
import edu.cornell.mannlib.vitro.webapp.utils.solr.AutoCompleteWords;
import edu.cornell.mannlib.vitro.webapp.utils.solr.FieldMap;
import edu.cornell.mannlib.vitro.webapp.utils.solr.SolrQueryUtils;

/**
 * Get the basic auto-complete info for the profile selection.
 * 
 */
public class BasicProfilesGetter extends AbstractAjaxResponder {
	private static final Log log = LogFactory.getLog(BasicProfilesGetter.class);

	private static final String WORD_DELIMITER = "[, ]+";
	private static final FieldMap RESPONSE_FIELDS = SolrQueryUtils
			.fieldMap().put(URI, "uri").put(NAME_RAW, "label")
			.put("bogus", "classLabel").put("bogus", "imageUrl");

	private static final String PROPERTY_PROFILE_TYPES = "proxy.eligibleTypeList";
	private static final String PARAMETER_SEARCH_TERM = "term";
	private static final String DEFAULT_PROFILE_TYPES = "http://www.w3.org/2002/07/owl#Thing";

	private final String term;
	private final AutoCompleteWords searchWords;
	private final List<String> profileTypes;

	public BasicProfilesGetter(HttpServlet servlet, VitroRequest vreq,
			HttpServletResponse resp) {
		super(servlet, vreq, resp);

		this.term = getStringParameter(PARAMETER_SEARCH_TERM, "");
		this.searchWords = SolrQueryUtils.parseForAutoComplete(term,
				WORD_DELIMITER);

		this.profileTypes = figureProfileTypes();

		log.debug(this);
	}

	private List<String> figureProfileTypes() {
		String typesString = ConfigurationProperties.getBean(vreq).getProperty(
				PROPERTY_PROFILE_TYPES, DEFAULT_PROFILE_TYPES);
		List<String> list = SolrQueryUtils.parseWords(typesString,
				WORD_DELIMITER);
		if (list.isEmpty()) {
			log.error("No types configured for profile pages in "
					+ PROPERTY_PROFILE_TYPES);
		}
		return list;
	}

	@Override
	public String prepareResponse() throws IOException, JSONException {
		log.debug("search term is '" + term + "'");
		if (this.term.isEmpty() || this.profileTypes.isEmpty()) {
			return EMPTY_RESPONSE;
		}

		try {
			ServletContext ctx = servlet.getServletContext();
			SolrServer solr = SolrSetup.getSolrServer(ctx);
			SolrQuery query = buildSolrQuery();
			QueryResponse queryResponse = solr.query(query);

			List<Map<String, String>> parsed = SolrQueryUtils
					.parseResponse(queryResponse, RESPONSE_FIELDS);

			String response = assembleJsonResponse(parsed);
			log.debug(response);
			return response;
		} catch (SolrServerException e) {
			log.error("Failed to get basic profile info", e);
			return EMPTY_RESPONSE;
		}
	}

	private SolrQuery buildSolrQuery() {
		SolrQuery q = new SolrQuery();
		q.setFields(NAME_RAW, URI);
		q.setSortField(NAME_LOWERCASE_SINGLE_VALUED, ORDER.asc);
		q.setFilterQueries(SolrQueryUtils.assembleConjunctiveQuery(RDFTYPE,
				profileTypes, OR));
		q.setStart(0);
		q.setRows(30);
		q.setQuery(searchWords.assembleQuery(NAME_UNSTEMMED, AC_NAME_STEMMED));
		return q;
	}

	@Override
	public String toString() {
		return "BasicProfilesGetter[term=" + term + ", searchWords="
				+ searchWords + ", profileTypes=" + profileTypes + "]";
	}

}
