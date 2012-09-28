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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;

public class ShowObjectPropertyHierarchyController extends FreemarkerHttpServlet {

	private static final Log log = LogFactory.getLog(ShowObjectPropertyHierarchyController.class.getName());
	
    private static final String TEMPLATE_NAME = "siteAdmin-objectPropHierarchy.ftl";

    private int MAXDEPTH = 5;

    private ObjectPropertyDao opDao = null;
    private VClassDao vcDao = null;
    private PropertyGroupDao pgDao = null;
    
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
                displayOption = "hierarchy";
            }
            body.put("displayOption", displayOption);
            
            if ( displayOption.equals("all") ) {
                body.put("pageTitle", "All Object Properties");
            }
            else {
                body.put("pageTitle", "Object Property Hierarchy");
            }
            
            body.put("propertyType", "object");
            
            opDao = vreq.getAssertionsWebappDaoFactory().getObjectPropertyDao();
            vcDao = vreq.getAssertionsWebappDaoFactory().getVClassDao();
            pgDao = vreq.getAssertionsWebappDaoFactory().getPropertyGroupDao();

            String json = new String();

            String ontologyUri = vreq.getParameter("ontologyUri");
            String startPropertyUri = vreq.getParameter("propertyUri");

            List<ObjectProperty> roots = null;

            if (startPropertyUri != null) {
        	    roots = new LinkedList<ObjectProperty>();
        	    ObjectProperty op = opDao.getObjectPropertyByURI(startPropertyUri);
        	    if (op == null) {
        		    op = new ObjectProperty();
        		    op.setURI(startPropertyUri);
        	    }
        	    roots.add(op);
            } else {
                roots = opDao.getRootObjectProperties();
                if (roots!=null){
                    Collections.sort(roots, new ObjectPropertyAlphaComparator()); // sorts by domain public
                }
            }

            int counter = 0;
            
            if (roots != null) {
                Iterator<ObjectProperty> rootIt = roots.iterator();
                if (!rootIt.hasNext()) {
                    ObjectProperty op = new ObjectProperty();
                    op.setURI(ontologyUri+"fake");
                    String notFoundMessage = "<strong>No object properties found.</strong>"; 
                    op.setDomainPublic(notFoundMessage);
                    json += addObjectPropertyDataToResultsList(op, 0, ontologyUri, counter);
                } else {
                    while (rootIt.hasNext()) {
                        ObjectProperty root = rootIt.next();
                        if ( (ontologyUri==null) || 
                    		    ( (ontologyUri != null) 
                    		    && (root.getNamespace() != null) 
                    		    && (ontologyUri.equals(root.getNamespace())) ) ) {
                    	            json += addChildren(root, 0, ontologyUri, counter);
                    	            counter += 1;
                	    }
                    }	
                    int length = json.length();
                    if ( length > 0 ) {
                        json += " }"; 
                    }
                }
            }

            body.put("jsonTree",json);
                    
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new TemplateResponseValues(TEMPLATE_NAME, body);

    }

    private String addChildren(ObjectProperty parent, int position, String ontologyUri, int counter) {
        String details = addObjectPropertyDataToResultsList(parent, position, ontologyUri, counter);
        int length = details.length();
        String leaves = "";
        leaves += details;
        List childURIstrs = opDao.getSubPropertyURIs(parent.getURI());
        if ((childURIstrs.size()>0) && position<MAXDEPTH) {
            List childProps = new ArrayList();
            Iterator childURIstrIt = childURIstrs.iterator();
            while (childURIstrIt.hasNext()) {
                String URIstr = (String) childURIstrIt.next();
                ObjectProperty child = (ObjectProperty) opDao.getObjectPropertyByURI(URIstr);
                childProps.add(child);
            }
            Collections.sort(childProps);
            Iterator childPropIt = childProps.iterator();
            while (childPropIt.hasNext()) {
                ObjectProperty child = (ObjectProperty) childPropIt.next();
                leaves += addChildren(child, position+1, ontologyUri, counter);
                if (!childPropIt.hasNext()) {
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

    private String addObjectPropertyDataToResultsList(ObjectProperty op, int position, String ontologyUri, int counter) {
        String tempString = "";
        if (ontologyUri == null || ( (op.getNamespace()!=null) && (op.getNamespace().equals(ontologyUri)) ) ) {
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
            
            String nameStr = getDisplayLabel(op)==null ? "(no name)" : getDisplayLabel(op);
            nameStr = nameStr.replace("\"","\\\"");
            nameStr = nameStr.replace("\'","\\\'");

            try {
            	tempString +=  "\"<a href='propertyEdit?uri="+URLEncoder.encode(op.getURI(),"UTF-8") + "'>" + nameStr +"</a>\", ";
            } catch (UnsupportedEncodingException uee) {
                tempString +=  "\"" + nameStr + "\"";
            	log.error("Unsupported: URLEncoder.encode() with UTF-8");
            }
            
            tempString += "\"data\": { \"internalName\": \"" + op.getLocalNameWithPrefix() + "\", ";

            VClass tmp = null;
            
            try {
            	tempString += "\"domainVClass\": \"" + (((tmp = vcDao.getVClassByURI(op.getDomainVClassURI())) != null && (tmp.getLocalNameWithPrefix() == null)) ? "" : vcDao.getVClassByURI(op.getDomainVClassURI()).getLocalNameWithPrefix()) + "\", " ;
            } catch (NullPointerException e) {
            	tempString += "\"domainVClass\": \"\",";
            }
            try {
            	tempString += "\"rangeVClass\": \"" + (((tmp = vcDao.getVClassByURI(op.getRangeVClassURI())) != null && (tmp.getLocalNameWithPrefix() == null)) ? "" : vcDao.getVClassByURI(op.getRangeVClassURI()).getLocalNameWithPrefix()) + "\", " ;
            } catch (NullPointerException e) {
            	tempString += "\"rangeVClass\": \"\",";
            }
            if (op.getGroupURI() != null) {
                PropertyGroup pGroup = pgDao.getGroupByURI(op.getGroupURI());
                tempString += "\"group\": \"" + ((pGroup == null) ? "unknown group" : pGroup.getName()) + "\" " ;
            } else {
                tempString += "\"group\": \"unspecified\"";
            }
            tempString += "}, \"children\": [";
            
            previous_posn = position;
        }
        return tempString;
    }

    public static class ObjectPropertyAlphaComparator implements Comparator<ObjectProperty> {
        public int compare(ObjectProperty op1, ObjectProperty op2) {
        	if (op1 == null) {
        		return 1;
        	} else if (op2 == null) {
        		return -1;
        	}
        	String propLabel1 = getDisplayLabel(op1);
        	String propLabel2 = getDisplayLabel(op2);
        	if (propLabel1 == null) {
        		return 1;
        	} else if (propLabel2 == null) {
        		return -1;
        	} else {
        		return Collator.getInstance().compare( propLabel1, propLabel2 );
        	}
        }
    }
    
    /*
     * should never be null
     */
    public static String getDisplayLabel(ObjectProperty op) {
    	String domainPublic = op.getDomainPublic();
    	String displayLabel = (domainPublic != null && domainPublic.length() > 0)  
			? domainPublic 
			: op.getLocalName();
		return (displayLabel != null) ? displayLabel : "[object property]" ;
    }

}