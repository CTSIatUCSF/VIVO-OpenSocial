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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.vocabulary.OWL;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.OntologyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;


public class ShowClassHierarchyController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ShowClassHierarchyController.class.getName());
    
    private static final String TEMPLATE_NAME = "siteAdmin-classHierarchy.ftl";
	private int MAXDEPTH = 7;

    private VClassDao vcDao = null;
    
    private int previous_posn = 0;
    
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.EDIT_ONTOLOGY.ACTIONS;
	}
    
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();
        try {
            String displayOption = "";
            
            if ( vreq.getParameter("displayOption") != null ) {
                displayOption = vreq.getParameter("displayOption");
            }
            else {
                displayOption = "asserted";
            }

            body.put("displayOption", displayOption);
            boolean inferred = ( displayOption.equals("inferred") );
            if ( inferred ) {
                body.put("pageTitle", "Inferred Class Hierarchy");
            }
            else {
                body.put("pageTitle", "Asserted Class Hierarchy");
            }

            if (vreq.getAssertionsWebappDaoFactory() != null && !inferred) {
            	vcDao = vreq.getAssertionsWebappDaoFactory().getVClassDao();
            } else {
            	vcDao = vreq.getFullWebappDaoFactory().getVClassDao();
            }
            String json = new String();

            String ontologyUri = vreq.getParameter("ontologyUri");
            String startClassUri = vreq.getParameter("vclassUri");

            List<VClass> roots = null;

            if (ontologyUri != null) {
                roots = vcDao.getOntologyRootClasses(ontologyUri);
            } else if (startClassUri != null) {
            	roots = new LinkedList<VClass>();
            	roots.add(vcDao.getVClassByURI(startClassUri));
            } else {    	
           		roots = vcDao.getRootClasses();
            }

            if (roots.isEmpty()) {
            	roots = new LinkedList<VClass>();
            	roots.add(vreq.getFullWebappDaoFactory().getVClassDao()
            			.getTopConcept());
            }
            Collections.sort(roots);
            int counter = 0;

            Iterator rootIt = roots.iterator();
            if (!rootIt.hasNext()) {
                VClass vcw = new VClass();
                vcw.setName("<strong>No classes found.</strong>");
                json += addVClassDataToResultsList(vreq.getFullWebappDaoFactory(), vcw,0,ontologyUri,counter);
            } else {
                while (rootIt.hasNext()) {
                    VClass root = (VClass) rootIt.next();
    	            if (root != null) {
    	                json += addChildren(vreq.getFullWebappDaoFactory(), root, 0, ontologyUri,counter);
    	                counter += 1;
                    }
                }
                int length = json.length();
                if ( length > 0 ) {
                    json += " }"; 
                }
            }
            body.put("jsonTree",json);
 
        } catch (Throwable t) {
                t.printStackTrace();
        }
        
        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }

    private String addChildren(WebappDaoFactory wadf, VClass parent, int position, String ontologyUri, int counter) {
        String rowElts = addVClassDataToResultsList(wadf, parent, position, ontologyUri, counter);
    	int childShift = (rowElts.length() > 0) ? 1 : 0;  // if addVClassDataToResultsList filtered out the result, don't shift the children over 
        int length = rowElts.length();
        String leaves = "";
        leaves += rowElts;
        List childURIstrs = vcDao.getSubClassURIs(parent.getURI());
        if ((childURIstrs.size()>0) && position<MAXDEPTH) {
            List childClasses = new ArrayList();
            Iterator childURIstrIt = childURIstrs.iterator();
            while (childURIstrIt.hasNext()) {
                String URIstr = (String) childURIstrIt.next();
                try {
                    VClass child = (VClass) vcDao.getVClassByURI(URIstr);
                    if (!child.getURI().equals(OWL.Nothing.getURI())) {
                    	childClasses.add(child);
                    }
                } catch (Exception e) {}
            }
            Collections.sort(childClasses);
            Iterator childClassIt = childClasses.iterator();
            while (childClassIt.hasNext()) {
                VClass child = (VClass) childClassIt.next();
                leaves += addChildren(wadf, child, position + childShift, ontologyUri, counter);
                if (!childClassIt.hasNext()) {
                    if ( ontologyUri == null ) {
                        leaves += " }] ";
                    }
                    else if ( ontologyUri != null && length > 0 ) {
                        // need this for when we show the classes associated with an ontology
                        String ending = leaves.substring(leaves.length() - 2, leaves.length());
                        if ( ending.equals("] ") ) {
                            leaves += "}]";
                        }
                        else if  ( ending.equals(" [") ){
                            leaves += "] ";
                        }
                        else {
                            leaves += "}]";
                        }
                    }
                }
            }
        }
        else {
            if ( ontologyUri == null ) {
                 leaves += "] ";
            }
            else if ( ontologyUri != null && length > 0 ) {
                 leaves += "] ";
            }
        }
        return leaves;
    }

    private String addVClassDataToResultsList(WebappDaoFactory wadf, VClass vcw, int position, String ontologyUri, int counter) {
        String tempString = "";
        if (ontologyUri == null || ( (vcw.getNamespace()!=null) && (vcw.getNamespace().equals(ontologyUri)) ) ) {
            // first if statement ensures that the first class begins with correct format
            if ( counter < 1 && position < 1 ) {
                 tempString += "{ \"name\": ";
            }
            else if ( position == previous_posn ) {
                        tempString += "}, { \"name\": ";
            } 
            else if ( position > previous_posn ) {
                tempString += " { \"name\": ";
            }
            else if ( position < previous_posn ) {
                tempString += "}, { \"name\": ";
            }

            try {
                tempString +=  "\"<a href='vclassEdit?uri="+URLEncoder.encode(vcw.getURI(),"UTF-8")+"'>"+vcw.getLocalNameWithPrefix()+"</a>\", ";
            } catch (Exception e) {
                 tempString += "\" " + ((vcw.getLocalNameWithPrefix() == null) ? "" : vcw.getLocalNameWithPrefix()) + "\", ";
            }

            String shortDef = ((vcw.getShortDef() == null) ? "" : vcw.getShortDef()) ;
            shortDef = shortDef.replace("\"","\\\"");
            shortDef = shortDef.replace("\'","\\\'");
            tempString += "\"data\": { \"shortDef\": \"" + shortDef + "\", ";

            // Get group name if it exists
            VClassGroupDao groupDao= wadf.getVClassGroupDao();
            String groupURI = vcw.getGroupURI();
            String groupName = null;
            VClassGroup classGroup = null;
            if(groupURI != null) { 
            	classGroup = groupDao.getGroupByURI(groupURI);
            	if (classGroup != null) {
            		groupName = classGroup.getPublicName();
            	}
            }
            tempString += "\"classGroup\": \"" + ((groupName == null) ? "" : groupName) + "\", ";
            // Get ontology name
			String ontName = null;
			try {
            	OntologyDao ontDao = wadf.getOntologyDao();
            	Ontology ont = ontDao.getOntologyByURI(vcw.getNamespace());
            	ontName = ont.getName();
			} catch (Exception e) {}

            tempString += "\"ontology\": \"" + ((ontName == null) ? "" : ontName) + "\"}, \"children\": [";

            previous_posn = position;
        }
        return tempString;
    }

}
