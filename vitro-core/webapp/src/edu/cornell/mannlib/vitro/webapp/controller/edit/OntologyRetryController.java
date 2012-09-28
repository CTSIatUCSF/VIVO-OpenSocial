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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.EditProcessObject;
import edu.cornell.mannlib.vedit.beans.FormObject;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vedit.forwarder.PageForwarder;
import edu.cornell.mannlib.vedit.forwarder.impl.UrlForwarder;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vedit.validator.Validator;
import edu.cornell.mannlib.vedit.validator.impl.RequiredFieldValidator;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.OntologyDao;

public class OntologyRetryController extends BaseEditController {
	
	private static final Log log = LogFactory.getLog(OntologyRetryController.class.getName());

    public void doPost (HttpServletRequest req, HttpServletResponse response) {
        if (!isAuthorizedToDisplayPage(req, response, SimplePermission.EDIT_ONTOLOGY.ACTIONS)) {
        	return;
        }

    	VitroRequest request = new VitroRequest(req);

        //create an EditProcessObject for this and put it in the session
        EditProcessObject epo = super.createEpo(request);

        /*for testing*/
        Ontology testMask = new Ontology();
        epo.setBeanClass(Ontology.class);
        epo.setBeanMask(testMask);

        String action = "insert";

        OntologyDao oDao = request.getFullWebappDaoFactory().getOntologyDao();
        epo.setDataAccessObject(oDao);

        Ontology ontologyForEditing = null;
        if (!epo.getUseRecycledBean()){
            if (request.getParameter("uri") != null) {
                try {
                    ontologyForEditing = oDao.getOntologyByURI(request.getParameter("uri"));
                    action = "update";
                } catch (NullPointerException e) {
                    log.error("No ontology record found for the namespace "+request.getParameter("uri"));
                }
            } else {
                ontologyForEditing = new Ontology();
            }
            epo.setOriginalBean(ontologyForEditing);
        } else {
            ontologyForEditing = (Ontology) epo.getNewBean();
            action = "update";
            log.error("using newBean");
        }
        
        //validators
        List<Validator> validatorList = new ArrayList<Validator>();
        validatorList.add(new RequiredFieldValidator());
        epo.getValidatorMap().put("URI", validatorList);

        //make a simple mask for the class's id
        Object[] simpleMaskPair = new Object[2];
        simpleMaskPair[0]="Id";
        simpleMaskPair[1]=Integer.valueOf(ontologyForEditing.getId());
        epo.getSimpleMask().add(simpleMaskPair);

        //set up any listeners

        //make a postinsert pageforwarder that will send us to a new ontology's edit screen
        epo.setPostInsertPageForwarder(new OntologyInsertPageForwarder());
        //make a postdelete pageforwarder that will send us to the list of ontologies
        epo.setPostDeletePageForwarder(new UrlForwarder("listOntologies"));

        //set the getMethod so we can retrieve a new bean after we've inserted it
        try {
            Class[] args = new Class[1];
            args[0] = String.class;
            epo.setGetMethod(oDao.getClass().getDeclaredMethod("getOntologyByURI",args));
        } catch (NoSuchMethodException e) {
            log.error("OntologyRetryController could not find the getOntologyByURI method in the DAO");
        }


        FormObject foo = new FormObject();

        foo.setErrorMap(epo.getErrMsgMap());

        epo.setFormObject(foo);

        FormUtils.populateFormFromBean(ontologyForEditing,action,foo,epo.getBadValueMap());

        RequestDispatcher rd = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp","/templates/edit/formBasic.jsp");
        request.setAttribute("formJsp","/templates/edit/specific/ontology_retry.jsp");
        request.setAttribute("scripts","/templates/edit/formBasic.js");
        request.setAttribute("title","Ontology Editing Form");
        request.setAttribute("_action",action);
        request.setAttribute("unqualifiedClassName","Ontology");
        setRequestAttributes(request,epo);

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("OntologyRetryContro" +
                    "ller could not forward to view.");
            log.error(e.getMessage());
            log.error(e.getStackTrace());
        }

    }

    public void doGet (HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

    class OntologyInsertPageForwarder implements PageForwarder {

        public void doForward(HttpServletRequest request, HttpServletResponse response, EditProcessObject epo){
            String newOntologyUrl = "ontologyEdit?uri=";
            Ontology ont = (Ontology) epo.getNewBean();
            try {
                newOntologyUrl += URLEncoder.encode(ont.getURI(),"UTF-8");
                response.sendRedirect(newOntologyUrl);
            } catch (IOException ioe) {
                log.error("OntologyInsertPageForwarder could not send redirect.");
            }
        }
    }

}
