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
import java.util.List;

/**
 * Input parameter to changeSetUpdate() method in RDFService.
 * Represents a precondition query and an ordered list of model changes. 
 */

public interface ChangeSet {
		
	/**   
	 * @return String - a SPARQL query            
	 */	
	public String getPreconditionQuery();
	
	/**   
	 * @param preconditionQuery - a SPARQL query            
	 */	
	public void setPreconditionQuery(String preconditionQuery);
	
	/**   
	 * @return RDFService.SPARQLQueryType - the precondition query type           
	 */	
	public RDFService.SPARQLQueryType getPreconditionQueryType();
	
	/**   
	 * @param queryType - the precondition query type           
	 */	
	public void setPreconditionQueryType(RDFService.SPARQLQueryType queryType);

	/**   
	 * @return List<ModelChange> - list of model changes           
	 */		
	public List<ModelChange> getModelChanges();
	
	/**   
	 * Adds one model change representing an addition to the list of model changes
	 * 
	 * @param model - a serialized RDF model (collection of triples)  
	 * @param serializationFormat - format of the serialized RDF model
	 * @param graphURI - URI of the graph to which the RDF model should be added         
	 */		
	public void addAddition(InputStream model, 
			                RDFService.ModelSerializationFormat serializationFormat,
			                String graphURI);
	
	/**   
	 * Adds one model change representing a deletion to the list of model changes
	 * 
	 * @param model - a serialized RDF model (collection of triples)  
	 * @param serializationFormat - format of the serialized RDF model
	 * @param graphURI - URI of the graph from which the RDF model should be removed         
	 */		
	public void addRemoval(InputStream model,
			               RDFService.ModelSerializationFormat serializationFormat,
			               String graphURI);

	/**   
	 * Creates an instance of the ModelChange class 
	 * 
	 * @return ModelChange - an empty instance of the ModelChange class
	 */		
	public ModelChange manufactureModelChange();
	
	/**   
	 * Creates an instance of the ModelChange class
	 * 
	 * @param serializedModel - a serialized RDF model (collection of triples)  
	 * @param serializationFormat - format of the serialized RDF model
	 * @param operation - the type of operation to be performed with the serialized RDF model 
	 * @param graphURI - URI of the graph on which to apply the model change operation 
	 * 
	 * @return ModelChange - a ModelChange instance initialized with the input
	 *                       model, model format, operation and graphURI
	 */		
	public ModelChange manufactureModelChange(InputStream serializedModel,
                                              RDFService.ModelSerializationFormat serializationFormat,
                                              ModelChange.Operation operation,
                                              String graphURI);
	
	/**
	 * Adds an event that will be be passed to any change listeners in advance of
	 * the change set additions and retractions being performed. The event
	 * will only be fired if the precondition (if any) is met.
	 * 
	 * @param event - event to notify listeners of in advance of making
	 *                changes to the triple store.
	 */
	public void addPreChangeEvent(Object event);
	    
    /**
     * Adds an event that will be be passed to any change listeners after all of
     * the change set additions and retractions are performed.
     * 
     * @param event - the event to notify listeners of after the changes are 
     *                performed.
     */
    public void addPostChangeEvent(Object event);

    /**
     * Returns a list of events to pass to any change listeners in 
     * advance of the change set additions and retractions being performed.
     * 
     * @return List<Object>
     */
	public List<Object> getPreChangeEvents();
	
    /**
     * Returns a list of events to pass to any change listeners after 
     * the change set additions and retractions are performed.
     * 
     * @return List<Object>
     */
    public List<Object> getPostChangeEvents();
    
}
