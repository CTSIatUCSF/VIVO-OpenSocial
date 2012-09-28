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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.customlistview.InvalidConfigurationException;

public class UncollatedObjectPropertyTemplateModel extends ObjectPropertyTemplateModel {

    private static final Log log = LogFactory.getLog(UncollatedObjectPropertyTemplateModel.class);  
    
    private final List<ObjectPropertyStatementTemplateModel> statements;
    
    UncollatedObjectPropertyTemplateModel(ObjectProperty op, Individual subject, 
            VitroRequest vreq, boolean editing, 
            List<ObjectProperty> populatedObjectPropertyList)
        throws InvalidConfigurationException {
        
        super(op, subject, vreq, editing);
        statements = new ArrayList<ObjectPropertyStatementTemplateModel>();
        
        if (populatedObjectPropertyList.contains(op)) {
            log.debug("Getting data for populated object property " + op.getURI());
            
            /* Get the data */
            List<Map<String, String>> statementData = getStatementData();
            
            /* Apply postprocessing */
            postprocess(statementData);
            
            /* Put into data structure to send to template */            
            String objectKey = getObjectKey();
            for (Map<String, String> map : statementData) {
                statements.add(new ObjectPropertyStatementTemplateModel(subjectUri, 
                        propertyUri, objectKey, map, getTemplateName(), vreq));
            }
            
            postprocessStatementList(statements);
        } else {
            log.debug("Object property " + getUri() + " is unpopulated.");
        }
    }
    
    @Override
    protected boolean isEmpty() {
        return statements.isEmpty();
    }
    
    /* Template properties */

    public List<ObjectPropertyStatementTemplateModel> getStatements() {
        return statements;
    }
    
    @Override
    public boolean isCollatedBySubclass() {
        return false;
    }
    
    /* Template methods */
    
    public ObjectPropertyStatementTemplateModel first() {
        return ( (statements == null || statements.isEmpty()) ) ? null : statements.get(0);
    }
}
