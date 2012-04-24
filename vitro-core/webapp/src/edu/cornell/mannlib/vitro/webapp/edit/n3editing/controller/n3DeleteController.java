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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.EditConfiguration;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditN3Generator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditSubmission;

/**
 * N3 based deletion.
 *
 * Build up the n3 using the fields from an edit configuration and then remove
 * all of those statements from the systems model.  In general this should
 * do the same thing as an update with processRdfForm2.jsp but it should just
 * build the assertions graph and remove that from the system model.
 *
 */

public class n3DeleteController extends FreemarkerHttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(n3DeleteController.class);
	
	@Override
	protected ResponseValues processRequest(VitroRequest vreq){
		
	    /* the post parameters seem to get consumed by the parsing so
	     * we have to make a copy. */
		Map<String, String[]> queryParameters = null;
		queryParameters = vreq.getParameterMap();
		
		List<String> errorMessages = new ArrayList<String>();
		
		HttpSession session = vreq.getSession();
		EditConfiguration editConfiguration = EditConfiguration.getConfigFromSession(session, vreq);
		if(editConfiguration == null){
			//TODO: previously forwarded to noEditConfigFound.jsp
			//probably needs to forward to something else now
		}
		
		EditN3Generator n3Subber = editConfiguration.getN3Generator();
		EditSubmission submission = new EditSubmission(queryParameters, editConfiguration);
		
		Map<String, String> errors = submission.getValidationErrors();
		EditSubmission.putEditSubmissionInSession(session, submission);
		
		if(errors != null && !errors.isEmpty()){
			String form = editConfiguration.getFormUrl();
			vreq.setAttribute("formUrl", form);
			//TODO: forwards to form. Needs to change
			return null;
		}
		
		List<Model> requiredAssertionsToDelete = new ArrayList<Model>();
		List<Model> optionalAssertionsToDelete = new ArrayList<Model>();
		
		boolean requestIsAValidDelete = editConfiguration.getObject() != null && editConfiguration.getObject().trim().length() > 0;
		
		
		
		return null;
	}
}
