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

package edu.cornell.mannlib.vitro.webapp.dao.filtering.filters;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.jga.fn.UnaryFunctor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.RequestIdentifiers;
import edu.cornell.mannlib.vitro.webapp.auth.identifier.common.HasPermission;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.DisplayByRolePermission;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.Permission;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.PermissionRegistry;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayDataProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayDataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayObjectProperty;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.display.DisplayObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;

/**
 * Filter the properties depending on what DisplayByRolePermission is on the
 * request. If no request, or no permission, use the Public permission.
 */
public class FilterByRoleLevelPermission extends VitroFiltersImpl {
	private static final Log log = LogFactory
			.getLog(FilterByRoleLevelPermission.class);

	private final Permission permission;

	private static Permission getDefaultPermission(ServletContext ctx) {
		if (ctx == null) {
			throw new NullPointerException("context may not be null.");
		}

		return PermissionRegistry.getRegistry(ctx).getPermission(
				DisplayByRolePermission.NAMESPACE + "Public");
	}

	private static Permission getPermissionFromRequest(HttpServletRequest req) {
		if (req == null) {
			throw new NullPointerException("request may not be null.");
		}

		IdentifierBundle ids = RequestIdentifiers.getIdBundleForRequest(req);
		for (Permission p : HasPermission.getPermissions(ids)) {
			if (p instanceof DisplayByRolePermission) {
				return p;
			}
		}
		return getDefaultPermission(req.getSession().getServletContext());
	}

	/** Get the DisplayByRolePermission from the request, or use Public. */
	public FilterByRoleLevelPermission(HttpServletRequest req) {
		this(getPermissionFromRequest(req));
	}

	/** Use the Public permission. */
	public FilterByRoleLevelPermission(ServletContext ctx) {
		this(getDefaultPermission(ctx));
	}

	/** Use the specified permission. */
	public FilterByRoleLevelPermission(Permission permission) {
		if (permission == null) {
			throw new NullPointerException("permission may not be null.");
		}

		this.permission = permission;

		setDataPropertyFilter(new DataPropertyFilterByPolicy());
		setObjectPropertyFilter(new ObjectPropertyFilterByPolicy());
		setDataPropertyStatementFilter(new DataPropertyStatementFilterByPolicy());
		setObjectPropertyStatementFilter(new ObjectPropertyStatementFilterByPolicy());
	}

	boolean checkAuthorization(RequestedAction whatToAuth) {
		boolean decision = permission.isAuthorized(whatToAuth);
		log.debug("decision is " + decision);
		return decision;
	}

	private class DataPropertyFilterByPolicy extends
			UnaryFunctor<DataProperty, Boolean> {
		@Override
		public Boolean fn(DataProperty dp) {
			return checkAuthorization(new DisplayDataProperty(dp));
		}
	}

	private class ObjectPropertyFilterByPolicy extends
			UnaryFunctor<ObjectProperty, Boolean> {
		@Override
		public Boolean fn(ObjectProperty op) {
			return checkAuthorization(new DisplayObjectProperty(op));
		}
	}

	private class DataPropertyStatementFilterByPolicy extends
			UnaryFunctor<DataPropertyStatement, Boolean> {
		@Override
		public Boolean fn(DataPropertyStatement dps) {
			return checkAuthorization(new DisplayDataPropertyStatement(dps));
		}
	}

	private class ObjectPropertyStatementFilterByPolicy extends
			UnaryFunctor<ObjectPropertyStatement, Boolean> {
		@Override
		public Boolean fn(ObjectPropertyStatement ops) {
			return checkAuthorization(new DisplayObjectPropertyStatement(ops));
		}
	}

}
