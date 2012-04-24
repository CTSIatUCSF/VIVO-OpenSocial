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

package edu.cornell.mannlib.vitro.webapp.servlet; 

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.bo.ConceptInfo;
import edu.cornell.mannlib.semservices.bo.SemanticServicesError;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService.BeanToJsonSerializer;
import edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService.ConceptSearchServiceUtils;

public class ConceptSearchServlet extends VitroHttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ConceptSearchServlet.class);
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
        VitroRequest vreq = new VitroRequest(req);

        try{
        	ServletContext ctx = vreq.getSession().getServletContext();
        	//Captures both concept list and any errors if they exist
        	ConceptInfo conceptInfo = new ConceptInfo();
    		conceptInfo.setSemanticServicesError(null);

        	//Json output should be written out
        	List<Concept> results =  null;
        	try {
        		results = ConceptSearchServiceUtils.getSearchResults(ctx, vreq);
        	} 
        	catch (Exception ex) {
        		 SemanticServicesError semanticServicesError = new SemanticServicesError(
        	               "Exception encountered ", ex.getMessage(), "fatal");
        		 log.error("An error occurred retrieving search results");
        		 conceptInfo.setSemanticServicesError(semanticServicesError);
        	}
        	conceptInfo.setConceptList(results);
        	String json = renderJson(conceptInfo);
        	json = StringUtils.replaceChars(json, "\r\t\n", "");
            PrintWriter writer = resp.getWriter();
            resp.setContentType("application/json");
            writer.write(json);
            writer.close();
        	
        }catch(Exception ex){
            log.warn(ex,ex);            
        }        
    }
    
    protected String renderJson(ConceptInfo conceptInfo) {

        JSONObject jsonObject = null;
        jsonObject = BeanToJsonSerializer.serializeToJsonObject(conceptInfo);
        log.debug(jsonObject.toString());
        return jsonObject.toString();
    }

}
