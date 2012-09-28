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

package edu.cornell.mannlib.vivo.auth.policy;

import java.util.List;

import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships.RelationshipChecker;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt.AbstractPropertyStatementAction;

/**
 * Does the requested action involve a change to an Information Resource that
 * the self-editor is authorized to modify?
 */
public class InformationResourceChecker extends RelationshipChecker {
	private static final String NS_CORE = "http://vivoweb.org/ontology/core#";
	private static final String URI_INFORMATION_RESOURCE_TYPE = NS_CORE
			+ "InformationResource";
	private static final String URI_EDITOR_PROPERTY = "http://purl.org/ontology/bibo/editor";
	private static final String URI_FEATURES_PROPERTY = NS_CORE + "features";
	private static final String URI_IN_AUTHORSHIP_PROPERTY = NS_CORE
			+ "informationResourceInAuthorship";
	private static final String URI_LINKED_AUTHOR_PROPERTY = NS_CORE
			+ "linkedAuthor";

	private final String[] resourceUris;

	public InformationResourceChecker(AbstractPropertyStatementAction action) {
		super(action.getOntModel());
		this.resourceUris = action.getResourceUris();
	}

	/**
	 * A self-editor is authorized to add, edit, or delete a statement if the
	 * subject or object refers to an Information Resource, and if the
	 * self-editor:
	 * 
	 * 1) is an Author of that Information Resource
	 * 
	 * 2) is an Editor of that Information Resource, or
	 * 
	 * 3) is Featured in that Information Resource.
	 */
	public PolicyDecision isAuthorized(List<String> userUris) {
		for (String resourceUri : resourceUris) {
			if (isInformationResource(resourceUri)) {
				if (anyUrisInCommon(userUris, getUrisOfEditors(resourceUri))) {
					return authorizedEditor(resourceUri);
				}
				if (anyUrisInCommon(userUris, getUrisOfAuthors(resourceUri))) {
					return authorizedAuthor(resourceUri);
				}
				if (anyUrisInCommon(userUris, getUrisOfFeatured(resourceUri))) {
					return authorizedFeatured(resourceUri);
				}
			}
		}
		return null;
	}

	// ----------------------------------------------------------------------
	// methods for InformationResource
	// ----------------------------------------------------------------------

	private boolean isInformationResource(String resourceUri) {
		return isResourceOfType(resourceUri, URI_INFORMATION_RESOURCE_TYPE);
	}

	private List<String> getUrisOfEditors(String resourceUri) {
		return getObjectsOfProperty(resourceUri, URI_EDITOR_PROPERTY);
	}

	private List<String> getUrisOfFeatured(String resourceUri) {
		return getObjectsOfProperty(resourceUri, URI_FEATURES_PROPERTY);
	}

	private List<String> getUrisOfAuthors(String resourceUri) {
		return getObjectsOfLinkedProperty(resourceUri,
				URI_IN_AUTHORSHIP_PROPERTY, URI_LINKED_AUTHOR_PROPERTY);
	}

	private PolicyDecision authorizedEditor(String uri) {
		return authorizedDecision("User is an editor of " + uri);
	}

	private PolicyDecision authorizedAuthor(String uri) {
		return authorizedDecision("User is author of " + uri);
	}

	private PolicyDecision authorizedFeatured(String uri) {
		return authorizedDecision("User is featured in " + uri);
	}

}
