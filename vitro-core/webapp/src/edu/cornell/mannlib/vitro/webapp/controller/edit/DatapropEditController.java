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

package edu.cornell.mannlib.vitro.webapp.controller.edit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyDao;
import edu.cornell.mannlib.vitro.webapp.dao.PropertyGroupDao;

public class DatapropEditController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(DatapropEditController.class.getName());

    public void doPost (HttpServletRequest request, HttpServletResponse response) {
        if (!isAuthorizedToDisplayPage(request, response, SimplePermission.EDIT_ONTOLOGY.ACTIONS)) {
        	return;
        }

    	VitroRequest vreq = new VitroRequest(request);
    	
        final int NUM_COLS=17;

        String datapropURI = request.getParameter("uri");

        DataPropertyDao dpDao = vreq.getFullWebappDaoFactory().getDataPropertyDao();
        DataProperty dp = dpDao.getDataPropertyByURI(datapropURI);
        PropertyGroupDao pgDao = vreq.getFullWebappDaoFactory().getPropertyGroupDao();

        ArrayList results = new ArrayList();
        results.add("data property");         // column 1
        results.add("public display label");  // column 2
        results.add("property group");        // column 3
        results.add("ontology");              // column 4
        results.add("RDF local name");        // column 5
        results.add("domain class");          // column 6
        results.add("range datatype");        // column 7
        results.add("functional");            // column 8
        results.add("public description");    // column 9
        results.add("example");               // column 10
        results.add("editor description");    // column 11
        results.add("display level");         // column 12
        results.add("update level");          // column 13
        results.add("display tier");          // column 14
        results.add("display limit");         // column 15
        results.add("custom entry form");     // column 16
        results.add("URI");                   // column 17

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);

        results.add(dp.getLocalNameWithPrefix()); // column 1
        results.add(dp.getPublicName() == null ? "(no public label)" : dp.getPublicName()); // column 2
        
        if (dp.getGroupURI() != null) {
            PropertyGroup pGroup = pgDao.getGroupByURI(dp.getGroupURI());
            if (pGroup != null) {
                results.add(pGroup.getName()); // column 3
            } else {
                results.add(dp.getGroupURI());
            }
        } else {
            results.add("(unspecified)");
        }
        
        String ontologyName = null;
        if (dp.getNamespace() != null) {
            Ontology ont = vreq.getFullWebappDaoFactory().getOntologyDao().getOntologyByURI(dp.getNamespace());
            if ( (ont != null) && (ont.getName() != null) ) {
                ontologyName = ont.getName();
            }
        }
        results.add(ontologyName==null ? "(not identified)" : ontologyName); // column 4

        results.add(dp.getLocalName()); // column 5

        // we support parents now, but not the simple getParent() style method
        //String parentPropertyStr = "<i>(datatype properties are not yet modeled in a property hierarchy)</i>"; // TODO - need multiple inheritance
        //results.add(parentPropertyStr);
        
        // TODO - need unionOf/intersectionOf-style domains for domain class
        String domainStr="";
        try {
            domainStr = (dp.getDomainClassURI() == null) ? "" : "<a href=\"vclassEdit?uri="+URLEncoder.encode(dp.getDomainClassURI(),"UTF-8")+"\">"+dp.getDomainClassURI()+"</a>";
        } catch (UnsupportedEncodingException e) {
            log.error(e, e);
        }
        results.add(domainStr); // column 6

        String rangeStr = (dp.getRangeDatatypeURI() == null) ? "<i>untyped</i> (rdfs:Literal)" : dp.getRangeDatatypeURI();
        results.add(rangeStr); // column 7
        
        results.add(dp.getFunctional() ? "true" : "false"); // column 8
        
        String publicDescriptionStr = (dp.getPublicDescription() == null) ? "" : dp.getPublicDescription(); // column 9
        results.add(publicDescriptionStr);        
        String exampleStr = (dp.getExample() == null) ? "" : dp.getExample();  // column 10
        results.add(exampleStr);
        String descriptionStr = (dp.getDescription() == null) ? "" : dp.getDescription();  // column 11
        results.add(descriptionStr);
        
        results.add(dp.getHiddenFromDisplayBelowRoleLevel()  == null ? "(unspecified)" : dp.getHiddenFromDisplayBelowRoleLevel().getLabel()); // column 12
        results.add(dp.getProhibitedFromUpdateBelowRoleLevel() == null ? "(unspecified)" : dp.getProhibitedFromUpdateBelowRoleLevel().getLabel()); // column 13
        results.add(String.valueOf(dp.getDisplayTier()));  // column 14
        results.add(String.valueOf(dp.getDisplayLimit()));  // column 15
        results.add(dp.getCustomEntryForm() == null ? "(unspecified)" : dp.getCustomEntryForm());  // column 16
        results.add(dp.getURI() == null ? "" : dp.getURI()); // column 17
        request.setAttribute("results",results);
        request.setAttribute("columncount",NUM_COLS);
        request.setAttribute("suppressquery","true");

        boolean FORCE_NEW = true;
        
        EditProcessObject epo = super.createEpo(request, FORCE_NEW);
        FormObject foo = new FormObject();
        HashMap OptionMap = new HashMap();
        // add the options
        foo.setOptionLists(OptionMap);
        epo.setFormObject(foo);

        DataPropertyDao assertionsDpDao = (vreq.getAssertionsWebappDaoFactory() != null) 
            ? vreq.getAssertionsWebappDaoFactory().getDataPropertyDao()
            : vreq.getFullWebappDaoFactory().getDataPropertyDao();
        
        List superURIs = assertionsDpDao.getSuperPropertyURIs(dp.getURI(),false);
        List superProperties = new ArrayList();
        Iterator superURIit = superURIs.iterator();
        while (superURIit.hasNext()) {
            String superURI = (String) superURIit.next();
            if (superURI != null) {
                DataProperty superProperty = assertionsDpDao.getDataPropertyByURI(superURI);
                if (superProperty != null) {
                    superProperties.add(superProperty);
                }
            }
        }
        request.setAttribute("superproperties",superProperties);

        List subURIs = assertionsDpDao.getSubPropertyURIs(dp.getURI());
        List subProperties = new ArrayList();
        Iterator subURIit = subURIs.iterator();
        while (subURIit.hasNext()) {
            String subURI = (String) subURIit.next();
            DataProperty subProperty = dpDao.getDataPropertyByURI(subURI);
            if (subProperty != null) {
                subProperties.add(subProperty);
            }
        }
        request.setAttribute("subproperties",subProperties);
        
        List eqURIs = assertionsDpDao.getEquivalentPropertyURIs(dp.getURI());
        List eqProperties = new ArrayList();
        Iterator eqURIit = eqURIs.iterator();
        while (eqURIit.hasNext()) {
            String eqURI = (String) eqURIit.next();
            DataProperty eqProperty = dpDao.getDataPropertyByURI(eqURI);
            if (eqProperty != null) {
                eqProperties.add(eqProperty);
            }
        }
        request.setAttribute("equivalentProperties", eqProperties);
        
        ApplicationBean appBean = vreq.getAppBean();
        
        request.setAttribute("epoKey",epo.getKey());
        request.setAttribute("datatypeProperty", dp);
        request.setAttribute("bodyJsp","/templates/edit/specific/dataprops_edit.jsp");
        request.setAttribute("title","Data Property Control Panel");
        request.setAttribute("css", "<link rel=\"stylesheet\" type=\"text/css\" href=\""+appBean.getThemeDir()+"css/edit.css\"/>");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("DatapropEditController could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request,response);
    }

}