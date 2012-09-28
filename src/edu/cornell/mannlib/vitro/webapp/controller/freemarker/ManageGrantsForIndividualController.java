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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;


public class ManageGrantsForIndividualController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ManageGrantsForIndividualController.class.getName());
    private VClassDao vcDao = null;
    private static final String TEMPLATE_NAME = "manageGrantsForIndividual.ftl";
    private List<String> allSubclasses;
    
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.DO_FRONT_END_EDITING.ACTIONS;
	}

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();

        String subjectUri = vreq.getParameter("subjectUri");
        
        body.put("subjectUri", subjectUri);

        if (vreq.getAssertionsWebappDaoFactory() != null) {
        	vcDao = vreq.getAssertionsWebappDaoFactory().getVClassDao();
        } else {
        	vcDao = vreq.getFullWebappDaoFactory().getVClassDao();
        }

        HashMap<String, List<Map<String,String>>>  grants = getGrants(subjectUri, vreq);
        log.debug("grants = " + grants);
        body.put("grants", grants);
        body.put("allSubclasses", allSubclasses);
        
        Individual subject = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);
        if( subject != null && subject.getName() != null ){
             body.put("subjectName", subject.getName());
        }else{
             body.put("subjectName", null);
        }
        
        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }
  
    private static String GRANT_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "PREFIX vitro: <http://vitro.mannlib.cornell.edu/ns/vitro/0.7#> \n"
        + "PREFIX afn:  <http://jena.hpl.hp.com/ARQ/function#> \n"
        + "SELECT DISTINCT ?subclass ?role (str(?label2) as ?label) ?activity ?hideThis WHERE { \n"
        + "    ?subject ?roleProp ?role . \n"
        + "    ?roleProp rdfs:subPropertyOf core:hasResearcherRole . \n"
        + "    ?role vitro:mostSpecificType ?subclass \n"
        + "    OPTIONAL { ?role core:roleRealizedIn ?activity . \n" 
        + "               ?activity rdfs:label ?label2  \n" 
        + "    } \n"
        + "    OPTIONAL { ?role core:roleContributesTo ?activity . \n" 
        + "               ?activity rdfs:label ?label2  \n" 
        + "    } \n"
        + "    OPTIONAL { ?role core:hideFromDisplay ?hideThis } \n" 
        + "} ORDER BY ?subclass ?label2";
    
       
    HashMap<String, List<Map<String,String>>>  getGrants(String subjectUri, VitroRequest vreq) {
          
        String queryStr = QueryUtils.subUriForQueryVar(GRANT_QUERY, "subject", subjectUri);
        log.debug("queryStr = " + queryStr);
        HashMap<String, List<Map<String,String>>>  subclassToGrants = new HashMap<String, List<Map<String,String>>>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                RDFNode subclassUri= soln.get("subclass");
                if ( subclassUri != null ) {
                    String subclassUriStr = soln.get("subclass").toString();
                    VClass vClass = (VClass) vcDao.getVClassByURI(subclassUriStr);
                    String subclass = ((vClass.getName() == null) ? subclassUriStr : vClass.getName());
                    if(!subclassToGrants.containsKey(subclass)) {
                        subclassToGrants.put(subclass, new ArrayList<Map<String,String>>()); //list of grant information
                    }
                    List<Map<String,String>> grantsList = subclassToGrants.get(subclass);
                    grantsList.add(QueryUtils.querySolutionToStringValueMap(soln)); 
                }       
            }
        } catch (Exception e) {
            log.error(e, e);
        }    
       
        allSubclasses = new ArrayList<String>(subclassToGrants.keySet());
        Collections.sort(allSubclasses);
        return subclassToGrants;
    }
}


