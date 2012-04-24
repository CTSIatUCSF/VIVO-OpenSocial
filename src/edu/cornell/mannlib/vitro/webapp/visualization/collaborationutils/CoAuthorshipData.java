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
package edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;

public class CoAuthorshipData extends CollaborationData {

	public CoAuthorshipData(Collaborator egoCollaborator,
			Set<Collaborator> collaborators, Set<Collaboration> collaborations) {
		super(egoCollaborator, collaborators, collaborations);
	}
	
	public Set<Map<String, String>> initializeEdgeSchema() {

		Set<Map<String, String>> edgeSchema = new HashSet<Map<String, String>>();
		
			Map<String, String> schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "collaborator1");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "collaborator1");
			schemaAttributes.put("attr.type", "string");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "collaborator2");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "collaborator2");
			schemaAttributes.put("attr.type", "string");
		
		edgeSchema.add(schemaAttributes);		
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "number_of_coauthored_works");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "number_of_coauthored_works");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "earliest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "earliest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_earliest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "num_earliest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "latest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "latest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_latest_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "num_latest_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_unknown_collaboration");
			schemaAttributes.put("for", "edge");
			schemaAttributes.put("attr.name", "num_unknown_collaboration");
			schemaAttributes.put("attr.type", "int");
		
		edgeSchema.add(schemaAttributes);
		
		return edgeSchema;
	}
	

	public Set<Map<String, String>> initializeNodeSchema() {
		
		Set<Map<String, String>> nodeSchema = new HashSet<Map<String, String>>();

			Map<String, String> schemaAttributes = new LinkedHashMap<String, String>();   
			
			schemaAttributes.put("id", "url");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "url");
			schemaAttributes.put("attr.type", "string");
		
		nodeSchema.add(schemaAttributes);
	
			schemaAttributes = new LinkedHashMap<String, String>();
		
			schemaAttributes.put("id", "label");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "label");
			schemaAttributes.put("attr.type", "string");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
		
			schemaAttributes.put("id", "profile_url");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "profile_url");
			schemaAttributes.put("attr.type", "string");
		
		nodeSchema.add(schemaAttributes);
	
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "number_of_authored_works");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "number_of_authored_works");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "earliest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "earliest_publication");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_earliest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_earliest_publication");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "latest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "latest_publication");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_latest_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_latest_publication");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
			schemaAttributes = new LinkedHashMap<String, String>();
			
			schemaAttributes.put("id", "num_unknown_publication");
			schemaAttributes.put("for", "node");
			schemaAttributes.put("attr.name", "num_unknown_publication");
			schemaAttributes.put("attr.type", "int");
		
		nodeSchema.add(schemaAttributes);
		
		
		return nodeSchema;
	}

}
