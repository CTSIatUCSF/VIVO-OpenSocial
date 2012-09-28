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

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction.propstmt;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;

/**
 * A base class for requested actions that involve adding, editing, or dropping
 * data property statements from a model.
 */
public abstract class AbstractDataPropertyStatementAction extends
		AbstractPropertyStatementAction {
	private final String subjectUri;
	private final String predicateUri;

	public AbstractDataPropertyStatementAction(OntModel ontModel,
			String subjectUri, String predicateUri) {
		super(ontModel);
		this.subjectUri = subjectUri;
		this.predicateUri = predicateUri;
	}

	public AbstractDataPropertyStatementAction(OntModel ontModel,
			DataPropertyStatement dps) {
		super(ontModel);
		this.subjectUri = (dps.getIndividual() == null) ? dps
				.getIndividualURI() : dps.getIndividual().getURI();
		this.predicateUri = dps.getDatapropURI();
	}

	public String getSubjectUri() {
		return subjectUri;
	}

	@Override
	public String getPredicateUri() {
		return predicateUri;
	}

	@Override
	public String[] getResourceUris() {
		return new String[] {subjectUri};
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": <" + subjectUri + "> <"
				+ predicateUri + ">";
	}
}
