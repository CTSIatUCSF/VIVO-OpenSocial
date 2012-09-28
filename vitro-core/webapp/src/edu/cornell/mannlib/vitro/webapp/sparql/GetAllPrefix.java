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
package edu.cornell.mannlib.vitro.webapp.sparql;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;

import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.OntologyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

/**
 * This servlet gets all the prefix for initizing the sparql query builder.
 * 
 * @author yuysun
 */

public class GetAllPrefix extends BaseEditController {

	private static final Log log = LogFactory.getLog(GetAllPrefix.class);

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isAuthorizedToDisplayPage(request, response,
				SimplePermission.USE_MISCELLANEOUS_PAGES.ACTIONS)) {
        	return;
		}

		VitroRequest vreq = new VitroRequest(request);	
		Map<String, String> prefixMap = getPrefixMap(vreq.getFullWebappDaoFactory());		
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String respo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		respo += "<options>";
		List<String> prefixList = new ArrayList<String>();
		prefixList.addAll(prefixMap.keySet());
		Collections.sort(prefixList, Collator.getInstance());
		for (String prefix : prefixList) {
		    respo += makeOption(prefix, prefixMap.get(prefix));
		}
		respo += "</options>";
		out.println(respo);
		out.flush();
		out.close();
	}
	
	/**
	 * Returns a map of prefixes for use in building queries.  Will manufacture a 
	 * prefix for any namespace that doesn't have an associated owl:Ontology resource
	 * with a prefix annotation  
	 * @param wadf
	 * @return map of prefix strings to namespace URIs
	 */
	private Map<String, String> getPrefixMap(WebappDaoFactory wadf) {
	    Map<String, String> prefixMap = new HashMap<String, String>();
	    
	    OntologyDao oDao = wadf.getOntologyDao();
	    for(Ontology o : oDao.getAllOntologies()) {
	        if (o.getPrefix() != null) {
	            prefixMap.put(o.getPrefix(), o.getURI());
	        }
	    }
	    
	    // add standard namespaces
	    addPrefixIfNecessary("owl", OWL.NAMESPACE, prefixMap);
	    addPrefixIfNecessary("rdf", RDF.NAMESPACE, prefixMap);
	    addPrefixIfNecessary("rdfs", RDFS.getURI(), prefixMap);
	    addPrefixIfNecessary("swrl", "http://www.w3.org/2003/11/swrl#", prefixMap);
	    addPrefixIfNecessary("swrlb", "http://www.w3.org/2003/11/swrlb#", prefixMap);
	    addPrefixIfNecessary("xsd", XSD.getURI(), prefixMap);
	    addPrefixIfNecessary("vitro", VitroVocabulary.vitroURI, prefixMap);

	    // we also need to manufacture prefixes for namespaces used by any class or 
	    // property, regardless of whether there's an associated owl:Ontology.
	    int newPrefixCount = 0;
	    List<BaseResourceBean> ontEntityList = new ArrayList<BaseResourceBean>();
	    ontEntityList.addAll(wadf.getVClassDao().getAllVclasses());
	    ontEntityList.addAll(wadf.getObjectPropertyDao().getAllObjectProperties());
	    ontEntityList.addAll(wadf.getDataPropertyDao().getAllDataProperties());
	    for (BaseResourceBean ontEntity : ontEntityList) {
	        if (!ontEntity.isAnonymous() 
	                && !prefixMap.containsValue(ontEntity.getNamespace())) {
	            newPrefixCount++;
	            prefixMap.put("p."  + Integer.toString(
	                    newPrefixCount), ontEntity.getNamespace());
	        }
	    }
	    
	    return prefixMap;
	}
	
	private void addPrefixIfNecessary(String prefix, String namespace, 
	        Map<String, String> prefixMap) {
	    if (!prefixMap.containsValue(namespace)) {
	        prefixMap.put(prefix, namespace);
	    }
	}
	
	/**
	 * Makes the markup for a prefix option
	 * @param prefix
	 * @param URI
	 * @return option string
	 */
	private String makeOption(String prefix, String URI) {
	    return "<option>" + "<key>" + prefix + "</key>"
                + "<value>" + URI + "</value>"
                + "</option>";
	}
	

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
