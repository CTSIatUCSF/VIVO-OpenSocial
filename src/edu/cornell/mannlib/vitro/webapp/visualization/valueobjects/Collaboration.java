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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UniqueIDGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;

/**
 * 
 * This is stores collaboration information mainly for ego-centric visualizations.
 * 
 * @author cdtank
 *
 */
public class Collaboration {

	private int collaborationID;
	private Map<String, Integer> yearToActivityCount;
	private Set<Activity> activities = new HashSet<Activity>();
	private Collaborator sourceCollaborator;
	private Collaborator targetCollaborator;

	public Collaboration(Collaborator sourceCollaborator, 
						 Collaborator  targetCollaborator, 
						 Activity seedActivity,
						 UniqueIDGenerator uniqueIDGenerator) {
		collaborationID = uniqueIDGenerator.getNextNumericID();
		this.sourceCollaborator = sourceCollaborator;
		this.targetCollaborator = targetCollaborator;
		this.activities.add(seedActivity);
	}

	public int getCollaborationID() {
		return collaborationID;
	}
	
	public Collaborator getSourceCollaborator() {
		return sourceCollaborator;
	}

	public Collaborator getTargetCollaborator() {
		return targetCollaborator;
	}
	
	public Set<Activity> getCollaborationActivities() {
		return activities;
	}
	
	public int getNumOfCollaborations() {
		return activities.size();
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
	}
	
	public Map<String, Integer> getYearToActivityCount() {
		if (yearToActivityCount == null) {
			yearToActivityCount = UtilityFunctions.getYearToActivityCount(activities);
		}
		return yearToActivityCount;
	}
	
	/*
	 * getEarliest, Latest & Unknown Activity YearCount should only be used after 
	 * the parsing of the entire sparql is done. Else it will give results based on
	 * incomplete dataset.
	 * */
	@SuppressWarnings("serial")
	public Map<String, Integer> getEarliestCollaborationYearCount() {

		/*
		 * We do not want to consider the default Activity year when we are checking 
		 * for the min or max Activity year. 
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(this.getYearToActivityCount()
																	.keySet());
		yearsToBeConsidered.remove(VOConstants.DEFAULT_ACTIVITY_YEAR);
		
		/*
		 * There can be a case when the only Activity the author has no attached year to it
		 * so essentially an "Unknown". In that case Collections.max or min will throw an 
		 * NoSuchElementException.
		 * 
		 * If there is no maximum year available then we should imply so by returning a "null".
		 * */
		if (yearsToBeConsidered.size() > 0) {
			final String earliestYear = Collections.min(yearsToBeConsidered);
			final Integer earliestYearActivityCount = this.getYearToActivityCount()
															.get(earliestYear);
			
			return new HashMap<String, Integer>() { {
				put(earliestYear, earliestYearActivityCount);
			} };
		} else {
			return null;
		}
	}

	@SuppressWarnings("serial")
	public Map<String, Integer> getLatestCollaborationYearCount() {
		
		/*
		 * We do not want to consider the default Activity year when we are checking 
		 * for the min or max Activity year. 
		 * */
		Set<String> yearsToBeConsidered = new HashSet<String>(this.getYearToActivityCount()
																	.keySet());
		yearsToBeConsidered.remove(VOConstants.DEFAULT_ACTIVITY_YEAR);
		
		/*
		 * There can be a case when the only Activity the collaborator has no attached year to it
		 * so essentially an "Unknown". In that case Collections.max or min will throw an 
		 * NoSuchElementException.
		 * 
		 * If there is no maximum year available then we should imply so by returning a "null".
		 * */
		if (yearsToBeConsidered.size() > 0) {
			final String latestYear = Collections.max(yearsToBeConsidered);
			final Integer latestYearActivityCount = this.getYearToActivityCount().get(latestYear);
			
			return new HashMap<String, Integer>() { {
				put(latestYear, latestYearActivityCount);
			} };
		} else {
			return null;
		}
	}
	
	public Integer getUnknownCollaborationYearCount() {
		
		Integer unknownYearActivityCount = this.getYearToActivityCount()
										.get(VOConstants.DEFAULT_ACTIVITY_YEAR);
		
		/*
		 * If there is no unknown year available then we should imply so by returning a "null".
		 * */
		if (unknownYearActivityCount != null) {
			return unknownYearActivityCount;
		} else {
			return null;
		}
	}
	

}
