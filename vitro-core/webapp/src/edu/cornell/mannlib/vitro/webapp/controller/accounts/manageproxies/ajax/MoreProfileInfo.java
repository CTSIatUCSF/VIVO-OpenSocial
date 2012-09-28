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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.manageproxies.ajax;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.AbstractAjaxResponder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.web.images.PlaceholderUtil;

/**
 * Get more information (class label and image URL) about a selected proxy.
 */
public class MoreProfileInfo extends AbstractAjaxResponder {
	private static final Log log = LogFactory.getLog(MoreProfileInfo.class);

	private static final String PARAMETER_PROFILE_URI = "uri";

	private final ObjectPropertyStatementDao opsDao;

	private final String profileUri;

	public MoreProfileInfo(HttpServlet servlet, VitroRequest vreq,
			HttpServletResponse resp) {
		super(servlet, vreq, resp);
		opsDao = vreq.getWebappDaoFactory().getObjectPropertyStatementDao();

		profileUri = getStringParameter(PARAMETER_PROFILE_URI, "");
	}

	@Override
	public String prepareResponse() throws IOException, JSONException {
		log.debug("profile URI is '" + profileUri + "'");
		if (profileUri.isEmpty()) {
			return EMPTY_RESPONSE;
		}

		Individual profileInd = indDao.getIndividualByURI(profileUri);
		if (profileInd == null) {
			log.debug("no such individual");
			return EMPTY_RESPONSE;
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("imageUrl", getFullImageUrl(profileInd));
		map.put("classLabel", getMostSpecificTypeLabel(profileInd.getURI()));

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(map);
		String response = jsonArray.toString();

		log.debug("response is '" + response + "'");
		return response;
	}

	private String getMostSpecificTypeLabel(String uri) {
		Map<String, String> types = opsDao
				.getMostSpecificTypesInClassgroupsForIndividual(uri);
		if (types.isEmpty()) {
			return "";
		} else {
			return types.values().iterator().next();
		}
	}

	private String getFullImageUrl(Individual ind) {
		String path = ind.getThumbUrl();
		if ((path == null) || path.isEmpty()) {
			path = PlaceholderUtil.getPlaceholderImagePathForIndividual(vreq,
					ind.getURI());
		}
		return UrlBuilder.getUrl(path);
	}
}
