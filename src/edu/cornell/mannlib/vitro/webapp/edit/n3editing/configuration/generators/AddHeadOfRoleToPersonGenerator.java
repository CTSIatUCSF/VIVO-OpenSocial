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

public class AddHeadOfRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
	
	private static String template = "addHeadOfRoleToPerson.ftl";
	
    //Should this be overridden
	@Override
	String getTemplate() {
		return template;
	}
	
	@Override
	String getRoleType() {
		return "http://vivoweb.org/ontology/core#LeaderRole";
	}
	
	@Override
	RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
		return RoleActivityOptionTypes.HARDCODED_LITERALS;
	}
	
	@Override
	String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
		return null; //not needed since this is HARDCODED_LITERALS
	}	

	/** Head Of role involves hard-coded options for the "right side" of the role or activity */
	@Override
	HashMap<String, String> getRoleActivityTypeLiteralOptions() {
		HashMap<String, String> literalOptions = new HashMap<String, String>();
		literalOptions.put("", "Select type");
        literalOptions.put("http://vivoweb.org/ontology/core#Association", "Association");
        literalOptions.put("http://vivoweb.org/ontology/core#Center", "Center");
        literalOptions.put("http://vivoweb.org/ontology/core#ClinicalOrganization", "Clinical Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#College", "College");
        literalOptions.put("http://vivoweb.org/ontology/core#Committee", "Committee");                     
        literalOptions.put("http://vivoweb.org/ontology/core#Consortium", "Consortium");
        literalOptions.put("http://vivoweb.org/ontology/core#Department", "Department");
        literalOptions.put("http://vivoweb.org/ontology/core#Division", "Division"); 
        literalOptions.put("http://purl.org/NET/c4dm/event.owl#Event", "Event"); 
        literalOptions.put("http://vivoweb.org/ontology/core#ExtensionUnit", "Extension Unit");
        literalOptions.put("http://vivoweb.org/ontology/core#Foundation", "Foundation");
        literalOptions.put("http://vivoweb.org/ontology/core#FundingOrganization", "Funding Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#GovernmentAgency", "Government Agency");
        literalOptions.put("http://vivoweb.org/ontology/core#Hospital", "Hospital");
        literalOptions.put("http://vivoweb.org/ontology/core#Institute", "Institute");
        literalOptions.put("http://vivoweb.org/ontology/core#Laboratory", "Laboratory");
        literalOptions.put("http://vivoweb.org/ontology/core#Library", "Library");
        literalOptions.put("http://vivoweb.org/ontology/core#Museum", "Museum");        
        literalOptions.put("http://xmlns.com/foaf/0.1/Organization", "Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#PrivateCompany", "Private Company");
        literalOptions.put("http://vivoweb.org/ontology/core#Program", "Program");
        literalOptions.put("http://vivoweb.org/ontology/core#Project", "Project");
        literalOptions.put("http://vivoweb.org/ontology/core#Publisher", "Publisher");
        literalOptions.put("http://vivoweb.org/ontology/core#ResearchOrganization", "Research Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#School", "School");
        literalOptions.put("http://vivoweb.org/ontology/core#Service","Service");
        literalOptions.put("http://vivoweb.org/ontology/core#Team", "Team");
        literalOptions.put("http://vivoweb.org/ontology/core#StudentOrganization", "Student Organization");
        literalOptions.put("http://vivoweb.org/ontology/core#University", "University");
		return literalOptions;
	}

	@Override
	boolean isShowRoleLabelField(){return true;}

    
}
