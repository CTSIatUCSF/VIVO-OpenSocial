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
package edu.cornell.mannlib.vitro.webapp.controller;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;

public class IndividualListRdfController extends VitroHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(IndividualListRdfController.class.getName());
    
	public static final int ENTITY_LIST_CONTROLLER_MAX_RESULTS = 30000;
	    
    public void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    	    
    	// Make the query
    	String vclassUri = req.getParameter("vclass");
    	String queryStr = VitroSearchTermNames.RDFTYPE + ":\"" + vclassUri + "\"";
    	SolrQuery query = new SolrQuery(queryStr);
    	query.setStart(0)
    	     .setRows(ENTITY_LIST_CONTROLLER_MAX_RESULTS)
    	     .setFields(VitroSearchTermNames.URI);
    	     // For now, we're only displaying the url, so no need to sort.
    	     //.setSortField(VitroSearchTermNames.NAME_LOWERCASE_SINGLE_VALUED);

    	// Execute the query
        SolrServer solr = SolrSetup.getSolrServer(getServletContext());
        QueryResponse response = null;
        
        try {
            response = solr.query(query);            
        } catch (Throwable t) {
            log.error(t, t);            
        }

        if ( response == null ) {         
            throw new ServletException("Could not run search in IndividualListRdfController");        
        }

        SolrDocumentList docs = response.getResults();
        
        if (docs == null) {
            throw new ServletException("Could not run search in IndividualListRdfController");    
        }

        Model model = ModelFactory.createDefaultModel();
        for (SolrDocument doc : docs) {
            String uri = doc.get(VitroSearchTermNames.URI).toString();
            Resource resource = ResourceFactory.createResource(uri);
            RDFNode node = (RDFNode) ResourceFactory.createResource(vclassUri);
            model.add(resource, RDF.type, node);
        }

        res.setContentType(RDFXML_MIMETYPE); 
    	model.write(res.getOutputStream(), "RDF/XML");
    }
    
    public void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
    	doGet(req,res);
    }
    
}
