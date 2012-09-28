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

package edu.cornell.mannlib.vitro.webapp.controller.edit.listing;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VClassGroupDao;

public class AllClassGroupsListingController extends BaseEditController {

    private static final long serialVersionUID = 1L;

    @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
    	if (!isAuthorizedToDisplayPage(request, response, SimplePermission.EDIT_ONTOLOGY.ACTIONS)) {
    		return;
    	}
    	
        VitroRequest vreq = new VitroRequest(request);

        VClassGroupDao dao = vreq.getFullWebappDaoFactory().getVClassGroupDao();

        List<VClassGroup> groups = dao.getPublicGroupsWithVClasses(); 
        // uses an unfiltered dao so will see all classes

        List<String> results = new ArrayList<String>();
        results.add("XX");
        results.add("Group");
        results.add("display rank");
        results.add("");
        results.add("XX");

        if (groups != null) {
        	for(VClassGroup vcg: groups) {
                results.add("XX");
                String publicName = vcg.getPublicName();
                if ( StringUtils.isBlank(publicName) ) {
                    publicName = "(unnamed group)";
                }           
                try {
                    results.add("<a href=\"./editForm?uri="+URLEncoder.encode(vcg.getURI(),"UTF-8")+"&amp;controller=Classgroup\">"+publicName+"</a>");
                } catch (Exception e) {
                    results.add(publicName);
                }
                Integer t;
                results.add(((t = Integer.valueOf(vcg.getDisplayRank())) != -1) ? t.toString() : "");
                results.add(""); // VClassGroup doesn't yet supprt getModTime()
                results.add("XX");
                List<VClass> classList = vcg.getVitroClassList();
                if (classList != null && classList.size()>0) {
                    results.add("+");
                    results.add("XX");
                    results.add("Class");
                    results.add("example");
                    results.add("description");
                    results.add("@@entities");
                    Iterator<VClass> classIt = classList.iterator();
                    while (classIt.hasNext()) {
                        VClass vcw = classIt.next();
                        results.add("XX");
                        if (vcw.getName() != null && vcw.getURI() != null) {
                            try {
                                results.add("<a href=\"vclassEdit?uri="+URLEncoder.encode(vcw.getURI(),"UTF-8")+"\">"+vcw.getName()+"</a>");
                            } catch (Exception e) {
                                results.add(vcw.getName());
                            }
                        } else {
                            results.add("");
                        }
                        String exampleStr = (vcw.getExample() == null) ? "" : vcw.getName();
                        results.add(exampleStr);
                        String descriptionStr = (vcw.getDescription() == null) ? "" : vcw.getDescription();
                        results.add(descriptionStr);
                        if (classIt.hasNext())
                            results.add("@@entities");
                    }
                }
            }
            request.setAttribute("results",results);
        }

        request.setAttribute("columncount",new Integer(5));
        request.setAttribute("suppressquery","true");
        request.setAttribute("title","Class Groups");
        request.setAttribute("bodyJsp", Controllers.HORIZONTAL_JSP);
        request.setAttribute("horizontalJspAddButtonUrl", Controllers.RETRY_URL);
        request.setAttribute("horizontalJspAddButtonText", "Add new class group");
        request.setAttribute("horizontalJspAddButtonControllerParam", "Classgroup");
        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        try {
            rd.forward(request,response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
