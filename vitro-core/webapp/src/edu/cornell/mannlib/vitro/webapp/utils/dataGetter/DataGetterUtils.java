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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

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
import com.hp.hpl.jena.vocabulary.OWL;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListController;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.IndividualListController.PageRecord;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.json.JsonServlet;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;


public class DataGetterUtils {
    
    final static Log log = LogFactory.getLog(DataGetterUtils.class);

    /**
     * Get a list of DataGetter objects that are associated with a page.
     * This should not return PageDataGetters and should not throw an 
     * exception if a page has PageDataGetters.  
     */
    public static List<DataGetter> getDataGettersForPage( VitroRequest vreq, Model displayModel, String pageURI) 
    throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException{
        //get data getter uris for pageURI
        List<String> dgUris = getDataGetterURIsForPageURI( displayModel, pageURI);
        
        List<DataGetter> dgList = new ArrayList<DataGetter>();
        for( String dgURI: dgUris){
            DataGetter dg =dataGetterForURI(vreq, displayModel, dgURI) ;
            if( dg != null )
                dgList.add(dg); 
        }
        log.debug("getDataGettersForPage: " + dgList);
        return dgList;
    }

    /**
     * Returns a DataGetter using information in the 
     * displayModel for the individual with the URI given by dataGetterURI
     * to configure it. 
     * 
     * May return null.
     * This should not throw an exception if the URI exists and has a type
     * that does not implement the DataGetter interface.
     */
    public static DataGetter dataGetterForURI(VitroRequest vreq, Model displayModel, String dataGetterURI) 
    throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException 
    {
        //get java class for dataGetterURI
        String dgClassName = getJClassForDataGetterURI(displayModel, dataGetterURI);
        
        //figure out if it implements interface DataGetter
        Class<?> clz = Class.forName(dgClassName);
        if( ! DataGetter.class.isAssignableFrom(clz) ){
    		log.debug("Class doesn't implement DataGetter: '" + dgClassName + "'");
            return null;
        }
        
        // we want a constructor that will work for one of these argument lists (in this order)
        Object[][] argLists = new Object[][] {
        		{ vreq, displayModel, dataGetterURI }, 
        		{ displayModel, dataGetterURI }, 
        		{ vreq }, 
        		{}
        	};
        
        // look through the available constructors for the best fit
        for (Object[] argList: argLists) {
        	for (Constructor<?> ct: clz.getConstructors()) {
        		if (isConstructorSuitableForArguments(ct, argList)) {
        			log.debug("Using this constructor: " + ct);
        			return (DataGetter) ct.newInstance(argList);
        		}
        	}
        }
        
		log.debug("Didn't find a suitable constructor for '" + dgClassName + "'");
        return null;
    }
    
    private static boolean isConstructorSuitableForArguments(Constructor<?> ct, Object[] args) {
		Class<?>[] parameterTypes = ct.getParameterTypes();
		if (args.length != parameterTypes.length) {
			return false;
		}
		for (int i = 0; i < args.length; i++) {
			Class<? extends Object> argClass = args[i].getClass();
			if (! parameterTypes[i].isAssignableFrom(argClass)) {
				return false;
			}
		}
		return true;
    }

    public static String getJClassForDataGetterURI(Model displayModel, String dataGetterURI) throws IllegalAccessException {
        String query = prefixes +
        "SELECT ?type WHERE { ?dgURI rdf:type ?type } ";
        Query dgTypeQuery = QueryFactory.create(query);
        
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("dgURI", ResourceFactory.createResource( dataGetterURI ));
        
        List<String> types = new ArrayList<String>();         
        displayModel.enterCriticalSection(false);
        try{
            QueryExecution qexec = QueryExecutionFactory.create(dgTypeQuery,displayModel,initialBindings );
            try{                                                    
                ResultSet results = qexec.execSelect();                
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    Resource type = soln.getResource("type");
                    if( type != null && type.getURI() != null){
                        types.add( DataGetterUtils.getClassNameFromUri( type.getURI() ));
                    }
                }
            }finally{ qexec.close(); }
        }finally{ displayModel.leaveCriticalSection(); }
        
        
        return chooseType( types, displayModel, dataGetterURI);
    }
    
    
    private static List<String> getDataGetterURIsForPageURI(Model displayModel, String pageURI) {
        String query = prefixes + 
             "SELECT ?dataGetter WHERE { ?pageURI display:hasDataGetter ?dataGetter }";
        Query dgForPageQuery = QueryFactory.create(query);
        
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("pageURI", ResourceFactory.createResource( pageURI ));
        
        List<String> dgURIs = new ArrayList<String>();
        displayModel.enterCriticalSection(false);
        try{
            QueryExecution qexec = QueryExecutionFactory.create(dgForPageQuery,displayModel,initialBindings );
            try{                                                    
                ResultSet results = qexec.execSelect();                
                while (results.hasNext()) {
                    QuerySolution soln = results.nextSolution();
                    Resource dg = soln.getResource("dataGetter");
                    if( dg != null && dg.getURI() != null){
                        dgURIs.add( dg.getURI());
                    }
                }
            }finally{ qexec.close(); }
        }finally{ displayModel.leaveCriticalSection(); }
                
        return dgURIs;
    }
    
    private static String chooseType(List<String> types, Model displayModel, String dataGetterURI) throws IllegalAccessException {
        //currently just get the first one that is not owl:Thing
        for(String type : types){
            if( ! StringUtils.isEmpty( type ) && !type.equals( OWL.Thing.getURI() ))
                return type;
        }
        throw new IllegalAccessException("No useful type defined for <" + dataGetterURI + ">");        
    }
    //Copied from PageDaoJena
    static protected String nodeToString( RDFNode node ){
        if( node == null ){
            return "";
        }else if( node.isLiteral() ){
            Literal literal = node.asLiteral();
            return literal.getLexicalForm();
        }else if( node.isURIResource() ){
            Resource resource = node.asResource();
            return resource.getURI();
        }else if( node.isAnon() ){  
            Resource resource = node.asResource();
            return resource.getId().getLabelString(); //get b-node id
        }else{
            return "";
        }
    }
    
    static final String prefixes = 
        "PREFIX rdf:   <" + VitroVocabulary.RDF +"> \n" +
        "PREFIX rdfs:  <" + VitroVocabulary.RDFS +"> \n" + 
        "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#> \n" +
        "PREFIX display: <" + DisplayVocabulary.DISPLAY_NS +"> \n";

    //This query is used in more than one place, so can be placed here
    //An alternative is to have individuals for classes data getter extend classgroupdatagetter
    //This currently assumes one class group uri per data getter, but this can be extended
    /**
     * For page data getter conversions
     */
    /**
     * Get Individual count for Solr query for intersection of multiple classes
     */
    public static long getIndividualCountForIntersection(VitroRequest vreq, ServletContext context, List<String> classUris) {
    	 return IndividualListController.getIndividualCount(classUris, vreq.getWebappDaoFactory().getIndividualDao(), context);
    }
    
    //Return data getter type to be employed in display model
    public static String generateDataGetterTypeURI(String dataGetterClassName) {
    	return "java:" + dataGetterClassName;
    }
    
    public static final String getClassGroupForDataGetter(Model displayModel, String dataGetterURI) {
    	String classGroupUri = null; 
    	QuerySolutionMap initBindings = new QuerySolutionMap();
         initBindings.add("dataGetterURI", ResourceFactory.createResource(dataGetterURI));
         
         int count = 0;
         //Get the class group
         Query dataGetterConfigurationQuery = QueryFactory.create(classGroupForDataGetterQuery) ;               
         displayModel.enterCriticalSection(Lock.READ);
         try{
             QueryExecution qexec = QueryExecutionFactory.create(
                     dataGetterConfigurationQuery, displayModel, initBindings) ;        
             ResultSet res = qexec.execSelect();
             try{                
                 while( res.hasNext() ){
                     count++;
                     QuerySolution soln = res.next();
                     
                      
                     
                     //model is OPTIONAL
                     RDFNode node = soln.getResource("classGroupUri");
                     if( node != null && node.isURIResource() ){
                         classGroupUri = node.asResource().getURI();                        
                     }else{
                         classGroupUri = null;
                     }
                       
                 }
             }finally{ qexec.close(); }
         }finally{ displayModel.leaveCriticalSection(); }
         return classGroupUri;
    }
    
    /**
     * Process results related to VClass or vclasses. Handles both single and multiple vclasses being sent.
     */
    public static JSONObject processVclassResultsJSON(Map<String, Object> map, VitroRequest vreq, boolean multipleVclasses) {
        JSONObject rObj = new JSONObject();
        VClass vclass=null;         
        
        try { 
              
            // Properties from ontologies used by VIVO - should not be in vitro
            DataProperty fNameDp = (new DataProperty());                         
            fNameDp.setURI("http://xmlns.com/foaf/0.1/firstName");
            DataProperty lNameDp = (new DataProperty());
            lNameDp.setURI("http://xmlns.com/foaf/0.1/lastName");
            DataProperty preferredTitleDp = (new DataProperty());
            preferredTitleDp.setURI("http://vivoweb.org/ontology/core#preferredTitle");
              
            if( log.isDebugEnabled() ){
                @SuppressWarnings("unchecked")
                Enumeration<String> e = vreq.getParameterNames();
                while(e.hasMoreElements()){
                    String name = (String)e.nextElement();
                    log.debug("parameter: " + name);
                    for( String value : vreq.getParameterValues(name) ){
                        log.debug("value for " + name + ": '" + value + "'");
                    }            
                }
            }
              
            //need an unfiltered dao to get firstnames and lastnames
            WebappDaoFactory fullWdf = vreq.getFullWebappDaoFactory();
                      
            String[] vitroClassIdStr = vreq.getParameterValues("vclassId");                            
            if ( vitroClassIdStr != null && vitroClassIdStr.length > 0){    
                for(String vclassId: vitroClassIdStr) {
                    vclass = vreq.getWebappDaoFactory().getVClassDao().getVClassByURI(vclassId);
                    if (vclass == null) {
                        log.error("Couldn't retrieve vclass ");   
                        throw new Exception ("Class " + vclassId + " not found");
                    }  
                  }
            }else{
                log.error("parameter vclassId URI parameter expected ");
                throw new Exception("parameter vclassId URI parameter expected ");
            }
            List<String> vclassIds = Arrays.asList(vitroClassIdStr);                           
            //if single vclass expected, then include vclass. This relates to what the expected behavior is, not size of list 
            if(!multipleVclasses) {
                //currently used for ClassGroupPage
                rObj.put("vclass", 
                          new JSONObject().put("URI",vclass.getURI())
                                  .put("name",vclass.getName()));
            } else {
                //For now, utilize very last VClass (assume that that is the one to be employed)
                //TODO: Find more general way of dealing with this
                //put multiple ones in?
                if(vclassIds.size() > 0) {
                	int numberVClasses = vclassIds.size();
                    vclass = vreq.getWebappDaoFactory().getVClassDao().getVClassByURI(vclassIds.get(numberVClasses - 1));
                    rObj.put("vclass", new JSONObject().put("URI",vclass.getURI())
                              .put("name",vclass.getName()));
                } 
                // rObj.put("vclasses",  new JSONObject().put("URIs",vitroClassIdStr)
                //                .put("name",vclass.getName()));
            }
            if (vclass != null) {                                    
                  
                rObj.put("totalCount", map.get("totalCount"));
                rObj.put("alpha", map.get("alpha"));
                                  
                List<Individual> inds = (List<Individual>)map.get("entities");
                log.debug("Number of individuals returned from request: " + inds.size());
                JSONArray jInds = new JSONArray();
                for(Individual ind : inds ){
                    JSONObject jo = new JSONObject();
                    jo.put("URI", ind.getURI());
                    jo.put("label",ind.getRdfsLabel());
                    jo.put("name",ind.getName());
                    jo.put("thumbUrl", ind.getThumbUrl());
                    jo.put("imageUrl", ind.getImageUrl());
                    jo.put("profileUrl", UrlBuilder.getIndividualProfileUrl(ind, vreq));
                      
                    jo.put("mostSpecificTypes", JsonServlet.getMostSpecificTypes(ind,fullWdf));                                          
                    jo.put("preferredTitle", JsonServlet.getDataPropertyValue(ind, preferredTitleDp, fullWdf));                    
                      
                    jInds.put(jo);
                }
                rObj.put("individuals", jInds);
                  
                JSONArray wpages = new JSONArray();
                //Made sure that PageRecord here is SolrIndividualListController not IndividualListController
                List<PageRecord> pages = (List<PageRecord>)map.get("pages");                
                for( PageRecord pr: pages ){                    
                    JSONObject p = new JSONObject();
                    p.put("text", pr.text);
                    p.put("param", pr.param);
                    p.put("index", pr.index);
                    wpages.put( p );
                }
                rObj.put("pages",wpages);    
                  
                JSONArray jletters = new JSONArray();
                List<String> letters = Controllers.getLetters();
                for( String s : letters){
                    JSONObject jo = new JSONObject();
                    jo.put("text", s);
                    jo.put("param", "alpha=" + URLEncoder.encode(s, "UTF-8"));
                    jletters.put( jo );
                }
                rObj.put("letters", jletters);
            }            
        } catch(Exception ex) {
             log.error("Error occurred in processing JSON object", ex);
        }
        return rObj;
    }
    
    private static final String forClassGroupURI = "<" + DisplayVocabulary.FOR_CLASSGROUP + ">";

    private static final String classGroupForDataGetterQuery =
        "PREFIX display: <" + DisplayVocabulary.DISPLAY_NS +"> \n" +
        "SELECT ?classGroupUri WHERE { \n" +
        "  ?dataGetterURI "+forClassGroupURI+" ?classGroupUri . \n" +
        "}";      
    
    
    /**
     * 
     * Convert data to JSON for page uri based on type and related datagetters
     * TODO: How to handle different data getters?  Will this replace json fields or add to them?
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public static JSONObject covertDataToJSONForPage(VitroRequest vreq, String pageUri, Model displayModel) throws InstantiationException, IllegalAccessException, ClassNotFoundException {       
        //Get PageDataGetter types associated with pageUri
        JSONObject rObj = null;   
        try{
	        List<DataGetter> dataGetters = getDataGettersForPage(vreq, displayModel, pageUri);
	        for(DataGetter getter: dataGetters) {
	        	 JSONObject typeObj = null;
	             try{
	            	 //Assumes the data getter itself will have a convert to json method
	            	 /*
	                 typeObj = getter.convertToJSON(data, vreq);
	                 if( typeObj != null) {
	                     //Copy over everything from this type Obj to 
	                     //TODO: Review how to handle duplicate keys, etc.
	                     if(rObj != null) {
	                         //For now, just nests as separate entry
	                         rObj.put(getter.getType(), typeObj);
	                     } else {
	                         rObj = typeObj;
	                     }
	                 } */     
	        	
	            } catch(Throwable th){
	                log.error(th,th);
	            }
	        }     
        } catch(Throwable th) {
        	log.error(th, th);
        }
        return rObj;
    }
    
    
    /***
     * For the page, get the actual Data Getters to be employed.
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    /*
    public static List<PageDataGetter> DataGetterObjects(VitroRequest vreq, String pageUri) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    	List<PageDataGetter> dataGetterObjects = new ArrayList<PageDataGetter>();
    	
    	List<String> dataGetterClassNames = vreq.getWebappDaoFactory().getPageDao().getDataGetterClass(pageUri);
    	if( dataGetterClassNames == null )
    	    return Collections.emptyList();
    	
    	for(String dgClassName: dataGetterClassNames) {
    		String className = getClassNameFromUri(dgClassName);
    		Class clz =  Class.forName(className);
    		
    		if( DataGetterUtils.isInstanceOfInterface(clz, PageDataGetter.class)){    		        		
    		    Object obj = clz.newInstance();
    		    if(obj != null && obj instanceof PageDataGetter) {
    		        PageDataGetter pg = (PageDataGetter) obj;
    		        dataGetterObjects.add(pg);
    		    }	    		
    		}// else skip if class does not implement PageDataGetter
    	} 
	        
    	return dataGetterObjects;
    }
    */
    
    //Class URIs returned include "java:" and to instantiate object need to remove java: portion
    public static String getClassNameFromUri(String dataGetterClassUri) {
    	if( !StringUtils.isEmpty(dataGetterClassUri) && dataGetterClassUri.contains("java:")) {
    		String[] splitArray = dataGetterClassUri.split("java:");
    		if(splitArray.length > 1) {
    			return splitArray[1];
    		}
    	}
    	return dataGetterClassUri;
    }
    

    
    /*
     * Copied from JSONServlet as expect this to be related to VitroClassGroup
     */
    public static JSONObject processVClassGroupJSON(VitroRequest vreq, ServletContext context, VClassGroup vcg) {
        JSONObject map = new JSONObject();           
        try {
            ArrayList<JSONObject> classes = new ArrayList<JSONObject>(vcg.size());
            for( VClass vc : vcg){
                JSONObject vcObj = new JSONObject();
                vcObj.put("name", vc.getName());
                vcObj.put("URI", vc.getURI());
                vcObj.put("entityCount", vc.getEntityCount());
                classes.add(vcObj);
            }
            map.put("classes", classes);                
            map.put("classGroupName", vcg.getPublicName());
            map.put("classGroupUri", vcg.getURI());
        
        } catch(Exception ex) {
            log.error("Error occurred in processing VClass group ", ex);
        }
        return map;        
    }
    
	
    //Get All VClass Groups information
    //Used within menu management and processing
    //TODO: Check if more appropriate location possible
    public static List<HashMap<String, String>> getClassGroups(ServletContext context) {
    	//Wanted this to be 
    	VClassGroupCache vcgc = VClassGroupCache.getVClassGroupCache(context);
        List<VClassGroup> vcgList = vcgc.getGroups();
        //For now encoding as hashmap with label and URI as trying to retrieve class group
        //results in errors for some reason
        List<HashMap<String, String>> classGroups = new ArrayList<HashMap<String, String>>();
        for(VClassGroup vcg: vcgList) {
        	HashMap<String, String> hs = new HashMap<String, String>();
        	hs.put("publicName", vcg.getPublicName());
        	hs.put("URI", vcg.getURI());
        	classGroups.add(hs);
        }
        return classGroups;
    }
    
    
   //TODO: Check whether this needs to be put here or elsewhere, as this is data getter specific
    //with respect to class groups
  //Need to use VClassGroupCache to retrieve class group information - this is the information returned from "for class group"
	public static void getClassGroupForDataGetter(ServletContext context, Map<String, Object> pageData, Map<String, Object> templateData) {
    	//Get the class group from VClassGroup, this is the same as the class group for the class group page data getter
		//and the associated class group (not custom) for individuals datagetter
		String classGroupUri = (String) pageData.get("classGroupUri");
		VClassGroupCache vcgc = VClassGroupCache.getVClassGroupCache(context);
    	VClassGroup group = vcgc.getGroup(classGroupUri);

		templateData.put("classGroup", group);
		templateData.put("associatedPage", group.getPublicName());
		templateData.put("associatedPageURI", group.getURI());
    }
    
	
	
    
}
