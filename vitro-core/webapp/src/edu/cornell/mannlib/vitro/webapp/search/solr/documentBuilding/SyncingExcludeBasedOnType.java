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
package edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;

/**
 * This excludes based on types defined as EXCLUDE_CLASS in the 
 * configuration RDF model. 
 */
public class SyncingExcludeBasedOnType extends ExcludeBasedOnType implements ModelChangedListener{
    static final Log log = LogFactory.getLog(SyncingExcludeBasedOnType.class);        

    private static final String queryForProhibitedClasses = 
        "SELECT ?prohibited WHERE{" +
        "?searchConfig <" + DisplayVocabulary.EXCLUDE_CLASS + "> ?prohibited . " +
        "}";
    
    String searchIndexURI = DisplayVocabulary.SEARCH_INDEX_URI;        
    
    public SyncingExcludeBasedOnType( Model model){            
        this.setExcludedTypes( buildProhibitedClassesList(searchIndexURI, model) );
        log.info("types excluded from search: " + typeURIs);
    }       
        
    private List<String> buildProhibitedClassesList( String URI, Model model){
        List<String> newProhibitedClasses = new ArrayList<String>();
        
        QuerySolutionMap initialBinding = new QuerySolutionMap();
        Resource searchConfig = ResourceFactory.createResource(URI);
        initialBinding.add("searchConfig", searchConfig);

        Query query = QueryFactory.create(queryForProhibitedClasses);
        model.enterCriticalSection(Lock.READ);
        try{
            QueryExecution qExec = QueryExecutionFactory.create(query,model,initialBinding);
            try{
                ResultSet results = qExec.execSelect();
                for(;results.hasNext();){
                    QuerySolution soln = results.nextSolution();                
                    RDFNode n = soln.get("prohibited");
                    if( n.isResource() && !n.isAnon()){
                        newProhibitedClasses.add(((Resource) n).getURI());
                    }else{
                        log.warn("unexpected node in object position for prohibited classes: " + n.toString());
                    }
                }
            }catch(Throwable t){
                log.error(t,t);         
            }finally{ qExec.close(); }
        }finally{ model.leaveCriticalSection(); }                                
        
        return newProhibitedClasses;
    }

    
    /* ************* Methods for ModelChangeListener *************** */
    
    @Override
    public void addedStatement(Statement s) {
        try{
            if( isExcludeClassPredicate( s ) && isAboutSearchIndex(s)){             
                if( s.getObject() != null && s.getObject().canAs(Resource.class)){                
                    String classURI = ((Resource)s.getObject().as(Resource.class)).getURI();     
                    this.addTypeToExclude(classURI);
                    log.debug("prohibited classes: " + this.typeURIs);
                }
            }
        }catch(Exception ex){
            log.error("could not add statement",ex);
        }
        
    }

    @Override
    public void removedStatement(Statement s) { 
        try{
            if( isExcludeClassPredicate( s ) && isAboutSearchIndex(s)){             
                if( s.getObject() != null && s.getObject().canAs(Resource.class)){            
                    String classURI = ((Resource)s.getObject().as(Resource.class)).getURI();                            
                    this.removeTypeToExclude(classURI);
                    log.debug("prohibited classes: " + this.typeURIs);
                }
            }
        }catch(Exception ex){
            log.error("could not remove statement",ex);
        }
    }
    
    private boolean isExcludeClassPredicate(Statement s){
        return s != null
            && s.getPredicate() != null
            && DisplayVocabulary.EXCLUDE_CLASS.getURI().equals( s.getPredicate().getURI()); 
    }
    
    private boolean isAboutSearchIndex(Statement s){
        if( s.getSubject() != null ){
            String subURI = ((Resource) s.getSubject()).getURI() ;
            return this.searchIndexURI.equals(subURI);
        }else{
            return false;
        }
    }
    
    @Override
    public void addedStatements(Statement[] stmts) {
        if( stmts != null ){
            for( Statement stmt : stmts){
                addedStatement(stmt);
            }
        }        
    }

    @Override
    public void addedStatements(List<Statement> stmts) {
        if( stmts != null ){
            for( Statement stmt : stmts){
                addedStatement(stmt);
            }
        }   
    }

    @Override
    public void addedStatements(StmtIterator it) {
        while(it.hasNext()){
            Statement stmt = it.nextStatement();
            addedStatement(stmt);
        }        
    }

    @Override
    public void addedStatements(Model model) {
        if( model != null){
            addedStatements(model.listStatements(
                    model.createResource(searchIndexURI), 
                    DisplayVocabulary.EXCLUDE_CLASS, 
                    (RDFNode)null));
        }        
    }

    @Override
    public void notifyEvent(Model arg0, Object arg1) {
        //nothing        
    }

    @Override
    public void removedStatements(Statement[] stmts) {
        if( stmts != null ){
            for( Statement stmt : stmts){
                removedStatement(stmt);
            }
        }       
    }

    @Override
    public void removedStatements(List<Statement> stmts) {
        if( stmts != null ){
            for( Statement stmt : stmts){
                removedStatement(stmt);
            }
        }       
    }

    @Override
    public void removedStatements(StmtIterator it) {
        while(it.hasNext()){
            Statement stmt = it.nextStatement();
            removedStatement(stmt);
        } 
    }

    @Override
    public void removedStatements(Model model) {
        if( model != null){
            removedStatements(model.listStatements(
                    model.createResource(searchIndexURI), 
                    DisplayVocabulary.EXCLUDE_CLASS, 
                    (RDFNode)null));
        }                
    }

}
