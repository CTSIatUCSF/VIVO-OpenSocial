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

import static edu.cornell.mannlib.vitro.webapp.controller.accounts.UserAccountsOrdering.DEFAULT_ORDERING;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.cornell.mannlib.vitro.webapp.controller.accounts.UserAccountsOrdering;
import edu.cornell.mannlib.vitro.webapp.controller.accounts.UserAccountsSelectionCriteria;

public class UserAccountsSelectionCriteriaTest {
	private UserAccountsSelectionCriteria criteria;

	@Test(expected = IllegalArgumentException.class)
	public void accountsPerPageOutOfRange() {
		criteria = create(0, 10, DEFAULT_ORDERING, "role", "search");
	}

	@Test(expected = IllegalArgumentException.class)
	public void pageIndexOutOfRange() {
		criteria = create(10, -1, DEFAULT_ORDERING, "role", "search");
	}

	@Test
	public void orderByIsNull() {
		criteria = create(10, 1, null, "role", "search");
		assertEquals("ordering", UserAccountsOrdering.DEFAULT_ORDERING,
				criteria.getOrderBy());
	}

	@Test
	public void roleFilterUriIsNull() {
		criteria = create(10, 1, DEFAULT_ORDERING, null, "search");
		assertEquals("roleFilter", "", criteria.getRoleFilterUri());
	}

	@Test
	public void searchTermIsNull() {
		criteria = create(10, 1, DEFAULT_ORDERING, "role", null);
		assertEquals("searchTerm", "", criteria.getSearchTerm());
	}

	private UserAccountsSelectionCriteria create(int accountsPerPage,
			int pageIndex, UserAccountsOrdering orderBy, String roleFilterUri,
			String searchTerm) {
		return new UserAccountsSelectionCriteria(accountsPerPage, pageIndex,
				orderBy, roleFilterUri, searchTerm);
	}

}
