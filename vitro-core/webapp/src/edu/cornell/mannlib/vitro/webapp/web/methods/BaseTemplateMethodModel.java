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

package edu.cornell.mannlib.vitro.webapp.web.methods;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModel;

public abstract class BaseTemplateMethodModel implements TemplateMethodModel {

    private static final Log log = LogFactory.getLog(BaseTemplateMethodModel.class);
    
    public abstract Map<String, Object> help(String name);
    
    protected String mergeToHelpTemplate(Map<String, Object> map, Environment env) {
        return processTemplateToString("help-method.ftl", map, env);        
    }
    
    public static String processTemplateToString(String templateName, Map<String, Object> map, Environment env) {
        Template template = getTemplate(templateName, env);
        StringWriter sw = new StringWriter();
        try {
            template.process(map, sw);
        } catch (TemplateException e) {
            log.error("Template Exception creating processing environment", e);
        } catch (IOException e) {
            log.error("IOException creating processing environment", e);
        }
        return sw.toString();        
    }
    
    private static Template getTemplate(String templateName, Environment env) {
        Template template = null;
        try {
            template = env.getConfiguration().getTemplate(templateName);
        } catch (IOException e) {
            // RY Should probably throw this error instead.
            log.error("Cannot get template " + templateName, e);
        }  
        return template;        
    }

}
