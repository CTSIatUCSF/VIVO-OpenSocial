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

package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.IndexingException;
import edu.cornell.mannlib.vitro.webapp.search.beans.IndexerIface;
import edu.cornell.mannlib.vitro.webapp.search.docbuilder.Obj2DocIface;
import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.IndividualToSolrDocument;


public class SolrIndexer implements IndexerIface {
    private final static Log log = LogFactory.getLog(SolrIndexer.class);
    
    protected SolrServer server;
    protected boolean indexing;        
    protected HashSet<String> urisIndexed;    
    protected IndividualToSolrDocument individualToSolrDoc;
    
    /**
     * System is shutting down if true.
     */
    protected boolean shutdownRequested = false;
    
    /**
     * This records when a full re-index starts so that once it is done
     * all the documents on the Solr service that are earlier than the
     * reindexStart can be removed. 
     */
    protected long reindexStart = 0L;
    
    /**
     * If true, then a full index rebuild was requested and reindexStart
     * will be used to determine what documents to remove from the index
     * once the re-index is complete.
     */
    protected boolean doingFullIndexRebuild = false;
    
    public SolrIndexer( SolrServer server, IndividualToSolrDocument indToDoc){
        this.server = server; 
        this.individualToSolrDoc = indToDoc;        
    }
    
    @Override
    public void index(Individual ind) throws IndexingException {
        if( ! indexing )
            throw new IndexingException("SolrIndexer: must call " +
                    "startIndexing() before index().");        
        
        if( ind == null )
            log.debug("Individual to index was null, ignoring.");
        
        try{
            if( urisIndexed.contains(ind.getURI()) ){
                log.debug("already indexed " + ind.getURI() );
                return;
            }else{
            	SolrInputDocument solrDoc = null;
            	synchronized(this){
            		urisIndexed.add(ind.getURI());
            	}
                log.debug("indexing " + ind.getURI());      
                solrDoc = individualToSolrDoc.translate(ind);

                if( solrDoc != null){
                	if( log.isDebugEnabled()){
                		log.info("boost for " + ind.getName() + " is " + solrDoc.getDocumentBoost());
                		log.debug( solrDoc.toString() );
                	}                	                	                	
                	
                    UpdateResponse res = server.add( solrDoc );
                    log.debug("response after adding docs to server: "+ res);                
                }else{
                    log.debug("removing from index " + ind.getURI());                    
                    removeFromIndex(ind.getURI());
                }                            
            }
        } catch (IOException ex) {
            throw new IndexingException(ex.getMessage());
        } catch (SolrServerException ex) {
            throw new IndexingException(ex.getMessage());
        }                 
    }

    @Override
    public boolean isIndexing() {     
        return indexing;
    }

    @Override
    public void prepareForRebuild() throws IndexingException {
        reindexStart = System.currentTimeMillis();
        doingFullIndexRebuild = true;
    }

    @Override
    public void removeFromIndex(String uri) throws IndexingException {
        if( uri != null ){            
            try {
                server.deleteById(individualToSolrDoc.getIdForUri(uri));
                log.debug("deleted " + " " + uri);                                       
            } catch (SolrServerException e) {
                log.error( "could not delete individual " + uri, e);
            } catch (IOException e) {
                log.error( "could not delete individual " + uri, e);
            }
        }        
    }

    @Override
    public synchronized void startIndexing() throws IndexingException {
        if( indexing)
            log.debug("SolrIndexer.startIndexing() Indexing in progress, waiting for completion...");
        while( indexing && ! shutdownRequested ){ //wait for indexing to end.            
            try{ wait( 250 ); } 
            catch(InterruptedException ex){}
        }
               
        log.debug("Starting to index");
        indexing = true;
        urisIndexed = new HashSet<String>();        
        notifyAll();        
    }        
    
    @Override
    public void abortIndexingAndCleanUp() {
        shutdownRequested = true;
        try{
            individualToSolrDoc.shutdown();
        }catch(Exception e){
            if( log != null)
                log.debug(e,e);
        }                
        endIndexing();
    }
   
    @Override
    public synchronized void endIndexing() {
        try {
            if( doingFullIndexRebuild ){
                removeDocumentsFromBeforeRebuild( );
            }
         } catch (Throwable e) {
             if( ! shutdownRequested )
                 log.debug("could not remove documents from before build, " ,e);
        }
        try {
           UpdateResponse res = server.commit();           
        } catch (Throwable e) {
            if( ! shutdownRequested ){
                log.debug("could not commit to solr server, " +
                "this should not be a problem since solr will do autocommit");
            }
        }                
        indexing = false;
        notifyAll();
    }

    protected void removeDocumentsFromBeforeRebuild(){
        try {
            server.deleteByQuery("indexedTime:[ * TO " + reindexStart + " ]");
            server.commit();            
        } catch (SolrServerException e) {
            if( ! shutdownRequested )
                log.error("could not delete documents from before rebuild.",e);            
        } catch (IOException e) {
            if( ! shutdownRequested )
                log.error("could not delete documents from before rebuild.",e);
        }
    }
    
    
    @Override
    public long getModified() {
    	long modified = 0;

    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:*");
    	query.addSortField("indexedTime", SolrQuery.ORDER.desc);

    	try {
    		QueryResponse rsp = server.query(query);
    		SolrDocumentList docs = rsp.getResults();
    		if(docs!=null){
    			modified = (Long)docs.get(0).getFieldValue("indexedTime");	
    		}
    	} catch (SolrServerException e) {
    		log.error(e,e);
    	}

    	return modified;
    }

    /**
     * Returns true if there are documents in the index, false if there are none,
     * and returns false on failure to connect to server.
     */
    public boolean isIndexEmpty() {
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:*");
    	try {
    		QueryResponse rsp = server.query(query);
    		SolrDocumentList docs = rsp.getResults();
    		if(docs==null || docs.size()==0){
    			return true;
    		}
    	} catch (SolrServerException e) {
    		log.error("Could not connect to solr server" ,e.getRootCause());
    	}
        return false;
    }

}
