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
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;

public class ListPropertyGroupsController extends FreemarkerHttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ListPropertyGroupsController.class);
    private static final boolean WITH_PROPERTIES = true;
    private static final String TEMPLATE_NAME = "siteAdmin-objectPropHierarchy.ftl";

    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.EDIT_ONTOLOGY.ACTIONS;
	}
    
    @Override
	protected ResponseValues processRequest(VitroRequest vreq) {
    	
        Map<String, Object> body = new HashMap<String, Object>();
        try {

            body.put("displayOption", "group");
            body.put("pageTitle", "Property Groups");

            PropertyGroupDao dao = vreq.getFullWebappDaoFactory().getPropertyGroupDao();

            List<PropertyGroup> groups = dao.getPublicGroups(WITH_PROPERTIES);
        
//            Comparator<Property> comparator = new PropertySorter();

                String json = new String();
                int counter = 0;

                if (groups != null) {
                	for(PropertyGroup pg: groups) {
                        if ( counter > 0 ) {
                            json += ", ";
                        }
                        String publicName = pg.getName();
                        if ( StringUtils.isBlank(publicName) ) {
                            publicName = "(unnamed group)";
                        }           
                        publicName = publicName.replace("\"","\\\"");
                        publicName = publicName.replace("\'","\\\'");
                        try {
                            json += "{ \"name\": \"<a href='./editForm?uri="+URLEncoder.encode(pg.getURI(),"UTF-8")+"&amp;controller=Classgroup'>" + publicName + "</a>\", ";
                        } catch (Exception e) {
                            json += "{ \"name\": \"" + publicName + "\", ";
                        }
                        Integer t;

                        json += "\"data\": { \"displayRank\": \"" + (((t = Integer.valueOf(pg.getDisplayRank())) != -1) ? t.toString() : "") + "\"}, ";

                        List<Property> propertyList = pg.getPropertyList();
                        if (propertyList != null && propertyList.size()>0) {
                            json += "\"children\": [";
                            Iterator<Property> propIt = propertyList.iterator();
                            while (propIt.hasNext()) {
                                Property prop = propIt.next();
                                String controllerStr = "propertyEdit";
                                String nameStr = 
                                	   (prop.getLabel() == null) 
                                	           ? "" 
                                	           : prop.getLabel();
                                if (prop instanceof ObjectProperty) {
                                	nameStr = ((ObjectProperty) prop).getDomainPublic();
                                } else if (prop instanceof DataProperty) {
                                	controllerStr = "datapropEdit";
                                	nameStr = ((DataProperty) prop).getName();
                                }
                                if (prop.getURI() != null) {
                                    try {
                                        json += "{ \"name\": \"<a href='" + controllerStr 
                                             + "?uri="+URLEncoder.encode(prop.getURI(),"UTF-8")+"'>"+ nameStr +"</a>\", ";
                                    } catch (Exception e) {
                                        json += "\"" + nameStr + "\", ";
                                    }
                                } else {
                                    json += "\"\", ";
                                }

                                json += "\"data\": { \"shortDef\": \"\"}, \"children\": [] ";
                                if (propIt.hasNext())
                                    json += "} , ";
                                else 
                                    json += "}] ";
                            }
                        }
                        else {
                            json += "\"children\": [] ";
                        }
                        json += "} ";
                        counter += 1;
                    }
                }

                body.put("jsonTree",json);
                log.debug("json = " + json);
            } catch (Throwable t) {
                    t.printStackTrace();
            }

            return new TemplateResponseValues(TEMPLATE_NAME, body);
        }

/*    
    private class PropertySorter implements Comparator<Property> {
    	
    	private Collator coll = Collator.getInstance();
    	
    	public int compare(Property p1, Property p2) {
    		String name1 = getName(p1);
    		String name2 = getName(p2);
    		if (name1 == null && name2 != null) {
    			return 1;
    		} else if (name2 == null && name1 != null) {
    			return -1;
    		} else if (name1 == null && name2 == null) {
    			return 0;
    		}
    		return coll.compare(name1, name2);
    	}
    	
    	private String getName(Property prop) {
    		if (prop instanceof ObjectProperty) {
    			return ((ObjectProperty) prop).getDomainPublic();
    		} else if (prop instanceof DataProperty) {
    			return ((DataProperty) prop).getName();
    		} else {
    			return prop.getLabel();
    		}
    	}
    }
*/    
}
