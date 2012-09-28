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

package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.Dataset;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.DataVisualizationController;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.IllegalConstructedModelIdentifierException;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModel;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

public class ModelConstructorRequestHandler implements
		VisualizationRequestHandler {

    public static final Actions REQUIRED_ACTIONS = SimplePermission.REFRESH_VISUALIZATION_CACHE.ACTIONS;
    
	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {
		return regenerateConstructedModels(vitroRequest, dataSource);
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException(
				"Cached Model does not provide Data Response.");
	}

	private ResponseValues renderRefreshCacheMarkup(VitroRequest vitroRequest,
			Log log, Dataset dataSource) {

		String standaloneTemplate = "regenerateConstructedModels.ftl";

		List<ConstructedModel> currentConstructedModels = new ArrayList<ConstructedModel>();
		List<String> unidentifiedModels = new ArrayList<String>();

		for (String currentIdentifier : ConstructedModelTracker.getAllModels()
				.keySet()) {
			try {
				ConstructedModel parseModelIdentifier = ConstructedModelTracker
						.parseModelIdentifier(currentIdentifier);

				parseModelIdentifier.setIndividualLabel(UtilityFunctions
						.getIndividualLabelFromDAO(vitroRequest,
								parseModelIdentifier.getUri()));

				currentConstructedModels.add(parseModelIdentifier);
			} catch (IllegalConstructedModelIdentifierException e) {
				unidentifiedModels.add(e.getMessage());
			}
		}

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("title", "Regenerate Constructed Models");
		body.put("vivoDefaultNamespace", vitroRequest.getWebappDaoFactory()
				.getDefaultNamespace());
		body.put("currentModels", currentConstructedModels);
		body.put("unidentifiedModels", unidentifiedModels);

		return new TemplateResponseValues(standaloneTemplate, body);
	}

	private Map<String, String> regenerateConstructedModels(VitroRequest vitroRequest, 
															Dataset dataSource) {

		List<ConstructedModel> refreshedModels = new ArrayList<ConstructedModel>();

		Set<String> currentModelIdentifiers = new HashSet<String>(ConstructedModelTracker.getAllModels().keySet());
		
		for (String currentIdentifier : currentModelIdentifiers) {
			try {

				ConstructedModel parseModelIdentifier = ConstructedModelTracker
																.parseModelIdentifier(currentIdentifier);

				ConstructedModelTracker.removeModel(parseModelIdentifier.getUri(), 
													parseModelIdentifier.getType());

				ModelConstructorUtilities.getOrConstructModel(parseModelIdentifier.getUri(), 
															  parseModelIdentifier.getType(), dataSource);
				refreshedModels.add(parseModelIdentifier);

			} catch (IllegalConstructedModelIdentifierException e) {
				e.printStackTrace();
			} catch (MalformedQueryParametersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Map<String, String> fileData = new HashMap<String, String>();

		fileData.put(DataVisualizationController.FILE_CONTENT_TYPE_KEY,
				"application/octet-stream");
		
		Gson json = new Gson();
		
		fileData.put(DataVisualizationController.FILE_CONTENT_KEY,
				json.toJson(refreshedModels));
		return fileData;
	}

	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataSource)
			throws MalformedQueryParametersException {

		return renderRefreshCacheMarkup(vitroRequest, log, dataSource);
	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataSource) throws MalformedQueryParametersException {

		return renderRefreshCacheMarkup(vitroRequest, log, dataSource);
	}

	@Override
	public Actions getRequiredPrivileges() {
		return REQUIRED_ACTIONS;
	}

}
