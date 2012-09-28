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

import javax.servlet.http.HttpSession;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

/**
 * Generate the EditConfiguration for the Institutional Internal Class Form.
 * see http://issues.library.cornell.edu/browse/NIHVIVO-2666
 *  
 *
 */
public class InstitutionalInternalClassForm extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {

    String INTERNAL_CLASS_ANNOTATION_URI= "<http://example.com/vivo#ChangeMeUniveristy>";
    
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) { 
        EditConfigurationVTwo editConfig = new EditConfigurationVTwo();
        
        //set up the template for the form
        editConfig.setTemplate("institutionalInternalClassForm.ftl");
        
        //Set the n3 that is required for the edit
        //bdc34: don't know how the annotation will be structured
        StringList n3ForInternalClass =new StringList( " ?internalClassUri "+INTERNAL_CLASS_ANNOTATION_URI+" \"true\" . " );
        editConfig.setN3Required( n3ForInternalClass );
        
        //bdc34: maybe this is redundant with the keys of the fields Map?
        editConfig.setUrisOnform( new StringList( "internalClassUri" ));                        
        
        //edit config should have URL to submit the form to
        //editConfig.setUrl 
        //set the url pattern that the client will return to after a successful edit
        editConfig.setUrlPatternToReturnTo("/siteAdmin");        
        
        editConfig.setSubmitToUrl("/edit/process");
        //prepare
        prepare(vreq, editConfig);
        return editConfig;
    }

    
    public class StringList extends ArrayList<String>{
        public StringList( String ... strings){
            super();
            for( String str: strings){
                this.add(str);
            }            
        }
    }
}
