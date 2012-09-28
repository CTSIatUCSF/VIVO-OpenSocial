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
import edu.cornell.mannlib.vitro.webapp.auth.permissions.Permission;
import edu.cornell.mannlib.vitro.webapp.beans.PermissionSet;

/**
 * The current user has this Permission, through one or more PermissionSets.
 */
public class HasPermissionSet extends AbstractCommonIdentifier implements
		Identifier, Comparable<HasPermissionSet> {
	public static Collection<HasPermission> getIdentifiers(IdentifierBundle ids) {
		return getIdentifiersForClass(ids, HasPermission.class);
	}

	public static Collection<Permission> getPermissions(IdentifierBundle ids) {
		Set<Permission> set = new HashSet<Permission>();
		for (HasPermission id : getIdentifiers(ids)) {
			set.add(id.getPermission());
		}
		return set;
	}

	private final PermissionSet permissionSet; // never null

	public HasPermissionSet(PermissionSet permissionSet) {
		if (permissionSet == null) {
			throw new NullPointerException("permissionSet may not be null.");
		}
		this.permissionSet = permissionSet;
	}

	public PermissionSet getPermissionSet() {
		return permissionSet;
	}

	@Override
	public String toString() {
		return "HasPermissionSet[" + permissionSet.getLabel() + "]";
	}

	@Override
	public int hashCode() {
		return permissionSet.getUri().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HasPermissionSet)) {
			return false;
		}
		HasPermissionSet that = (HasPermissionSet) obj;
		return this.permissionSet.getUri().equals(that.permissionSet.getUri());
	}

	@Override
	public int compareTo(HasPermissionSet that) {
		return this.permissionSet.getUri().compareTo(
				that.permissionSet.getUri());
	}
}
