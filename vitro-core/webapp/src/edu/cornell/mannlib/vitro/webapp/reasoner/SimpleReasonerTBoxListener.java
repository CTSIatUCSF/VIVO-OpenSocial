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

package edu.cornell.mannlib.vitro.webapp.reasoner;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.listeners.StatementListener;
import com.hp.hpl.jena.rdf.model.Statement;

import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;
import edu.cornell.mannlib.vitro.webapp.utils.threads.VitroBackgroundThread;

/**
 * Route notification of changes to TBox to the incremental ABox reasoner.
 * The incremental ABox reasoner handles only subClass and
 * equivalentClass class axioms. Reasoning dones as a result of TBox
 * changes is always done in a separate thread.
 */

public class SimpleReasonerTBoxListener extends StatementListener {

	private static final Log log = LogFactory.getLog(SimpleReasonerTBoxListener.class);
	
    private SimpleReasoner simpleReasoner;
    private Thread workerThread;
    private boolean stopRequested;
    private String name;

	private volatile boolean processingUpdates = false;
	private ConcurrentLinkedQueue<ModelUpdate> modelUpdates = null;

	public SimpleReasonerTBoxListener(SimpleReasoner simpleReasoner) {
		this.simpleReasoner = simpleReasoner;
		this.stopRequested = false;
		this.modelUpdates = new ConcurrentLinkedQueue<ModelUpdate>();
		this.processingUpdates = false;
	}

	public SimpleReasonerTBoxListener(SimpleReasoner simpleReasoner, String name) {
		this.simpleReasoner = simpleReasoner;
	    this.name = name;
		this.stopRequested = false;
		this.modelUpdates = new ConcurrentLinkedQueue<ModelUpdate>();
		this.processingUpdates = false;
	}
	
	@Override
	public void addedStatement(Statement statement) {
		
		ModelUpdate mu = new ModelUpdate(statement, ModelUpdate.Operation.ADD, JenaDataSourceSetupBase.JENA_TBOX_ASSERTIONS_MODEL);
		processUpdate(mu);
	}

	@Override
	public void removedStatement(Statement statement) {
		
		ModelUpdate mu = new ModelUpdate(statement, ModelUpdate.Operation.RETRACT, JenaDataSourceSetupBase.JENA_TBOX_ASSERTIONS_MODEL);	
		processUpdate(mu);
	}
	
	private synchronized void processUpdate(ModelUpdate mu) {
		if (!processingUpdates && (modelUpdates.peek() != null)) {
			log.warn("TBoxProcessor thread was not running and work queue is not empty. size = " + modelUpdates.size() + " The work will be processed now.");
		}
		
		modelUpdates.add(mu);
		
		if (!processingUpdates) {
			processingUpdates = true;
			workerThread = new TBoxUpdateProcessor("TBoxUpdateProcessor (" + getName() + ")");
			workerThread.start();
		}
	}
	      
   private synchronized ModelUpdate nextUpdate() {
	    ModelUpdate mu = modelUpdates.poll();
	    processingUpdates = (mu != null);
	    return mu;
   }
   	
   public String getName() {
		return (name == null) ? "SimpleReasonerTBoxListener" : name;	
   }

   public void setStopRequested() {
	    this.stopRequested = true;
   }
	
   private class TBoxUpdateProcessor extends VitroBackgroundThread {      
        public TBoxUpdateProcessor(String name) {
        	super(name);
        }
        
        @Override
        public void run() {  
            try {
	        	 log.debug("starting thread");
	        	 ModelUpdate mu = nextUpdate();
	        	 while (mu != null && !stopRequested) {       	   
	    		    if (mu.getOperation() == ModelUpdate.Operation.ADD) {
	    				simpleReasoner.addedTBoxStatement(mu.getStatement());	
	    		    } else if (mu.getOperation() == ModelUpdate.Operation.RETRACT) {
	    			    simpleReasoner.removedTBoxStatement(mu.getStatement());
	    		    } else {
	    			    log.error("unexpected operation value in ModelUpdate object: " + mu.getOperation());
	    		    }
	    		    mu = nextUpdate();
	        	 }	        	
            }  finally {
        	     processingUpdates = false;
        	     log.debug("ending thread");
            }
        }
    }
}