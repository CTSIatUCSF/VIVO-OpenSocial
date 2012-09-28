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

package edu.cornell.mannlib.vitro.webapp.dao.jena;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.BulkUpdateHandler;
import com.hp.hpl.jena.graph.Capabilities;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEventManager;
import com.hp.hpl.jena.graph.GraphStatisticsHandler;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.TransactionHandler;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.AddDeniedException;
import com.hp.hpl.jena.shared.DeleteDeniedException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

public class BlankNodeFilteringGraph implements Graph {
	
    private Graph graph;
    
	public BlankNodeFilteringGraph(Graph graph) {
        this.graph = graph;
    }

	@Override
	public void add(Triple t) throws AddDeniedException {
		graph.add(t);
	}

	@Override
	public void close() {
		graph.close();
	}

	@Override
	public boolean contains(Node arg0, Node arg1, Node arg2) {
		return graph.contains(arg0, arg1, arg2);
	}

	@Override
	public boolean contains(Triple arg0) {
		return graph.contains(arg0);
	}

	@Override
	public void delete(Triple t) throws DeleteDeniedException {
		graph.delete(t);
	}

	@Override
	public boolean dependsOn(Graph arg0) {
		return graph.dependsOn(arg0);
	}

	@Override
	public ExtendedIterator<Triple> find(Node subject, Node predicate, Node object) {
		
        List<Triple> nbTripList = new ArrayList<Triple>();
		ExtendedIterator<Triple> triples = graph.find(subject, predicate, object);
		
		while (triples.hasNext()) {
			Triple triple = triples.next();
			
			if (!triple.getSubject().isBlank() && !triple.getObject().isBlank()) {
				nbTripList.add(triple);
			}
		}
		
        return WrappedIterator.create(nbTripList.iterator());
	}

	@Override
	public ExtendedIterator<Triple> find(TripleMatch tripleMatch) {
	     Triple t = tripleMatch.asTriple();
	     return find(t.getSubject(), t.getPredicate(), t.getObject());
	}

	@Override
	public BulkUpdateHandler getBulkUpdateHandler() {
		return graph.getBulkUpdateHandler();
	}

	@Override
	public Capabilities getCapabilities() {
		return graph.getCapabilities();
	}

	@Override
	public GraphEventManager getEventManager() {
		return graph.getEventManager();
	}

	@Override
	public PrefixMapping getPrefixMapping() {
		return graph.getPrefixMapping();
	}

	@Override
	public Reifier getReifier() {
		return graph.getReifier();
	}

	@Override
	public GraphStatisticsHandler getStatisticsHandler() {
		return graph.getStatisticsHandler();
	}

	@Override
	public TransactionHandler getTransactionHandler() {
		return graph.getTransactionHandler();
	}

	@Override
	public boolean isClosed() {
		return graph.isClosed();
	}

	@Override
	public boolean isEmpty() {
		return graph.isEmpty();
	}

	@Override
	public boolean isIsomorphicWith(Graph arg0) {
		return graph.isIsomorphicWith(arg0);
	}

	@Override
	public QueryHandler queryHandler() {
		return graph.queryHandler();
	}

	@Override
	public int size() {
		return graph.size();
	}
}
