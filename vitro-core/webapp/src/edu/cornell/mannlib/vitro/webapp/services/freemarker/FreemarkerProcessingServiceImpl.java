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

package edu.cornell.mannlib.vitro.webapp.services.freemarker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerConfiguration;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerConfigurationLoader;
import edu.cornell.mannlib.vitro.webapp.utils.log.LogUtils;
import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * An implementation of the FreemarkerProcessingService.
 */
public class FreemarkerProcessingServiceImpl implements
		FreemarkerProcessingService {
	private static final Log log = LogFactory
			.getLog(FreemarkerProcessingServiceImpl.class);

	@Override
	public boolean isTemplateAvailable(String templateName,
			HttpServletRequest req) throws TemplateProcessingException {
		return null != getTemplate(templateName, req);
	}

	@Override
	public String renderTemplate(String templateName, Map<String, Object> map,
			HttpServletRequest req) throws TemplateProcessingException {
		log.debug("renderTemplate: '" + templateName + "' with "
				+ LogUtils.deepFormatForLog(log, "debug", map));
		Template template = getTemplate(templateName, req);
		return processTemplate(template, map, req);
	}

	/**
	 * Fetch this template from a file and parse it. If there are any problems,
	 * return "null".
	 */
	private Template getTemplate(String templateName, HttpServletRequest req)
			throws TemplateProcessingException {
		Template template = null;
		try {
			Configuration config = FreemarkerConfigurationLoader
					.getConfig(new VitroRequest(req));
			template = config.getTemplate(templateName);
		} catch (ParseException e) {
			log.warn("Failed to parse the template at '" + templateName + "'"
					+ e);
			throw new TemplateParsingException(e);
		} catch (FileNotFoundException e) {
			log.debug("No template found for '" + templateName + "'");
			throw new TemplateProcessingException(e);
		} catch (IOException e) {
			log.warn("Failed to read the template at '" + templateName + "'", e);
			throw new TemplateProcessingException(e);
		}
		return template;
	}

	private String processTemplate(Template template, Map<String, Object> map,
			HttpServletRequest req) throws TemplateProcessingException {

		StringWriter writer = new StringWriter();
		try {
			// Add directives to the map. For some reason, having them in the
			// configuration is not enough.
			map.putAll(FreemarkerConfiguration.getDirectives());

			// Add request and servlet context as custom attributes of the
			// environment, so they
			// can be used in directives.
			Environment env = template.createProcessingEnvironment(map, writer);
			env.setCustomAttribute("request", req);
			env.setCustomAttribute("context", req.getSession()
					.getServletContext());
			env.process();
			return writer.toString();
		} catch (TemplateException e) {
			throw new TemplateProcessingException(
					"TemplateException creating processing environment", e);
		} catch (IOException e) {
			throw new TemplateProcessingException(
					"IOException creating processing environment", e);
		}
	}
}
