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
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;

/**
 * @author bkoniden (Deepak Konidena)
 * modified by @author cdtank (Chintan Tank)
 * last modified at Mar 21, 2011 2:57:20 PM 
 */
public class SubEntity extends Individual {

	private Set<Activity> activities = new HashSet<Activity>();
	private Set<String> entityTypes = new HashSet<String>();
	private VOConstants.EntityClassType entityClass;
	private String lastCachedAtDateTime = null;
	
	public SubEntity(String individualURI) {
		super(individualURI);
	}
	
	public SubEntity(String individualURI, String individualLabel) {
		super(individualURI, individualLabel);
	}
	
	@Override
	public String toString() {
		return this.getIndividualLabel();
	}
	
	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}
	
	public void addActivities(Collection<Activity> activities) {
		this.activities.addAll(activities);
	}
	
	public Set<Activity> getActivities() {
		return activities;
	}
	
	public void addEntityTypeLabel(String typeLabel) {
		this.entityTypes.add(typeLabel);
	}

	public Set<String> getEntityTypeLabels() {
		return entityTypes;
	}
	
	public void setEntityClass(VOConstants.EntityClassType entityClass) {
		this.entityClass = entityClass;
	}

	public VOConstants.EntityClassType getEntityClass() {
		return entityClass;
	}

	public void setLastCachedAtDateTime(String lastCachedAtDateTime) {
		this.lastCachedAtDateTime = lastCachedAtDateTime;
	}

	public String getLastCachedAtDateTime() {
		return lastCachedAtDateTime;
	}

}
