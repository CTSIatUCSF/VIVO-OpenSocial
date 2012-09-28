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

package edu.cornell.mannlib.vitro.webapp.rdfservice;

import java.io.InputStream;
import java.util.List;

/**
 * Interface for API to write, read, and update Vitro's RDF store, with support 
 * to allow listening, logging and auditing.
 */

public interface RDFService {

	public enum SPARQLQueryType {
	    SELECT, CONSTRUCT, DESCRIBE, ASK
	}

	public enum ModelSerializationFormat {
	    RDFXML, N3, NTRIPLE
	}
	
	public enum ResultFormat {
	    JSON, CSV, XML, TEXT
	}
	
	/**
	 * Performs a series of additions to and or removals from specified graphs
	 * in the RDF store.  preConditionSparql is executed against the 
	 * union of all the graphs in the knowledge base before any updates are made. 
	 * If the precondition query returns a non-empty result, no updates
	 * are made made. 
	 * 
	 * @param changeSet - a set of changes to be performed on the RDF store.
	 *    
	 * @return boolean - indicates whether the precondition was satisfied            
	 */
	public boolean changeSetUpdate(ChangeSet changeSet) throws RDFServiceException;
		
	/**
	 * If the given individual already exists in the default write graph, throws an 
	 * RDFServiceException, otherwise adds one type assertion to the default write
	 * graph.
	 * 
	 * @param individualURI - URI of the individual to be added
	 * @param individualTypeURI - URI of the type for the individual
	 */
	public void newIndividual(String individualURI, String individualTypeURI) throws RDFServiceException;

	/**
	 * If the given individual already exists in the given graph, throws an 
	 * RDFServiceException, otherwise adds one type assertion to the given
	 * graph.
	 *
	 * @param individualURI - URI of the individual to be added
	 * @param individualTypeURI - URI of the type for the individual
	 * @param graphURI - URI of the graph to which to add the individual
	 */
	public void newIndividual(String individualURI, String individualTypeURI, String graphURI) throws RDFServiceException;
	
	/**
	 * Performs a SPARQL construct query against the knowledge base. The query may have
	 * an embedded graph identifier. If the query does not contain a graph identifier
	 * the query is executed against the union of all named and unnamed graphs in the 
	 * store.
	 * 
	 * @param query - the SPARQL query to be executed against the RDF store
	 * @param resultFormat - type of serialization for RDF result of the SPARQL query
	 * 
	 * @return InputStream - the result of the query
	 * 
	 */
	public InputStream sparqlConstructQuery(String query, RDFService.ModelSerializationFormat resultFormat) throws RDFServiceException;
	
	/**
	 * Performs a SPARQL describe query against the knowledge base. The query may have
	 * an embedded graph identifier. If the query does not contain a graph identifier
	 * the query is executed against the union of all named and unnamed graphs in the 
	 * store.
	 * 
	 * @param query - the SPARQL query to be executed against the RDF store
	 * @param resultFormat - type of serialization for RDF result of the SPARQL query
	 * 
	 * @return InputStream - the result of the query
	 * 
	 */
	public InputStream sparqlDescribeQuery(String query, RDFService.ModelSerializationFormat resultFormat) throws RDFServiceException;
	
	/**
	 * Performs a SPARQL select query against the knowledge base. The query may have
	 * an embedded graph identifier. If the query does not contain a graph identifier
	 * the query is executed against the union of all named and unnamed graphs in the 
	 * store.
	 * 
	 * @param query - the SPARQL query to be executed against the RDF store
	 * @param resultFormat - format for the result of the Select query
	 * 
	 * @return InputStream - the result of the query
	 * 
	 */
	public InputStream sparqlSelectQuery(String query, RDFService.ResultFormat resultFormat) throws RDFServiceException;
	
	/**
	 * Performs a SPARQL ASK query against the knowledge base. The query may have
	 * an embedded graph identifier. If the query does not contain a graph identifier
	 * the query is executed against the union of all named and unnamed graphs in the 
	 * store.
	 * 
	 * @param query - the SPARQL ASK query to be executed against the RDF store
	 * 
	 * @return  boolean - the result of the SPARQL ASK query 
	 */
	public boolean sparqlAskQuery(String query) throws RDFServiceException;
	
	/**
	 * Returns a list of all the graph URIs in the RDF store.
	 * 
	 * @return  List<String> - list of all the named graph URIs in the RDF store. 
	 *                         Return an empty list of there no named graphs in
	 *                         the store. 
	 */
	public List<String> getGraphURIs() throws RDFServiceException;

	/**
	 * To be determined. This is a place holder and is not implemented
	 * in current implementations.
	 */
	public void getGraphMetadata() throws RDFServiceException;
		
	/**
	 * Returns the URI of the default write graph
	 * 
	 * @return String URI of default write graph. Returns null if no
	 *         default write graph has been set.
	 */
	public String getDefaultWriteGraphURI() throws RDFServiceException;
	
	/**
	 * Registers a listener to listen to changes in any graph in
	 * the RDF store.
	 * 
	 * @param changeListener - the change listener
	 */
	public void registerListener(ChangeListener changeListener) throws RDFServiceException;
	
	/**
	 * Unregisters a listener from listening to changes in
	 * any graph in the RDF store
	 * 
	 * @param changeListener - the change listener
	 */
	public void unregisterListener(ChangeListener changeListener) throws RDFServiceException;

	/**
	 * Creates a ChangeSet object
	 * 
	 * @return ChangeSet an empty ChangeSet object
	 */
	public ChangeSet manufactureChangeSet();	
		
	/**
     * Frees any resources held by this RDFService object
     * 
     * The implementation of this method should be idempotent so that
     * multiple invocations do not cause an error.
     */
    public void close();
}
