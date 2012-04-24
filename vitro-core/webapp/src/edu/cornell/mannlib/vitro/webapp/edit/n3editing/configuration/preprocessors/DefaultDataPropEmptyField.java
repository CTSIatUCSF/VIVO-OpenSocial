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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditSubmissionUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.controller.ProcessRdfFormController.Utilities;

/**
 * Editors have gotten into the habit of clearing the text from the
 * textarea and saving it to invoke a delete.  see Issue VITRO-432   
 *
 */
public class DefaultDataPropEmptyField implements ModelChangePreprocessor{

    @Override
    public void preprocess(Model retractionsModel, Model additionsModel,
            HttpServletRequest request) {
        
        EditConfigurationVTwo configuration = EditConfigurationUtils.getEditConfiguration(request);
        
        HttpSession session = request.getSession();
        MultiValueEditSubmission submission = EditSubmissionUtils.getEditSubmissionFromSession(session,configuration);
        
        //if data property, then check for empty string condition
        //which means only one value and it is an empty string
        if( checkForEmptyString(submission, configuration, new VitroRequest(request)) ) {
            additionsModel.removeAll();
        }        
    }
    

    protected boolean checkForEmptyString(
            MultiValueEditSubmission submission,
            EditConfigurationVTwo configuration, 
            VitroRequest vreq) {
        
        if(EditConfigurationUtils.isDataProperty(configuration.getPredicateUri(), vreq)) {
            // Our editors have gotten into the habit of clearing the text from the
            // textarea and saving it to invoke a delete.  see Issue VITRO-432   
            if (configuration.getFields().size() == 1) {
                String onlyField = configuration.getFields().keySet().iterator().next();
                List<Literal> value = submission.getLiteralsFromForm().get(onlyField);
                if( value == null || value.size() == 0){
                    return true;
                }else {
                    if(value.size() == 1) {
                        if( "".equals(value.get(0).getLexicalForm())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;    
    }

}
