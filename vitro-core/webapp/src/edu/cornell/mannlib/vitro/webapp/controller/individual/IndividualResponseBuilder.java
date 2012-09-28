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

import java.io.IOException;
import java.lang.Integer;
import java.lang.String;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.web.beanswrappers.ReadOnlyBeansWrapper;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModel;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individuallist.ListedIndividual;
import edu.ucsf.vitro.opensocial.OpenSocialManager;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * We have determined that the request is for a normal Individual, and needs an
 * HTML response. Assemble the information for that response.
 * 
 * TODO clean this up.
 */
class IndividualResponseBuilder {
	private static final Log log = LogFactory
			.getLog(IndividualResponseBuilder.class);
	
    private static final Map<String, String> namespaces = new HashMap<String, String>() {{
        put("display", VitroVocabulary.DISPLAY);
        put("vitro", VitroVocabulary.vitroURI);
        put("vitroPublic", VitroVocabulary.VITRO_PUBLIC);
    }};

	private final VitroRequest vreq;
	private final WebappDaoFactory wadf;
	private final IndividualDao iDao;
	private final ObjectPropertyDao opDao;

	private final Individual individual;
	
	public IndividualResponseBuilder(VitroRequest vreq, Individual individual) {
		this.vreq = vreq;
		this.wadf = vreq.getWebappDaoFactory();
		this.iDao = wadf.getIndividualDao();
		this.opDao = wadf.getObjectPropertyDao();

		this.individual = individual;
	}

	ResponseValues assembleResponse() throws TemplateModelException {
		Map<String, Object> body = new HashMap<String, Object>();
		
		body.put("title", individual.getName());            
		body.put("relatedSubject", getRelatedSubject());
		body.put("namespaces", namespaces);
		body.put("temporalVisualizationEnabled", getTemporalVisualizationFlag());
		body.put("verbosePropertySwitch", getVerbosePropertyValues());
		
		IndividualTemplateModel itm = getIndividualTemplateModel(individual);
		/* We need to expose non-getters in displaying the individual's property list, 
		 * since it requires calls to methods with parameters.
		 * This is still safe, because we are only putting BaseTemplateModel objects
		 * into the data model: no real data can be modified. 
		 */
		// body.put("individual", wrap(itm, BeansWrapper.EXPOSE_SAFE));
	    body.put("labelCount", getLabelCount(itm.getUri(), vreq));
		body.put("individual", wrap(itm, new ReadOnlyBeansWrapper()));
		
		body.put("headContent", getRdfLinkTag(itm));	       
		
		//If special values required for individuals like menu, include values in template values
		body.putAll(getSpecialEditingValues());
		
        // VIVO OpenSocial Extension by UCSF
        try {
	        OpenSocialManager openSocialManager = new OpenSocialManager(vreq, 
	        		itm.isEditable() ? "individual-EDIT-MODE" : "individual", itm.isEditable());
	        openSocialManager.setPubsubData(OpenSocialManager.JSON_PERSONID_CHANNEL, 
	        		OpenSocialManager.buildJSONPersonIds(individual, "1 person found"));
	        body.put(OpenSocialManager.TAG_NAME, openSocialManager);
	        if (openSocialManager.isVisible()) {
	        	body.put("bodyOnload", "my.init();");
	        }
        } catch (JSONException e) {
            log.error("JSONException in doTemplate()", e);
        } catch (IOException e) {
        	log.error("IOException in doTemplate()", e);
        } catch (SQLException e) {
            log.error("SQLException in doTemplate()", e);
        }	               
        
		String template = new IndividualTemplateLocator(vreq, individual).findTemplate();
		        
		return new TemplateResponseValues(template, body);
	}

	/**
	 * Check if a "relatedSubjectUri" parameter has been supplied, and, if so,
	 * retrieve the related individual.
	 * 
	 * Some individuals make little sense standing alone and should be displayed
	 * in the context of their relationship to another.
	 */
    private Map<String, Object> getRelatedSubject() {
        Map<String, Object> map = null;
        
        String relatedSubjectUri = vreq.getParameter("relatedSubjectUri"); 
        if (relatedSubjectUri != null) {
            Individual relatedSubjectInd = iDao.getIndividualByURI(relatedSubjectUri);
            if (relatedSubjectInd != null) {
                map = new HashMap<String, Object>();
                map.put("name", relatedSubjectInd.getName());

                // TODO find out which of these values is the correct one
                map.put("url", UrlBuilder.getIndividualProfileUrl(relatedSubjectInd, vreq));
                map.put("url", (new ListedIndividual(relatedSubjectInd, vreq)).getProfileUrl());
                
                String relatingPredicateUri = vreq.getParameter("relatingPredicateUri");
                if (relatingPredicateUri != null) {
                    ObjectProperty relatingPredicateProp = opDao.getObjectPropertyByURI(relatingPredicateUri);
                    if (relatingPredicateProp != null) {
                        map.put("relatingPredicateDomainPublic", relatingPredicateProp.getDomainPublic());
                    }
                }
            }
        }
        return map;
    }
    
	private boolean getTemporalVisualizationFlag() {
		String property = ConfigurationProperties.getBean(vreq).getProperty(
				"visualization.temporal");
		return "enabled".equals(property);
	}

    private Map<String, Object> getVerbosePropertyValues() {
        Map<String, Object> map = null;
        
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.SEE_VERBOSE_PROPERTY_INFORMATION.ACTIONS)) {
            // Get current verbose property display value
            String verbose = vreq.getParameter("verbose");
            Boolean verboseValue;
            // If the form was submitted, get that value
            if (verbose != null) {
                verboseValue = "true".equals(verbose);
            // If form not submitted, get the session value
            } else {
                Boolean verbosePropertyDisplayValueInSession = (Boolean) vreq.getSession().getAttribute("verbosePropertyDisplay"); 
                // True if session value is true, otherwise (session value is false or null) false
                verboseValue = Boolean.TRUE.equals(verbosePropertyDisplayValueInSession);           
            }
            vreq.getSession().setAttribute("verbosePropertyDisplay", verboseValue);
            
            map = new HashMap<String, Object>();
            map.put("currentValue", verboseValue);

            /* Factors contributing to switching from a form to an anchor element:
               - Can't use GET with a query string on the action unless there is no form data, since
                 the form data is appended to the action with a "?", so there can't already be a query string
                 on it.
               - The browser (at least Firefox) does not submit a form that has no form data.
               - Some browsers might strip the query string off the form action of a POST - though 
                 probably they shouldn't, because the HTML spec allows a full URI as a form action.
               - Given these three, the only reliable solution is to dynamically create hidden inputs
                 for the query parameters. 
               - Much simpler is to just create an anchor element. This has the added advantage that the
                 browser doesn't ask to resend the form data when reloading the page.
             */
            String url = vreq.getRequestURI() + "?verbose=" + !verboseValue;
            // Append request query string, except for current verbose value, to url
            String queryString = vreq.getQueryString();
            if (queryString != null) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    if (! param.startsWith("verbose=")) {
                        url += "&" + param;
                    }
                }
            }
            map.put("url", url);            
        } else {
            vreq.getSession().setAttribute("verbosePropertyDisplay", false);
        }
        
        return map;
    }
    
	private IndividualTemplateModel getIndividualTemplateModel(
			Individual individual) {
		//individual.sortForDisplay();
		return new IndividualTemplateModel(individual, vreq);
	}
		
    private TemplateModel wrap(Object obj, BeansWrapper wrapper) throws TemplateModelException {
        return wrapper.wrap(obj);
    }

    private String getRdfLinkTag(IndividualTemplateModel itm) {
        String linkTag = null;
        String linkedDataUrl = itm.getRdfUrl();
        if (linkedDataUrl != null) {
            linkTag = "<link rel=\"alternate\" type=\"application/rdf+xml\" href=\"" +
                          linkedDataUrl + "\" /> ";
        }
        return linkTag;
    }
    
    //Get special values for cases such as Menu Management editing
    private Map<String, Object> getSpecialEditingValues() {
        Map<String, Object> map = new HashMap<String, Object>();
        
    	if(vreq.getAttribute(VitroRequest.SPECIAL_WRITE_MODEL) != null) {
    		map.put("reorderUrl", UrlBuilder.getUrl(DisplayVocabulary.REORDER_MENU_URL));
    	}
    	
    	return map;
    }

    private static String LABEL_COUNT_QUERY = ""
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "SELECT ( str(COUNT(?label)) AS ?labelCount ) WHERE { \n"
        + "    ?subject rdfs:label ?label \n"
        + "    FILTER isLiteral(?label) \n"
        + "}" ;
           
    private static Integer getLabelCount(String subjectUri, VitroRequest vreq) {
          
        String queryStr = QueryUtils.subUriForQueryVar(LABEL_COUNT_QUERY, "subject", subjectUri);
        log.debug("queryStr = " + queryStr);
        int theCount = 0;
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            if (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                String countStr = soln.get("labelCount").toString();
                theCount = Integer.parseInt(countStr);
            }
        } catch (Exception e) {
            log.error(e, e);
        }    
        return theCount;
    }
}
