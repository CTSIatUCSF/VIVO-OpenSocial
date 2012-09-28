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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.ProcessRdfForm;


public class EditConfigurationConstants {
    /** Constants used by edit configuration */
	//forces creation of new uri if present
    public static final String NEW_URI_SENTINEL = ">NEW URI REQUIRED<";
    public static final String BLANK_SENTINEL = ">SUBMITTED VALUE WAS BLANK<";

    //For freemarker configuration
    public static Map<String, String> exportConstants() {
    	Map<String, String> constants = new HashMap<String, String>();
    	java.lang.reflect.Field[] fields = EditConfigurationConstants.class.getDeclaredFields();
    	for(java.lang.reflect.Field f: fields) {
    		if(Modifier.isStatic(f.getModifiers()) && 
    			Modifier.isPublic(f.getModifiers())) {
    			try {
    				constants.put(f.getName(), f.get(null).toString());
    			} catch(Exception ex) {
    				log.error("An exception occurred in trying to retrieve this field ", ex);
    			}
    		}
    	}
    	return constants;
    }
    private static Log log = LogFactory.getLog(EditConfigurationConstants.class);

}
