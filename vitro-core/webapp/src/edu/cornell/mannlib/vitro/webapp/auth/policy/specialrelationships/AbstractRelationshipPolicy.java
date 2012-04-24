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

package edu.cornell.mannlib.vitro.webapp.auth.policy.specialrelationships;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.auth.policy.BasicPolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.bean.PropertyRestrictionPolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyIface;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * A collection of building-block methods so we can code a policy based on the
 * relationship of the object being edited to the identity of the user doing the
 * editing.
 */
public abstract class AbstractRelationshipPolicy implements PolicyIface {
	private static final Log log = LogFactory
			.getLog(AbstractRelationshipPolicy.class);

	protected final ServletContext ctx;
	protected final OntModel model;

	public AbstractRelationshipPolicy(ServletContext ctx, OntModel model) {
		this.ctx = ctx;
		this.model = model;
	}

	protected boolean canModifyResource(String uri) {
		return PropertyRestrictionPolicyHelper.getBean(ctx).canModifyResource(
				uri, RoleLevel.SELF);
	}

	protected boolean canModifyPredicate(String uri) {
		return PropertyRestrictionPolicyHelper.getBean(ctx).canModifyPredicate(
				uri, RoleLevel.SELF);
	}

	protected boolean isResourceOfType(String resourceUri, String typeUri) {
		Selector selector = createSelector(resourceUri,
				VitroVocabulary.RDF_TYPE, typeUri);

		StmtIterator stmts = null;
		model.enterCriticalSection(Lock.READ);
		try {
			stmts = model.listStatements(selector);
			if (stmts.hasNext()) {
				log.debug("resource '" + resourceUri + "' is of type '"
						+ typeUri + "'");
				return true;
			} else {
				log.debug("resource '" + resourceUri + "' is not of type '"
						+ typeUri + "'");
				return false;
			}
		} finally {
			if (stmts != null) {
				stmts.close();
			}
			model.leaveCriticalSection();
		}
	}

	protected List<String> getObjectsOfProperty(String resourceUri,
			String propertyUri) {
		List<String> list = new ArrayList<String>();

		Selector selector = createSelector(resourceUri, propertyUri, null);

		StmtIterator stmts = null;
		model.enterCriticalSection(Lock.READ);
		try {
			stmts = model.listStatements(selector);
			while (stmts.hasNext()) {
				list.add(stmts.next().getObject().toString());
			}
			log.debug("Objects of property '" + propertyUri + "' on resource '"
					+ resourceUri + "': " + list);
			return list;
		} finally {
			if (stmts != null) {
				stmts.close();
			}
			model.leaveCriticalSection();
		}
	}

	protected List<String> getObjectsOfLinkedProperty(String resourceUri,
			String linkUri, String propertyUri) {
		List<String> list = new ArrayList<String>();

		Selector selector = createSelector(resourceUri, linkUri, null);

		StmtIterator stmts = null;

		model.enterCriticalSection(Lock.READ);
		try {
			stmts = model.listStatements(selector);
			while (stmts.hasNext()) {
				RDFNode objectNode = stmts.next().getObject();
				if (objectNode.isResource()) {
					log.debug("found authorship for '" + resourceUri + "': "
							+ objectNode);
					list.addAll(getUrisOfAuthors(objectNode.asResource(),
							propertyUri));
				}
			}
			log.debug("Objects of linked properties '" + linkUri + "' ==> '"
					+ propertyUri + "' on '" + resourceUri + "': " + list);
			return list;
		} finally {
			if (stmts != null) {
				stmts.close();
			}
			model.leaveCriticalSection();
		}
	}

	/** Note that we must already be in a critical section! */
	private List<String> getUrisOfAuthors(Resource authorship,
			String propertyUri) {
		List<String> list = new ArrayList<String>();

		Selector selector = createSelector(authorship, propertyUri, null);

		StmtIterator stmts = null;
		try {
			stmts = model.listStatements(selector);
			while (stmts.hasNext()) {
				list.add(stmts.next().getObject().toString());
			}
			return list;
		} finally {
			if (stmts != null) {
				stmts.close();
			}
		}
	}

	protected Selector createSelector(String subjectUri, String predicateUri,
			String objectUri) {
		Resource subject = (subjectUri == null) ? null : model
				.getResource(subjectUri);
		return createSelector(subject, predicateUri, objectUri);
	}

	protected Selector createSelector(Resource subject, String predicateUri,
			String objectUri) {
		Property predicate = (predicateUri == null) ? null : model
				.getProperty(predicateUri);
		RDFNode object = (objectUri == null) ? null : model
				.getResource(objectUri);
		return new SimpleSelector(subject, predicate, object);
	}

	protected boolean anyUrisInCommon(List<String> userUris,
			List<String> editorsOrAuthors) {
		for (String userUri : userUris) {
			if (editorsOrAuthors.contains(userUri)) {
				return true;
			}
		}
		return false;
	}

	protected PolicyDecision cantModifyResource(String uri) {
		return inconclusiveDecision("No access to admin resources; cannot modify "
				+ uri);
	}

	protected PolicyDecision cantModifyPredicate(String uri) {
		return inconclusiveDecision("No access to admin predicates; cannot modify "
				+ uri);
	}

	protected PolicyDecision userNotAuthorizedToStatement() {
		return inconclusiveDecision("User has no access to this statement.");
	}

	/** An INCONCLUSIVE decision with a message like "PolicyClass: message". */
	protected PolicyDecision inconclusiveDecision(String message) {
		return new BasicPolicyDecision(Authorization.INCONCLUSIVE, getClass()
				.getSimpleName() + ": " + message);
	}

	/** An AUTHORIZED decision with a message like "PolicyClass: message". */
	protected PolicyDecision authorizedDecision(String message) {
		return new BasicPolicyDecision(Authorization.AUTHORIZED, getClass()
				.getSimpleName() + ": " + message);
	}

}
