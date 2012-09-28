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

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateProcessingHelper {
    
    private static final Log log = LogFactory.getLog(TemplateProcessingHelper.class);
    
    private Configuration config = null;
    private HttpServletRequest request = null;
    private ServletContext context = null;
    
    public TemplateProcessingHelper(HttpServletRequest request, ServletContext context) {
        this.config = FreemarkerConfigurationLoader.getConfig(new VitroRequest(request));
        this.request = request;
        this.context = context;
    }
    
    public StringWriter processTemplate(String templateName, Map<String, Object> map) 
        throws TemplateProcessingException {
        Template template = getTemplate(templateName);
        StringWriter sw = new StringWriter();        
        processTemplate(template, map, sw);
        return sw;
    }
    
    private void processTemplate(Template template, Map<String, Object> map, Writer writer)
        throws TemplateProcessingException {
        
        try {
            Environment env = template.createProcessingEnvironment(map, writer);
            // Add request and servlet context as custom attributes of the environment, so they
            // can be used in directives.
            env.setCustomAttribute("request", request);
            env.setCustomAttribute("context", context);
            
            // Define a setup template to be included by every page template
            String templateType = (String) map.get("templateType");
            if (FreemarkerHttpServlet.PAGE_TEMPLATE_TYPE.equals(templateType)) {
                env.include(getTemplate("pageSetup.ftl"));
            }
            
            env.process();
        } catch (TemplateException e) {
            throw new TemplateProcessingException("TemplateException creating processing environment", e);
        } catch (IOException e) {
            throw new TemplateProcessingException("IOException creating processing environment", e);            
        }        
    }

    private Template getTemplate(String templateName) throws TemplateProcessingException {
        Template template = null;
        try {
            template = config.getTemplate(templateName);
        } catch (IOException e) {
            String msg;
            if (e instanceof freemarker.core.ParseException) {
                msg = "Syntax error in template " + templateName;
            } else if (e instanceof java.io.FileNotFoundException) {
                msg = "Cannot find template " + templateName;                  
            } else {
                msg = "IOException getting template " + templateName;
            }
            throw new TemplateProcessingException(msg, e);
        }  
        return template;
    }

    @SuppressWarnings("serial")
    public class TemplateProcessingException extends Exception {

        public TemplateProcessingException(String message) {
            super(message);
        } 
        
        public TemplateProcessingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
