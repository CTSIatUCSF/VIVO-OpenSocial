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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.reasoner.SimpleReasoner;

public class SimpleReasonerRecomputeController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(
            SimpleReasonerRecomputeController.class);
    
    private static final String RECOMPUTE_INFERENCES_FTL = "recomputeInferences.ftl";
    
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
    	return SimplePermission.USE_MISCELLANEOUS_ADMIN_PAGES.ACTIONS;
	}

	protected ResponseValues processRequest(VitroRequest vreq) { 
        Map<String, Object> body = new HashMap<String, Object>();
        
        String messageStr = "";
        try {
        	
        	Object sr = getServletContext().getAttribute(SimpleReasoner.class.getName());
        	
            if (!(sr instanceof SimpleReasoner)) {
                messageStr = "No SimpleReasoner has been set up.";
                
            } else {
                SimpleReasoner simpleReasoner = (SimpleReasoner) sr;
                if (simpleReasoner.isABoxReasoningAsynchronous()) {
                    messageStr = "Reasoning is currently in asynchronous mode so a recompute cannot be started. Please try again later.";
                } else if (simpleReasoner.isRecomputing()) {
                        messageStr = 
                            "The system is currently in the process of " +
                            "recomputing inferences.";
                } else {
                    String submit = (String)vreq.getParameter("submit");
                    if (submit != null) {
                        new Thread(new Recomputer((simpleReasoner))).start();
                        messageStr = "Recompute of inferences started. See vivo log for further details.";                       
                    } else {
                        body.put("formAction", UrlBuilder.getUrl("/RecomputeInferences"));
                    } 
                }
            }
            
        } catch (Exception e) {
            log.error("Error recomputing inferences with SimpleReasoner", e);
            body.put("errorMessage", 
                    "There was an error while recomputing inferences: " + 
                    e.getMessage());
          return new ExceptionResponseValues(
            RECOMPUTE_INFERENCES_FTL, body, e);  
        }
        
        body.put("message", messageStr); 
        return new TemplateResponseValues(RECOMPUTE_INFERENCES_FTL, body);
    }
    
    private class Recomputer implements Runnable {
        
        private SimpleReasoner simpleReasoner;
        
        public Recomputer(SimpleReasoner simpleReasoner) {
            this.simpleReasoner = simpleReasoner;
        }
        
        public void run() {
            simpleReasoner.recompute();
        }
        
    }
    
}
