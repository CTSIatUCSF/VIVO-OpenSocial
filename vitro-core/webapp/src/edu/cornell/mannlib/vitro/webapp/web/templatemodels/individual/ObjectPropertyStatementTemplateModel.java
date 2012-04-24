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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropObjectPropStmt;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditObjPropStmt;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

public class ObjectPropertyStatementTemplateModel extends PropertyStatementTemplateModel {
    
    private static final Log log = LogFactory.getLog(ObjectPropertyStatementTemplateModel.class); 
    
    private static final String EDIT_PATH = "editRequestDispatch";

    private final Map<String, String> data;
    
    // Used for editing
    private final String objectUri;
    private final String templateName;
    private final String objectKey;
    public ObjectPropertyStatementTemplateModel(String subjectUri, String propertyUri, String objectKey, 
            Map<String, String> data, EditingPolicyHelper policyHelper, String templateName, VitroRequest vreq) {
        super(subjectUri, propertyUri, policyHelper, vreq);
        
        this.data = data;
        this.objectUri = data.get(objectKey);        
        this.templateName = templateName;
        //to keep track of later
        this.objectKey = objectKey;
        setEditUrls(policyHelper);
    }

    protected void setEditUrls(EditingPolicyHelper policyHelper) {
        // If the policyHelper is non-null, we are in edit mode, so create the list of editing permissions.
        // We do this now rather than in getEditUrl() and getDeleteUrl(), because getEditUrl() also needs to know
        // whether a delete is allowed.
        if (policyHelper != null) {
            ObjectPropertyStatement ops = new ObjectPropertyStatementImpl(subjectUri, propertyUri, objectUri);
            
            // Do delete url first, since used in building edit url
            setDeleteUrl(policyHelper, ops);
            setEditUrl(policyHelper, ops);
        }        
    }
    
    protected void setDeleteUrl(EditingPolicyHelper policyHelper, ObjectPropertyStatement ops) {
        
        // Determine whether the statement can be deleted
        RequestedAction action = new DropObjectPropStmt(subjectUri, propertyUri, objectUri);
        if ( ! policyHelper.isAuthorizedAction(action) ) {    
            return;
        }
        
        if (propertyUri.equals(VitroVocabulary.IND_MAIN_IMAGE)) {
            deleteUrl = ObjectPropertyTemplateModel.getImageUploadUrl(subjectUri, "delete");
        } else {
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
            
            deleteUrl = UrlBuilder.getUrl(EDIT_PATH, params);
        }    
    }
    
    protected void setEditUrl(EditingPolicyHelper policyHelper, ObjectPropertyStatement ops) {
        
        // Determine whether the statement can be edited
        RequestedAction action =  new EditObjPropStmt(ops);
        if ( ! policyHelper.isAuthorizedAction(action) ) {
            return;
        }
        
        if (propertyUri.equals(VitroVocabulary.IND_MAIN_IMAGE)) {
            editUrl = ObjectPropertyTemplateModel.getImageUploadUrl(subjectUri, "edit");
        } else {
            ParamMap params = new ParamMap(
                    "subjectUri", subjectUri,
                    "predicateUri", propertyUri,
                    "objectUri", objectUri);
            
            if ( deleteUrl.isEmpty() ) {
                params.put("deleteProhibited", "prohibited");
            }
            
            params.putAll(UrlBuilder.getModelParams(vreq));
            
            editUrl = UrlBuilder.getUrl(EDIT_PATH, params);
        }       
    }
    
    
    /* Template methods */

    public Object get(String key) {
        return cleanTextForDisplay( data.get(key) );
    }
  
    public String uri(String key) {
    	return cleanURIForDisplay(data.get(key));
    }
  
}
