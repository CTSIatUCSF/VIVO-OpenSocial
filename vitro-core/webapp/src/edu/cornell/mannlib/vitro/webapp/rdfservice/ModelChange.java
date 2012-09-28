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

package edu.cornell.mannlib.vitro.webapp.rdfservice;

import java.io.InputStream;

/**
 * A ModelChange is one component of a ChangeSet.
 * Represents a model (collection of RDF triples), the URI
 * of a graph, and an indication of whether to add or 
 * remove the model from the graph.
 */

public interface ModelChange {

	public enum Operation {
	    ADD, REMOVE
	}
	
	/**   
	 * @return InputStream - the serialized model (collection of RDF triples) representing a change to make           
	 */
	public InputStream getSerializedModel();
	
	/**
	 * @param serializedModel - the serialized model (collection of RDF triples) representing a change to make           
	 */
	public void setSerializedModel(InputStream serializedModel);
	
	/**
	 * @return RDFService.ModelSerializationFormat - the serialization format of the model
	 */
	public RDFService.ModelSerializationFormat getSerializationFormat();
	
	/**
	 * @param serializationFormat - the serialization format of the model
	 */
	public void setSerializationFormat(RDFService.ModelSerializationFormat serializationFormat);
	
	/**
	 * @return ModelChange.Operation - the operation to be performed
	 */
	public ModelChange.Operation getOperation();

	/**
	 * @param operation - the operation to be performed
	 */
	public void setOperation(ModelChange.Operation operation);

	/**
	 * @return String - the URI of the graph to which to apply the change
	 */
	public String getGraphURI();

	/**
	 * @param graphURI - the URI of the graph to which to apply the change
	 *                   If the graphURI is null the change applies to the
	 *                   default write graph. If this method is not used to
	 *                   set the write graph the default write graph will be used.
	 */
	public void setGraphURI(String graphURI);
}
