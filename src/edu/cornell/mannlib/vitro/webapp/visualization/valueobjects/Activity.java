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

import edu.cornell.mannlib.vitro.webapp.visualization.constants.VOConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;

/**
 * This interface will make sure that VOs conveying any person's academic output like publications,
 * grants etc implement certain methods which will be used to generalize methods which are just 
 * interested in certain common properties like what was the year in which the activity was 
 * published (or started). 
 * @author cdtank
 */
public class Activity extends Individual {
	
	private String activityDate;

	public Activity(String activityURI) {
		super(activityURI);
	}
	
	public String getActivityURI() {
		return this.getIndividualURI();
	}
	
	public String getActivityLabel() {
		return this.getIndividualLabel();
	}
	
	public void setActivityLabel(String activityLabel) {
		this.setIndividualLabel(activityLabel);
	}
	
	/**
	 * This method will be called to get the final/inferred year for the publication. 
	 * The 2 choices, in order, are,
	 * 		1. parsed year from xs:DateTime object saved in core:dateTimeValue 
	 * 		2. Default Publication Year 
	 * @return
	 */
	public String getParsedActivityYear() {
		
		return UtilityFunctions.getValidYearFromCoreDateTimeString(activityDate,
				VOConstants.DEFAULT_ACTIVITY_YEAR);
	}
	
	/**
	 * This method should be used to get the raw date & not the parsed publication year. 
	 * For the later use getParsedPublicationYear.
	 * @return
	 */
	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}
}
