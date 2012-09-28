
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;

import com.hp.hpl.jena.vocabulary.OWL;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.search.IndexingException;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;

public class IndividualToSolrDocument {
        
    public static final Log log = LogFactory.getLog(IndividualToSolrDocument.class.getName());
    
    public static VitroSearchTermNames term = new VitroSearchTermNames();              
    
    protected final String label = "http://www.w3.org/2000/01/rdf-schema#label";
    
    protected List<DocumentModifier> documentModifiers = new ArrayList<DocumentModifier>();

    protected List<SearchIndexExcluder> excludes;
        
    public IndividualToSolrDocument(List<SearchIndexExcluder> excludes, List<DocumentModifier> docModifiers){
        this.excludes = excludes;
        this.documentModifiers = docModifiers;
    }    

	@SuppressWarnings("static-access")
    public SolrInputDocument translate(Individual ind) throws IndexingException{
        try{    	            	      	        	        	
        	String excludeMsg = checkExcludes( ind );
        	if( excludeMsg != DONT_EXCLUDE){
        	    log.debug(ind.getURI() + " " + excludeMsg);
        	    return null;
        	}        	    
        		            
        	SolrInputDocument doc = new SolrInputDocument();                    	
        	
            //DocID
            doc.addField(term.DOCID, getIdForUri( ind.getURI() ) );
            
            //vitro id
            doc.addField(term.URI, ind.getURI());
                    
    		//get label from ind
    		addLabel(ind, doc);
    		
        	//add classes, classgroups get if prohibited because of its class
            StringBuffer classPublicNames = new StringBuffer("");
        	addClasses(ind, doc, classPublicNames);
        	        	        	        	
        	// collecting URIs and rdfs:labels of objects of statements         	
        	StringBuffer objectNames = new StringBuffer("");        	
        	StringBuffer addUri = new StringBuffer("");           	
        	addObjectPropertyText(ind, doc, objectNames, addUri);        	                 	                                           	     
                        
            //time of index in msec past epoch
            doc.addField(term.INDEXEDTIME, new Long( (new DateTime()).getMillis() ) ); 
                        
            addAllText( ind, doc, classPublicNames, objectNames );
               
            //boost for entity
            if(ind.getSearchBoost() != null && ind.getSearchBoost() != 0) {            	
                doc.setDocumentBoost(ind.getSearchBoost());                    
            }    
            
            runAdditionalDocModifers(ind,doc,addUri);            
            
            return doc;
        }catch(SkipIndividualException ex){
            //indicates that this individual should not be indexed by returning null
            log.debug(ex);
            return null;
        }catch(Exception th){
            //Odd exceptions can get thrown on shutdown
            if( log != null )
                log.error(th,th);
            return null;
        }
    }
    
           
	protected String checkExcludes(Individual ind) {
        for( SearchIndexExcluder excluder : excludes){
            try{
                String msg = excluder.checkForExclusion(ind);
                if( msg != DONT_EXCLUDE)
                    return msg;
            }catch (Exception e) {
                return e.getMessage();
            }
        }	    
	    return DONT_EXCLUDE;
    }

	protected Map<String,Long> docModClassToTime = new HashMap<String,Long>();
	protected long docModCount =0;
	
    protected void runAdditionalDocModifers( Individual ind, SolrInputDocument doc, StringBuffer addUri ) 
    throws SkipIndividualException{
        //run the document modifiers
        if( documentModifiers != null && !documentModifiers.isEmpty()){
        	docModCount++;        	
            for(DocumentModifier modifier: documentModifiers){
            	
            	long start = System.currentTimeMillis();
            	
                modifier.modifyDocument(ind, doc, addUri);
                
                if( log.isDebugEnabled()){                	
	                long delta = System.currentTimeMillis() - start;
	                synchronized(docModClassToTime){
	                	Class clz = modifier.getClass();	                	
	                	if( docModClassToTime.containsKey( clz.getName() )){
	                		Long time = docModClassToTime.get(clz.getName() );
	                		docModClassToTime.put(clz.getName(), time + delta);	                		
	                	}else{
	                		docModClassToTime.put(clz.getName(), delta);
	                	}
	                }
	                if( docModCount % 200 == 0 ){
	                	log.debug("DocumentModifier timings");
	                	for( Entry<String, Long> entry: docModClassToTime.entrySet()){	                		
	                		log.debug("average msec to run " + entry.getKey() + ": " + (entry.getValue()/docModCount));                		
	                	}
	                }
                }
            }
        }        
    }
    
    protected void addAllText(Individual ind, SolrInputDocument doc, StringBuffer classPublicNames, StringBuffer objectNames) {
        String t=null;
        //ALLTEXT, all of the 'full text'
        StringBuffer allTextValue = new StringBuffer();

        //collecting data property statements
        List<DataPropertyStatement> dataPropertyStatements = ind.getDataPropertyStatements();
        if (dataPropertyStatements != null) {
            Iterator<DataPropertyStatement> dataPropertyStmtIter = dataPropertyStatements.iterator();
            while (dataPropertyStmtIter.hasNext()) {
                DataPropertyStatement dataPropertyStmt =  dataPropertyStmtIter.next();
                if(dataPropertyStmt.getDatapropURI().equals(label)){ // we don't want label to be added to alltext
                    continue;
                } else if(dataPropertyStmt.getDatapropURI().equals("http://vivoweb.org/ontology/core#preferredTitle")){
                	//add the preferredTitle field
                	String preferredTitle = null;
                	doc.addField(term.PREFERRED_TITLE, ((preferredTitle=dataPropertyStmt.getData()) == null)?"":preferredTitle);
                	log.debug("Preferred Title: " + dataPropertyStmt.getData());
                }
                allTextValue.append(" ");
                allTextValue.append(((t=dataPropertyStmt.getData()) == null)?"":t);
            }
        }
         
        allTextValue.append(objectNames.toString());
        
        allTextValue.append(' ');                      
        allTextValue.append(classPublicNames);
        
        try {
            String stripped = Jsoup.parse(allTextValue.toString()).text();
            allTextValue.setLength(0);
            allTextValue.append(stripped);
        } catch(Exception e) {
            log.debug("Could not strip HTML during search indexing. " + e);
        }
                
        String alltext = allTextValue.toString();
        
        doc.addField(term.ALLTEXT, alltext);
        doc.addField(term.ALLTEXTUNSTEMMED, alltext);
        doc.addField(term.ALLTEXT_PHONETIC, alltext);
    }





    /**
     * Get the rdfs:labes for objects of statements and put in objectNames.
     *  Get the URIs for objects of statements and put in addUri.
     */
    protected void addObjectPropertyText(Individual ind, SolrInputDocument doc,
            StringBuffer objectNames, StringBuffer addUri) {
        List<ObjectPropertyStatement> objectPropertyStatements = ind.getObjectPropertyStatements();
        if (objectPropertyStatements != null) {
            Iterator<ObjectPropertyStatement> objectPropertyStmtIter = objectPropertyStatements.iterator();
            while (objectPropertyStmtIter.hasNext()) {
                ObjectPropertyStatement objectPropertyStmt = objectPropertyStmtIter.next();
                if( "http://www.w3.org/2002/07/owl#differentFrom".equals(objectPropertyStmt.getPropertyURI()) ){
                    continue;
                }
                try {
                    objectNames.append(" ");
                    String t=null;
                    objectNames.append(((t=objectPropertyStmt.getObject().getRdfsLabel()) == null)?"":t);   
                    addUri.append(" ");
                    addUri.append(((t=objectPropertyStmt.getObject().getURI()) == null)?"":t);
                } catch (Exception e) { 
                     log.debug("could not index name of related object: " + e.getMessage());
                }
            }
        }        
    }

    /**
     * Adds the info about the classes that the individual is a member
     * of, classgroups and checks if prohibited.
     * @param classPublicNames 
     * @returns true if prohibited from search
     * @throws SkipIndividualException 
     */
    protected void addClasses(Individual ind, SolrInputDocument doc, StringBuffer classPublicNames) throws SkipIndividualException{
        ArrayList<String> superClassNames = null;        
        
        List<VClass> vclasses = ind.getVClasses(false);
        if( vclasses == null || vclasses.isEmpty() ){
            throw new SkipIndividualException("Not indexing because individual has no classes");
        }        
                
        for(VClass clz : vclasses){
            if(clz.getURI() == null){
                continue;
            }else if(OWL.Thing.getURI().equals(clz.getURI())){
                //don't add owl:Thing as the type in the index
                continue;
            } else {                                
                if( clz.getSearchBoost() != null){                	
                    doc.setDocumentBoost(doc.getDocumentBoost() + clz.getSearchBoost());
                }
                
                doc.addField(term.RDFTYPE, clz.getURI());
                
                if(clz.getLocalName() != null){
                    doc.addField(term.CLASSLOCALNAME, clz.getLocalName());
                    doc.addField(term.CLASSLOCALNAMELOWERCASE, clz.getLocalName().toLowerCase());
                }
                
                if(clz.getName() != null){
                    classPublicNames.append(" ");
                    classPublicNames.append(clz.getName());
                }
                
                //Add the Classgroup URI to a field
                if(clz.getGroupURI() != null){
                    doc.addField(term.CLASSGROUP_URI,clz.getGroupURI());
                }               
            }
        }                                                
    }
        
    protected void addLabel(Individual ind, SolrInputDocument doc) {
        String value = "";
        String label = ind.getRdfsLabel();
        if (label != null) {
            value = label;
        } else {
            value = ind.getLocalName();
        }            

        doc.addField(term.NAME_RAW, value);
        doc.addField(term.NAME_LOWERCASE_SINGLE_VALUED,value);
        
        // NAME_RAW will be copied by solr into the following fields:
        // NAME_LOWERCASE, NAME_UNSTEMMED, NAME_STEMMED, NAME_PHONETIC, AC_NAME_UNTOKENIZED, AC_NAME_STEMMED
    }
    
    public Object getIndexId(Object obj) {
        throw new Error("IndiviudalToSolrDocument.getIndexId() is unimplemented");        
    }
    
    public String getIdForUri(String uri){
        if( uri != null ){
            return  "vitroIndividual:" + uri;
        }else{
            return null;
        }
    }
    
    public String getQueryForId(String uri ){
        return term.DOCID + ':' + getIdForUri(uri);
    }
    
    public Individual unTranslate(Object result) {
        Individual ent = null;

        if( result != null && result instanceof SolrDocument){
            SolrDocument hit = (SolrDocument) result;
            String uri= (String) hit.getFirstValue(term.URI);

            ent = new IndividualImpl();
            ent.setURI(uri);
        }
        return ent;
    }
    
    public void shutdown(){
        for(DocumentModifier dm: documentModifiers){
            try{
                dm.shutdown();
            }catch(Exception e){
                if( log != null)
                    log.debug(e,e);
            }
        }
    }

    protected static final String DONT_EXCLUDE =null;
}
