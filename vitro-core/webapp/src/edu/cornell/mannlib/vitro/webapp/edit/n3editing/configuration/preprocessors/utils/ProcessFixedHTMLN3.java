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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

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

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
//Returns the appropriate n3 based on data getter
public  class ProcessFixedHTMLN3 extends ProcessDataGetterAbstract {
	private static String classType = "java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.FixedHTMLDataGetter";
	private Log log = LogFactory.getLog(ProcessFixedHTMLN3.class);

	public ProcessFixedHTMLN3(){
		
	}
	//Pass in variable that represents the counter 

	//TODO: ensure correct model returned
	//We shouldn't use the ACTUAL values here but generate the n3 required
	//?dataGetter a FixedHTMLDataGetter ; display:saveToVar ?saveToVar; display:htmlValue ?htmlValue .
    public List<String> retrieveN3Required(int counter) {
    	String dataGetterVar = getDataGetterVar(counter);
    	//UPDATE: Using variable for class type
    	String classTypeVar = getN3VarName(classTypeVarBase, counter);
    	String n3 = dataGetterVar + " a " + classTypeVar + "; \n" + 
    	"display:saveToVar " + getN3VarName("saveToVar", counter) + "; \n" + 
    	"display:htmlValue " + getN3VarName("htmlValue", counter) + " .";
    	List<String> requiredList = new ArrayList<String>();
    	requiredList.add(getPrefixes() + n3);
    	return requiredList;
    	
    }
    public List<String> retrieveN3Optional(int counter) {
    	return null;
    }
  
    
    public List<String> retrieveLiteralsOnForm(int counter) {
    	List<String> literalsOnForm = new ArrayList<String>();
    	literalsOnForm.add(getVarName("saveToVar",counter));
    	literalsOnForm.add(getVarName("htmlValue", counter));
    	return literalsOnForm;
    	
    }
    
     
    public List<String> retrieveUrisOnForm(int counter) {
    	List<String> urisOnForm = new ArrayList<String>();
    	//UPDATE: adding class type as uri on form
    	urisOnForm.add(getVarName(classTypeVarBase, counter));
    	return urisOnForm;
    	
    }
    
   public List<FieldVTwo> retrieveFields(int counter) {
	   List<FieldVTwo> fields = new ArrayList<FieldVTwo>();
	  
	   //fields.add(new FieldVTwo().setName(getVarName("dataGetter", counter)));
	   fields.add(new FieldVTwo().setName(getVarName("saveToVar", counter)));
	   fields.add(new FieldVTwo().setName(getVarName("htmlValue", counter)));
	   //UPDATE: adding class type to the uris on the form
	   fields.add(new FieldVTwo().setName(getVarName(classTypeVarBase, counter)));
	   return fields;
   }
   
   public List<String> getLiteralVarNamesBase() {
	   return Arrays.asList("saveToVar", "htmlValue");   
   }

   //these are for the fields ON the form
   public List<String> getUriVarNamesBase() {
	   return Arrays.asList(classTypeVarBase);   
   }

   //For Existing Values in case of editing
  
   //Execute populate before retrieval
   public void populateExistingValues(String dataGetterURI, int counter, OntModel queryModel) {
	   //First, put dataGetterURI within scope as well
	   this.populateExistingDataGetterURI(dataGetterURI, counter);
	 //Put in type
	   this.populateExistingClassType(this.getClassType(), counter);
	//Sparql queries for values to be executed
	   //And then placed in the correct place/literal or uri
	   String querystr = getExistingValuesSparqlQuery(dataGetterURI);
	   QueryExecution qe = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   Literal saveToVarLiteral = qs.getLiteral("saveToVar");
        	   Literal htmlValueLiteral = qs.getLiteral("htmlValue");
        	   //Put both literals in existing literals
        	   existingLiteralValues.put(this.getVarName("saveToVar", counter),
        			   new ArrayList<Literal>(Arrays.asList(saveToVarLiteral)));
        	   existingLiteralValues.put(this.getVarName("htmlValue", counter),
        			   new ArrayList<Literal>(Arrays.asList(htmlValueLiteral)));
           }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }
	   
	   
   }
  
   
   //?dataGetter a FixedHTMLDataGetter ; display:saveToVar ?saveToVar; display:htmlValue ?htmlValue .
   protected String getExistingValuesSparqlQuery(String dataGetterURI) {
	   String query = this.getSparqlPrefix() + " SELECT ?saveToVar ?htmlValue WHERE {" + 
			   "<" + dataGetterURI + "> display:saveToVar ?saveToVar . \n" + 
			   "<" + dataGetterURI + "> display:htmlValue ?htmlValue . \n" + 
			   "}";
	   return query;
   }
   
   
   //Method to create a JSON object with existing values to return to form
   //There may be a better way to do this without having to run the query twice
   //TODO: Refactor code if required
   public JSONObject getExistingValuesJSON(String dataGetterURI, OntModel queryModel, ServletContext context) {
	   JSONObject jObject = new JSONObject();
	   jObject.element("dataGetterClass", classType);
	   jObject.element(classTypeVarBase, classType);
	   String querystr = getExistingValuesSparqlQuery(dataGetterURI);
	   QueryExecution qe = null;
       try{
           Query query = QueryFactory.create(querystr);
           qe = QueryExecutionFactory.create(query, queryModel);
           ResultSet results = qe.execSelect();
           while( results.hasNext()){
        	   QuerySolution qs = results.nextSolution();
        	   Literal saveToVarLiteral = qs.getLiteral("saveToVar");
        	   Literal htmlValueLiteral = qs.getLiteral("htmlValue");
        	   String htmlValueString = htmlValueLiteral.getString();
        	   htmlValueString = this.replaceQuotes(htmlValueString);
        	   jObject.element("saveToVar", saveToVarLiteral.getString());
        	   //TODO: Handle single and double quotes within string and escape properlyu
        	   jObject.element("htmlValue", htmlValueString);
           }
       } catch(Exception ex) {
    	   log.error("Exception occurred in retrieving existing values with query " + querystr, ex);
       }
       return jObject;
	   
   }
   
   //Escape single and double quotes for html string to be returned to form
   public String replaceQuotes(String inputStr) {
	   return inputStr.replaceAll("\'", "&#39;").replaceAll("\"", "&quot;");
	   
   }

   //This class can be extended so returning type here
   public String getClassType() {
	   return classType;
   }
}


