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

package edu.cornell.mannlib.vitro.webapp.dao;

import java.util.Collection;

import edu.cornell.mannlib.vitro.webapp.beans.PermissionSet;
import edu.cornell.mannlib.vitro.webapp.beans.UserAccount;

/**
 * Methods for dealing with UserAccount and PermissionSet objects in the User
 * Accounts model.
 */
public interface UserAccountsDao {

	/**
	 * Get all of the UserAccounts in the model.
	 */
	Collection<UserAccount> getAllUserAccounts();

	/**
	 * Get the UserAccount for this URI.
	 * 
	 * @return null if the URI is null, or if there is no such UserAccount
	 */
	UserAccount getUserAccountByUri(String uri);

	/**
	 * Get the UserAccount for this Email address.
	 * 
	 * @return null if the Email address is null, or if there is no such
	 *         UserAccount
	 */
	UserAccount getUserAccountByEmail(String emailAddress);

	/**
	 * Get the UserAccount for this External Authentication ID
	 * 
	 * @return null if the ID is null, or if there is no such UserAccount
	 */
	UserAccount getUserAccountByExternalAuthId(String externalAuthId);
	
	/**
	 * Get any UserAccounts who act as proxy editors for this profile page.
	 */
	Collection<UserAccount> getUserAccountsWhoProxyForPage(String profilePageUri);

	/**
	 * Create a new UserAccount in the model.
	 * 
	 * On entry, the URI of the UserAccount should be empty. On exit, the URI
	 * which was created for this UserAccount will be stored in the UserAccount,
	 * as well as being returned by the method.
	 * 
	 * Does not confirm that PermissionSet objects already exist for the
	 * PermissionSet URIs referenced by the UserAcocunt.
	 * 
	 * @throws NullPointerException
	 *             if the UserAccount is null.
	 * @throws IllegalArgumentException
	 *             if the URI of the UserAccount is not empty.
	 */
	String insertUserAccount(UserAccount userAccount);

	/**
	 * Update the values on a UserAccount that already exists in the model.
	 * 
	 * Does not confirm that PermissionSet objects already exist for the
	 * PermissionSet URIs referenced by the UserAcocunt.
	 * 
	 * @throws NullPointerException
	 *             if the UserAccount is null.
	 * @throws IllegalArgumentException
	 *             if a UserAccount with this URI does not already exist in the
	 *             model.
	 */
	void updateUserAccount(UserAccount userAccount);

	/**
	 * Remove the UserAccount with this URI from the model.
	 * 
	 * If the URI is null, or if no UserAccount with this URI is found in the
	 * model, no action is taken.
	 */
	void deleteUserAccount(String userAccountUri);

	/**
	 * Set so that these UserAccounts, and only these, are authorized as proxies on this
	 * profile page.
	 */
	void setProxyAccountsOnProfile(String profilePageUri, Collection<String> userAccountUris);
	
	/**
	 * Get the PermissionSet for this URI.
	 * 
	 * @return null if the URI is null, or if there is no such PermissionSet.
	 */
	PermissionSet getPermissionSetByUri(String uri);

	/**
	 * Get all of the PermissionSets in the model, sorted by URI.
	 * 
	 * @return a collection which might be empty, but is never null.
	 */
	Collection<PermissionSet> getAllPermissionSets();

}
