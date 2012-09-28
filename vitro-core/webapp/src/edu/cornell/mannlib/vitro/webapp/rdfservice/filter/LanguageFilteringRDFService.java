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

package edu.cornell.mannlib.vitro.webapp.rdfservice.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeListener;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeSet;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;

public class LanguageFilteringRDFService implements RDFService {
        
    private static final Log log = LogFactory.getLog(LanguageFilteringRDFService.class);
    private RDFService s;
    private List<String> langs;
    
    public LanguageFilteringRDFService(RDFService service, List<String> langs) {
        this.s = service;
        this.langs = normalizeLangs(langs);
    }
    
    private List<String> normalizeLangs(List<String> langs) {
        List<String> normalizedLangs = new ArrayList<String>();
        String currentBaseLang = null;
        for (String lang : langs) {
            String normalizedLang = StringUtils.lowerCase(lang);
            String baseLang = normalizedLang.split("-")[0];
            if (currentBaseLang == null) {
                currentBaseLang = baseLang;
            } else if (!currentBaseLang.equals(baseLang)) {
                if (!normalizedLangs.contains(currentBaseLang)) {
                    normalizedLangs.add(currentBaseLang);
                }
                currentBaseLang = baseLang;
            }
        }
        if (currentBaseLang != null && !normalizedLangs.contains(currentBaseLang)) {
            normalizedLangs.add(currentBaseLang);
        }
        return normalizedLangs;
    }

    @Override
    public boolean changeSetUpdate(ChangeSet changeSet) 
            throws RDFServiceException {
        return s.changeSetUpdate(changeSet);
    }

    @Override
    public void newIndividual(String individualURI, String individualTypeURI)
            throws RDFServiceException {
        s.newIndividual(individualURI, individualTypeURI);
    }

    @Override
    public void newIndividual(String individualURI,
            String individualTypeURI, String graphURI)
            throws RDFServiceException {
        s.newIndividual(individualURI, individualTypeURI, graphURI);
    }

    @Override
    public InputStream sparqlConstructQuery(String query,
            ModelSerializationFormat resultFormat)
            throws RDFServiceException {
        Model m = RDFServiceUtils.parseModel(s.sparqlConstructQuery(query, resultFormat), resultFormat);
        InputStream in = outputModel(filterModel(m), resultFormat);
        return in;
    }

    @Override
    public InputStream sparqlDescribeQuery(String query,
            ModelSerializationFormat resultFormat)
            throws RDFServiceException {
        Model m = RDFServiceUtils.parseModel(s.sparqlDescribeQuery(query, resultFormat), resultFormat);
        return outputModel(filterModel(m), resultFormat);        
    }
    
    private InputStream outputModel(Model m, ModelSerializationFormat resultFormat) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        m.write(out, RDFServiceUtils.getSerializationFormatString(resultFormat));
        return new ByteArrayInputStream(out.toByteArray());
    }
    
    private Model filterModel(Model m) {
        List<Statement> retractions = new ArrayList<Statement>();
        StmtIterator stmtIt = m.listStatements();
        while (stmtIt.hasNext()) {
            Statement stmt = stmtIt.nextStatement();
            if (stmt.getObject().isLiteral()) {
                List<Statement> candidatesForRemoval = m.listStatements(
                        stmt.getSubject(), stmt.getPredicate(), (RDFNode) null).toList();
                if (candidatesForRemoval.size() == 1) {
                    continue;
                }
                Collections.sort(candidatesForRemoval, new StatementSortByLang());
                Iterator<Statement> candIt = candidatesForRemoval.iterator();
                String langRegister = null;
                boolean chuckRemaining = false;
                while(candIt.hasNext()) {
                    Statement s = candIt.next();
                    if (!s.getObject().isLiteral()) {
                        continue;
                    } else if (chuckRemaining) {
                        retractions.add(s);
                    } 
                    String lang = s.getObject().asLiteral().getLanguage();
                    if (langRegister == null) {
                        langRegister = lang;
                    } else if (!langRegister.equals(lang)) {
                        chuckRemaining = true;
                        retractions.add(s);
                    }
                }
            }
            
        }
        m.remove(retractions);
        return m;
    }

    @Override
    public InputStream sparqlSelectQuery(String query,
            ResultFormat resultFormat) throws RDFServiceException {        
        ResultSet resultSet = ResultSetFactory.fromJSON(
                s.sparqlSelectQuery(query, RDFService.ResultFormat.JSON));
        List<QuerySolution> solnList = getSolutionList(resultSet);
        List<String> vars = resultSet.getResultVars();
        Iterator<String> varIt = vars.iterator();
        while (varIt.hasNext()) {
            String var = varIt.next();           
            for (int i = 0 ; i < solnList.size(); i ++ ) {
                QuerySolution s = solnList.get(i);
                if (s == null) {
                    continue;
                }
                RDFNode node = s.get(var);
                if (node == null || !node.isLiteral()) {
                    continue;
                }
                List<RowIndexedLiteral> candidatesForRemoval = 
                        new ArrayList<RowIndexedLiteral>();
                candidatesForRemoval.add(new RowIndexedLiteral(node.asLiteral(), i));
                for (int j = i + 1; j < solnList.size(); j ++) {
                    QuerySolution t = solnList.get(j);
                    if (t == null) {
                        continue;
                    }
                    if (matchesExceptForVar(s, t, var, vars)) {
                        candidatesForRemoval.add(
                                new RowIndexedLiteral(t.getLiteral(var), j));
                    }
                }
                if (candidatesForRemoval.size() == 1) {
                    continue;
                }
                Collections.sort(candidatesForRemoval, new RowIndexedLiteralSortByLang());
                Iterator<RowIndexedLiteral> candIt = candidatesForRemoval.iterator();
                String langRegister = null;
                boolean chuckRemaining = false;
                while(candIt.hasNext()) {
                    RowIndexedLiteral rlit = candIt.next();
                    if (chuckRemaining) {
                        solnList.set(rlit.getIndex(), null);
                    } else if (langRegister == null) {
                        langRegister = rlit.getLiteral().getLanguage();
                    } else if (!langRegister.equals(rlit.getLiteral().getLanguage())) {
                        chuckRemaining = true;
                        solnList.set(rlit.getIndex(), null);
                    }
                }
            }
        }
        List<QuerySolution> compactedList = new ArrayList<QuerySolution>();
        Iterator<QuerySolution> solIt = solnList.iterator();
        while(solIt.hasNext()) {
            QuerySolution soln = solIt.next();
            if (soln != null) {
                compactedList.add(soln);
            }
        }
        ResultSet filtered = new FilteredResultSet(compactedList, resultSet);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
        switch (resultFormat) {
           case CSV:
              ResultSetFormatter.outputAsCSV(outputStream, filtered);
              break;
           case TEXT:
              ResultSetFormatter.out(outputStream, filtered);
              break;
           case JSON:
              ResultSetFormatter.outputAsJSON(outputStream, filtered);
              break;
           case XML:
              ResultSetFormatter.outputAsXML(outputStream, filtered);
              break;
           default: 
              throw new RDFServiceException("unrecognized result format");
        }        
        return new ByteArrayInputStream(outputStream.toByteArray());       
    }
    
    private class RowIndexedLiteral {
        
        private Literal literal;
        private int index;
        
        public RowIndexedLiteral(Literal literal, int index) {
            this.literal = literal;
            this.index = index;
        }
        
        public Literal getLiteral() {
            return this.literal;
        }
        
        public int getIndex() {
            return index;   
        }
        
    }
    
    private boolean matchesExceptForVar(QuerySolution a, QuerySolution b, 
            String varName, List<String> varList) {
        if (varName == null) {
            throw new RuntimeException("expected non-null variable nane");
        }
        for (String var : varList) {
            RDFNode nodea = a.get(var);
            RDFNode nodeb = b.get(var);
            if (var.equals(varName)) {
                if (nodea == null || !nodea.isLiteral() || nodeb == null || !nodeb.isLiteral()) {
                    return false;
                }
            } else {
                if (nodea == null && nodeb == null) {
                    continue;
                } else if (nodea == null && nodeb != null) {
                    return false;
                } else if (nodeb == null && nodea != null) {
                    return false;
                }
                if (!a.get(var).equals(b.get(var))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private List<QuerySolution> getSolutionList(ResultSet resultSet) {
        List<QuerySolution> solnList = new ArrayList<QuerySolution>();
        while (resultSet.hasNext()) {
            QuerySolution soln = resultSet.nextSolution();
            solnList.add(soln);
        }
        return solnList;
    }

    @Override
    public boolean sparqlAskQuery(String query) throws RDFServiceException {
        return s.sparqlAskQuery(query);
    }

    @Override
    public List<String> getGraphURIs() throws RDFServiceException {
        return s.getGraphURIs();
    }

    @Override
    public void getGraphMetadata() throws RDFServiceException {
        s.getGraphMetadata();
    }

    @Override
    public String getDefaultWriteGraphURI() throws RDFServiceException {
        return s.getDefaultWriteGraphURI();
    }

    @Override
    public void registerListener(ChangeListener changeListener)
            throws RDFServiceException {
        // TODO Auto-generated method stub
        s.registerListener(changeListener);
    }

    @Override
    public void unregisterListener(ChangeListener changeListener)
            throws RDFServiceException {
        s.unregisterListener(changeListener);
    }

    @Override
    public ChangeSet manufactureChangeSet() {
        return s.manufactureChangeSet();
    }

    @Override
    public void close() {
        s.close();            
    }
       
    private class LangSort {
        
        protected int compareLangs(String t1lang, String t2lang) {
            t1lang = StringUtils.lowerCase(t1lang);
            t2lang = StringUtils.lowerCase(t2lang);
            if ( t1lang == null && t2lang == null) {
                return 0;
            } else if (t1lang == null) { 
                return 1;
            } else if (t2lang == null) {
                return -1;
            } else {        
                int t1langPref = langs.indexOf(t1lang);
                int t2langPref = langs.indexOf(t2lang);
                if (t1langPref == -1 && t2langPref == -1) {
                    if ("".equals(t1lang) && "".equals(t2lang)) {
                        return 0;
                    } else if ("".equals(t1lang) && !("".equals(t2lang))) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else if (t1langPref > -1 && t2langPref == -1) {
                    return -1;
                } else if (t1langPref == -1 && t2langPref > -1) {
                    return 1;
                } else {
                    return t1langPref - t2langPref;
                }
            }
        }
        
    }
    
    private class RowIndexedLiteralSortByLang extends LangSort implements Comparator<RowIndexedLiteral> {
        
        public int compare(RowIndexedLiteral rilit1, RowIndexedLiteral rilit2) {
            if (rilit1 == null || rilit2 == null) {
                return 0;
            }
            
            String t1lang = rilit1.getLiteral().getLanguage();
            String t2lang = rilit2.getLiteral().getLanguage();
                    
            return compareLangs(t1lang, t2lang);
        }   
    }
    
    private class StatementSortByLang extends LangSort implements Comparator<Statement> {
        
        public int compare(Statement s1, Statement s2) {
            if (s1 == null || s2 == null) {
                return 0;
            } else if (!s1.getObject().isLiteral() || !s2.getObject().isLiteral()) {
                return 0;
            }
            
            String s1lang = s1.getObject().asLiteral().getLanguage();
            String s2lang = s2.getObject().asLiteral().getLanguage();
                    
            return compareLangs(s1lang, s2lang);
        }
    }
    
}
    
    
    
