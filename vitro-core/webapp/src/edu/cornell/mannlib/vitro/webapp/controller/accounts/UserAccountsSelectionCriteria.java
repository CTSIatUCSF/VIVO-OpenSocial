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

package edu.cornell.mannlib.vitro.webapp.controller.accounts;

/**
 * On what basis are we selecting user accounts?
 * 
 * Search terms are matched against email, and against firstName combined with
 * lastName. Searches are case-insensitive.
 */
public class UserAccountsSelectionCriteria {
	public static final int DEFAULT_ACCOUNTS_PER_PAGE = 25;

	public static final UserAccountsSelectionCriteria DEFAULT_CRITERIA = new UserAccountsSelectionCriteria(
			DEFAULT_ACCOUNTS_PER_PAGE, 1,
			UserAccountsOrdering.DEFAULT_ORDERING, "", "");

	/** How many accounts should we bring back, at most? */
	private final int accountsPerPage;

	/** What page are we on? (1-origin) */
	private final int pageIndex;

	/** How are they sorted? */
	private final UserAccountsOrdering orderBy;

	/** What role are we filtering by, if any? */
	private final String roleFilterUri;

	/** What term are we searching on, if any? */
	private final String searchTerm;

	public UserAccountsSelectionCriteria(int accountsPerPage, int pageIndex,
			UserAccountsOrdering orderBy, String roleFilterUri,
			String searchTerm) {
		if (accountsPerPage <= 0) {
			throw new IllegalArgumentException("accountsPerPage "
					+ "must be a positive integer, not " + accountsPerPage);
		}
		this.accountsPerPage = accountsPerPage;

		if (pageIndex <= 0) {
			throw new IllegalArgumentException("pageIndex must be a "
					+ "non-negative integer, not " + pageIndex);
		}
		this.pageIndex = pageIndex;

		this.orderBy = nonNull(orderBy, UserAccountsOrdering.DEFAULT_ORDERING);

		this.roleFilterUri = nonNull(roleFilterUri, "");
		this.searchTerm = nonNull(searchTerm, "");
	}

	public int getAccountsPerPage() {
		return accountsPerPage;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public UserAccountsOrdering getOrderBy() {
		return orderBy;
	}

	public String getRoleFilterUri() {
		return roleFilterUri;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	private <T> T nonNull(T t, T nullValue) {
		return (t == null) ? nullValue : t;
	}

	@Override
	public String toString() {
		return "UserAccountsSelectionCriteria[accountsPerPage="
				+ accountsPerPage + ", pageIndex=" + pageIndex + ", orderBy="
				+ orderBy + ", roleFilterUri='" + roleFilterUri
				+ "', searchTerm='" + searchTerm + "']";
	}
}
