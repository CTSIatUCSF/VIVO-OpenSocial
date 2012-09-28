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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Provide the ability to process a Freemarker template outside of the context
 * of a FreemarkerHttpServlet.
 * 
 * The most likely place to use this is when rendering a short view that was
 * invoked through an AJAX call.
 */
public interface FreemarkerProcessingService {
	/**
	 * Is there an accessible template by this name?
	 * 
	 * The question is asked in the context of the current request, which
	 * determines the theme directory.
	 * 
	 * @throws TemplateProcessingException
	 *             If the template is found, but cannot be parsed.
	 */
	boolean isTemplateAvailable(String templateName, HttpServletRequest req)
			throws TemplateProcessingException;

	/**
	 * Process a Freemarker template with a data map, producing string of HTML.
	 * 
	 * This is done in the context of the current HttpServletRequest, which
	 * provides a wide range of ancillary information, including (but not
	 * limited to) theme directory, context path, info on logged-in user,
	 * authorizations for the current user, etc., etc.
	 */
	String renderTemplate(String templateName, Map<String, Object> map,
			HttpServletRequest req) throws TemplateProcessingException;

	/**
	 * Indicates a failure to render the given template with the given data.
	 */
	@SuppressWarnings("serial")
	public static class TemplateProcessingException extends Exception {

		public TemplateProcessingException() {
			super();
		}

		public TemplateProcessingException(String message) {
			super(message);
		}

		public TemplateProcessingException(Throwable cause) {
			super(cause);
		}

		public TemplateProcessingException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * Template parser detected a problem.
	 */
	@SuppressWarnings("serial")
	public static class TemplateParsingException extends
			TemplateProcessingException {

		public TemplateParsingException() {
			super();
		}

		public TemplateParsingException(String message) {
			super(message);
		}

		public TemplateParsingException(Throwable cause) {
			super(cause);
		}

		public TemplateParsingException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
