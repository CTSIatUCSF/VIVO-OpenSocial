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

package edu.cornell.mannlib.vitro.webapp.search.indexing;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.IndexingException;
import edu.cornell.mannlib.vitro.webapp.search.beans.IndexerIface;
import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.IndividualToSolrDocument;

class IndexWorkerThread extends Thread{
	
    protected final int threadNum;
	protected IndividualToSolrDocument individualToSolrDoc;
	protected final IndexerIface indexer;
	protected final Iterator<Individual> individualsToIndex;
	protected boolean stopRequested = false;
	
	private Log log = LogFactory.getLog(IndexWorkerThread.class);
	private static AtomicLong countCompleted= new AtomicLong();		
	private static AtomicLong countToIndex= new AtomicLong();		
	private static long starttime = 0;		
	
	public IndexWorkerThread(IndexerIface indexer, int threadNum , Iterator<Individual> individualsToIndex){
	    super("IndexWorkerThread"+threadNum);
		this.indexer = indexer;
		this.threadNum = threadNum;
		this.individualsToIndex = individualsToIndex;		
	}							
	
	public void requestStop(){
	    stopRequested = true;
	}
	
	public void run(){	    
	    
	    while( ! stopRequested ){	        	       
	        
            //do the actual indexing work
            log.debug("work found for Woker number " + threadNum);	            
            addDocsToIndex();
                
            // done so shut this thread down.
            stopRequested = true;            	          
	    }  			    
		log.debug("Worker number " + threadNum + " exiting.");
	}
	
	protected void addDocsToIndex() {

		while( individualsToIndex.hasNext() ){		    
		    //need to stop right away if requested to 
		    if( stopRequested ) return;		    
		    try{
    	        //build the document and add it to the index
    		    Individual ind = null;
    	        try {
    	            ind = individualsToIndex.next();	            
    	            indexer.index( ind );
                } catch (IndexingException e) {
                    if( stopRequested )
                        return;
                    
                    if( ind != null )
                        log.error("Could not index individual " + ind.getURI() , e );
                    else
                        log.warn("Could not index, individual was null");
                }
    		    
    			
				long countNow = countCompleted.incrementAndGet();
				if( log.isInfoEnabled() ){            
					if( (countNow % 100 ) == 0 ){
						long dt = (System.currentTimeMillis() - starttime);
						log.info("individuals indexed: " + countNow + " in " + dt + " msec " +
								" time per individual = " + (dt / countNow) + " msec" );                          
					}                
				} 
		    }catch(Throwable th){
		        //on tomcat shutdown odd exceptions get thrown and log can be null
		        if( log != null && ! stopRequested )
		            log.error("Exception during index building",th);		            
		    }
		}
	}

	public static void resetCounters(long time, long workload) {
		IndexWorkerThread.starttime = time;
		IndexWorkerThread.countToIndex.set(workload);
		IndexWorkerThread.countCompleted.set(0);
	}
	
	public static long getCount() {
		return countCompleted.get();
	}
	
	public static long getCountToIndex() {
		return countToIndex.get();
	}
}
