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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.services.shortview.ShortViewService;
import edu.cornell.mannlib.vitro.webapp.services.shortview.ShortViewService.ShortViewContext;
import edu.cornell.mannlib.vitro.webapp.services.shortview.ShortViewServiceSetup;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModel;

/**
 * Does a Solr search for individuals, and uses the short view to render each of
 * the results.
 */
public class GetRenderedSolrIndividualsByVClass extends JsonObjectProducer {
	private static final Log log = LogFactory
			.getLog(GetRenderedSolrIndividualsByVClass.class);

	protected GetRenderedSolrIndividualsByVClass(VitroRequest vreq) {
		super(vreq);
	}

	/**
	 * Search for individuals by VClass. The class URI and the paging
	 * information are in the request parameters.
	 */
	@Override
	protected JSONObject process() throws Exception {
		JSONObject rObj = null;
		VClass vclass = getVclassParameter(vreq);
		String vclassId = vclass.getURI();

		vreq.setAttribute("displayType", vclassId);
		rObj = JsonServlet.getSolrIndividualsByVClass(vclassId, vreq, ctx);
		addShortViewRenderings(rObj);

		return rObj;
	}

	/**
	 * Look through the return object. For each individual, render the short
	 * view and insert the resulting HTML into the object.
	 */
	private void addShortViewRenderings(JSONObject rObj) throws JSONException {
		JSONArray individuals = rObj.getJSONArray("individuals");
		String vclassName = rObj.getJSONObject("vclass").getString("name");
		for (int i = 0; i < individuals.length(); i++) {
			JSONObject individual = individuals.getJSONObject(i);
			individual.put("shortViewHtml",
					renderShortView(individual.getString("URI"), vclassName));
		}
	}

	private String renderShortView(String individualUri, String vclassName) {
		IndividualDao iDao = vreq.getWebappDaoFactory().getIndividualDao();
		Individual individual = iDao.getIndividualByURI(individualUri);

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("individual",
				new IndividualTemplateModel(individual, vreq));
		modelMap.put("vclass", vclassName);

		ShortViewService svs = ShortViewServiceSetup.getService(ctx);
		return svs.renderShortView(individual, ShortViewContext.BROWSE,
				modelMap, vreq);
	}
}
