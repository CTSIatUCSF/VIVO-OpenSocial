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

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;

/**
 * Holds the Individuals that were found in a Solr search query.
 * 
 * Provides a convenience method to run the query and to find the Individuals.
 */
public class IndividualListQueryResults {
	private static final Log log = LogFactory
			.getLog(IndividualListQueryResults.class);

	private static final IndividualListQueryResults EMPTY_RESULT = new IndividualListQueryResults(
			0, new ArrayList<Individual>());

	// ----------------------------------------------------------------------
	// Convenience method
	// ----------------------------------------------------------------------

	public static IndividualListQueryResults runQuery(SolrQuery query,
			IndividualDao indDao, ServletContext context)
			throws SolrServerException {

		SolrServer solr = SolrSetup.getSolrServer(context);
		QueryResponse response = null;
		response = solr.query(query);

		if (response == null) {
			log.debug("response from search query was null");
			return EMPTY_RESULT;
		}

		SolrDocumentList docs = response.getResults();
		if (docs == null) {
			log.debug("results from search query response was null");
			return EMPTY_RESULT;
		}

		// get list of individuals for the search results
		long hitCount = docs.getNumFound();
		log.debug("Number of search results: " + hitCount);

		List<Individual> individuals = new ArrayList<Individual>(docs.size());
		for (SolrDocument doc : docs) {
			String uri = doc.get(VitroSearchTermNames.URI).toString();
			Individual individual = indDao.getIndividualByURI(uri);
			if (individual == null) {
				log.debug("No individual for search document with uri = " + uri);
			} else {
				individuals.add(individual);
				log.debug("Adding individual " + uri + " to individual list");
			}
		}

		return new IndividualListQueryResults((int) hitCount, individuals);
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final int hitCount;
	private final List<Individual> individuals;

	public IndividualListQueryResults(int hitCount, List<Individual> individuals) {
		this.hitCount = hitCount;
		this.individuals = individuals;
	}

	public int getHitCount() {
		return hitCount;
	}

	public List<Individual> getIndividuals() {
		return individuals;
	}

}
