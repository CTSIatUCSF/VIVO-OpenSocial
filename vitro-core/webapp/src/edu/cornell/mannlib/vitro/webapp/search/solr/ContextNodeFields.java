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
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;

/**
 * DocumentModifier that will run SPARQL queries for an
 * Individual and add all the columns from all the rows
 * in the solution set to the ALLTEXT field.
 *  
 * @author bdc34
 *
 */
public class ContextNodeFields implements DocumentModifier{
    protected Model model;
    protected List<String> queries = new ArrayList<String>();
    
    protected boolean shutdown = false;    
    protected Log log = LogFactory.getLog(ContextNodeFields.class);                      
             
   
    /**
     * Construct this with a model to query when building Solr Documents and
     * a list of the SPARQL queries to run.
     */
    protected ContextNodeFields(Model model, List<String> queries){
        this.model = model;        
        this.queries = queries;
    }        
    
    /**
     * Implement this method to get values that will be added to ALLTEXT 
     * field of solr Document for each individual.
     * 
     * @param individual
     * @return StringBuffer with text values to add to ALLTEXT field of solr Document.
     */
    protected StringBuffer getValues( Individual individual ){
        return executeQueryForValues( individual, queries );        
    }                
    
    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc, StringBuffer addUri) {        
        if( individual == null )
            return;
        
        log.debug( "doing context nodes for: " +  individual.getURI());
        
        /* get text from the context nodes and add the to ALLTEXT */        
        StringBuffer values = getValues( individual );        
        
        SolrInputField field = doc.getField(VitroSearchTermNames.ALLTEXT);
        if( field == null ){
            doc.addField(VitroSearchTermNames.ALLTEXT, values);           
        }else{
            field.addValue(values, field.getBoost());
        }                                      
    }
    
    
    protected StringBuffer executeQueryForValues( Individual individual, Collection<String> queries){
        /* execute all the queries on the list and concat the values to add to all text */
        
        StringBuffer allValues = new StringBuffer("");
        
        QuerySolutionMap initialBinding = new QuerySolutionMap();               
        initialBinding.add("uri", ResourceFactory.createResource(individual.getURI()));

        for(String query : queries ){    
            StringBuffer valuesForQuery = new StringBuffer();
            
            Query sparqlQuery = QueryFactory.create( query, Syntax.syntaxARQ);
            model.getLock().enterCriticalSection(Lock.READ);
            try{
                QueryExecution qExec = 
                    QueryExecutionFactory.create(sparqlQuery, model, initialBinding);
                try{                
                    ResultSet results = qExec.execSelect();                
                    while(results.hasNext()){                                                                               
                        valuesForQuery.append( 
                                getTextForRow( results.nextSolution() ) ) ;
                    }
                }catch(Throwable t){
                    if( ! shutdown )
                        log.error(t,t);
                } finally{
                    qExec.close();
                } 
            }finally{
                model.getLock().leaveCriticalSection();
            }
            if(log.isDebugEnabled()){
                log.debug("query: '" + query + "'");
                log.debug("text for query: '" + valuesForQuery.toString() + "'");
            }
            allValues.append(valuesForQuery);
        }
        return allValues;        
    }       

    protected String getTextForRow( QuerySolution row){
        if( row == null )
            return "";

        StringBuffer text = new StringBuffer();
        Iterator<String> iter =  row.varNames() ;
        while( iter.hasNext()){
            String name = iter.next();
            RDFNode node = row.get( name );
            if( node != null ){
                text.append(" ").append( node.toString() );
            }else{
                log.debug(name + " is null");
            }                        
        }        
        return text.toString();
    }
    
    
    public void shutdown(){
        shutdown=true;  
    }
}
