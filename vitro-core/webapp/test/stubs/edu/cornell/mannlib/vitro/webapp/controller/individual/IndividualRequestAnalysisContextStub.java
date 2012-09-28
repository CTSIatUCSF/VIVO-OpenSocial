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

package stubs.edu.cornell.mannlib.vitro.webapp.controller.individual;

import java.util.HashMap;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalysisContext;

/**
 * A simple implementation of the Analysis Context for the Individual Request.
 */
public class IndividualRequestAnalysisContextStub implements
		IndividualRequestAnalysisContext {

	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------

	private final String defaultNamespace;
	private final Map<String, Individual> individualsByUri = new HashMap<String, Individual>();
	private final Map<String, Individual> profilePages = new HashMap<String, Individual>();
	private final Map<String, String> namespacesByPrefix = new HashMap<String, String>();
	private final Map<String, String> aliasUrlsByIndividual = new HashMap<String, String>();

	public IndividualRequestAnalysisContextStub(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	public void addIndividual(Individual individual) {
		individualsByUri.put(individual.getURI(), individual);
	}

	public void addProfilePage(String netId, Individual individual) {
		profilePages.put(netId, individual);
	}

	public void setNamespacePrefix(String prefix, String namespace) {
		namespacesByPrefix.put(prefix, namespace);
	}

	public void setAliasUrl(String individualUri, String aliasUrl) {
		aliasUrlsByIndividual.put(individualUri, aliasUrl);
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

	@Override
	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	@Override
	public String getNamespaceForPrefix(String prefix) {
		if (prefix == null) {
			return "";
		}
		String namespace = namespacesByPrefix.get(prefix);
		return (namespace == null) ? "" : namespace;
	}

	@Override
	public Individual getIndividualByURI(String individualUri) {
		if (individualUri == null) {
			return null;

		}
		return individualsByUri.get(individualUri);
	}

	@Override
	public Individual getIndividualByNetId(String netId) {
		if (netId == null) {
			return null;
		}
		return profilePages.get(netId);
	}

	@Override
	public String getAliasUrlForBytestreamIndividual(Individual individual) {
		if (individual == null) {
			return null;
		}
		return aliasUrlsByIndividual.get(individual.getURI());
	}
}
