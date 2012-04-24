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

package edu.cornell.mannlib.vitro.webapp.utils.jena;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.OWL;

public class SesameSyncUtils {

	private static final Log log = LogFactory.getLog(SesameSyncUtils.class);
	
	public void writeModelToSesameContext
	  (Model jenaModel, String serverURI, String repositoryId, String contextId) 
	  throws RepositoryException, IOException, RDFParseException {
	    Repository myRepository = new HTTPRepository(serverURI, repositoryId);
	    myRepository.initialize();
	    RepositoryConnection myConn = myRepository.getConnection();    
	    
	    myConn.setAutoCommit(false);
	    try {
	    
		    Resource contextRes = (contextId != null) 
		    	? new URIImpl(contextId) : null ;
		    		
		    if (contextRes != null) {
		    	myConn.clear(contextRes);
		    } else {
		    	myConn.clear();
		    }

		    PipedInputStream in = new PipedInputStream();
		    PipedOutputStream out = new PipedOutputStream(in);
		    
		    
		    
		    try {
			    
		    	new Thread(new JenaOutputter(jenaModel, out, myConn), "SesameSyncUtilities.JenaOutputter").start();
		    	
			    if (contextRes != null) {
			    	myConn.add(in,"http://example.org/base/", RDFFormat.NTRIPLES, contextRes);
			    } else {
			    	myConn.add(in,"http://example.org/base/", RDFFormat.NTRIPLES);
			    }
		    } finally {
		    	in.close();
		    }
		    
		    myConn.commit();
		    
	    } catch (Throwable e) {
	    	myConn.rollback();
            e.printStackTrace();
            log.error("Error writing to Sesame repository", e);
            throw new RuntimeException("Error writing to Sesame repository", e);
	    } finally {
	    	myConn.close();
	    } 
  
	}
	
	private List<String> getIndividualURIs(Model model) {
		List<String> individualURIs = new ArrayList<String>();	
		String queryStr = "SELECT DISTINCT ?s WHERE { \n" +
		                  "    ?s a <" + OWL.Thing.getURI() + "> \n" +
		                  "}";
		Query query = QueryFactory.create(queryStr);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		try {
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution qsoln = rs.nextSolution();
				String individualURI = qsoln.getResource("s").getURI();
				if (individualURI != null) {
					individualURIs.add(individualURI);
				}
			}
		} finally {
			qe.close();
		}
		return individualURIs;
	}
	
	private class JenaOutputter implements Runnable {
		
		private Model model;
		private OutputStream out;
        private RepositoryConnection rconn;
		
		public JenaOutputter(Model model, OutputStream out, RepositoryConnection rconn) {
			this.model = model;
			this.out = out;
            this.rconn = rconn;
		}
		
		public void run() {		
		    Model t = ModelFactory.createDefaultModel();
			try {
		        List<String> individualURIs = getIndividualURIs(model);
		    	log.info(individualURIs.size() + " individuals to send to Sesame");
		    	int i = 0;
			    for (String individualURI : individualURIs) {
			    	t.removeAll();
			    	t.add(model.listStatements(
			    			model.getResource(
			    					individualURI), null, (RDFNode) null));
			    	t.write(out, "N-TRIPLE");
			    	i++;
			    	if (i % 100 == 0) {
                        try {
                            rconn.commit();
                        } catch (Throwable e) {
                            log.error(e, e);
                        }
			    		log.info(i + " individuals sent to Sesame");
			    	}
			    }
			} finally {
				try {
					out.flush();
					out.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
	}
	
}
