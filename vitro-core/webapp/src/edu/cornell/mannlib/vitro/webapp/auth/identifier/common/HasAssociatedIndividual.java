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

package edu.cornell.mannlib.vitro.webapp.auth.identifier.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.Identifier;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;

/**
 * The current user is associated with this Individual page, and has editing
 * rights relating to it.
 * 
 * Subclasses exist to indicate how that association is created, as either a
 * Self-Editor or a Proxy Editor. In some cases (e.g., the MyProfile link) the
 * distinction is important.
 */
public abstract class HasAssociatedIndividual extends AbstractCommonIdentifier
		implements Identifier {

	private static Collection<HasAssociatedIndividual> getIdentifiers(
			IdentifierBundle ids) {
		return getIdentifiersForClass(ids, HasAssociatedIndividual.class);
	}

	public static Collection<String> getIndividualUris(IdentifierBundle ids) {
		Set<String> set = new HashSet<String>();
		for (HasAssociatedIndividual id : getIdentifiers(ids)) {
			set.add(id.getAssociatedIndividualUri());
		}
		return set;
	}

	private final String associatedIndividualUri;

	public HasAssociatedIndividual(String associatedIndividualUri) {
		this.associatedIndividualUri = associatedIndividualUri;
	}

	public String getAssociatedIndividualUri() {
		return associatedIndividualUri;
	}
}
