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

public class SpecialBulkUpdateHandlerGraph implements Graph {

    private Graph g;
    private BulkUpdateHandler b;
    
    public SpecialBulkUpdateHandlerGraph(Graph g, BulkUpdateHandler b) {
        this.g = g;
        this.b = b;
    }

    public void add(Triple arg0) throws AddDeniedException {
        g.add(arg0);
    }

    public void close() {
        g.close();
    }

    public boolean contains(Node arg0, Node arg1, Node arg2) {
        return g.contains(arg0, arg1, arg2);
    }

    public boolean contains(Triple arg0) {
        return g.contains(arg0);
    }

    public void delete(Triple arg0) throws DeleteDeniedException {
        g.delete(arg0);
    }

    public boolean dependsOn(Graph arg0) {
        return g.dependsOn(arg0);
    }

    public ExtendedIterator<Triple> find(Node arg0, Node arg1, Node arg2) {
        return g.find(arg0, arg1, arg2);
    }

    public ExtendedIterator<Triple> find(TripleMatch arg0) {
        return g.find(arg0);
    }

    public BulkUpdateHandler getBulkUpdateHandler() {
        return b;
    }

    public Capabilities getCapabilities() {
        return g.getCapabilities();
    }

    public GraphEventManager getEventManager() {
        return g.getEventManager();
    }

    public PrefixMapping getPrefixMapping() {
        return g.getPrefixMapping();
    }

    public Reifier getReifier() {
        return g.getReifier();
    }

    public GraphStatisticsHandler getStatisticsHandler() {
        return g.getStatisticsHandler();
    }

    public TransactionHandler getTransactionHandler() {
        return g.getTransactionHandler();
    }

    public boolean isClosed() {
        return g.isClosed();
    }

    public boolean isEmpty() {
        return g.isEmpty();
    }

    public boolean isIsomorphicWith(Graph arg0) {
        return g.isIsomorphicWith(arg0);
    }

    public QueryHandler queryHandler() {
        return g.queryHandler();
    }

    public int size() {
        return g.size();
    }
    
}
