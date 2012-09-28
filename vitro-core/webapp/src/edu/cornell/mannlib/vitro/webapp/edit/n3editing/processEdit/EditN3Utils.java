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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.xerces.util.XMLChar;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasProfile;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.IsUser;

public class EditN3Utils {
    /** Several places could give an editor URI. Return the first one. */
    public static String getEditorUri(HttpServletRequest request) {
        IdentifierBundle ids = RequestIdentifiers.getIdBundleForRequest(request);

        List<String> uris = new ArrayList<String>();
        uris.addAll(IsUser.getUserUris(ids));
        uris.addAll(HasProfile.getProfileUris(ids));
        uris.add("Unknown N3 Editor");
        return uris.get(0);
    }   
    
    /**
     * Strips from a string any characters that are not valid in XML 1.0
     * @param in
     * @return
     */
    public static String stripInvalidXMLChars(String in) {
        if (in == null) {
            return null;
        }
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (!XMLChar.isInvalid(c)) {
                out.append(c);
            }
        }
        return out.toString();
    }
    
    
//    public static void addModTimes( Model additions, Model retractions, Model contextModel ){    	    	
//    	Property modtime = ResourceFactory.createProperty(VitroVocabulary.MODTIME);
//    	Date time = Calendar.getInstance().getTime();
//    	
//    	//get all resources in additions and retractions that are not types
//    	additions.listStatements()
//    	
//    	Lock lock = contextModel.getLock();
//	    try {
//        
//            String existingValue = null;
//            Statement stmt = res.getProperty(modtime);
//            if (stmt != null) {
//                RDFNode object = stmt.getObject();
//                if (object != null && object.isLiteral()){
//                    existingValue = ((Literal)object).getString();
//                }
//            }
//            String formattedDateStr = xsdDateTimeFormat.format(time);
//            if ( (existingValue!=null && value == null) || (existingValue!=null && value != null && !(existingValue.equals(formattedDateStr)) ) ) {
//                model.removeAll(res, modtime, null);
//            }
//            if ( (existingValue==null && value != null) || (existingValue!=null && value != null && !(existingValue.equals(formattedDateStr)) ) ) {
//                model.add(res, modtime, formattedDateStr, XSDDatatype.XSDdateTime);
//            }
//	        
//	    } catch (Exception e) {
//	        log.error("Error in updatePropertyDateTimeValue");
//	        log.error(e, e);
//	    }
//    }
//    
//    private static List<URIResource>
}
