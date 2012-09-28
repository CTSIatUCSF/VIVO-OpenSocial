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

package edu.cornell.mannlib.vitro.webapp.controller.json;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * A base for classes that produce a JSON array based on the parameters in the
 * VitroRequest.
 * 
 * Catches any exceptions. Logs the error and returns an empty JSON array.
 */
public abstract class JsonArrayProducer extends JsonProducer {
	private static final Log log = LogFactory.getLog(JsonArrayProducer.class);

	protected final VitroRequest vreq;
	protected final ServletContext ctx;

	protected JsonArrayProducer(VitroRequest vreq) {
		this.vreq = vreq;
		this.ctx = vreq.getSession().getServletContext();
	}

	/**
	 * Sub-classes implement this method. Given the request, produce a JSON
	 * object as the result.
	 */
	protected abstract JSONArray process() throws Exception;

	public final void process(HttpServletResponse resp) throws IOException {
		JSONArray jsonArray = null;
		try {
			jsonArray = process();
		} catch (Exception e) {
			log.error("Failed to create JSON response" + e);
			resp.setStatus(500 /* HttpURLConnection.HTTP_SERVER_ERROR */);
		}

		if (jsonArray == null) {
			jsonArray = new JSONArray();
		}

		log.debug("Response to JSON request: " + jsonArray.toString());

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json;charset=UTF-8");
		Writer writer = resp.getWriter();
		writer.write(jsonArray.toString());
	}
}
