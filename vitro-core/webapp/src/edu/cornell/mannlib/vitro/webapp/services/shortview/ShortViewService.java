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

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * Define a service that will produce HTML snippets for short views on
 * Individuals.
 */
public interface ShortViewService {

	/**
	 * Render the short view template that applies to this individual in this
	 * context. The data in the modelMap can be used to populate the template,
	 * along with any additional data returned by custom data getters.
	 * 
	 * If there are any problems, return a dummy piece of text that includes the
	 * label of the individual. Never return null or empty string.
	 * 
	 * This method should not be called from within an ongoing Freemarker
	 * process. In that case, use getShortViewInfo() instead.
	 */
	String renderShortView(Individual individual, ShortViewContext context,
			Map<String, Object> modelMap, VitroRequest vreq);

	/**
	 * What template should be used to render the short view of this individual
	 * in this context? What data is available from custom data getters?
	 * 
	 * Ask the Application Ontology for short view specifications on each of the
	 * most specific classes for this individual. If more than one such class
	 * has an applicable short view, the class with with the first URI
	 * (alphabetically) will be used.
	 */
	TemplateAndSupplementalData getShortViewInfo(Individual individual,
			ShortViewContext svContext, VitroRequest vreq);

	/**
	 * The information associated with a particular short view.
	 */
	public interface TemplateAndSupplementalData {
		/**
		 * The name of the template to be used in the short view.
		 * 
		 * Either the custom view assigned to the individual and context, or the
		 * default view. Never empty or null, but it might refer to a template
		 * that can't be located.
		 */
		String getTemplateName();

		/**
		 * The results of any custom data getters were associated with this
		 * individual in this short view context.
		 * 
		 * May be empty, but never null.
		 */
		Map<String, Object> getSupplementalData();
	}

	/**
	 * The available contexts for short views.
	 */
	public enum ShortViewContext {
		SEARCH("view-search-default.ftl"), INDEX("view-index-default.ftl"), BROWSE(
				"view-browse-default.ftl");

		private final String defaultTemplateName;

		ShortViewContext(String defaultTemplateName) {
			this.defaultTemplateName = defaultTemplateName;
		}

		public String getDefaultTemplateName() {
			return defaultTemplateName;
		}

		public static ShortViewContext fromString(String string) {
			for (ShortViewContext c : ShortViewContext.values()) {
				if (c.name().equalsIgnoreCase(string)) {
					return c;
				}
			}
			return null;
		}

		public static String valueList() {
			return StringUtils.join(ShortViewContext.values(), ", ");
		}
	}

}
