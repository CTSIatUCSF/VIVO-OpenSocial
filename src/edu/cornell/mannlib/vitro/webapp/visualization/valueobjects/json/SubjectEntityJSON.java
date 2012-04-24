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
package edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Individual;

public class SubjectEntityJSON {
	
	private String subjectEntityLabel;
	private String subjectEntityURI;
	private Map<String, String> parentURIToLabel = new HashMap<String, String>();
	
	public SubjectEntityJSON(String subjectEntityURI, String label,
			Set<Individual> parentOrganizations) {
		this.subjectEntityURI = subjectEntityURI;
		this.subjectEntityLabel = label;
		
		this.setParentURIToLabel(parentOrganizations);
	}

	public String getSubjectEntityURI() {
		return subjectEntityURI;
	}

	public void setSubjectEntityURI(String subjectEntityURI) {
		this.subjectEntityURI = subjectEntityURI;
	}

	public String getSubjectEntityLabel() {
		return subjectEntityLabel;
	}

	public void setSubjectEntityLabel(String label) {
		this.subjectEntityLabel = label;
	}

	public Map<String, String> getParentURIToLabel() {
		return parentURIToLabel;
	}

	public void setParentURIToLabel(Set<Individual> parentOrganizations) {
		for (Individual parentOrganization : parentOrganizations) {
			this.parentURIToLabel.put(parentOrganization.getIndividualURI(), parentOrganization.getIndividualLabel());
		}
	}
}
