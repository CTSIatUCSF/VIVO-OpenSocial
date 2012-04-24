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

import java.lang.reflect.Method;
import java.util.LinkedHashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class Tags extends BaseTemplateModel {
    
    private static final Log log = LogFactory.getLog(Tags.class);
    
    protected final LinkedHashSet<String> tags;

    public Tags() {
        this.tags = new LinkedHashSet<String>();
    }
    
    public Tags(LinkedHashSet<String> tags) {
        this.tags = tags;
    }
    
    public TemplateModel wrap() {
        try {
            return new TagsWrapper().wrap(this);    	
        } catch (TemplateModelException e) {
            log.error("Error creating Tags template model");
            return null;
        }
    }
    
    /** Script and stylesheet lists are wrapped with a specialized BeansWrapper
     * that exposes certain write methods, instead of the configuration's object wrapper,
     * which doesn't. The templates can then add stylesheets and scripts to the lists
     * by calling their add() methods.
     * @param Tags tags
     * @return TemplateModel
     */
    static public class TagsWrapper extends BeansWrapper {
        
        public TagsWrapper() {
            // Start by exposing all safe methods.
            setExposureLevel(EXPOSE_SAFE);
        }
        
        @SuppressWarnings("rawtypes")
        @Override
        protected void finetuneMethodAppearance(Class cls, Method method, MethodAppearanceDecision decision) {
            
            try {
                String methodName = method.getName();
                if ( ! ( methodName.equals("add") || methodName.equals("list")) ) {
                    decision.setExposeMethodAs(null);
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        }
    }
    
    
    /* Template methods */

    public void add(String... tags) {
        for (String tag : tags) {
            add(tag);
        }
    }
    
    public void add(String tag) {
        tags.add(tag);
    }
 
    public String list() {
        return StringUtils.join(tags, "\n");
    }
    

}
