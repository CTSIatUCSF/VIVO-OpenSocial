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

import java.util.Set;

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

public class DifferenceGraph implements Graph {

    private Graph g;
    private Graph subtract;
    
    public DifferenceGraph(Graph g, Graph subtract) {    
        this.g = g;
        this.subtract = subtract;
    }
    
    @Override
    public void close() {
        // not clear what the best behavior here is
    }

    @Override
    public boolean contains(Triple arg0) {
        return g.contains(arg0) && !subtract.contains(arg0);
    }

    @Override
    public boolean contains(Node arg0, Node arg1, Node arg2) {
        return g.contains(arg0, arg1, arg2) && !subtract.contains(arg0, arg1, arg2);
    }

    @Override
    public void delete(Triple arg0) throws DeleteDeniedException {
        g.delete(arg0);
    }

    @Override
    public boolean dependsOn(Graph arg0) {
        return g.dependsOn(arg0);
    }

    @Override
    public ExtendedIterator<Triple> find(TripleMatch arg0) {
        Set<Triple> tripSet = g.find(arg0).toSet();
        tripSet.removeAll(subtract.find(arg0).toSet());
        return WrappedIterator.create(tripSet.iterator());
    }

    @Override
    public ExtendedIterator<Triple> find(Node arg0, Node arg1, Node arg2) {
        Set<Triple> tripSet = g.find(arg0, arg1, arg2).toSet();
        tripSet.removeAll(subtract.find(arg0, arg1, arg2).toSet());
        return WrappedIterator.create(tripSet.iterator());
    }

    @Override
    public BulkUpdateHandler getBulkUpdateHandler() {
        return g.getBulkUpdateHandler();
    }

    @Override
    public Capabilities getCapabilities() {
        return g.getCapabilities();
    }

    @Override
    public GraphEventManager getEventManager() {
        return g.getEventManager();
    }

    @Override
    public PrefixMapping getPrefixMapping() {
        return g.getPrefixMapping();
    }

    @Override
    public Reifier getReifier() {
        return g.getReifier();
    }

    @Override
    public GraphStatisticsHandler getStatisticsHandler() {
        return g.getStatisticsHandler();
    }

    @Override
    public TransactionHandler getTransactionHandler() {
        return g.getTransactionHandler();
    }

    @Override
    public boolean isClosed() {
        return g.isClosed();
    }

    @Override
    public boolean isEmpty() {
        return g.isEmpty();
    }

    @Override
    public boolean isIsomorphicWith(Graph arg0) {
        return g.isIsomorphicWith(arg0);
    }

    @Override
    public QueryHandler queryHandler() {
        return g.queryHandler();
    }

    @Override
    public int size() {
        return g.size() - subtract.size();
    }

    @Override
    public void add(Triple arg0) throws AddDeniedException {
        g.add(arg0);
    }

}
