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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyInstance;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.OntologyDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyInstanceDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class ListPropertyWebappsController extends FreemarkerHttpServlet {
    private static Log log = LogFactory.getLog( ListPropertyWebappsController.class );

    private static final String TEMPLATE_NAME = "siteAdmin-objectPropHierarchy.ftl";
        
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.EDIT_ONTOLOGY.ACTIONS;
	}
    
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();
        try {

            body.put("displayOption", "all");
            body.put("pageTitle", "All Object Properties");
            body.put("propertyType", "object");

            String noResultsMsgStr = "No object properties found";

            String ontologyUri = vreq.getParameter("ontologyUri");

            ObjectPropertyDao dao = vreq.getFullWebappDaoFactory().getObjectPropertyDao();
            PropertyInstanceDao piDao = vreq.getFullWebappDaoFactory().getPropertyInstanceDao();
            VClassDao vcDao = vreq.getFullWebappDaoFactory().getVClassDao();
            PropertyGroupDao pgDao = vreq.getFullWebappDaoFactory().getPropertyGroupDao();

            String vclassURI = vreq.getParameter("vclassUri");
        
            List props = new ArrayList();
            if (vreq.getParameter("propsForClass") != null) {
                noResultsMsgStr = "There are no object properties that apply to this class.";
            
                // incomplete list of classes to check, but better than before
                List<String> superclassURIs = vcDao.getAllSuperClassURIs(vclassURI);
                superclassURIs.add(vclassURI);
                superclassURIs.addAll(vcDao.getEquivalentClassURIs(vclassURI));
            
                Map<String, PropertyInstance> propInstMap = new HashMap<String, PropertyInstance>();
                for (String classURI : superclassURIs) {
            	    Collection<PropertyInstance> propInsts = piDao.getAllPropInstByVClass(classURI);
            	    for (PropertyInstance propInst : propInsts) {
            		    propInstMap.put(propInst.getPropertyURI(), propInst);
            	    }
                }
                List<PropertyInstance> propInsts = new ArrayList<PropertyInstance>();
                propInsts.addAll(propInstMap.values());
                Collections.sort(propInsts);
            
                Iterator propInstIt = propInsts.iterator();
                HashSet propURIs = new HashSet();
                while (propInstIt.hasNext()) {
                    PropertyInstance pi = (PropertyInstance) propInstIt.next();
                    if (!(propURIs.contains(pi.getPropertyURI()))) {
                        propURIs.add(pi.getPropertyURI());
                        ObjectProperty prop = (ObjectProperty) dao.getObjectPropertyByURI(pi.getPropertyURI());
                        if (prop != null) {
                            props.add(prop);
                        }
                    }
                }
            } else {
                props = (vreq.getParameter("iffRoot")!=null)
                    ? dao.getRootObjectProperties()
                    : dao.getAllObjectProperties();
            }
        
            OntologyDao oDao = vreq.getFullWebappDaoFactory().getOntologyDao();
            HashMap<String,String> ontologyHash = new HashMap<String,String>();

            Iterator propIt = props.iterator();
            List<ObjectProperty> scratch = new ArrayList();
            while (propIt.hasNext()) {
                ObjectProperty p = (ObjectProperty) propIt.next();
                if (p.getNamespace()!=null) {
                    if( !ontologyHash.containsKey( p.getNamespace() )){
                        Ontology o = (Ontology)oDao.getOntologyByURI(p.getNamespace());
                        if (o==null) {
                            if (!VitroVocabulary.vitroURI.equals(p.getNamespace())) {
                                log.debug("doGet(): no ontology object found for the namespace "+p.getNamespace());
                            }
                        } else {
                            ontologyHash.put(p.getNamespace(), o.getName() == null ? p.getNamespace() : o.getName());
                        }
                    }
                    if (ontologyUri != null && p.getNamespace().equals(ontologyUri)) {
                        scratch.add(p);
                    }
                }
            }

            if (ontologyUri != null) {
                props = scratch;
            }

            if (props != null) {
        	    Collections.sort(props, new ShowObjectPropertyHierarchyController.ObjectPropertyAlphaComparator());
            }

            String json = new String();
            int counter = 0;

            if (props != null) {
                if (props.size()==0) {
                    json = "{ \"name\": \"" + noResultsMsgStr + "\" }";
                } else {
                    Iterator propsIt = props.iterator();
                    while (propsIt.hasNext()) {
                        if ( counter > 0 ) {
                            json += ", ";
                        }
                        ObjectProperty prop = (ObjectProperty) propsIt.next();
                    
                        String propNameStr = ShowObjectPropertyHierarchyController.getDisplayLabel(prop);
                        try {
                            json += "{ \"name\": \"<a href='./propertyEdit?uri="+URLEncoder.encode(prop.getURI(),"UTF-8")+"'>" 
                                 + propNameStr + "</a>\", "; 
                         } catch (Exception e) {
                             json += "{ \"name\": \"" + propNameStr + "\", "; 
                         }
                    
                         json += "\"data\": { \"internalName\": \"" + prop.getLocalNameWithPrefix() + "\", "; 
                    
                         VClass vc = (prop.getDomainVClassURI() != null) ? vcDao.getVClassByURI(prop.getDomainVClassURI()) : null;
                         String domainStr = (vc != null) ? vc.getLocalNameWithPrefix() : ""; 
                         json += "\"domainVClass\": \"" + domainStr + "\", " ;
                    
                         vc = (prop.getRangeVClassURI() != null) ? vcDao.getVClassByURI(prop.getRangeVClassURI()) : null;
                         String rangeStr = (vc != null) ? vc.getLocalNameWithPrefix() : ""; 
                         json += "\"rangeVClass\": \"" + rangeStr + "\", " ; 
                    
                         if (prop.getGroupURI() != null) {
                             PropertyGroup pGroup = pgDao.getGroupByURI(prop.getGroupURI());
                             json += "\"group\": \"" + ((pGroup == null) ? "unknown group" : pGroup.getName()) + "\" } } " ; 
                         } else {
                             json += "\"group\": \"unspecified\" } }" ;
                         }
                         counter += 1;
                     }
                 }
                 body.put("jsonTree",json);
             }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }
}
