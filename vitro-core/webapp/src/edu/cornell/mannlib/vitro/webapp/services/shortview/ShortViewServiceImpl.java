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

package edu.cornell.mannlib.vitro.webapp.services.shortview;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.services.freemarker.FreemarkerProcessingService;
import edu.cornell.mannlib.vitro.webapp.services.freemarker.FreemarkerProcessingService.TemplateParsingException;
import edu.cornell.mannlib.vitro.webapp.services.freemarker.FreemarkerProcessingService.TemplateProcessingException;
import edu.cornell.mannlib.vitro.webapp.services.freemarker.FreemarkerProcessingServiceSetup;
import edu.cornell.mannlib.vitro.webapp.services.shortview.FakeApplicationOntologyService.TemplateAndDataGetters;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.DataGetter;

/**
 * The basic implementation of ShortViewService
 */
public class ShortViewServiceImpl implements ShortViewService {
	private static final Log log = LogFactory
			.getLog(ShortViewServiceImpl.class);

	/*
	 * TODO this should use a real connection to the ApplicationOntology to find
	 * the short view to use for each individiual in a given context.
	 */
	private final FakeApplicationOntologyService faker;

	public ShortViewServiceImpl(FakeApplicationOntologyService faker) {
		this.faker = faker;
	}

	@Override
	public String renderShortView(Individual individual,
			ShortViewContext context, Map<String, Object> modelMap,
			VitroRequest vreq) {

		TemplateAndSupplementalData tsd = getShortViewInfo(individual, context,
				vreq);
		String templateName = tsd.getTemplateName();
		Map<String, Object> supplementalData = tsd.getSupplementalData();

		try {
			Map<String, Object> fullModelMap = new HashMap<String, Object>(
					modelMap);
			fullModelMap.putAll(supplementalData);

			FreemarkerProcessingService fps = FreemarkerProcessingServiceSetup
					.getService(vreq.getSession().getServletContext());

			if (!fps.isTemplateAvailable(templateName, vreq)) {
				return "<p>Can't find the short view template '" + templateName
						+ "' for " + individual.getName() + "</p>";
			}

			return fps.renderTemplate(templateName, fullModelMap, vreq);
		} catch (TemplateParsingException e) {
			log.error(e, e);
			return "<p>Can't parse the short view template '" + templateName
					+ "' for " + individual.getName() + "</p>";
		} catch (TemplateProcessingException e) {
			if (e.getCause() instanceof FileNotFoundException) {
				log.error(e);
				return "<p>Can't find the short view template '" + templateName
						+ "' for " + individual.getName() + "</p>";
			} else {
				log.error(e, e);
				return "<p>Can't process the short view template '"
						+ templateName + "' for " + individual.getName()
						+ "</p>";
			}
		} catch (Exception e) {
			log.error(e, e);
			return "<p>Failed to render the short view for "
					+ individual.getName() + "</p>";
		}
	}

	@Override
	public TemplateAndSupplementalData getShortViewInfo(Individual individual,
			ShortViewContext svContext, VitroRequest vreq) {
		TemplateAndDataGetters tdg = fetchTemplateAndDataGetters(individual,
				svContext, vreq);
		Map<String, Object> gotData = runDataGetters(tdg.getDataGetters(),
				individual);
		return new TemplateAndSupplementalDataImpl(tdg.getTemplateName(),
				gotData);
	}

	/** Get most specific classes from Individual, sorted by alpha. */
	private SortedSet<String> figureMostSpecificClassUris(Individual individual) {
		SortedSet<String> classUris = new TreeSet<String>();
		List<ObjectPropertyStatement> stmts = individual
				.getObjectPropertyStatements(VitroVocabulary.MOST_SPECIFIC_TYPE);
		for (ObjectPropertyStatement stmt : stmts) {
			classUris.add(stmt.getObjectURI());
		}
		return classUris;
	}

	/** Find the template and data getters for this individual in this context. */
	private TemplateAndDataGetters fetchTemplateAndDataGetters(
			Individual individual, ShortViewContext svContext, VitroRequest vreq) {
		List<String> classUris = new ArrayList<String>();
		classUris.addAll(figureMostSpecificClassUris(individual));

		for (String classUri : classUris) {
			TemplateAndDataGetters tdg = faker.getShortViewProperties(vreq,
					individual, classUri, svContext.name());
			if (tdg != null) {
				return tdg;
			}
		}

		// Didn't find one? Use the default values.
		return new TemplateAndDataGetters(svContext.getDefaultTemplateName());
	}

	/** Build a data map from the combined results of all data getters. */
	private Map<String, Object> runDataGetters(Set<DataGetter> dataGetters,
			Individual individual) {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("individualUri", individual.getURI());
		Map<String, Object> gotData = new HashMap<String, Object>();
		for (DataGetter dg : dataGetters) {
			gotData.putAll(dg.getData(valueMap));
		}
		return gotData;
	}

	private static class TemplateAndSupplementalDataImpl implements
			TemplateAndSupplementalData {
		private final String templateName;
		private final Map<String, Object> customData;

		public TemplateAndSupplementalDataImpl(String templateName,
				Map<String, Object> customData) {
			this.templateName = templateName;
			this.customData = customData;
		}

		@Override
		public String getTemplateName() {
			return templateName;
		}

		@Override
		public Map<String, Object> getSupplementalData() {
			return customData;
		}

	}
}
