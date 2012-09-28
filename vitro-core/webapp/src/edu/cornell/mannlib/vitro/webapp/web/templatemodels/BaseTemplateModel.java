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

package edu.cornell.mannlib.vitro.webapp.web.templatemodels;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.web.AntiScript;

public abstract class BaseTemplateModel {

    private static final Log log = LogFactory.getLog(BaseTemplateModel.class);

	private static final String URI_CHARACTERS = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&%'()*+,;=";
    
    // Convenience method so subclasses can call getUrl(path)
    protected String getUrl(String path) {
        return UrlBuilder.getUrl(path);
    }

    // Convenience method so subclasses can call getUrl(path, params)
    protected String getUrl(String path, ParamMap params) {
        return UrlBuilder.getUrl(path, params);
    }
    
    // Convenience method so subclasses can call getUrl(path, params)
    protected String getUrl(String path, String... params) {
        return UrlBuilder.getUrl(path, params);
    }

    /**
     * Used to do any processing for display of URIs or URLs.  
     * 
     * If we used AntiSami on a URI it would escape any ampersands as &amp;
     * and perhaps do other nastiness as well. Instead we delete any character 
     * that shouldn't be in a URI.
     */
    protected String cleanURIForDisplay( String dirty ){
        if( dirty == null )
            return null;
        
    	StringBuilder clean = new StringBuilder(dirty.length());
    	for (char ch: dirty.toCharArray()) {
    		if (URI_CHARACTERS.indexOf(ch) != -1) {
    			clean.append(ch);
    		}
    	}
        return clean.toString();
    }
    
    /**
     * Used to do any processing for display of general text.  
     * Currently this only checks for XSS exploits.
     */
    protected String cleanTextForDisplay( String dirty){
        return AntiScript.cleanText(dirty);
    }
    
    /**
     * Used to do any processing for display of values in
     * a map.  Map may be modified. 
     */
    protected <T> void cleanMapValuesForDisplay( Map<T,String> map){
        AntiScript.cleanMapValues(map);
    }
    
}
