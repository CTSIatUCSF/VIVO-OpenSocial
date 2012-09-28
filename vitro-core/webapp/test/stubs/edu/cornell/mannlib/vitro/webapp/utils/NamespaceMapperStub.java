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

package stubs.edu.cornell.mannlib.vitro.webapp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.utils.NamespaceMapper;

/**
 * A minimal implementation of the NamespaceMapper.
 * 
 * I have only implemented the methods that I needed. Feel free to implement
 * others.
 */
public class NamespaceMapperStub implements NamespaceMapper {
	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------
	
	private final Map<String, String> prefixMap = new HashMap<String, String>();
	
	public void setPrefixForNamespace(String prefix, String namespace) {
		prefixMap.put(prefix, namespace);
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

	@Override
	public String getNamespaceForPrefix(String prefix) {
		return prefixMap.get(prefix);
	}

	// ----------------------------------------------------------------------
	// Un-implemented methods
	// ----------------------------------------------------------------------

	@Override
	public void addedStatement(Statement arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.addedStatement() not implemented.");
	}

	@Override
	public void addedStatements(Statement[] arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.addedStatements() not implemented.");
	}

	@Override
	public void addedStatements(List<Statement> arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.addedStatements() not implemented.");
	}

	@Override
	public void addedStatements(StmtIterator arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.addedStatements() not implemented.");
	}

	@Override
	public void addedStatements(Model arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.addedStatements() not implemented.");
	}

	@Override
	public void notifyEvent(Model arg0, Object arg1) {
		throw new RuntimeException(
				"NamespaceMapperStub.notifyEvent() not implemented.");
	}

	@Override
	public void removedStatement(Statement arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.removedStatement() not implemented.");
	}

	@Override
	public void removedStatements(Statement[] arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.removedStatements() not implemented.");
	}

	@Override
	public void removedStatements(List<Statement> arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.removedStatements() not implemented.");
	}

	@Override
	public void removedStatements(StmtIterator arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.removedStatements() not implemented.");
	}

	@Override
	public void removedStatements(Model arg0) {
		throw new RuntimeException(
				"NamespaceMapperStub.removedStatements() not implemented.");
	}

	@Override
	public String getPrefixForNamespace(String namespace) {
		throw new RuntimeException(
				"NamespaceMapperStub.getPrefixForNamespace() not implemented.");
	}

	@Override
	public List<String> getPrefixesForNamespace(String namespace) {
		throw new RuntimeException(
				"NamespaceMapperStub.getPrefixesForNamespace() not implemented.");
	}

}
