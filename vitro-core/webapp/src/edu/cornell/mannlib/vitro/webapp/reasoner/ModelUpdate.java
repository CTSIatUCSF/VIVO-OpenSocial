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

package edu.cornell.mannlib.vitro.webapp.reasoner;

import com.hp.hpl.jena.rdf.model.Statement;


public class ModelUpdate {

	public static enum Operation {ADD, RETRACT};
	
	private Operation operation;
	private Statement statement;
	private String modelURI;
	//JenaDataSourceSetupBase.JENA_DB_MODEL;
	//JenaDataSourceSetupBase.JENA_TBOX_ASSERTIONS_MODEL;
	
	
	public ModelUpdate() {

    }

	public ModelUpdate(Statement statement,
			           Operation operation,
	                   String modelURI) {
		
		this.operation = operation;
		this.statement = statement;
		this.modelURI = modelURI;
    }

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public String getModelURI() {
		return modelURI;
	}

	public void setModelURI(String modelURI) {
		this.modelURI = modelURI;
	}
	
	@Override public String toString() {
		String ret = "operation = " + this.operation + ",";
		ret += " model = " + this.modelURI + ",";
		ret += " statement = " + SimpleReasoner.stmtString(statement);
		
		
		return ret;
	}
}
