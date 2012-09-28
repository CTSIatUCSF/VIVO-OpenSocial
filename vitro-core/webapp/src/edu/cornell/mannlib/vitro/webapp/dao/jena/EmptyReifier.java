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
import java.util.Collections;

import org.apache.commons.collections.iterators.EmptyIterator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Reifier;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

public class EmptyReifier implements Reifier {

    private Graph g;
    
    public EmptyReifier(Graph g) {
        this.g = g;
    }
    
    @Override
    public Triple getTriple(Node arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExtendedIterator<Node> allNodes() {
        return WrappedIterator.create(Collections.EMPTY_LIST.iterator());
    }

    @Override
    public ExtendedIterator<Node> allNodes(Triple arg0) {
        return WrappedIterator.create(Collections.EMPTY_LIST.iterator());
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public ExtendedIterator<Triple> find(TripleMatch arg0) {
        return g.find(arg0);
    }

    @Override
    public ExtendedIterator<Triple> findEither(TripleMatch arg0, boolean arg1) {
        return WrappedIterator.create(EmptyIterator.INSTANCE);
    }

    @Override
    public ExtendedIterator<Triple> findExposed(TripleMatch arg0) {
        return WrappedIterator.create(EmptyIterator.INSTANCE);
    }

    @Override
    public Graph getParentGraph() {
        return g;
    }

    @Override
    public ReificationStyle getStyle() {
        return ReificationStyle.Minimal;
    }

    @Override
    public boolean handledAdd(Triple arg0) {
        g.add(arg0);
        return true;
    }

    @Override
    public boolean handledRemove(Triple arg0) {
        g.delete(arg0);
        return true;
    }

    @Override
    public boolean hasTriple(Node arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasTriple(Triple arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Node reifyAs(Node arg0, Triple arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(Triple arg0) {
        g.delete(arg0);
    }

    @Override
    public void remove(Node arg0, Triple arg1) {
        g.delete(arg1);
    }

    @Override
    public int size() {
        return g.size();
    }

}
