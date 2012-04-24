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
package edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues;


/**
 * This could be called the "Redirect to where I say, damm it" ResponseValue.
 * 
 * It redirects to the URL specified.  It does not attempt to add a 
 * context node.  This is useful when you want to redirect to a URL
 * created by the UrlBuilder which uses statics to sneak a context
 * into the URL strings it creates.    
 */
public class DirectRedirectResponseValues extends RedirectResponseValues {

    /** This will redirect to the url. It will not add the context to the url.*/
    public DirectRedirectResponseValues(String url, int statusCode) {
        super(url, statusCode);        
    }
    
    /** This will redirect to the url. It will not add the context to the url.*/
    public DirectRedirectResponseValues(String url){
        super(url);
    }

    /**
     * Does not add context.
     */
    @Override
    protected String getRedirectUrl(String url) {
        return url;
    }        
}
