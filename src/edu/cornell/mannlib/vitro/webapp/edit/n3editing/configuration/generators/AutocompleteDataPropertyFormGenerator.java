
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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;

/**
 * Generates the edit configuration for a default property form.
 *
 */
public class AutocompleteDataPropertyFormGenerator extends DefaultDataPropertyFormGenerator {

	//The only thing that changes here are the templates
	private Log log = LogFactory.getLog(AutocompleteObjectPropertyFormGenerator.class);
	private String dataPropertyTemplate = "autoCompleteDataPropForm.ftl";
	

	@Override
	public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) {
		EditConfigurationVTwo ec = super.getEditConfiguration(vreq, session);
		this.addFormSpecificData(ec, vreq);
		return ec;
	}
	
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		//Filter setting - i.e. sparql query for filtering out results from autocomplete
		formSpecificData.put("sparqlForAcFilter", getSparqlForAcFilter(vreq));
		editConfiguration.setTemplate(dataPropertyTemplate);
		//Add edit model
		formSpecificData.put("editMode", getEditMode(vreq));
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	public String getSparqlForAcFilter(VitroRequest vreq) {
		String subject = EditConfigurationUtils.getSubjectUri(vreq);			
		String predicate = EditConfigurationUtils.getPredicateUri(vreq);
		//Get all objects for existing predicate, filters out results from addition and edit
		String query =  "SELECT ?dataLiteral WHERE { " + 
			"<" + subject + "> <" + predicate + "> ?dataLiteral .} ";
		return query;
	}
	
	//Get edit mode
	public String getEditMode(VitroRequest vreq) {
       if(isUpdate(vreq)) 
    	   return "edit";
        else
    	   return "add";
	}
	
	private boolean isUpdate(VitroRequest vreq) {
		Integer dataHash = EditConfigurationUtils.getDataHash(vreq);
		return ( dataHash != null );
	}
	
}
