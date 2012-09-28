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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.DropDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.RdfLiteralHash;

public class DataPropertyStatementTemplateModel extends PropertyStatementTemplateModel {
    private static final Log log = LogFactory.getLog(DataPropertyStatementTemplateModel.class); 
    
    private final Literal literalValue;
    private final String deleteUrl;
    private final String editUrl;
    private final String templateName;

    //Extended to include vitro request to check for special parameters
    public DataPropertyStatementTemplateModel(String subjectUri, String propertyUri, Literal literal,
            String templateName, VitroRequest vreq) {
        super(subjectUri, propertyUri, vreq);
        
        this.literalValue = literal;
        this.templateName = templateName;

        // Do delete url first, since used in building edit url
        this.deleteUrl = makeDeleteUrl();            
        this.editUrl = makeEditUrl();       
    }
    
	private String makeDeleteUrl() {
        // Determine whether the statement can be deleted
		DataPropertyStatement dps = makeStatement();
        RequestedAction action = new DropDataPropertyStatement(vreq.getJenaOntModel(), dps);
        if ( ! PolicyHelper.isAuthorizedForActions(vreq, action) ) {
            return "";
        }
        
        ParamMap params = new ParamMap(
                "subjectUri", subjectUri,
                "predicateUri", propertyUri,
                "datapropKey", makeHash(dps),
                "cmd", "delete");
        
        params.put("templateName", templateName);
        params.putAll(UrlBuilder.getModelParams(vreq));
        
        return UrlBuilder.getUrl(EDIT_PATH, params);
	}

	private String makeEditUrl() {
        // vitro:moniker is deprecated. We display existing data values so editors can 
        // move them to other properties and delete, but don't allow editing.
        if ( propertyUri.equals(VitroVocabulary.MONIKER) ) {
            return "";           
        }
        
        // Determine whether the statement can be edited
		DataPropertyStatement dps = makeStatement();
        RequestedAction action = new EditDataPropertyStatement(vreq.getJenaOntModel(), dps);
        if ( ! PolicyHelper.isAuthorizedForActions(vreq, action) ) {
            return "";
        }
        
        ParamMap params = new ParamMap(
                "subjectUri", subjectUri,
                "predicateUri", propertyUri,
                "datapropKey", makeHash(dps));
        
        if ( deleteUrl.isEmpty() ) {
            params.put("deleteProhibited", "prohibited");
        }
        
        params.putAll(UrlBuilder.getModelParams(vreq));
        
        return UrlBuilder.getUrl(EDIT_PATH, params);             
	}
        
	private DataPropertyStatement makeStatement() {
		DataPropertyStatement dps = new DataPropertyStatementImpl(subjectUri, propertyUri, literalValue.getLexicalForm());
		// Language and datatype are needed to get the correct hash value
		dps.setLanguage(literalValue.getLanguage());
		dps.setDatatypeURI(literalValue.getDatatypeURI());
		return dps;
	}

	private String makeHash(DataPropertyStatement dps) {
        // Language and datatype are needed to get the correct hash value
        return String.valueOf(RdfLiteralHash.makeRdfLiteralHash(dps));
	}

    /* Template properties */
    
    public String getValue() {
        //attempt to strip any odd HTML
        return cleanTextForDisplay( literalValue.getLexicalForm() );
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
