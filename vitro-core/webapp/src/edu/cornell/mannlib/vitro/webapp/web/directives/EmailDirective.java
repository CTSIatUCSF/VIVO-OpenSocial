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

package edu.cornell.mannlib.vitro.webapp.web.directives;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailMessage;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Process the inputs for a FreemarkerEmailMessage.
 * 
 * @see FreemarkerEmailMessage
 */
public class EmailDirective extends BaseTemplateDirectiveModel {

	private static final Log log = LogFactory.getLog(EmailDirective.class);

	private final FreemarkerEmailMessage message;

	public EmailDirective(FreemarkerEmailMessage message) {
		this.message = message;
	}

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {

		String subject = getOptionalSimpleScalarParameter(params, "subject");
		if (subject != null) {
			message.setSubject(subject);
		}

		String htmlContent = getOptionalSimpleScalarParameter(params, "html");
		if (htmlContent != null) {
			message.setHtmlContent(htmlContent);
		}

		String textContent = getOptionalSimpleScalarParameter(params, "text");
		if (textContent != null) {
			message.setTextContent(textContent);
		}

		if ((htmlContent == null) && (textContent == null)) {
			throw new TemplateModelException("The email directive must have "
					+ "either a 'html' parameter or a 'text' parameter.");
		}
	}

	@Override
	public Map<String, Object> help(String name) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		map.put("effect",
				"Create an email message from the parameters set in the invoking template.");

		Map<String, String> params = new HashMap<String, String>();
		params.put("subject", "email subject (optional)");
		params.put("html", "HTML version of email message (optional)");
		params.put("text", "Plain text version of email message (optional)");
		map.put("parameters", params);

		List<String> examples = new ArrayList<String>();
		examples.add("&lt;email subject=\"Password reset confirmation\" html=html text=text&gt;");
        examples.add("&lt;email html=html text=text&gt;");
		map.put("examples", examples);

		return map;
	}
}
