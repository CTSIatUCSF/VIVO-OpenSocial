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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.N3ValidatorVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;

public class AutocompleteRequiredInputValidator implements N3ValidatorVTwo {

    private static String MISSING_LABEL_ERROR = "Please select an existing value or enter a new value in the Name field.";
    
    private String uriReceiver;
    private String labelInput;
    
    public AutocompleteRequiredInputValidator(String uriReceiver, String labelInput) {
        this.uriReceiver = uriReceiver;
        this.labelInput = labelInput;
    }
    
    @Override
    public Map<String, String> validate(EditConfigurationVTwo editConfig,
            MultiValueEditSubmission editSub) {
        Map<String,List<String>> urisFromForm = editSub.getUrisFromForm();
        Map<String,List<Literal>> literalsFromForm = editSub.getLiteralsFromForm();

        Map<String,String> errors = new HashMap<String,String>();   
        
        List<String> selectedUri = urisFromForm.get(uriReceiver);
        
        // If there's a presentationUri, then we're done. If not, check to see if the label exists.
        // If that's null, too, it's an error.
        if (allListElementsEmpty(selectedUri) || selectedUri.contains(">SUBMITTED VALUE WAS BLANK<")) {
            selectedUri = null;
        }
        if (selectedUri != null) {
            return null;
        }
        else {
            List<Literal> specifiedLabel = literalsFromForm.get(labelInput);
            if (specifiedLabel != null && specifiedLabel.size() > 0) {
                return null;
            }
            else {
                errors.put(labelInput, MISSING_LABEL_ERROR);
            }
        }        

        return errors.size() != 0 ? errors : null;
    }
    
    private boolean allListElementsEmpty(List<String> checkList) {
    	if(checkList == null)
    		return true;
    	if(checkList.isEmpty()) {
    		return true;
    	}
    	boolean allEmpty = true;
    	for(String s: checkList) {
    		if(s.length() != 0){
    			allEmpty = false;
    			break;
    		}
    	}
    	return allEmpty;
    }

}
