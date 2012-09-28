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

package edu.cornell.mannlib.vitro.webapp.controller.individual;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;

/**
 * Handles requests for entity information.
 */
public class IndividualController extends FreemarkerHttpServlet {
	private static final Log log = LogFactory
			.getLog(IndividualController.class);

	private static final String TEMPLATE_HELP = "individual-help.ftl";

	/**
	 * Use this map to decide which MIME type is suited for the "accept" header.
	 */
	public static final Map<String, Float> ACCEPTED_CONTENT_TYPES = initializeContentTypes();
	private static Map<String, Float> initializeContentTypes() {
		HashMap<String, Float> map = new HashMap<String, Float>();
		map.put(HTML_MIMETYPE, 0.5f);
		map.put(XHTML_MIMETYPE, 0.5f);
		map.put("application/xml", 0.5f);
		map.put(RDFXML_MIMETYPE, 1.0f);
		map.put(N3_MIMETYPE, 1.0f);
		map.put(TTL_MIMETYPE, 1.0f);
		return Collections.unmodifiableMap(map);
	}

	@Override
	protected ResponseValues processRequest(VitroRequest vreq) {
		try {
			/*
			 * What type of request is this?
			 */
			IndividualRequestInfo requestInfo = analyzeTheRequest(vreq);

			switch (requestInfo.getType()) {
			case RDF_REDIRECT:
				/*
				 * If someone expects RDF by asking for the individual with an
				 * "accept" HTTP header, redirect them to the preferred URL.
				 */
				return new RedirectResponseValues(requestInfo.getRedirectUrl(),
						HttpServletResponse.SC_SEE_OTHER);
			case NO_INDIVIDUAL:
				/*
				 * If we can't figure out what individual you want, or if there
				 * is no such individual, show an informative error page.
				 */
				return doNotFound();
			case BYTESTREAM_REDIRECT:
				/*
				 * If the Individual requested is a FileBytestream, redirect
				 * them to the direct download URL, so they will get the correct
				 * filename, etc.
				 */
				return new RedirectResponseValues(requestInfo.getRedirectUrl(),
						HttpServletResponse.SC_SEE_OTHER);
			case LINKED_DATA:
				/*
				 * If they are asking for RDF using the preferred URL, give it
				 * to them.
				 */
				return new IndividualRdfAssembler(vreq,
						requestInfo.getIndividual(), requestInfo.getRdfFormat())
						.assembleRdf();
			default:
				/*
				 * Otherwise, prepare an HTML response for the requested
				 * individual.
				 */
				return new IndividualResponseBuilder(vreq,
						requestInfo.getIndividual()).assembleResponse();
			}
		} catch (Throwable e) {
			log.error(e, e);
			return new ExceptionResponseValues(e);
		}
	}

	private IndividualRequestInfo analyzeTheRequest(VitroRequest vreq) {
		return new IndividualRequestAnalyzer(vreq,
				new IndividualRequestAnalysisContextImpl(vreq)).analyze();
	}

	private ResponseValues doNotFound() {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("title", "Individual Not Found");
		body.put("errorMessage", "The individual was not found in the system.");

		return new TemplateResponseValues(TEMPLATE_HELP, body,
				HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
