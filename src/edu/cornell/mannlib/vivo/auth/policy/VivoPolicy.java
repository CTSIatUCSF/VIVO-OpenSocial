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

package edu.cornell.mannlib.vivo.auth.policy;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.policy.BasicPolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ServletPolicyList;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.DefaultInconclusivePolicy;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AddObjectPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropObjectPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditObjPropStmt;

public class VivoPolicy extends DefaultInconclusivePolicy{
	private static final Log log = LogFactory.getLog(VivoPolicy.class);

	private static final String CORE = "http://vivoweb.org/ontology/core#";
	private static final String PUB_TO_AUTHORSHIP =  CORE + "informationResourceInAuthorship"; 
	private static final String PERSON_TO_AUTHORSHIP =  CORE + "authorInAuthorship";
	private static final String AUTHORSHIP_TO_PERSON = CORE + "linkedAuthor";
	private static final String AUTHORSHIP_TO_PUB = CORE + "linkedInformationResource";
	private static final String INDIVIDUAL_TO_WEBPAGE = CORE + "webpage";
	private static final String WEBPAGE_TO_INDIVIDUAL = CORE + "webpageOf";
	private static final String HAS_RESEARCH_AREA = CORE + "hasResearchArea";
	private static final String HAS_SUBJECT_AREA = CORE + "hasSubjectArea";
	private static final String RESEARCH_AREA_OF = CORE + "researchAreaOf";
	private static final String SUBJECT_AREA_OF = CORE + "subjectAreaOf";

	
	@Override
	public PolicyDecision isAuthorized(IdentifierBundle whoToAuth,
			RequestedAction whatToAuth) {

		if( whatToAuth instanceof DropObjectPropStmt ){
			DropObjectPropStmt dops = (DropObjectPropStmt)whatToAuth;
			
			String predicateUri = dops.getUriOfPredicate();
			
			/* Do not offer the user the option to delete so they will use the custom form instead */ 
			/* see issue NIHVIVO-739 */
			if( PUB_TO_AUTHORSHIP.equals( predicateUri )) {
				return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
						"Use the custom edit form for core:informationResourceInAuthorship");
			}
			
			else if( PERSON_TO_AUTHORSHIP.equals( predicateUri )) {
				return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
						"Use the custom edit form for core:authorInAuthorship");
			}
			 
			else if( AUTHORSHIP_TO_PERSON.equals( predicateUri )){
				return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
					"Use the custom edit form on information resource to edit authors.");
			}
			
			else if( AUTHORSHIP_TO_PUB.equals( predicateUri )){
				return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
					"Use the custom edit form on information resource to edit authors.");
			}	
			
			else if ( INDIVIDUAL_TO_WEBPAGE.equals( predicateUri ) || WEBPAGE_TO_INDIVIDUAL.equals( predicateUri )) {
                return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
                    "Use the custom edit form for core:webpage");			    
			} 
			else if ( HAS_RESEARCH_AREA.equals( predicateUri ) || RESEARCH_AREA_OF.equals( predicateUri )) {
                return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
                "Use the custom edit form for core:hasResearchArea");			    
			}
			else if ( HAS_SUBJECT_AREA.equals( predicateUri ) || SUBJECT_AREA_OF.equals( predicateUri )) {
                return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
                    "Use the custom edit form for core:hasSubjectArea");			    
			}
		}
		else if( whatToAuth instanceof AddObjectPropStmt ){
 
			AddObjectPropStmt aops = (AddObjectPropStmt)whatToAuth;
	        
			String predicateUri = aops.getUriOfPredicate();
			
			if( AUTHORSHIP_TO_PERSON.equals( predicateUri )){
				return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
					"Use the custom edit form on information resource to edit authors.");
			}
			
			else if( AUTHORSHIP_TO_PUB.equals( predicateUri )){
				return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
					"Use the custom edit form on information resource to edit authors.");
			}	
			
            else if( WEBPAGE_TO_INDIVIDUAL.equals( predicateUri )){
                return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
                    "Use the custom edit form on an individual to edit webpages.");
            }   
		}
		
		else if (whatToAuth instanceof EditObjPropStmt ) {
		    
		    EditObjPropStmt aops = (EditObjPropStmt)whatToAuth;
	            
	        String predicateUri = aops.getUriOfPredicate();
	        
	        if( WEBPAGE_TO_INDIVIDUAL.equals( predicateUri )){	        
	            return new BasicPolicyDecision(Authorization.UNAUTHORIZED, 
                    "Use the custom edit form on an individual to edit webpages.");	
	        }
		}
		
		return super.isAuthorized(whoToAuth, whatToAuth);						
	}

	// ----------------------------------------------------------------------
	// setup
	// ----------------------------------------------------------------------
	
	public static class Setup implements ServletContextListener{
		/**
		 * Make a policy and add it to the ServletContext. The policy doesn't
		 * use any Identifiers, so no need to add an IdentifierBundleFactory.
		 */
		@Override
		public void contextInitialized(ServletContextEvent sce) {		
		   log.debug("Setting up VivoPolicy");
	       ServletPolicyList.addPolicy(sce.getServletContext(), new VivoPolicy());
		}

		@Override
		public void contextDestroyed(ServletContextEvent arg0) {
			//do nothing		
		}

	}

}
