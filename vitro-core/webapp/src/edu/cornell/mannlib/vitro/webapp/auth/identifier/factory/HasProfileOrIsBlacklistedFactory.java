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

package edu.cornell.mannlib.vitro.webapp.auth.identifier.factory;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.ArrayIdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.Identifier;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasProfile;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.IsBlacklisted;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;

/**
 * Find all of the individuals that are associated with the current user, and
 * create either an IsBlacklisted or HasAssociatedIndividual for each one.
 */
public class HasProfileOrIsBlacklistedFactory extends
		BaseUserBasedIdentifierBundleFactory {
	private static final Log log = LogFactory
			.getLog(HasProfileOrIsBlacklistedFactory.class);

	public HasProfileOrIsBlacklistedFactory(ServletContext ctx) {
		super(ctx);
	}

	@Override
	public IdentifierBundle getIdentifierBundleForUser(UserAccount user) {
		ArrayIdentifierBundle ids = new ArrayIdentifierBundle();

		for (Individual ind : getAssociatedIndividuals(user)) {
			// If they are blacklisted, this factory will return an identifier
			Identifier id = IsBlacklisted.getInstance(ind, ctx);
			if (id != null) {
				ids.add(id);
			} else {
				ids.add(new HasProfile(ind.getURI()));
			}
		}

		return ids;
	}

	/**
	 * Get all Individuals associated with the current user as SELF.
	 */
	private Collection<Individual> getAssociatedIndividuals(UserAccount user) {
		Collection<Individual> individuals = new ArrayList<Individual>();

		if (user == null) {
			log.debug("No Associated Individuals: not logged in.");
			return individuals;
		}

		SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(ctx);
		individuals.addAll(sec.getAssociatedIndividuals(indDao, user));

		return individuals;
	}
}