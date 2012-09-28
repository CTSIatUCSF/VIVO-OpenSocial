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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.admin.ajax;

import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.AC_NAME_STEMMED;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.NAME_RAW;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.NAME_UNSTEMMED;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.RDFTYPE;
import static edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames.URI;
import static edu.cornell.mannlib.vitro.webapp.utils.solr.SolrQueryUtils.Conjunction.OR;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.AbstractAjaxResponder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;
import edu.cornell.mannlib.vitro.webapp.utils.solr.AutoCompleteWords;
import edu.cornell.mannlib.vitro.webapp.utils.solr.FieldMap;
import edu.cornell.mannlib.vitro.webapp.utils.solr.SolrQueryUtils;
import edu.cornell.mannlib.vitro.webapp.utils.solr.SolrResponseFilter;

/**
 * Get a list of Profiles with last names that begin with this search term, and
 * that have no matching property, unless it matches the current externalAuthId.
 * (So a Profile that is currently associated with the user is not excluded from
 * the list.)
 * 
 * For each such Profile, return the label, the URL and the URI.
 * 
 * If the matching property is not defined, or if the search term is empty, or
 * if an error occurs, return an empty result.
 */
class ProfileAutoCompleter extends AbstractAjaxResponder implements
		SolrResponseFilter {
	private static final Log log = LogFactory
			.getLog(ProfileAutoCompleter.class);

	private static final String PARAMETER_SEARCH_TERM = "term";
	private static final String PARAMETER_ETERNAL_AUTH_ID = "externalAuthId";

	private static final Collection<String> profileTypes = Collections
			.singleton("http://xmlns.com/foaf/0.1/Person");

	private static final String WORD_DELIMITER = "[, ]+";
	private static final FieldMap RESPONSE_FIELDS = SolrQueryUtils.fieldMap()
			.put(URI, "uri").put(NAME_RAW, "label");

	private static final Syntax SYNTAX = Syntax.syntaxARQ;

	/** Use this to check whether search results should be filtered out. */
	private static final String QUERY_TEMPLATE = "" //
			+ "SELECT DISTINCT ?id \n" //
			+ "WHERE {\n" //
			+ "    <%uri%> <%matchingPropertyUri%> ?id . \n" //
			+ "} \n" //
			+ "LIMIT 1 \n";

	private final String externalAuthId;
	private final String selfEditingIdMatchingProperty;
	private final String term;
	private final AutoCompleteWords searchWords;
	private final OntModel fullModel;

	public ProfileAutoCompleter(HttpServlet parent, VitroRequest vreq,
			HttpServletResponse resp) {
		super(parent, vreq, resp);
		this.externalAuthId = getStringParameter(PARAMETER_ETERNAL_AUTH_ID, "");

		this.term = getStringParameter(PARAMETER_SEARCH_TERM, "");
		this.searchWords = SolrQueryUtils.parseForAutoComplete(term,
				WORD_DELIMITER);

		// TODO This seems to expose the matching property and mechanism too
		// much. Can this be done within SelfEditingConfiguration somehow?
		this.selfEditingIdMatchingProperty = SelfEditingConfiguration.getBean(
				vreq).getMatchingPropertyUri();

		this.fullModel = vreq.getJenaOntModel();
	}

	@Override
	public String prepareResponse() throws IOException, JSONException {
		if (term.isEmpty()) {
			return EMPTY_RESPONSE;
		}
		if (selfEditingIdMatchingProperty == null) {
			return EMPTY_RESPONSE;
		}
		if (selfEditingIdMatchingProperty.isEmpty()) {
			return EMPTY_RESPONSE;
		}

		try {
			SolrQuery query = buildSolrQuery();
			QueryResponse queryResponse = executeSolrQuery(query);

			List<Map<String, String>> maps = SolrQueryUtils
					.parseAndFilterResponse(queryResponse, RESPONSE_FIELDS,
							this, 30);

			addProfileUrls(maps);

			String response = assembleJsonResponse(maps);
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
		q.setRows(10000);
		q.setQuery(searchWords.assembleQuery(NAME_UNSTEMMED, AC_NAME_STEMMED));
		return q;
	}

	private QueryResponse executeSolrQuery(SolrQuery query)
			throws SolrServerException {
		ServletContext ctx = servlet.getServletContext();
		SolrServer solr = SolrSetup.getSolrServer(ctx);
		return solr.query(query);
	}

	/**
	 * For each URI in the maps, insert the corresponding URL.
	 */
	private void addProfileUrls(List<Map<String, String>> maps) {
		for (Map<String, String> map : maps) {
			String uri = map.get("uri");
			String url = UrlBuilder.getIndividualProfileUrl(uri, vreq);
			map.put("url", url);
		}
	}

	/**
	 * To test whether a search result is acceptable, find the matching property
	 * for the individual.
	 * 
	 * We will accept any individual without a matching property, or with a
	 * matching property that matches the user we are editing.
	 */
	@Override
	public boolean accept(Map<String, String> map) {
		String uri = map.get("uri");
		if (uri == null) {
			log.debug("reject result with no uri");
			return false;
		}

		String id = runQueryAndGetId(uri);
		if (id.isEmpty() || id.equals(externalAuthId)) {
			log.debug("accept '" + uri + "' with id='" + id + "'");
			return true;
		}

		log.debug("reject '" + uri + "' with id='" + id + "'");
		return false;
	}

	/**
	 * Run the query for the filter. Return the ID, if one was found.
	 */
	private String runQueryAndGetId(String uri) {
		String queryString = QUERY_TEMPLATE.replace("%matchingPropertyUri%",
				selfEditingIdMatchingProperty).replace("%uri%", uri);

		QueryExecution qe = null;
		try {
			Query query = QueryFactory.create(queryString, SYNTAX);
			qe = QueryExecutionFactory.create(query, fullModel);

			ResultSet resultSet = qe.execSelect();
			if (!resultSet.hasNext()) {
				return "";
			}

			QuerySolution solution = resultSet.next();
			Literal literal = solution.getLiteral("id");
			if (literal == null) {
				return "";
			}

			return literal.getString();
		} catch (Exception e) {
			log.error("Failed to execute the query: " + queryString, e);
			return "";
		} finally {
			if (qe != null) {
				qe.close();
			}
		}
	}
}