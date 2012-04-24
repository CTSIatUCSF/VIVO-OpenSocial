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

package edu.cornell.mannlib.vitro.webapp.controller.accounts.admin;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.InsertException;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Create a new profile with the given VClass URI, and info from the user account.
 */
public class UserAccountsProfileCreator {
	private static final String URI_FOAF_FIRST_NAME = "http://xmlns.com/foaf/0.1/firstName";
	private static final String URI_FOAF_LAST_NAME = "http://xmlns.com/foaf/0.1/lastName";

	public static String createProfile(IndividualDao indDao,
			DataPropertyStatementDao dpsDao, String profileClassUri,
			UserAccount account) throws InsertException {
		IndividualImpl i = new IndividualImpl();
		i.setVClassURI(profileClassUri);
		String indUri = indDao.insertNewIndividual(i);

		addProp(dpsDao, indUri, URI_FOAF_FIRST_NAME, account.getFirstName());
		addProp(dpsDao, indUri, URI_FOAF_LAST_NAME, account.getLastName());

		String label = account.getLastName() + ", " + account.getFirstName();
		addProp(dpsDao, indUri, VitroVocabulary.LABEL, label);
		
		return indUri;
	}

	private static void addProp(DataPropertyStatementDao dpsDao, String indUri,
			String propertyUri, String value) {
		DataPropertyStatementImpl dps = new DataPropertyStatementImpl(indUri,
				propertyUri, value);
		dpsDao.insertNewDataPropertyStatement(dps);
	}
}
