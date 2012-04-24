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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddReviewerRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {		
	
	@Override
	String getTemplate() { return "addReviewerRoleToPerson.ftl"; }


    //The default activityToRolePredicate and roleToActivityPredicates are 
	//correct for this subclass so they don't need to be overwritten
	public String getActivityToRolePredicate(VitroRequest vreq) {
		return "http://vivoweb.org/ontology/core#linkedRole";
	}

	public String getRoleToActivityPredicate(VitroRequest vreq) {
		return "<http://vivoweb.org/ontology/core#forInformationResource>";
	}
	
	//role type will always be set based on particular form
	public String getRoleType() {
		//TODO: Get dynamic way of including vivoweb ontology
		return "http://vivoweb.org/ontology/core#ReviewerRole";
	}
	
	//Each subclass generator will return its own type of option here:
	//whether literal hardcoded, based on class group, or subclasses of a specific class
	//The latter two will apparently lend some kind of uri to objectClassUri ?
	public RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.CHILD_VCLASSES;
	}
	
	//This too will depend on the specific subclass of generator
	public String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return "http://vivoweb.org/ontology/core#InformationResource";
	}
	

	//Reviewer role involves hard-coded options for the "right side" of the role or activity
	protected HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select type");
		return literalOptions;
	}

	//isShowRoleLabelField remains true for this so doesn't need to be overwritten
	public boolean isShowRoleLabelField() {
		return false;
	}

    
}
