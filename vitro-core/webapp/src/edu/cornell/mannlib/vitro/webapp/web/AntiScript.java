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

package edu.cornell.mannlib.vitro.webapp.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * This is a class to provide methods to strip bad HTML from user input.
 * The primary goal of this is to avoid XSS attacks.  
 */
public class AntiScript {

    private static final Log log = LogFactory.getLog(AntiScript.class);
    
    private static Policy policy;
    private static AntiSamy antiSamy;
        
    private static final String ANTI_SCRIPT_SCANNER = "ANTI_SCRIPT_SCANNER";
    private static String ANTI_SCRIPT_POLICY_FILE = "/edu/cornell/mannlib/vitro/webapp/web/antisamy-vitro-1.4.4.xml";

    /**
     * This will attempt to return HTML that has been cleaned up according
     * to the policy.  
     * 
     * If there is any error during the scan, an error message
     * will be returned instead of the HTML.  This might not be ideal so
     * consider changing it once we see how this works. Other options include
     * returning an empty string or some other error message.  Returning 
     * the un-scanned HTML is not a secure option as it may contain scripts.
     * 
     * This will return null if dirtyInput is null.
     */
    public static String cleanText( String dirtyInput ){
        if( dirtyInput == null )
            return null;
        
        AntiSamy as = getAntiSamyScanner();        
        CleanResults cr;
        try {
            cr = as.scan(dirtyInput);
            return cr.getCleanHTML();
        } catch (ScanException e) {
            log.error("Error while scanning HTML" ,e );
        } catch (PolicyException e) {
            log.error("Error while scanning HTML", e);
        }        
        return "AntiScript: HTML caused scan error.";
    }
    
    /**
     * Method to clean a URL or URI.  
     */
    public static String cleanURI( String dirtyInput ){
        return cleanText(dirtyInput);
    }
    
    /**
     * Method to clean all of the values in a map where the values are of
     * type String.
     */
    public static <T> void cleanMapValues( Map<T,String> map ){
        for( T key : map.keySet() ){            
            map.put(key, cleanText(map.get(key)) );
        }        
    }
    
    /**
     * Try to get the static policy, if none exists, create a new one.
     * This is a anti-script policy for use with OWASP AntiSamy, not a vivo auth Policy.
     * Returns null if no policy can be created.
     */
    protected static Policy getAntiScriptPolicy( ){

        if( policy == null ){
            Policy newPolicy;
            try {
                String url = ANTI_SCRIPT_POLICY_FILE;                
                URL policyFile= AntiScript.class.getResource( url );                                                             
                newPolicy = Policy.getInstance( policyFile );                
                log.debug("anti-script policy loaded successfully");
                policy = newPolicy;
            } catch (PolicyException e) {
                log.error("Anti-Script policy not setup.", e);
                return null;
            } catch (Throwable e) {
                log.error("Anti-Script policy not setup.", e);
                return null;
            }           
        }
        
        return policy;                              
    }
    
    /**
     * Try to get a static AntiSamy HTML scanner object that is shared the
     * whole application. This may return a scanner with a null
     * policy if the policy is not setup correctly.
     */
    public static AntiSamy getAntiSamyScanner(  ){
        
        if( antiSamy == null ){
            antiSamy = new AntiSamy( getAntiScriptPolicy() );            
            log.debug("anti-script scanner loaded successfully");                       
        }
         
        return antiSamy;                       
    }     
}
