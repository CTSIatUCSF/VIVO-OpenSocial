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
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Datatype;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.DatatypeDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class ListDatatypePropertiesController extends FreemarkerHttpServlet {

    private static Log log = LogFactory.getLog( ListDatatypePropertiesController.class );

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
            body.put("pageTitle", "All Data Properties");
            body.put("propertyType", "data");

            String noResultsMsgStr = "No data properties found";

            String ontologyUri = vreq.getParameter("ontologyUri");

            DataPropertyDao dao = vreq.getFullWebappDaoFactory().getDataPropertyDao();
            VClassDao vcDao = vreq.getFullWebappDaoFactory().getVClassDao();
            DatatypeDao dDao = vreq.getFullWebappDaoFactory().getDatatypeDao();
            PropertyGroupDao pgDao = vreq.getFullWebappDaoFactory().getPropertyGroupDao();

            List<DataProperty> props = new ArrayList<DataProperty>();
            if (vreq.getParameter("propsForClass") != null) {
                noResultsMsgStr = "There are no data properties that apply to this class.";
                Collection <DataProperty> dataProps = dao.getDataPropertiesForVClass(vreq.getParameter("vclassUri"));
                Iterator<DataProperty> dataPropIt = dataProps.iterator();
                HashSet<String> propURIs = new HashSet<String>();
                while (dataPropIt.hasNext()) {
                    DataProperty dp = dataPropIt.next();
                    if (!(propURIs.contains(dp.getURI()))) {
                        propURIs.add(dp.getURI());
                        DataProperty prop = dao.getDataPropertyByURI(dp.getURI());
                        if (prop != null) {
                            props.add(prop);
                        }
                    }
                }
            } else {
        	    props = dao.getAllDataProperties();
            }

            if (ontologyUri != null) {
                List<DataProperty> scratch = new ArrayList<DataProperty>();
                for (DataProperty p: props) {
                    if (p.getNamespace().equals(ontologyUri)) {
                        scratch.add(p);
                    }
                }
                props = scratch;
            }

            if (props != null) {
        	    Collections.sort(props);
            }

            String json = new String();
            int counter = 0;

            if (props != null) {
                if (props.size()==0) {
                    json = "{ \"name\": \"" + noResultsMsgStr + "\" }";
                } else {
            	    for (DataProperty prop: props) {
                        if ( counter > 0 ) {
                            json += ", ";
                        }
                        
                        String nameStr = prop.getPublicName()==null ? prop.getName()==null ? prop.getURI()==null ? "(no name)" : prop.getURI() : prop.getName() : prop.getPublicName();
                        try {
                            json += "{ \"name\": \"<a href='datapropEdit?uri="+URLEncoder.encode(prop.getURI(),"UTF-8")+"'>" + nameStr + "</a>\", "; 
                        } catch (Exception e) {
                            json += "{ \"name\": \"" + nameStr + "\", ";
                        }
                        
                        json += "\"data\": { \"internalName\": \"" + prop.getLocalNameWithPrefix() + "\", ";
                        
/*                        VClass vc = null;
                        String domainStr="";
                        if (prop.getDomainClassURI() != null) {
                            vc = vcDao.getVClassByURI(prop.getDomainClassURI());
                            if (vc != null) {
                                try {
                                    domainStr="<a href=\"vclassEdit?uri="+URLEncoder.encode(prop.getDomainClassURI(),"UTF-8")+"\">"+vc.getName()+"</a>";
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
*/                        
                        VClass vc = (prop.getDomainClassURI() != null) ? vcDao.getVClassByURI(prop.getDomainClassURI()) : null;
                        String domainStr = (vc != null) ? vc.getLocalNameWithPrefix() : ""; 
                        json += "\"domainVClass\": \"" + domainStr + "\", " ;

                        Datatype rangeDatatype = dDao.getDatatypeByURI(prop.getRangeDatatypeURI());
                        String rangeDatatypeStr = (rangeDatatype==null)?prop.getRangeDatatypeURI():rangeDatatype.getName();
                        json += "\"rangeVClass\": \"" + rangeDatatypeStr + "\", " ; 

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
