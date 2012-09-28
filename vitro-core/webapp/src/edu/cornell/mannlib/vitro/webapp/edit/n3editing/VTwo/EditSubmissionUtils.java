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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class EditSubmissionUtils {

    protected static final String MULTI_VALUED_EDIT_SUBMISSION = "MultiValueEditSubmission";
    
    /* *************** Static utility methods to get EditSub from Session *********** */

    public static MultiValueEditSubmission getEditSubmissionFromSession(HttpSession sess, EditConfigurationVTwo editConfig){
        Map<String,MultiValueEditSubmission> submissions = 
            (Map<String,MultiValueEditSubmission>)sess.getAttribute(MULTI_VALUED_EDIT_SUBMISSION);
        if( submissions == null )
          return null;
        if( editConfig != null )
            return submissions.get(  editConfig.getEditKey() ); //this might be null
        else
            return null;
    }

    public static void putEditSubmissionInSession(HttpSession sess, MultiValueEditSubmission editSub){
        Map<String,MultiValueEditSubmission> submissions = (Map<String,MultiValueEditSubmission>)sess.getAttribute(MULTI_VALUED_EDIT_SUBMISSION);
        if( submissions == null ){
            submissions = new HashMap<String,MultiValueEditSubmission>();
            sess.setAttribute(MULTI_VALUED_EDIT_SUBMISSION,submissions);
        }
        submissions.put(editSub.editKey, editSub);
    }


    public static void clearEditSubmissionInSession(HttpSession sess, MultiValueEditSubmission editSub){
        if( sess == null) return;
        if( editSub == null ) return;
        Map<String,MultiValueEditSubmission> submissions = (Map<String,MultiValueEditSubmission>)sess.getAttribute(MULTI_VALUED_EDIT_SUBMISSION);
        if( submissions == null ){
            throw new Error("MultiValueEditSubmission: could not get a Map of MultiValueEditSubmission from the session.");
        }

        submissions.remove( editSub.editKey );
    }

    public static void clearAllEditSubmissionsInSession(HttpSession sess ){
        if( sess == null) return;
        sess.removeAttribute(MULTI_VALUED_EDIT_SUBMISSION);
    }

    public static Map<String, String[]> convertParams(
            Map<String, List<String>> queryParameters) {
        HashMap<String,String[]> out = new HashMap<String,String[]>();
        for( String key : queryParameters.keySet()){
            List item = queryParameters.get(key);            
            out.put(key, (String[])item.toArray(new String[item.size()]));
        }
        return out;
    }                 
}
