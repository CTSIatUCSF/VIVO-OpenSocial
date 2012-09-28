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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import javax.servlet.ServletContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
//Returns the appropriate n3 based on data getter
public  class ProcessClassGroupDataGetterN3 extends ProcessDataGetterAbstract {
	private static String classType = "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData";
	public static  String classGroupVarBase = "classGroup";
	private Log log = LogFactory.getLog(ProcessClassGroupDataGetterN3.class);

	public ProcessClassGroupDataGetterN3(){
		
	}
	//Pass in variable that represents the counter 

	//TODO: ensure correct model returned
	//We shouldn't use the ACTUAL values here but generate the n3 required
    public List<String> retrieveN3Required(int counter) {
    	return this.retrieveN3ForTypeAndClassGroup(counter);
    	
    }
    public List<String> retrieveN3Optional(int counter) {
    	return null;
    }
    
    public List<String> retrieveN3ForTypeAndClassGroup(int counter) {
    	String n3ForType = this.getN3ForTypePartial(counter);
    	String n3 = n3ForType +"; \n" + 
    		"<" + DisplayVocabulary.FOR_CLASSGROUP + "> " + getN3VarName(classGroupVarBase, counter) + " .";
    	List<String> n3List = new ArrayList<String>();
    	n3List.add(getPrefixes() + n3);
    	return n3List;
    }
    
    public String getN3ForTypePartial(int counter) {
    	//UPDATE: including class type as 
    	String dataGetterVar = getDataGetterVar(counter);
    	String classTypeVar = getN3VarName(classTypeVarBase, counter);
    	//String n3 = dataGetterVar + " a <" + getClassType() + ">";
    	String n3 = dataGetterVar + " a " + classTypeVar;

    	return n3;
    }
    
    //These methods will return the literals and uris expected within the n3
    //and the counter is used to ensure they are numbered correctly 
    
    public List<String> retrieveLiteralsOnForm(int counter) {
    	//no literals, just the class group URI
    	List<String> literalsOnForm = new ArrayList<String>();
    	return literalsOnForm;
    	
    }
    
     
    public List<String> retrieveUrisOnForm(int counter) {
    	List<String> urisOnForm = new ArrayList<String>();
    	//Class group is a URI
    	urisOnForm.add(getVarName("classGroup", counter));
    	//UPDATE: adding class type as uri on form
    	urisOnForm.add(getVarName(classTypeVarBase, counter));
    	return urisOnForm;
    	
    }
    
   public List<FieldVTwo> retrieveFields(int counter) {
	   List<FieldVTwo> fields = new ArrayList<FieldVTwo>();
	   fields.add(new FieldVTwo().setName(getVarName("classGroup", counter)));
	   //UPDATE: adding class type to the uris on the form
	   fields.add(new FieldVTwo().setName(getVarName(classTypeVarBase, counter)));

	   return fields;
   }
   
   //These var names  match the names of the elements within the json object returned with the info required for the data getter
   
   public List<String> getLiteralVarNamesBase() {
	   return Arrays.asList();   
   }

   //these are for the fields ON the form
   public List<String> getUriVarNamesBase() {
	   //UPDATE: adding class type as uri
	   return Arrays.asList("classGroup", classTypeVarBase);   
   }

   //This class can be extended so returning type here
   public String getClassType() {
	   return classType;
   }
   
   //for existing values
   //TODO: Update
   public void populateExistingValues(String dataGetterURI, int counter, OntModel queryModel) {
	   //First, put dataGetterURI within scope as well
	   this.populateExistingDataGetterURI(dataGetterURI, counter);
	   //Put in type
	   this.populateExistingClassType(this.getClassType(), counter);
	   //Sparql queries for values to be executed
	   //And then placed in the correct place/literal or uri
	   String querystr = getExistingValuesClassGroup(dataGetterURI);
	   QueryExecution qe = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   Resource classGroupResource = qs.getResource("classGroup");
        	   //Put both literals in existing literals
        	   existingUriValues.put(this.getVarName(classGroupVarBase, counter),
        			   new ArrayList<String>(Arrays.asList(classGroupResource.getURI())));
           }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }
	   
	   
   }
  
   
   //?dataGetter a FixedHTMLDataGetter ; display:saveToVar ?saveToVar; display:htmlValue ?htmlValue .
   protected String getExistingValuesClassGroup(String dataGetterURI) {
	   String query = this.getSparqlPrefix() + " SELECT ?classGroup  WHERE {" + 
			   "<" + dataGetterURI + "> <" + DisplayVocabulary.FOR_CLASSGROUP + "> ?classGroup  . \n" +
			   "}";
	   return query;
   }
   
   public JSONObject getExistingValuesJSON(String dataGetterURI, OntModel queryModel, ServletContext context) {
	   JSONObject jObject = new JSONObject();
	   jObject.element("dataGetterClass", classType);
	   jObject.element(classTypeVarBase, classType);
	   //Get class group
	   getExistingClassGroup(dataGetterURI, jObject, queryModel);
	   //Get classes within class group
	   getExistingClassesInClassGroup(context, dataGetterURI, jObject);
	   return jObject;
   }
   
   private void getExistingClassGroup(String dataGetterURI, JSONObject jObject, OntModel queryModel) {
	   String querystr = getExistingValuesClassGroup(dataGetterURI);
	   QueryExecution qe = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   Resource classGroupResource = qs.getResource("classGroup");
        	   //Put both literals in existing literals
        	   jObject.element(classGroupVarBase, classGroupResource.getURI());
           }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }
       
  
   }
   
   //Assumes JSON Object received will have the class group resource URI within it
   //TODO: Refactor to take advantage of existing code that uses OTHER JSON library
   protected void getExistingClassesInClassGroup(ServletContext context, String dataGetterURI, JSONObject jObject) {
	   //Check for class group resource within json object
	   if(jObject.containsKey(classGroupVarBase)) {
		   String classGroupURI = jObject.getString(classGroupVarBase);
		   //Get classes for classGroupURI and include in 
		   VClassGroupCache vcgc = VClassGroupCache.getVClassGroupCache(context);
		   VClassGroup group = vcgc.getGroup(classGroupURI);
		   populateClassesInClassGroupJSON(jObject, group);
	   } else {
		   log.error("JSONObject does not have class group URI included. ");
	   }
   }
   
   //JSONObject will include results JSON object that will include classes JSON Arrya as well as
   //class group information
   protected void populateClassesInClassGroupJSON(JSONObject jObject, VClassGroup group) {
	   JSONArray classes = new JSONArray();
       for( VClass vc : group){
           JSONObject vcObj = new JSONObject();
           vcObj.element("name", vc.getName());
           vcObj.element("URI", vc.getURI());
           classes.add(vcObj);
       }
       JSONObject results = new JSONObject();
      
       results.element("classes", classes);                
       results.element("classGroupName", group.getPublicName());
       results.element("classGroupUri", group.getURI());
       jObject.element("results", results);
   }
}


