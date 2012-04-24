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

package edu.cornell.mannlib.vitro.webapp.auth.requestedAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.identifier.IdentifierBundle;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.ifaces.RequestedAction;

/**
 * An immutable list of OR and AND relationships for the required
 * authorizations. A group of AND relationships is a "clause", and the list of
 * clauses are in an OR relationship.
 * 
 * Authorization is successful if ALL of the actions in ANY of the clauses are
 * authorized, or if there are NO clauses.
 */
public class Actions {
	private static final Log log = LogFactory.getLog(Actions.class);

	public static final Actions AUTHORIZED = new Actions();
	public static final Actions UNAUTHORIZED = new Actions(
			new UnauthorizedAction());

	public static Actions notNull(Actions actions) {
		return (actions == null) ? AUTHORIZED : actions;
	}

	private final List<Set<RequestedAction>> clauseList;

	public Actions(RequestedAction... actions) {
		this(Arrays.asList(actions));
	}

	public Actions(Collection<RequestedAction> actions) {
		this(Collections.<Set<RequestedAction>> emptyList(), actions);
	}

	private Actions(List<Set<RequestedAction>> oldList,
			Collection<RequestedAction> newActions) {
		List<Set<RequestedAction>> newList = new ArrayList<Set<RequestedAction>>();
		newList.addAll(oldList);

		Set<RequestedAction> newActionSet = new HashSet<RequestedAction>(
				newActions);
		if (!newActionSet.isEmpty()) {
			newList.add(Collections.unmodifiableSet(newActionSet));
		}
		this.clauseList = Collections.unmodifiableList(newList);
	}

	public Actions or(RequestedAction... newActions) {
		return or(Arrays.asList(newActions));
	}

	public Actions or(Collection<RequestedAction> newActions) {
		return new Actions(this.clauseList, newActions);
	}

	public boolean isEmpty() {
		for (Set<RequestedAction> clause : clauseList) {
			if (!clause.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/** No clauses means everything is authorized */
	public boolean isAuthorized(PolicyIface policy, IdentifierBundle ids) {
		if (clauseList.isEmpty()) {
			log.debug("Empty Actions is authorized");
			return true;
		}
		return isAuthorizedForClauseList(policy, ids);
	}

	/** Any entire clause is good enough. */
	private boolean isAuthorizedForClauseList(PolicyIface policy,
			IdentifierBundle ids) {
		for (Set<RequestedAction> clause : clauseList) {
			if (isAuthorizedForClause(policy, ids, clause)) {
				return true;
			}
		}
		return false;
	}

	/** All actions in a clause must be authorized. */
	private static boolean isAuthorizedForClause(PolicyIface policy,
			IdentifierBundle ids, Set<RequestedAction> clause) {
		for (RequestedAction action : clause) {
			if (!isAuthorizedForAction(policy, ids, action)) {
				log.debug("not authorized");
				return false;
			}
		}
		return true;
	}

	/** Is this action authorized? */
	private static boolean isAuthorizedForAction(PolicyIface policy,
			IdentifierBundle ids, RequestedAction action) {
		PolicyDecision decision = policy.isAuthorized(ids, action);
		log.debug("decision for '" + action.getClass().getSimpleName() + "' was: "
				+ decision);
		return (decision != null)
				&& (decision.getAuthorized() == Authorization.AUTHORIZED);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Actions[");
		for (Iterator<Set<RequestedAction>> cit = clauseList.iterator(); cit.hasNext();) {
			Set<RequestedAction> clause = cit.next();
			sb.append("(");
			for (Iterator<RequestedAction> it = clause.iterator(); it.hasNext();) {
				RequestedAction action = it.next();
				sb.append(action.getClass().getSimpleName());
				if (it.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append(")");
			if (cit.hasNext()) {
				sb.append(" or ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Nobody knows about this action class, so only the root user should be
	 * authorized for it.
	 */
	private static class UnauthorizedAction extends RequestedAction {
		// no members
	}
}
