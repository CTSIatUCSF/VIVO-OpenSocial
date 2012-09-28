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
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.LockMRSW;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.resultset.JSONInput;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.util.iterator.SingletonIterator;
import com.hp.hpl.jena.util.iterator.WrappedIterator;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;

public class RDFServiceDatasetGraph implements DatasetGraph {

    private RDFService rdfService;
    private Lock lock = new LockMRSW();
    
    public RDFServiceDatasetGraph(RDFService rdfService) {
        this.rdfService = rdfService;
    }

    private Graph getGraphFor(Quad q) {
        return getGraphFor(q.getGraph());
    }
    
    private Graph getGraphFor(Node g) {
        return (g == Node.ANY) 
                ? new RDFServiceGraph(rdfService) 
                : new RDFServiceGraph(rdfService, g.getURI());
    }
    
    @Override
    public void add(Quad arg0) {
        getGraphFor(arg0).add(new Triple(arg0.getSubject(), arg0.getPredicate(), arg0.getObject()));
    }

    @Override
    public void addGraph(Node arg0, Graph arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean contains(Quad arg0) {
        return getGraphFor(arg0).contains(new Triple(arg0.getSubject(), arg0.getPredicate(), arg0.getObject()));
    }

    @Override
    public boolean contains(Node arg0, Node arg1, Node arg2, Node arg3) {
        return getGraphFor(arg0).contains(arg1, arg2, arg3);
    }

    @Override
    public boolean containsGraph(Node arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void delete(Quad arg0) {
        getGraphFor(arg0).delete(new Triple(arg0.getSubject(), arg0.getPredicate(), arg0.getObject()));
    }

    @Override
    public void deleteAny(Node arg0, Node arg1, Node arg2, Node arg3) {
        // TODO check this
        getGraphFor(arg0).delete(new Triple(arg1, arg2, arg3));
    }

    @Override
    public Iterator<Quad> find() {
        return find(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
    }

    @Override
    public Iterator<Quad> find(Quad arg0) {
        return find(arg0.getSubject(), arg0.getPredicate(), arg0.getObject(), arg0.getGraph());
    }

    @Override
    public Iterator<Quad> find(Node graph, Node subject, Node predicate, Node object) {
        if (!isVar(subject) && !isVar(predicate)  && !isVar(object) &&!isVar(graph)) {
            if (contains(subject, predicate, object, graph)) {
                return new SingletonIterator(new Triple(subject, predicate, object));
            } else {
                return WrappedIterator.create(Collections.EMPTY_LIST.iterator());
            }
        }
        StringBuffer findQuery = new StringBuffer("SELECT * WHERE { \n");
        String graphURI = !isVar(graph) ? graph.getURI() : null;
        findQuery.append("  GRAPH ");
        if (graphURI != null) {
            findQuery.append("  <" + graphURI + ">");
        } else {
            findQuery.append("?g");
        }
        findQuery.append(" { ");
        findQuery.append(SparqlGraph.sparqlNode(subject, "?s"))
        .append(" ")
        .append(SparqlGraph.sparqlNode(predicate, "?p"))
        .append(" ")
        .append(SparqlGraph.sparqlNode(object, "?o"));
        findQuery.append("  } ");
        findQuery.append("\n}");
        
        //log.info(findQuery.toString());
        
        ResultSet rs = null;
        
        try {
            rs = JSONInput.fromJSON(rdfService.sparqlSelectQuery(
                    findQuery.toString(), RDFService.ResultFormat.JSON));
        } catch (RDFServiceException rdfse) {
            throw new RuntimeException(rdfse);
        }
        
        List<Quad> quadlist = new ArrayList<Quad>();
        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            Quad q = new Quad(isVar(graph) ? soln.get("?g").asNode() : graph,
                                  isVar(subject) ? soln.get("?s").asNode() : subject, 
                                  isVar(predicate) ? soln.get("?p").asNode() : predicate, 
                                  isVar(object) ? soln.get("?o").asNode() : object);
            //log.info(t);
            quadlist.add(q);
        }
        //log.info(triplist.size() + " results");
        return WrappedIterator.create(quadlist.iterator());    }

    @Override
    public Iterator<Quad> findNG(Node arg0, Node arg1, Node arg2, Node arg3) {
        // TODO check this
        return find(arg0, arg1, arg2, arg3);
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RDFServiceGraph getDefaultGraph() {
        return new RDFServiceGraph(rdfService);
    }

    @Override
    public RDFServiceGraph getGraph(Node arg0) {
        return new RDFServiceGraph(rdfService, arg0.getURI());
    }

    @Override
    public Lock getLock() {
        return lock;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<Node> listGraphNodes() {
        List<Node> graphNodeList = new ArrayList<Node>();
        try {
            for (String graphURI : rdfService.getGraphURIs()) {
                graphNodeList.add(Node.createURI(graphURI));   
            }
        } catch (RDFServiceException rdfse) {
            throw new RuntimeException(rdfse);
        }
        return graphNodeList.iterator();
    }

    @Override
    public void removeGraph(Node arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDefaultGraph(Graph arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public long size() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    private boolean isVar(Node node) {
        return (node == null || node.isVariable() || node == Node.ANY);
    }
    


}
