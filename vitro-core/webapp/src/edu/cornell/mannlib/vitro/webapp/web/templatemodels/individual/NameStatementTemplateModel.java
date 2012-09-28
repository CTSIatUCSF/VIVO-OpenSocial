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
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.EditDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.EditLiteral;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.RdfLiteralHash;

/**
 * This allows the template to treat an rdfs:label like a data property statement, and thus
 * have an editing link.
 * 
 * This has the same accessor methods as a DataPropertyStatementTemplateModel, but it is never
 * part of the GroupedPropertyList, and it never has a deleteUrl. 
 */
public class NameStatementTemplateModel extends PropertyStatementTemplateModel {
    private static final Log log = LogFactory.getLog(NameStatementTemplateModel.class);
    
    private final String stringValue;
    private final String editUrl;

    NameStatementTemplateModel(String subjectUri, VitroRequest vreq) {
        super(subjectUri, VitroVocabulary.LABEL, vreq);

        // NIHVIVO-2466 Use the same methods to get the label that are used elsewhere in the 
        // application, to guarantee consistent results for individuals with multiple labels
        // across the application. 
        WebappDaoFactory wdf = vreq.getWebappDaoFactory();
        IndividualDao iDao = wdf.getIndividualDao();
        EditLiteral literal = iDao.getLabelEditLiteral(subjectUri);
        
        if (literal == null) {
        	// If the individual has no rdfs:label, use the local name. It will not be editable. (This replicates previous behavior;
        	// perhaps we would want to allow a label to be added. But such individuals do not usually have their profiles viewed or
        	// edited directly.)
        	URI uri = new URIImpl(subjectUri);
        	this.stringValue = uri.getLocalName();
        	this.editUrl = "";
        } else {
            this.stringValue = cleanTextForDisplay( literal.getLexicalForm() );
            this.editUrl = makeEditUrl(literal);       
        }
    }
 
	private String makeEditUrl(Literal literal) {
        // Determine whether the statement can be edited
        DataPropertyStatement dps = makeStatement(literal);
        RequestedAction action = new EditDataPropertyStatement(vreq.getJenaOntModel(), dps);
        if ( ! PolicyHelper.isAuthorizedForActions(vreq, action) ) {
            return "";
        }
        
        ParamMap params = new ParamMap(
                "subjectUri", subjectUri,
                "predicateUri", propertyUri,
                "datapropKey", makeHash(dps),
                "deleteProhibited", "prohibited");
        
        params.putAll(UrlBuilder.getModelParams(vreq));
        
        return UrlBuilder.getUrl(EDIT_PATH, params);             
	}
        
	private DataPropertyStatement makeStatement(Literal literalValue) {
		DataPropertyStatement dps = new DataPropertyStatementImpl(subjectUri,
				propertyUri, literalValue.getLexicalForm());
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
    	return stringValue;
    }

    @Override
	public String getDeleteUrl() {
		return "";
	}
    
    @Override
	public String getEditUrl() {
		return editUrl;
	}

}
