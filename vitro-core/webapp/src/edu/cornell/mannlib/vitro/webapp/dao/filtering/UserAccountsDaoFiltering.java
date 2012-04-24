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

package edu.cornell.mannlib.vitro.webapp.dao.filtering;

import java.util.Collection;

import edu.cornell.mannlib.vitro.webapp.beans.PermissionSet;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;
import edu.cornell.mannlib.vitro.webapp.dao.UserAccountsDao;
import edu.cornell.mannlib.vitro.webapp.dao.filtering.filters.VitroFilters;

/**
 * This doesn't actually do any filtering. It's just a placeholder in case we
 * decide to filter either UserAccounts or PermissionSets.
 */
public class UserAccountsDaoFiltering extends BaseFiltering implements
		UserAccountsDao {

	private final UserAccountsDao innerDao;

	@SuppressWarnings("unused")
	private final VitroFilters filters;

	public UserAccountsDaoFiltering(UserAccountsDao userDao,
			VitroFilters filters) {
		this.innerDao = userDao;
		this.filters = filters;
	}

	@Override
	public Collection<UserAccount> getAllUserAccounts() {
		return innerDao.getAllUserAccounts();
	}

	@Override
	public UserAccount getUserAccountByUri(String uri) {
		return innerDao.getUserAccountByUri(uri);
	}

	@Override
	public UserAccount getUserAccountByEmail(String emailAddress) {
		return innerDao.getUserAccountByEmail(emailAddress);
	}

	@Override
	public UserAccount getUserAccountByExternalAuthId(String externalAuthId) {
		return innerDao.getUserAccountByExternalAuthId(externalAuthId);
	}

	@Override
	public Collection<UserAccount> getUserAccountsWhoProxyForPage(
			String profilePageUri) {
		return innerDao.getUserAccountsWhoProxyForPage(profilePageUri);
	}

	@Override
	public String insertUserAccount(UserAccount userAccount) {
		return innerDao.insertUserAccount(userAccount);
	}

	@Override
	public void updateUserAccount(UserAccount userAccount) {
		innerDao.updateUserAccount(userAccount);
	}

	@Override
	public void deleteUserAccount(String userAccountUri) {
		innerDao.deleteUserAccount(userAccountUri);
	}

	@Override
	public void setProxyAccountsOnProfile(String profilePageUri,
			Collection<String> userAccountUris) {
		innerDao.setProxyAccountsOnProfile(profilePageUri, userAccountUris);
	}

	@Override
	public PermissionSet getPermissionSetByUri(String uri) {
		return innerDao.getPermissionSetByUri(uri);
	}

	@Override
	public Collection<PermissionSet> getAllPermissionSets() {
		return innerDao.getAllPermissionSets();
	}

}