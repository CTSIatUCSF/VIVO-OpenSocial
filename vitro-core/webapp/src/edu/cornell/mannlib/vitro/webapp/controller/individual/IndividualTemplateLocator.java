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

package edu.cornell.mannlib.vitro.webapp.controller.individual;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.reasoner.SimpleReasoner;

/**
 * Figure out which Freemarker template to use when displaying this individual.
 * 
 * If one of the classes of the individual (or one of the superclasses) has a
 * custom template associated with it, use the first one that we find.
 * Otherwise, use the default template.
 * 
 * TODO examine the logic in this class. Is there anything we can get rid of?
 */
class IndividualTemplateLocator {
	private static final Log log = LogFactory
			.getLog(IndividualTemplateLocator.class);
	
    private static final String TEMPLATE_INDIVIDUAL_DEFAULT = "individual.ftl";
    
    private final VitroRequest vreq;
    private final ServletContext ctx;
    
    private final Individual individual;
	
	public IndividualTemplateLocator(VitroRequest vreq, Individual individual) {
		this.vreq = vreq;
		this.ctx = vreq.getSession().getServletContext();
		
		this.individual = individual;
	}

	// Determine whether the individual has a custom display template based on its class membership.
	// If not, return the default individual template.
	String findTemplate() {
	    
        @SuppressWarnings("unused")
        String vclassName = "unknown"; 
        String customTemplate = null;

        // First check vclass
        if( individual.getVClass() != null ){ 
            vclassName = individual.getVClass().getName();
            List<VClass> directClasses = individual.getVClasses(true);
            for (VClass vclass : directClasses) {
                customTemplate = vclass.getCustomDisplayView();
                if (customTemplate != null) {
                    if (customTemplate.length()>0) {
                        vclassName = vclass.getName(); // reset entity vclassname to name of class where a custom view; this call has side-effects
                        log.debug("Found direct class [" + vclass.getName() + "] with custom view " + customTemplate + "; resetting entity vclassName to this class");
                        break;
                    } else {
                        customTemplate = null;
                    }
                }
            }
            // If no custom template defined, check other vclasses
            if (customTemplate == null) {
                List<VClass> inferredClasses = individual.getVClasses(false);
                for (VClass vclass : inferredClasses) {
                    customTemplate = vclass.getCustomDisplayView();
                    if (customTemplate != null) {
                        if (customTemplate.length()>0) {
                            // note that NOT changing entity vclassName here yet
                            log.debug("Found inferred class [" + vclass.getName() + "] with custom view " + customTemplate);
                            break;
                        } else {
                            customTemplate = null;
                        }
                    }
                }
            }
            // If still no custom template defined, and inferencing is asynchronous (under RDB), check
            // the superclasses of the vclass for a custom template specification. 
            SimpleReasoner simpleReasoner = (SimpleReasoner) ctx.getAttribute(SimpleReasoner.class.getName());
            if (customTemplate == null && simpleReasoner != null && simpleReasoner.isABoxReasoningAsynchronous()) { 
                log.debug("Checking superclasses for custom template specification because ABox reasoning is asynchronous");
                for (VClass directVClass : directClasses) {
                    VClassDao vcDao = vreq.getWebappDaoFactory().getVClassDao();
                    List<String> superClassUris = vcDao.getAllSuperClassURIs(directVClass.getURI());
                    for (String uri : superClassUris) {
                        VClass vclass = vcDao.getVClassByURI(uri);
                        customTemplate = vclass.getCustomDisplayView();
                        if (customTemplate != null) {
                            if (customTemplate.length()>0) {
                                // note that NOT changing entity vclassName here
                                log.debug("Found superclass [" + vclass.getName() + "] with custom view " + customTemplate);
                                break;
                            } else {
                                customTemplate = null;
                            }                            
                        }                        
                    }
                }
            }
        } else if (individual.getVClassURI() != null) {
            log.debug("Individual " + individual.getURI() + " with class URI " +
                    individual.getVClassURI() + ": no class found with that URI");
        }
        
        return customTemplate != null ? customTemplate : TEMPLATE_INDIVIDUAL_DEFAULT;
        
	}


}
