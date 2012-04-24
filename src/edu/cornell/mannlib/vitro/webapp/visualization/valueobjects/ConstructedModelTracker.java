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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.IllegalConstructedModelIdentifierException;

public class ConstructedModelTracker {
	
	private static Map<String, Model> modelIdentifierToConstructedModel = new HashMap<String, Model>();
	
	public static void trackModel(String identifier, Model model) {
		modelIdentifierToConstructedModel.put(identifier, model);
	}
	
	public static Model getModel(String identifier) {
		return modelIdentifierToConstructedModel.get(identifier);
	}
	
	public static Model removeModel(String uri, String modelType) {
		return modelIdentifierToConstructedModel.remove(generateModelIdentifier(uri, modelType));
	}
	
	public static String generateModelIdentifier(String uri, String modelType) {
		
		if (uri == null) {
			uri = "";
		}
		return modelType +  "$" + uri;
	}
	
	public static Map<String, Model> getAllModels() {
		return modelIdentifierToConstructedModel;
	}
	
	public static ConstructedModel parseModelIdentifier(String modelIdentifier) 
			throws IllegalConstructedModelIdentifierException {
		
		String[] parts = StringUtils.split(modelIdentifier, '$');
		
		if (parts.length == 0) {
			throw new IllegalConstructedModelIdentifierException(modelIdentifier + " provided.");
		} else if (parts.length == 1) {
			return new ConstructedModel(parts[0], null);
		} else {
			return new ConstructedModel(parts[0], parts[1]);
		}
	}
}
