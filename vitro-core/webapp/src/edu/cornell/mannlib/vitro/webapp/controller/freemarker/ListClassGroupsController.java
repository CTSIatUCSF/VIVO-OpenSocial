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

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.VClassGroupDao;


public class ListClassGroupsController extends FreemarkerHttpServlet {

    private static final Log log = LogFactory.getLog(ListClassGroupsController.class.getName());
    
    private static final String TEMPLATE_NAME = "siteAdmin-classHierarchy.ftl";
        
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
		return SimplePermission.EDIT_ONTOLOGY.ACTIONS;
	}
    
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {

        Map<String, Object> body = new HashMap<String, Object>();
        try {

            body.put("displayOption", "group");
            body.put("pageTitle", "Class Groups");

            VClassGroupDao dao = vreq.getFullWebappDaoFactory().getVClassGroupDao();

            List<VClassGroup> groups = dao.getPublicGroupsWithVClasses(); 

            String json = new String();
            int counter = 0;

            if (groups != null) {
            	for(VClassGroup vcg: groups) {
                    if ( counter > 0 ) {
                        json += ", ";
                    }
                    String publicName = vcg.getPublicName();
                    if ( StringUtils.isBlank(publicName) ) {
                        publicName = "(unnamed group)";
                    }           
                    publicName = publicName.replace("\"","\\\"");
                    publicName = publicName.replace("\'","\\\'");
                    try {
                        json += "{ \"name\": \"<a href='./editForm?uri="+URLEncoder.encode(vcg.getURI(),"UTF-8")+"&amp;controller=Classgroup'>"+publicName+"</a>\", ";
                    } catch (Exception e) {
                        json += "{ \"name\": \"" + publicName + "\", ";
                    }
                    Integer t;
                    
                    json += "\"data\": { \"displayRank\": \"" + (((t = Integer.valueOf(vcg.getDisplayRank())) != -1) ? t.toString() : "") + "\"}, ";
                    
                    List<VClass> classList = vcg.getVitroClassList();
                    if (classList != null && classList.size()>0) {
                        json += "\"children\": [";
                        Iterator<VClass> classIt = classList.iterator();
                        while (classIt.hasNext()) {
                            VClass vcw = classIt.next();
                            if (vcw.getName() != null && vcw.getURI() != null) {
                                try {
                                    json += "{ \"name\": \"<a href='vclassEdit?uri="+URLEncoder.encode(vcw.getURI(),"UTF-8")+"'>"+vcw.getName()+"</a>\", ";
                                } catch (Exception e) {
                                    json += "\"" + vcw.getName() + "\", ";
                                }
                            } else {
                                json += "\"\", ";
                            }

                            String shortDefStr = (vcw.getShortDef() == null) ? "" : vcw.getShortDef();
                            shortDefStr = shortDefStr.replace("\"","\\\"");
                            shortDefStr = shortDefStr.replace("\'","\\\'");
                            json += "\"data\": { \"shortDef\": \"" + shortDefStr + "\"}, \"children\": [] ";
                            if (classIt.hasNext())
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

        } catch (Throwable t) {
                t.printStackTrace();
        }
        
        return new TemplateResponseValues(TEMPLATE_NAME, body);
    }

}
