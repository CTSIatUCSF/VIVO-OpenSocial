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

package edu.cornell.mannlib.vitro.webapp.rdfservice.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeSet;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ModelChange;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ModelChange.Operation;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;

public class ChangeSetImpl implements ChangeSet {
	
	public ChangeSetImpl() {
		modelChanges = new ArrayList<ModelChange>();
	}
	
	private String preconditionQuery;
	private RDFService.SPARQLQueryType queryType;
	private ArrayList<ModelChange> modelChanges = new ArrayList<ModelChange>();
	private ArrayList<Object> preChangeEvents = new ArrayList<Object>();
	private ArrayList<Object> postChangeEvents = new ArrayList<Object>();
 	
	@Override
	public String getPreconditionQuery() {
	   return preconditionQuery;	
	}
	
	@Override
	public void setPreconditionQuery(String preconditionQuery) {
		this.preconditionQuery = preconditionQuery;
	}
	
	@Override
	public RDFService.SPARQLQueryType getPreconditionQueryType() {
		return queryType;
	}
	
	@Override
	public void setPreconditionQueryType(RDFService.SPARQLQueryType queryType) {
		this.queryType = queryType;
	}

	@Override
	public List<ModelChange> getModelChanges() {
	    	return modelChanges;
	}
	
	@Override
	public void addAddition(InputStream model, RDFService.ModelSerializationFormat format, String graphURI) {
		modelChanges.add(manufactureModelChange(model,format, ModelChange.Operation.ADD, graphURI));
	}
	
	@Override
	public void addRemoval(InputStream model, RDFService.ModelSerializationFormat format, String graphURI) {
		modelChanges.add(manufactureModelChange(model, format, ModelChange.Operation.REMOVE, graphURI));
	}
	
	@Override
	public ModelChange manufactureModelChange() {
		return new ModelChangeImpl(); 
	}

	@Override
	public ModelChange manufactureModelChange(InputStream serializedModel,
                                              RDFService.ModelSerializationFormat serializationFormat,
                                              Operation operation,
                                              String graphURI) {
		return new ModelChangeImpl(serializedModel, serializationFormat, operation, graphURI); 
	}
	
	@Override 
	public void addPreChangeEvent(Object o) {
	    this.preChangeEvents.add(o);
	}
	
	@Override 
    public void addPostChangeEvent(Object o) {
        this.postChangeEvents.add(o);
    }
	
	@Override
	public List<Object> getPreChangeEvents() {
	    return this.preChangeEvents;
	}
	
	@Override
    public List<Object> getPostChangeEvents() {
        return this.postChangeEvents;
    }	
}
