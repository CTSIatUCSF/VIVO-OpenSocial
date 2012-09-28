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

import javax.servlet.RequestDispatcher;
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
import edu.cornell.mannlib.vitro.webapp.dao.PropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;


public class ListVClassWebappsController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ListVClassWebappsController.class.getName());
    
    private static final String TEMPLATE_NAME = "siteAdmin-classHierarchy.ftl";
        
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.EDIT_ONTOLOGY.ACTIONS;
	}
    
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();
        try {

            body.put("displayOption", "all");
            body.put("pageTitle", "All Classes");
            
            List<VClass> classes = null;

            if (vreq.getParameter("showPropertyRestrictions") != null) {
            	PropertyDao pdao = vreq.getFullWebappDaoFactory().getObjectPropertyDao();
            	classes = pdao.getClassesWithRestrictionOnProperty(vreq.getParameter("propertyURI"));
            } else {
            	VClassDao vcdao = vreq.getFullWebappDaoFactory().getVClassDao();

            	if (vreq.getParameter("iffRoot") != null) {
                    classes = vcdao.getRootClasses();
            	} else {
            		classes = vcdao.getAllVclasses();
            	}

            }
            String json = new String();
            int counter = 0;

            String ontologyURI = vreq.getParameter("ontologyUri");

            if (classes != null) {
                Collections.sort(classes);
                Iterator<VClass> classesIt = classes.iterator();
                while (classesIt.hasNext()) {
                    if ( counter > 0 ) {
                        json += ", ";
                    }
                    VClass cls = (VClass) classesIt.next();
                    if ( (ontologyURI==null) || ( (ontologyURI != null) && (cls.getNamespace()!=null) && (ontologyURI.equals(cls.getNamespace())) ) ) {
    	                if (cls.getName() != null)
    	                    try {
    	                        json += "{ \"name\": \"<a href='./vclassEdit?uri="+URLEncoder.encode(cls.getURI(),"UTF-8")+"'>"+cls.getLocalNameWithPrefix()+"</a>\", ";
    	                    } catch (Exception e) {
    	                        json += "{ \"name\": \"" + cls.getLocalNameWithPrefix() + "\", ";
    	                    }
    	                else
    	                    json += "{ \"name\": \"\"";
    	                String shortDef = (cls.getShortDef()==null) ? "" : cls.getShortDef();
    	                
    	                json += "\"data\": { \"shortDef\": \"" + shortDef + "\", ";

    	                // get group name
    	                WebappDaoFactory wadf = vreq.getFullWebappDaoFactory();
    	                VClassGroupDao groupDao= wadf.getVClassGroupDao();
    	                String groupURI = cls.getGroupURI();                
    	                String groupName = "";
    	                VClassGroup classGroup = null;
    	                if(groupURI != null) { 
    	                	classGroup = groupDao.getGroupByURI(groupURI);
    	                	if (classGroup!=null) {
    	                	    groupName = classGroup.getPublicName();
    	                	}
    	                }
                        
                        json += "\"classGroup\": \"" + groupName + "\", ";

    	                // get ontology name
    	                OntologyDao ontDao = wadf.getOntologyDao();
    	                String ontName = null;
    	                try {
    	                	Ontology ont = ontDao.getOntologyByURI(cls.getNamespace());
    	                	ontName = ont.getName();
    	                } catch (Exception e) {}
    	                ontName = (ontName == null) ? "" : ontName;
    	                
    	                json += "\"ontology\": \"" + ontName + "\"} }";
    	                
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
