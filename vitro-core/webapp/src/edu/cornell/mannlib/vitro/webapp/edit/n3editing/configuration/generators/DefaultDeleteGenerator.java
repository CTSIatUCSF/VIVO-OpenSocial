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

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

/**
 * Generates delete form which submits the deletion request to the deletion controller.
 * This is the page to which the user is redirected if they select Delete on the default property form. 
 *
 */
public class DefaultDeleteGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {
	
	private Log log = LogFactory.getLog(DefaultObjectPropertyFormGenerator.class);
	private String subjectUri = null;
	private String predicateUri = null;
	private String objectUri = null;	
	private Integer dataHash = 0;
	private DataPropertyStatement dps = null;
	private String dataLiteral = null;
	private String template = "confirmDeletePropertyForm.ftl";
	private static HashMap<String,String> defaultsForXSDtypes ;
	  static {
		defaultsForXSDtypes = new HashMap<String,String>();
		//defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","2001-01-01T12:00:00");
		defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","#Unparseable datetime defaults to now");
	  }
	  
	//In this case, simply return the edit configuration currently saved in session
	//Since this is forwarding from another form, an edit configuration should already exist in session
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) {
    	EditConfigurationVTwo editConfiguration = EditConfigurationVTwo.getConfigFromSession(session, vreq);
    	//Two paths for deletion: (i) from front page and (ii) from edit page of individual
    	//If (ii), edit configuration already exists but if (i) no edit configuration exists or is required for deletion
    	//so stub will be created that contains a minimal set of information
    	//Set template to be confirm delete
    	if(editConfiguration == null) {
    		editConfiguration = setupEditConfiguration(vreq, session);
    	}
    	editConfiguration.setTemplate(template);
    	//prepare update?
    	prepare(vreq, editConfiguration);
    	return editConfiguration;
    }

	private EditConfigurationVTwo  setupEditConfiguration(VitroRequest vreq, HttpSession session) {
		EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();
		initProcessParameters(vreq, session, editConfiguration);
		//set edit key for this as well
		editConfiguration.setEditKey(editConfiguration.newEditKey(session));
		return editConfiguration;
		
	}
	
	//Do need to know whether data or object property and how to handle that
    private void initProcessParameters(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
    	predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
    	editConfiguration.setSubjectUri(subjectUri);
    	editConfiguration.setPredicateUri(predicateUri);
    	editConfiguration.setEntityToReturnTo(subjectUri);
    	editConfiguration.setUrlPatternToReturnTo("/individual");

    	if(EditConfigurationUtils.isObjectProperty(predicateUri, vreq)) {
    		//not concerned about remainder, can move into default obj prop form if required
    		this.initObjectParameters(vreq);
    		this.processObjectPropForm(vreq, editConfiguration);
    	} else {    		
    	   this.processDataPropForm(vreq, session, editConfiguration);
    	}
    }


    
	private void initObjectParameters(VitroRequest vreq) {
		//in case of object property
    	objectUri = EditConfigurationUtils.getObjectUri(vreq);
	}

	private void processObjectPropForm(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
    	editConfiguration.setObject(objectUri);
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//TODO: Check if null in case no object uri exists but this is still an object property
    }
    
	
    private void processDataPropForm(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
        dataHash = EditConfigurationUtils.getDataHash(vreq);
        if( dataHash != null ){     
            log.debug("Found a datapropKey in parameters and parsed it to int: " + dataHash);
            editConfiguration.setDatapropKey( dataHash );
            dps = EditConfigurationUtils.getDataPropertyStatement(vreq, session, dataHash, predicateUri);
            if( dps != null ){
                editConfiguration.addFormSpecificData("dataPropertyLexicalValue", dps.getData());
            }else{
                editConfiguration.addFormSpecificData("dataPropertyLexicalValue", "unknown value");
            }
        }else{
            log.debug("Did NOT find a datapropKey for hte data hash.");
            editConfiguration.addFormSpecificData("dataPropertyLexicalValue", "unknown value");
        }                
    }
    


}
