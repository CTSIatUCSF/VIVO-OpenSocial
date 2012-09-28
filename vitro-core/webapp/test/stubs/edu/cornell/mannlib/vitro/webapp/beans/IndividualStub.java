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

package stubs.edu.cornell.mannlib.vitro.webapp.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;

/**
 * Mock the basic functions of Individual for unit tests.
 */
public class IndividualStub implements Individual {
	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------

	private final String uri;

	private final Set<DataPropertyStatement> dpsSet = new HashSet<DataPropertyStatement>();
	private final Set<ObjectPropertyStatement> opsSet = new HashSet<ObjectPropertyStatement>();
	private final Set<VClass> vClasses = new HashSet<VClass>();

	private String name = "NoName";

	public IndividualStub(String uri) {
		this.uri = uri;
	}

	public void addDataPropertyStatement(String predicateUri, String object) {
		dpsSet.add(new DataPropertyStatementImpl(this.uri, predicateUri, object));
	}

	public void addObjectPropertyStatement(String predicateUri, String objectUri) {
		opsSet.add(new ObjectPropertyStatementImpl(this.uri, predicateUri,
				objectUri));
	}

	public void addPopulatedObjectPropertyStatement(String predicateUri,
			String objectUri, Individual object) {
		ObjectPropertyStatementImpl stmt = new ObjectPropertyStatementImpl(
				this.uri, predicateUri, objectUri);
		stmt.setObject(object);
		opsSet.add(stmt);
	}

	public void addVclass(String namespace, String localname, String vClassName) {
		vClasses.add(new VClass(namespace, localname, vClassName));
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<DataPropertyStatement> getDataPropertyStatements() {
		return new ArrayList<DataPropertyStatement>(dpsSet);
	}

	@Override
	public List<DataPropertyStatement> getDataPropertyStatements(
			String propertyUri) {
		List<DataPropertyStatement> list = new ArrayList<DataPropertyStatement>();
		for (DataPropertyStatement dps : dpsSet) {
			if (dps.getDatapropURI().equals(propertyUri)) {
				list.add(dps);
			}
		}
		return list;
	}

	@Override
	public String getDataValue(String propertyUri) {
		for (DataPropertyStatement dps : dpsSet) {
			if (dps.getDatapropURI().equals(propertyUri)) {
				return dps.getData();
			}
		}
		return null;
	}

	@Override
	public List<ObjectPropertyStatement> getObjectPropertyStatements() {
		return new ArrayList<ObjectPropertyStatement>(opsSet);
	}

	@Override
	public List<ObjectPropertyStatement> getObjectPropertyStatements(
			String propertyUri) {
		List<ObjectPropertyStatement> list = new ArrayList<ObjectPropertyStatement>();
		for (ObjectPropertyStatement ops : opsSet) {
			if (ops.getPropertyURI().equals(propertyUri)) {
				list.add(ops);
			}
		}
		return list;
	}

	@Override
	public Individual getRelatedIndividual(String propertyUri) {
		for (ObjectPropertyStatement ops : opsSet) {
			if (ops.getPropertyURI().equals(propertyUri)) {
				return ops.getObject();
			}
		}
		return null;
	}


	@Override
	public boolean isVClass(String vclassUri) {
		for (VClass vc : vClasses) {
			if (vc.getURI().equals(vclassUri)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<VClass> getVClasses(boolean direct) {
		return new ArrayList<VClass>(vClasses);
	}

	@Override
	public void sortForDisplay() {
		// does nothing.
	}

	@Override
	public String getLocalName() {
		// Useless for now.
		return "BOGUS Local Name";
	}

	@Override
	public VClass getVClass() {
		for (VClass vc : vClasses) {
			return vc;
		}
		return null;
	}

	@Override
	public String getVClassURI() {
		for (VClass vc : vClasses) {
			return vc.getURI();
		}
		return null;
	}

	@Override
	public List<VClass> getVClasses() {
		return new ArrayList<VClass>(vClasses);
	}

	// ----------------------------------------------------------------------
	// Un-implemented methods
	// ----------------------------------------------------------------------

	@Override
	public boolean isAnonymous() {
		throw new RuntimeException(
				"ResourceBean.isAnonymous() not implemented.");
	}

	@Override
	public void setURI(String URI) {
		throw new RuntimeException("ResourceBean.setURI() not implemented.");
	}

	@Override
	public String getNamespace() {
		throw new RuntimeException(
				"ResourceBean.getNamespace() not implemented.");
	}

	@Override
	public void setNamespace(String namespace) {
		throw new RuntimeException(
				"ResourceBean.setNamespace() not implemented.");
	}

	@Override
	public void setLocalName(String localName) {
		throw new RuntimeException(
				"ResourceBean.setLocalName() not implemented.");
	}

	@Override
	public RoleLevel getHiddenFromDisplayBelowRoleLevel() {
		throw new RuntimeException(
				"ResourceBean.getHiddenFromDisplayBelowRoleLevel() not implemented.");
	}

	@Override
	public void setHiddenFromDisplayBelowRoleLevel(RoleLevel eR) {
		throw new RuntimeException(
				"ResourceBean.setHiddenFromDisplayBelowRoleLevel() not implemented.");
	}

	@Override
	public void setHiddenFromDisplayBelowRoleLevelUsingRoleUri(String roleUri) {
		throw new RuntimeException(
				"ResourceBean.setHiddenFromDisplayBelowRoleLevelUsingRoleUri() not implemented.");
	}

	@Override
	public RoleLevel getProhibitedFromUpdateBelowRoleLevel() {
		throw new RuntimeException(
				"ResourceBean.getProhibitedFromUpdateBelowRoleLevel() not implemented.");
	}

	@Override
	public void setProhibitedFromUpdateBelowRoleLevel(RoleLevel eR) {
		throw new RuntimeException(
				"ResourceBean.setProhibitedFromUpdateBelowRoleLevel() not implemented.");
	}

	@Override
	public void setProhibitedFromUpdateBelowRoleLevelUsingRoleUri(String roleUri) {
		throw new RuntimeException(
				"ResourceBean.setProhibitedFromUpdateBelowRoleLevelUsingRoleUri() not implemented.");
	}

	@Override
	public int compareTo(Individual o) {
		throw new RuntimeException(
				"Comparable<Individual>.compareTo() not implemented.");
	}

	@Override
	public String getRdfsLabel() {
		throw new RuntimeException("Individual.getRdfsLabel() not implemented.");
	}

	@Override
	public void setVClassURI(String in) {
		throw new RuntimeException("Individual.setVClassURI() not implemented.");
	}

	@Override
	public Timestamp getModTime() {
		throw new RuntimeException("Individual.getModTime() not implemented.");
	}

	@Override
	public void setModTime(Timestamp in) {
		throw new RuntimeException("Individual.setModTime() not implemented.");
	}

	@Override
	public List<ObjectProperty> getObjectPropertyList() {
		throw new RuntimeException(
				"Individual.getObjectPropertyList() not implemented.");
	}

	@Override
	public void setPropertyList(List<ObjectProperty> propertyList) {
		throw new RuntimeException(
				"Individual.setPropertyList() not implemented.");
	}

	@Override
	public List<ObjectProperty> getPopulatedObjectPropertyList() {
		throw new RuntimeException(
				"Individual.getPopulatedObjectPropertyList() not implemented.");
	}

	@Override
	public void setPopulatedObjectPropertyList(List<ObjectProperty> propertyList) {
		throw new RuntimeException(
				"Individual.setPopulatedObjectPropertyList() not implemented.");
	}

	@Override
	public Map<String, ObjectProperty> getObjectPropertyMap() {
		throw new RuntimeException(
				"Individual.getObjectPropertyMap() not implemented.");
	}

	@Override
	public void setObjectPropertyMap(Map<String, ObjectProperty> propertyMap) {
		throw new RuntimeException(
				"Individual.setObjectPropertyMap() not implemented.");
	}

	@Override
	public List<DataProperty> getDataPropertyList() {
		throw new RuntimeException(
				"Individual.getDataPropertyList() not implemented.");
	}

	@Override
	public void setDatatypePropertyList(List<DataProperty> datatypePropertyList) {
		throw new RuntimeException(
				"Individual.setDatatypePropertyList() not implemented.");
	}

	@Override
	public List<DataProperty> getPopulatedDataPropertyList() {
		throw new RuntimeException(
				"Individual.getPopulatedDataPropertyList() not implemented.");
	}

	@Override
	public void setPopulatedDataPropertyList(List<DataProperty> dataPropertyList) {
		throw new RuntimeException(
				"Individual.setPopulatedDataPropertyList() not implemented.");
	}

	@Override
	public Map<String, DataProperty> getDataPropertyMap() {
		throw new RuntimeException(
				"Individual.getDataPropertyMap() not implemented.");
	}

	@Override
	public void setDataPropertyMap(Map<String, DataProperty> propertyMap) {
		throw new RuntimeException(
				"Individual.setDataPropertyMap() not implemented.");
	}

	@Override
	public void setDataPropertyStatements(List<DataPropertyStatement> list) {
		throw new RuntimeException(
				"Individual.setDataPropertyStatements() not implemented.");
	}

	@Override
	public DataPropertyStatement getDataPropertyStatement(String propertyUri) {
		throw new RuntimeException(
				"Individual.getDataPropertyStatement() not implemented.");
	}

	@Override
	public List<String> getDataValues(String propertyUri) {
		throw new RuntimeException(
				"Individual.getDataValues() not implemented.");
	}

	@Override
	public void setVClass(VClass class1) {
		throw new RuntimeException("Individual.setVClass() not implemented.");
	}

	@Override
	public void setVClasses(List<VClass> vClassList, boolean direct) {
		throw new RuntimeException("Individual.setVClasses() not implemented.");
	}

	@Override
	public void setObjectPropertyStatements(List<ObjectPropertyStatement> list) {
		throw new RuntimeException(
				"Individual.setObjectPropertyStatements() not implemented.");
	}

	@Override
	public List<Individual> getRelatedIndividuals(String propertyUri) {
		throw new RuntimeException(
				"Individual.getRelatedIndividuals() not implemented.");
	}

	@Override
	public List<DataPropertyStatement> getExternalIds() {
		throw new RuntimeException(
				"Individual.getExternalIds() not implemented.");
	}

	@Override
	public void setExternalIds(List<DataPropertyStatement> externalIds) {
		throw new RuntimeException(
				"Individual.setExternalIds() not implemented.");
	}

	@Override
	public void setMainImageUri(String mainImageUri) {
		throw new RuntimeException(
				"Individual.setMainImageUri() not implemented.");
	}

	@Override
	public String getMainImageUri() {
		throw new RuntimeException(
				"Individual.getMainImageUri() not implemented.");
	}

	@Override
	public String getImageUrl() {
		throw new RuntimeException("Individual.getImageUrl() not implemented.");
	}

	@Override
	public String getThumbUrl() {
		throw new RuntimeException("Individual.getThumbUrl() not implemented.");
	}

	@Override
	public boolean hasThumb() {
		throw new RuntimeException("Individual.hasThumb() not implemented.");
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		throw new RuntimeException("Individual.toJSON() not implemented.");
	}

	@Override
	public Float getSearchBoost() {
		throw new RuntimeException(
				"Individual.getSearchBoost() not implemented.");
	}

	@Override
	public void setSearchBoost(Float boost) {
		throw new RuntimeException(
				"Individual.setSearchBoost() not implemented.");
	}

	@Override
	public String getSearchSnippet() {
		throw new RuntimeException(
				"Individual.getSearchSnippet() not implemented.");
	}

	@Override
	public void setSearchSnippet(String snippet) {
		throw new RuntimeException(
				"Individual.setSearchSnippet() not implemented.");
	}
}