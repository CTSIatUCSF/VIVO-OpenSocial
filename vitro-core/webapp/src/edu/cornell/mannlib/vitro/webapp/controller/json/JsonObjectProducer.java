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
import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * A base for classes that produce a JSON object, based on the parameters in the
 * request.
 * 
 * The result is never empty. At worst, it is an object that contains only an
 * "errorMessage" field.
 * 
 * If an exception occurrs during processing, The "errorMessage" field will
 * contain the exception message and the response status will be set to 500
 * (server error). Normally, "errorMessage" will be empty, and the status will
 * default to 200 (OK).
 */
public abstract class JsonObjectProducer extends JsonProducer {
	private static final Log log = LogFactory.getLog(JsonObjectProducer.class);

	protected final VitroRequest vreq;
	protected final ServletContext ctx;

	protected JsonObjectProducer(VitroRequest vreq) {
		this.vreq = vreq;
		this.ctx = vreq.getSession().getServletContext();
	}

	/**
	 * Sub-classes implement this method. Given the request, produce a JSON
	 * object as the result.
	 */
	protected abstract JSONObject process() throws Exception;

	public final void process(HttpServletResponse resp) throws IOException {
		JSONObject jsonObject = null;
		String errorMessage = "";

		try {
			jsonObject = process();
		} catch (Exception e) {
			log.error("Failed to create JSON response" + e);
			errorMessage = e.toString();
			resp.setStatus(500 /* HttpURLConnection.HTTP_SERVER_ERROR */);
		}

		if (jsonObject == null) {
			jsonObject = new JSONObject();
		}

		log.debug("Response to JSON request: " + jsonObject.toString());

		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json;charset=UTF-8");
			Writer writer = resp.getWriter();

			jsonObject.put("errorMessage", errorMessage);
			writer.write(jsonObject.toString());
		} catch (JSONException e) {
			log.error(e, e);
		}
	}
}
