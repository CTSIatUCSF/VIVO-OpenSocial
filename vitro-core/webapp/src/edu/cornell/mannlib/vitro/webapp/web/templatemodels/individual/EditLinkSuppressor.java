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

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * Sometimes we don't want to show an Add, Edit, or Delete link for a particular
 * property, no matter who the user is.
 * 
 * TODO These are hard-coded while we wait for the Application Ontology to be
 * implemented.
 */
public class EditLinkSuppressor {
	private static final Log log = LogFactory.getLog(EditLinkSuppressor.class);

	private static final String CORE = "http://vivoweb.org/ontology/core#";
	private static final String PUB_TO_AUTHORSHIP = core("informationResourceInAuthorship");
	private static final String PERSON_TO_AUTHORSHIP = core("authorInAuthorship");
	private static final String AUTHORSHIP_TO_PERSON = core("linkedAuthor");
	private static final String AUTHORSHIP_TO_PUB = core("linkedInformationResource");
	private static final String INDIVIDUAL_TO_WEBPAGE = core("webpage");
	private static final String WEBPAGE_TO_INDIVIDUAL = core("webpageOf");
	private static final String HAS_RESEARCH_AREA = core("hasResearchArea");
	private static final String HAS_SUBJECT_AREA = core("hasSubjectArea");
	private static final String RESEARCH_AREA_OF = core("researchAreaOf");
	private static final String SUBJECT_AREA_FOR = core("subjectAreaFor");

	private static String core(String localName) {
		return CORE + localName;
	}

	private static final List<String> suppressAddLinksForThese = Arrays
			.asList(new String[] { AUTHORSHIP_TO_PERSON, AUTHORSHIP_TO_PUB,
					WEBPAGE_TO_INDIVIDUAL });

	private static final List<String> suppressEditLinksForThese = Arrays
			.asList(new String[] { WEBPAGE_TO_INDIVIDUAL });

	private static final List<String> suppressDeleteLinksForThese = Arrays
			.asList(new String[] { PUB_TO_AUTHORSHIP, PERSON_TO_AUTHORSHIP,
					AUTHORSHIP_TO_PERSON, AUTHORSHIP_TO_PUB,
					INDIVIDUAL_TO_WEBPAGE, WEBPAGE_TO_INDIVIDUAL,
					HAS_RESEARCH_AREA, RESEARCH_AREA_OF, HAS_SUBJECT_AREA,
					SUBJECT_AREA_FOR });

	// TODO When we remove the hard-coding, vreq will allow us to find the
	// application ontology model.
	@SuppressWarnings("unused")
	private final VitroRequest vreq;

	public EditLinkSuppressor(VitroRequest vreq) {
		this.vreq = vreq;
	}

	/**
	 * Should we suppress the Add link on this property?
	 */
	public boolean isAddLinkSuppressed(String propertyUri) {
		if (propertyUri == null) {
			log.error("Suppressing the add link on a null property.");
			return true;
		}
		return suppressAddLinksForThese.contains(propertyUri);
	}

	/**
	 * Should we suppress the Edit link on this property?
	 */
	public boolean isEditLinkSuppressed(String propertyUri) {
		if (propertyUri == null) {
			log.error("Suppressing the edit link on a null property.");
			return true;
		}
		return suppressEditLinksForThese.contains(propertyUri);
	}

	/**
	 * Should we suppress the Delete link on this property?
	 */
	public boolean isDeleteLinkSuppressed(String propertyUri) {
		if (propertyUri == null) {
			log.error("Suppressing the delete link on a null property.");
			return true;
		}
		return suppressDeleteLinksForThese.contains(propertyUri);
	}
}
