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
package edu.cornell.mannlib.vitro.webapp.utils.dataGetter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;

public class FixedHTMLDataGetter extends DataGetterBase implements DataGetter{    
    String dataGetterURI;
    String htmlValue;
    String saveToVar;
    VitroRequest vreq;
    ServletContext context;
    private final static String defaultTemplate = "menupage--defaultFixedHtml.ftl";
    
    final static Log log = LogFactory.getLog(FixedHTMLDataGetter.class);
    
    /**
     * Constructor with display model and data getter URI that will be called by reflection.
     */
    public FixedHTMLDataGetter(VitroRequest vreq, Model displayModel, String dataGetterURI){
        this.configure(vreq, displayModel,dataGetterURI);
    }        
    
    @Override
    public Map<String, Object> getData(Map<String, Object> pageData) {     
    	 Map<String, Object> rmap = new HashMap<String,Object>();
    	 rmap.put("variableName", this.saveToVar);
         rmap.put(this.saveToVar, this.htmlValue); 
         //this is the default template set here - overridden by page level template if there is one
         rmap.put("bodyTemplate", defaultTemplate);
         return rmap;
    }

    /**
     * Configure this instance based on the URI and display model.
     */
    protected void configure(VitroRequest vreq, Model displayModel, String dataGetterURI) {
    	if( vreq == null ) 
    		throw new IllegalArgumentException("VitroRequest  may not be null.");
        if( displayModel == null ) 
            throw new IllegalArgumentException("Display Model may not be null.");
        if( dataGetterURI == null )
            throw new IllegalArgumentException("PageUri may not be null.");
                
        this.vreq = vreq;
        this.context = vreq.getSession().getServletContext();
        this.dataGetterURI = dataGetterURI;        
        
        QuerySolutionMap initBindings = new QuerySolutionMap();
        initBindings.add("dataGetterURI", ResourceFactory.createResource(this.dataGetterURI));
        
        int count = 0;
        Query dataGetterConfigurationQuery = QueryFactory.create(dataGetterQuery) ;               
        displayModel.enterCriticalSection(Lock.READ);
        try{
            QueryExecution qexec = QueryExecutionFactory.create(
                    dataGetterConfigurationQuery, displayModel, initBindings) ;        
            ResultSet res = qexec.execSelect();
            try{                
                while( res.hasNext() ){
                    count++;
                    QuerySolution soln = res.next();
                    
                    // is NOT OPTIONAL
                    Literal value = soln.getLiteral("htmlValue");
                    if( dataGetterConfigurationQuery == null )
                        log.error("no html value defined for page " + this.dataGetterURI);
                    else
                        this.htmlValue = value.getLexicalForm();                    
                    
                        
                    //saveToVar is OPTIONAL
                    Literal saveTo = soln.getLiteral("saveToVar");
                    if( saveTo != null && saveTo.isLiteral() ){
                        this.saveToVar = saveTo.asLiteral().getLexicalForm();                        
                    }else{
                        this.saveToVar = defaultVarNameForResults;
                    }
                }
            }finally{ qexec.close(); }
        }finally{ displayModel.leaveCriticalSection(); }                
    }
    
    

        private static final String saveToVarPropertyURI= "<" + DisplayVocabulary.SAVE_TO_VAR+ ">";
    private static final String htmlValuePropertyURI= "<" + DisplayVocabulary.FIXED_HTML_VALUE+ ">";

    public static final String defaultVarNameForResults = "results";
    
    /**
     * Query to get the definition of the SparqlDataGetter for a given URI.
     */
    private static final String dataGetterQuery =
        "PREFIX display: <" + DisplayVocabulary.DISPLAY_NS +"> \n" +
        "SELECT ?saveToVar ?htmlValue WHERE { \n" +
        "  ?dataGetterURI "+saveToVarPropertyURI+" ?saveToVar . \n" +
        "  ?dataGetterURI "+htmlValuePropertyURI+" ?htmlValue . \n" +
        "}";      

   
}
