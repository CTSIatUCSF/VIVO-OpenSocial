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

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.SimpleBulkUpdateHandler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.util.graph.GraphFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class SparqlGraphBulkUpdater extends SimpleBulkUpdateHandler {

    private static final Log log = LogFactory.getLog(SparqlGraphBulkUpdater.class);
    private SparqlGraph graph;
    
    public SparqlGraphBulkUpdater(SparqlGraph graph) {
        super(graph);
        this.graph = graph;
    }
    
    @Override
    public void add(Triple[] arg0) {
        Graph g = GraphFactory.createPlainGraph();
        for (int i = 0 ; i < arg0.length ; i++) {
            g.add(arg0[i]);
        }
        add(g);
    }

    @Override
    public void add(List<Triple> arg0) {
        Graph g = GraphFactory.createPlainGraph();
        for (Triple t : arg0) {
            g.add(t);
        }
        add(g);
    }

    @Override
    public void add(Iterator<Triple> arg0) {
        Graph g = GraphFactory.createPlainGraph();
        while (arg0.hasNext()) {
            Triple t = arg0.next();
            g.add(t);
        }
        add(g);
    }

    @Override
    public void add(Graph arg0) {
        add(arg0, false);
    }

    @Override
    public void add(Graph g, boolean arg1) {
        log.info("adding graph");
        Model[] model = separateStatementsWithBlankNodes(g);
        addModel(model[1] /* nonBlankNodeModel */);
        // replace following call with different method
        addModel(model[0] /*blankNodeModel*/);
    }
    
    /**
     * Returns a pair of models.  The first contains any statement containing at 
     * least one blank node.  The second contains all remaining statements.
     * @param g
     * @return
     */
    
    private Model[] separateStatementsWithBlankNodes(Graph g) {
        Model gm = ModelFactory.createModelForGraph(g);
        Model blankNodeModel = ModelFactory.createDefaultModel();
        Model nonBlankNodeModel = ModelFactory.createDefaultModel();
        StmtIterator sit = gm.listStatements();
        while (sit.hasNext()) {
            Statement stmt = sit.nextStatement();
            if (!stmt.getSubject().isAnon() && !stmt.getObject().isAnon()) {
                nonBlankNodeModel.add(stmt);
            } else {
                blankNodeModel.add(stmt);
            }
        }
        Model[] result = new Model[2];
        result[0] = blankNodeModel;
        result[1] = nonBlankNodeModel;
        return result;
    }
    

    @Override 
    public void delete(Graph g, boolean withReifications) {
        delete(g);
    }
    
    @Override 
    public void delete(Graph g) {
        Model[] model = separateStatementsWithBlankNodes(g);
        deleteModel(model[1] /*statements without blank nodes*/);
        // replace blank nodes in remaining statements with variables
        
        StringBuffer patternBuff = new StringBuffer();
        Iterator<Triple> tripIt = g.find(null, null, null);
        while(tripIt.hasNext()) {
            Triple t = tripIt.next();
            patternBuff.append(SparqlGraph.sparqlNodeDelete(t.getSubject(), null));
            patternBuff.append(" ");
            patternBuff.append(SparqlGraph.sparqlNodeDelete(t.getPredicate(), null));
            patternBuff.append(" ");
            patternBuff.append(SparqlGraph.sparqlNodeDelete(t.getObject(), null));
            patternBuff.append(" .\n");
        }
        
        StringBuffer queryBuff = new StringBuffer();
        String graphURI = graph.getGraphURI();
        queryBuff.append("DELETE { " + ((graphURI != null) ? "GRAPH <" + graphURI + "> { " : "" ) + " \n");
        queryBuff.append(patternBuff);
        if (graphURI != null) {
            queryBuff.append("    } \n");
        }
        queryBuff.append("} WHERE { \n");
        if (graphURI != null) {
            queryBuff.append("    GRAPH <" + graphURI + "> { \n");
        }
        queryBuff.append(patternBuff);
        if (graphURI != null) {
            queryBuff.append("    } \n");
        }
        queryBuff.append("} \n");
        
        log.debug(queryBuff.toString());
        
        graph.executeUpdate(queryBuff.toString());
        
    }
    
    public void addModel(Model model) {
        verbModel(model, "INSERT");
    }
    
    public void deleteModel(Model model) {
        verbModel(model, "DELETE");
    }
    
    private void verbModel(Model model, String verb) {
        Model m = ModelFactory.createDefaultModel();
        int testLimit = 1000;
        StmtIterator stmtIt = model.listStatements();
        int count = 0;
        try {
            while (stmtIt.hasNext()) {
                count++;
                m.add(stmtIt.nextStatement());
                if (count % testLimit == 0 || !stmtIt.hasNext()) {
                    StringWriter sw = new StringWriter();
                    m.write(sw, "N-TRIPLE");
                    StringBuffer updateStringBuff = new StringBuffer();
                    String graphURI = graph.getGraphURI();
                    updateStringBuff.append(verb + " DATA { " + ((graphURI != null) ? "GRAPH <" + graphURI + "> { " : "" ));
                    updateStringBuff.append(sw);
                    updateStringBuff.append(((graphURI != null) ? " } " : "") + " }");
                    
                    String updateString = updateStringBuff.toString();
                    
                    //log.info(updateString);
                    
                    graph.executeUpdate(updateString);

                    m.removeAll();
                }
            }
        } finally {
            stmtIt.close();
        }
    }
    

    @Override 
    public void removeAll() {
        removeAll(graph);
        notifyRemoveAll(); 
    }

    protected void notifyRemoveAll() { 
        manager.notifyEvent(graph, GraphEvents.removeAll);
    }

    @Override
    public void remove(Node s, Node p, Node o) {
        removeAll(graph, s, p, o);
        manager.notifyEvent(graph, GraphEvents.remove(s, p, o));
    }

    public static void removeAll(Graph g, Node s, Node p, Node o)
    {        
        ExtendedIterator<Triple> it = g.find( s, p, o );
        try { 
            while (it.hasNext()) {
                Triple t = it.next();
                g.delete(t);
                it.remove(); 
            } 
        }
        finally {
            it.close();
        }
    }

    public static void removeAll( Graph g )
    {
        ExtendedIterator<Triple> it = GraphUtil.findAll(g);
        try {
            while (it.hasNext()) {
                Triple t = it.next();
                g.delete(t);
                it.remove();
            } 
        } finally {
            it.close();
        }
        
        // get rid of remaining blank nodes using a SPARQL DELETE
        if (g instanceof SparqlGraph) {
            ((SparqlGraph) g).removeAll();
        }
                
    }

}
