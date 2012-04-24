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

import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaboration;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Collaborator;

public abstract class CollaborationData {
	
	private Set<Collaborator> collaborators;
	private Set<Collaboration> collaborations;
	private Collaborator egoCollaborator;
	private Set<Map<String, String>> NODE_SCHEMA;
	private Set<Map<String, String>> EDGE_SCHEMA;
		
	public CollaborationData(Collaborator egoCollaborator, 
							Set<Collaborator> collaborators, 
							Set<Collaboration> collaborations) {
		this.egoCollaborator = egoCollaborator;
		this.collaborators = collaborators;
		this.collaborations = collaborations;
	}
	
	public Set<Collaborator> getCollaborators() {
		return collaborators;
	}

	public Set<Collaboration> getCollaborations() {
		return collaborations;
	}	
	
	public Collaborator getEgoCollaborator() {
		return egoCollaborator;
	}
	
	/*
	 * Node Schema for graphML
	 * */
	public Set<Map<String, String>> getNodeSchema() {
		
		if (NODE_SCHEMA == null) {
			NODE_SCHEMA = initializeNodeSchema();			
		}
		
		return NODE_SCHEMA;
	}
	
	/*
	 * Edge Schema for graphML
	 * */
	public Set<Map<String, String>> getEdgeSchema() {
		
		if (EDGE_SCHEMA == null) {
			EDGE_SCHEMA = initializeEdgeSchema();			
		}
		
		return EDGE_SCHEMA;
	}

	abstract Set<Map<String, String>> initializeEdgeSchema();
	
	abstract Set<Map<String, String>> initializeNodeSchema();	
}
