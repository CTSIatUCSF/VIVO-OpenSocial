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
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.datatypes.RDFDatatype ;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;


public class ManageLabelsForIndividualController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ManageLabelsForIndividualController.class.getName());
    private static final String TEMPLATE_NAME = "manageLabelsForIndividual.ftl";
    
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.DO_FRONT_END_EDITING.ACTIONS;
	}

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();

        String subjectUri = vreq.getParameter("subjectUri");
        
        body.put("subjectUri", subjectUri);


        ArrayList<Literal> labels = getLabels(subjectUri, vreq);
        log.debug("labels = " + labels) ;
        body.put("labels", labels);
        
        Individual subject = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(subjectUri);
        if( subject != null && subject.getName() != null ){
             body.put("subjectName", subject.getName());
        }else{
             body.put("subjectName", null);
        }

        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }
  
    
    private static String LABEL_QUERY = ""
        + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
        + "SELECT DISTINCT ?label WHERE { \n"
        + "    ?subject rdfs:label ?label \n"
        + "} ORDER BY ?label";
    
       
    ArrayList<Literal>  getLabels(String subjectUri, VitroRequest vreq) {
          
        String queryStr = QueryUtils.subUriForQueryVar(LABEL_QUERY, "subject", subjectUri);
        log.debug("queryStr = " + queryStr);

        ArrayList<Literal>  labels = new ArrayList<Literal>();
        try {
            ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                Literal nodeLiteral = soln.get("label").asLiteral();
                labels.add(nodeLiteral); 


            }
        } catch (Exception e) {
            log.error(e, e);
        }    
       
        return labels;
    }
}


