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
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.Authorization;
import edu.cornell.mannlib.vitro.webapp.auth.policy.ifaces.PolicyDecision;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Look for relationships within an OntModel. Types of resources, links between
 * resources, and links between resources via a context node.
 * 
 * Also provides some convenience methods for test lists of URIs and for
 * creating PolicyDecisions.
 */
public class RelationshipChecker {
	private static final Log log = LogFactory.getLog(RelationshipChecker.class);

	private final OntModel ontModel;

	public RelationshipChecker(OntModel ontModel) {
		this.ontModel = ontModel;
	}

	/**
	 * Are there any URIs that appear in both of these lists?
	 */
	public boolean anyUrisInCommon(List<String> list1, List<String> list2) {
		List<String> urisInCommon = new ArrayList<String>(list1);
		urisInCommon.retainAll(list2);
		return !urisInCommon.isEmpty();
	}

	/**
	 * Is this resource a member of this type? That is, is there an statement of
	 * the form: <resourceUri> rdfs:type <typeUri>
	 */
	public boolean isResourceOfType(String resourceUri, String typeUri) {
		Selector selector = createSelector(resourceUri,
				VitroVocabulary.RDF_TYPE, typeUri);

		StmtIterator stmts = null;
		ontModel.enterCriticalSection(Lock.READ);
		try {
			stmts = ontModel.listStatements(selector);
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
			ontModel.leaveCriticalSection();
		}
	}

	/**
	 * Get a list of the object URIs that satisfy this statement:
	 * 
	 * <resourceUri> <propertyUri> <objectUri>
	 * 
	 * May return an empty list, but never returns null.
	 */
	public List<String> getObjectsOfProperty(String resourceUri,
			String propertyUri) {
		List<String> list = new ArrayList<String>();

		Selector selector = createSelector(resourceUri, propertyUri, null);

		StmtIterator stmts = null;
		ontModel.enterCriticalSection(Lock.READ);
		try {
			stmts = ontModel.listStatements(selector);
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
			ontModel.leaveCriticalSection();
		}
	}

	/**
	 * Get a list of the object URIs that satisfy these statements:
	 * 
	 * <resourceUri> <linkUri> <contextNodeUri>
	 * 
	 * <contextNodeUri> <propertyUri> <objectUri>
	 * 
	 * May return an empty list, but never returns null.
	 */
	public List<String> getObjectsOfLinkedProperty(String resourceUri,
			String linkUri, String propertyUri) {
		List<String> list = new ArrayList<String>();

		Selector selector = createSelector(resourceUri, linkUri, null);

		StmtIterator stmts = null;

		ontModel.enterCriticalSection(Lock.READ);
		try {
			stmts = ontModel.listStatements(selector);
			while (stmts.hasNext()) {
				RDFNode contextNode = stmts.next().getObject();
				if (contextNode.isResource()) {
					log.debug("found context node for '" + resourceUri + "': "
							+ contextNode);
					list.addAll(getObjectsOfProperty(contextNode.asResource()
							.getURI(), propertyUri));
				}
			}
			log.debug("Objects of linked properties '" + linkUri + "' ==> '"
					+ propertyUri + "' on '" + resourceUri + "': " + list);
			return list;
		} finally {
			if (stmts != null) {
				stmts.close();
			}
			ontModel.leaveCriticalSection();
		}
	}

	public Selector createSelector(String subjectUri, String predicateUri,
			String objectUri) {
		Resource subject = (subjectUri == null) ? null : ontModel
				.getResource(subjectUri);
		return createSelector(subject, predicateUri, objectUri);
	}

	public Selector createSelector(Resource subject, String predicateUri,
			String objectUri) {
		Property predicate = (predicateUri == null) ? null : ontModel
				.getProperty(predicateUri);
		RDFNode object = (objectUri == null) ? null : ontModel
				.getResource(objectUri);
		return new SimpleSelector(subject, predicate, object);
	}

	/** An AUTHORIZED decision with a message like "PolicyClass: message". */
	protected PolicyDecision authorizedDecision(String message) {
		return new BasicPolicyDecision(Authorization.AUTHORIZED, getClass()
				.getSimpleName() + ": " + message);
	}

}
