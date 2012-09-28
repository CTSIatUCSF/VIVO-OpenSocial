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

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class ObjectPropertyStatementTemplateModel extends PropertyStatementTemplateModel {
    private static final Log log = LogFactory.getLog(ObjectPropertyStatementTemplateModel.class); 
    
    private final Map<String, String> data;
    
    private final String objectUri;
    private final String templateName;
    private final String objectKey;
    private final String editUrl;
    private final String deleteUrl;
    
    public ObjectPropertyStatementTemplateModel(String subjectUri, String propertyUri, String objectKey, 
            Map<String, String> data, String templateName, VitroRequest vreq) {
        super(subjectUri, propertyUri, vreq);

        this.data = Collections.unmodifiableMap(new HashMap<String, String>(data));
        this.objectUri = data.get(objectKey);        
        this.templateName = templateName;
        //to keep track of later
        this.objectKey = objectKey;
        
        ObjectPropertyStatement ops = new ObjectPropertyStatementImpl(subjectUri, propertyUri, objectUri);
        
        // Do delete url first, since it is used in building edit url
        this.deleteUrl = makeDeleteUrl();
        this.editUrl = makeEditUrl(ops);
    }

	private String makeDeleteUrl() {
    	// Is the delete link suppressed for this property?
    	if (new EditLinkSuppressor(vreq).isDeleteLinkSuppressed(propertyUri)) {
    		return "";
    	}
        
        // Determine whether the statement can be deleted
		RequestedAction action = new DropObjectPropertyStatement(
				vreq.getJenaOntModel(), subjectUri, propertyUri, objectUri);
        if ( ! PolicyHelper.isAuthorizedForActions(vreq, action) ) {
            return "";
        }
        
        if (propertyUri.equals(VitroVocabulary.IND_MAIN_IMAGE)) {
            return ObjectPropertyTemplateModel.getImageUploadUrl(subjectUri, "delete");
        } 
        
        ParamMap params = new ParamMap(
                "subjectUri", subjectUri,
                "predicateUri", propertyUri,
                "objectUri", objectUri,
                "cmd", "delete",
                "objectKey", objectKey);
            
        for ( String key : data.keySet() ) {
            String value = data.get(key);
            // Remove an entry with a null value instead of letting it get passed
            // as a param with an empty value, in order to align with behavior on
            // profile page. E.g., if statement.moniker is null, a test for 
            // statement.moniker?? will yield different results if null on the 
            // profile page but an empty string on the deletion page.
            if (value != null) {
                params.put("statement_" + key, data.get(key));
            }
        }
        
        params.put("templateName", templateName);
        params.putAll(UrlBuilder.getModelParams(vreq));
        
        return UrlBuilder.getUrl(EDIT_PATH, params);
	}

	private String makeEditUrl(ObjectPropertyStatement ops) {
    	// Is the edit link suppressed for this property?
    	if (new EditLinkSuppressor(vreq).isEditLinkSuppressed(propertyUri)) {
    		return "";
    	}
        
       // Determine whether the statement can be edited
        RequestedAction action =  new EditObjectPropertyStatement(vreq.getJenaOntModel(), ops);
        if ( ! PolicyHelper.isAuthorizedForActions(vreq, action) ) {
            return "";
        }
        
        if (propertyUri.equals(VitroVocabulary.IND_MAIN_IMAGE)) {
            return ObjectPropertyTemplateModel.getImageUploadUrl(subjectUri, "edit");
        } 

        ParamMap params = new ParamMap(
                "subjectUri", subjectUri,
                "predicateUri", propertyUri,
                "objectUri", objectUri);
        
        if ( deleteUrl.isEmpty() ) {
            params.put("deleteProhibited", "prohibited");
        }
        
        params.putAll(UrlBuilder.getModelParams(vreq));
        
        return UrlBuilder.getUrl(EDIT_PATH, params);
	}
    
    /* Template methods */

    public Object get(String key) {
        return cleanTextForDisplay( data.get(key) );
    }
  
    public String uri(String key) {
    	return cleanURIForDisplay(data.get(key));
    }

	@Override
	public String getDeleteUrl() {
		return deleteUrl;
	}
  
	@Override
	public String getEditUrl() {
		return editUrl;
	}

}
