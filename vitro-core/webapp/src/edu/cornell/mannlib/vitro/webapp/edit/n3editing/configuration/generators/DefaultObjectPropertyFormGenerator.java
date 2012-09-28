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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import static edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.IndividualsViaObjectPropetyOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;
import edu.cornell.mannlib.vitro.webapp.search.beans.ProhibitedFromSearch;
import edu.cornell.mannlib.vitro.webapp.search.solr.SolrSetup;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;

/**
 * Generates the edit configuration for a default property form.
 * This handles the default object property auto complete.
 * 
 * If a default property form is request and the number of indivdiuals
 * found in the range is too large, the the auto complete setup and
 * template will be used instead.
 */
public class DefaultObjectPropertyFormGenerator implements EditConfigurationGenerator {

	private Log log = LogFactory.getLog(DefaultObjectPropertyFormGenerator.class);	
	private String subjectUri = null;
	private String predicateUri = null;
	private String objectUri = null;	
		
	private String objectPropertyTemplate = "defaultPropertyForm.ftl";
	private String acObjectPropertyTemplate = "autoCompleteObjectPropForm.ftl";		
	
	protected boolean doAutoComplete = false;
	protected boolean tooManyRangeIndividuals = false;
	
	protected long maxNonACRangeIndividualCount = 300;
	
	private static HashMap<String,String> defaultsForXSDtypes ;
	  static {
		defaultsForXSDtypes = new HashMap<String,String>();
		//defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","2001-01-01T12:00:00");
		defaultsForXSDtypes.put("http://www.w3.org/2001/XMLSchema#dateTime","#Unparseable datetime defaults to now");
	  }
	  
	  
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {
    	if(!EditConfigurationUtils.isObjectProperty(EditConfigurationUtils.getPredicateUri(vreq), vreq)) {    	    	
    	    throw new Exception("DefaultObjectPropertyFormGenerator does not handle data properties.");
    	}
    	
     	if( tooManyRangeOptions( vreq, session ) ){
    		tooManyRangeIndividuals = true;
    		doAutoComplete = true;
    	}
     	
    	//Check if create new and return specific edit configuration from that generator.
    	if(DefaultAddMissingIndividualFormGenerator.isCreateNewIndividual(vreq, session)) {
    		DefaultAddMissingIndividualFormGenerator generator = new DefaultAddMissingIndividualFormGenerator();
    		return generator.getEditConfiguration(vreq, session);
    	}
    	    	    	
    	//TODO: Add a generator for delete: based on command being delete - propDelete.jsp
        //Generate a edit configuration for the default object property form and return it.
    	//if(DefaultDeleteGenerator.isDelete( vreq,session)){
    	//  return (new DefaultDeleteGenerator()).getEditConfiguration(vreq,session);
    	
    	return getDefaultObjectEditConfiguration(vreq, session);
    }
	
    protected List<String> getRangeTypes(VitroRequest vreq) {
		Individual subject = EditConfigurationUtils.getSubjectIndividual(vreq);
		String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
		WebappDaoFactory wDaoFact = vreq.getWebappDaoFactory();
		List<String> types = new ArrayList<String>();
		List <VClass> vclasses = new ArrayList<VClass>();
        vclasses = wDaoFact.getVClassDao().getVClassesForProperty(subject.getVClassURI(),predicateUri);
        for(VClass v: vclasses) {
        	types.add(v.getURI());
        }       
        return types;
	}	
	
    private boolean tooManyRangeOptions(VitroRequest vreq, HttpSession session ) throws SolrServerException {
    	List<String> types = getRangeTypes(vreq);
    	SolrServer solrServer = SolrSetup.getSolrServer(session.getServletContext());
    	
    	//empty list means the range is not set to anything, force Thing
    	if(types.size() == 0 ){
    		types = new ArrayList<String>();
    		types.add(VitroVocabulary.OWL_THING);
    	}
    	
    	long count = 0;    		   
    	for( String type:types){
    		//solr query for type count.    		
    		SolrQuery query = new SolrQuery();
    		if( VitroVocabulary.OWL_THING.equals( type )){
    			query.setQuery( "*:*" );    			
    		}else{
    			query.setQuery( VitroSearchTermNames.RDFTYPE + ":" + type);
    		}
    		query.setRows(0);
    		
    		QueryResponse rsp = solrServer.query(query);
    		SolrDocumentList docs = rsp.getResults();
    		long found = docs.getNumFound();    
    		count = count + found;
    		if( count > maxNonACRangeIndividualCount )
    			break;
    	}
    	    	
    	return  count > maxNonACRangeIndividualCount ;    	    	   
	}

    
	private EditConfigurationVTwo getDefaultObjectEditConfiguration(VitroRequest vreq, HttpSession session) throws Exception {
    	EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();    	
    	
    	//process subject, predicate, object parameters
    	this.initProcessParameters(vreq, session, editConfiguration);
    	
      	//Assumes this is a simple case of subject predicate var
    	editConfiguration.setN3Required(this.generateN3Required(vreq));
    	    	
    	//n3 optional
    	editConfiguration.setN3Optional(this.generateN3Optional());
    	
    	//Todo: what do new resources depend on here?
    	//In original form, these variables start off empty
    	editConfiguration.setNewResources(new HashMap<String, String>());
    	//In scope
    	this.setUrisAndLiteralsInScope(editConfiguration);
    	
    	//on Form
    	this.setUrisAndLiteralsOnForm(editConfiguration, vreq);
    	
    	editConfiguration.setFilesOnForm(new ArrayList<String>());
    	
    	//Sparql queries
    	this.setSparqlQueries(editConfiguration);
    	
    	//set fields
    	setFields(editConfiguration, vreq, EditConfigurationUtils.getPredicateUri(vreq));
    	
    //	No need to put in session here b/c put in session within edit request dispatch controller instead
    	//placing in session depends on having edit key which is handled in edit request dispatch controller
    //	editConfiguration.putConfigInSession(editConfiguration, session);

    	prepareForUpdate(vreq, session, editConfiguration);
    	
    	//After the main processing is done, check if select from existing process
    	processProhibitedFromSearch(vreq, session, editConfiguration);
    	
    	//Form title and submit label moved to template
    	setTemplate(editConfiguration, vreq);
    	
    	editConfiguration.addValidator(new AntiXssValidation());
    	
    	//Set edit key
    	setEditKey(editConfiguration, vreq);
    	    	       	    	
    	//Adding additional data, specifically edit mode
    	if( doAutoComplete ){
    		addFormSpecificDataForAC(editConfiguration, vreq, session);
    	}else{	    
	        addFormSpecificData(editConfiguration, vreq);
    	}      
    	
    	return editConfiguration;
    }
    
    private void setEditKey(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	String editKey = EditConfigurationUtils.getEditKey(vreq);	
    	editConfiguration.setEditKey(editKey);
    }
    
	private void setTemplate(EditConfigurationVTwo editConfiguration,
			VitroRequest vreq) {
		if( doAutoComplete )
			editConfiguration.setTemplate(acObjectPropertyTemplate);
		else
			editConfiguration.setTemplate(objectPropertyTemplate);
		
	}

	//Initialize setup: process parameters
    private void initProcessParameters(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	String formUrl = EditConfigurationUtils.getFormUrlWithoutContext(vreq);

    	subjectUri = EditConfigurationUtils.getSubjectUri(vreq);
    	predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
    
    	editConfiguration.setFormUrl(formUrl);
    	
    	editConfiguration.setUrlPatternToReturnTo("/individual");
    	
    	editConfiguration.setVarNameForSubject("subject");
    	editConfiguration.setSubjectUri(subjectUri);
    	editConfiguration.setEntityToReturnTo(subjectUri);
    	editConfiguration.setVarNameForPredicate("predicate");
    	editConfiguration.setPredicateUri(predicateUri);
    	
    	
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//"object"       : [ "objectVar" ,  "${objectUriJson}" , "URI"],
    	if(EditConfigurationUtils.isObjectProperty(predicateUri, vreq)) {
    		log.debug("This is an object property: " + predicateUri);
    		this.initObjectParameters(vreq);
    		this.processObjectPropForm(vreq, editConfiguration);
    	} else {
    		log.debug("This is a data property: " + predicateUri);
    		return;
    	}
    }    

    
	private void initObjectParameters(VitroRequest vreq) {
		//in case of object property
    	objectUri = EditConfigurationUtils.getObjectUri(vreq);
	}

	private void processObjectPropForm(VitroRequest vreq, EditConfigurationVTwo editConfiguration) {
    	editConfiguration.setVarNameForObject("objectVar");    	
    	editConfiguration.setObject(objectUri);
    	//this needs to be set for the editing to be triggered properly, otherwise the 'prepare' method
    	//pretends this is a data property editing statement and throws an error
    	//TODO: Check if null in case no object uri exists but this is still an object property
    }
       
    //Get N3 required 
    //Handles both object and data property    
    private List<String> generateN3Required(VitroRequest vreq) {
    	List<String> n3ForEdit = new ArrayList<String>();
    	String editString = "?subject ?predicate ";    	
    	editString += "?objectVar";    	
    	editString += " .";
    	n3ForEdit.add(editString);
    	return n3ForEdit;
    }
    
    private List<String> generateN3Optional() {
    	List<String> n3Inverse = new ArrayList<String>();    	
    	n3Inverse.add("?objectVar ?inverseProp ?subject .");    	
    	return n3Inverse;
    	
    }
    
    //Set queries
    private String retrieveQueryForInverse () {
    	String queryForInverse =  "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
			+ " SELECT ?inverse_property "
			+ "    WHERE { ?inverse_property owl:inverseOf ?predicate } ";
    	return queryForInverse;
    }
    
    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration) {
    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
    	//note that at this point the subject, predicate, and object var parameters have already been processed
    	urisInScope.put(editConfiguration.getVarNameForSubject(), 
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(), 
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	//this shoudl happen in edit configuration prepare for object prop update
    	//urisInScope.put(editConfiguration.getVarNameForObject(), 
    	//		Arrays.asList(new String[]{editConfiguration.getObject()}));
    	//inverse property uris should be included in sparql for additional uris in edit configuration
    	editConfiguration.setUrisInScope(urisInScope);
    	//Uris in scope include subject, predicate, and object var
    	
    	editConfiguration.setLiteralsInScope(new HashMap<String, List<Literal>>());
    }
    
    //n3 should look as follows
    //?subject ?predicate ?objectVar 
    
    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();
    	List<String> literalsOnForm = new ArrayList<String>();
    	
    	//uris on form should be empty if data property
    	urisOnForm.add("objectVar");
    	
    	editConfiguration.setUrisOnform(urisOnForm);
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }
        
    //This is for various items
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration) {
    	//Sparql queries defining retrieval of literals etc.
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    	
    	Map<String, String> urisInScope = new HashMap<String, String>();
    	urisInScope.put("inverseProp", this.retrieveQueryForInverse());
    	editConfiguration.setSparqlForAdditionalUrisInScope(urisInScope);
    	
    	editConfiguration.setSparqlForExistingLiterals(generateSparqlForExistingLiterals());
    	editConfiguration.setSparqlForExistingUris(generateSparqlForExistingUris());
    }
    
    
    //Get page uri for object
    private HashMap<String, String> generateSparqlForExistingUris() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	return map;
    }
    
    private HashMap<String, String> generateSparqlForExistingLiterals() {
    	HashMap<String, String> map = new HashMap<String, String>();
    	return map;
    }
    
    private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) throws Exception {    	
		FieldVTwo field = new FieldVTwo();
    	field.setName("objectVar");    	
    	
    	List<String> validators = new ArrayList<String>();
    	validators.add("nonempty");
    	field.setValidators(validators);
    	    	
    	if( ! doAutoComplete ){
    		field.setOptions( new IndividualsViaObjectPropetyOptions(
    	        subjectUri, 
    	        predicateUri, 
    	        objectUri));
    	}else{
    		field.setOptions(null);
    	}
    	
    	Map<String, FieldVTwo> fields = new HashMap<String, FieldVTwo>();
    	fields.put(field.getName(), field);    	
    	    	    	
    	editConfiguration.setFields(fields);
    }       

	private void prepareForUpdate(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfiguration) {
    	//Here, retrieve model from 
    	Model model = (Model) session.getServletContext().getAttribute("jenaOntModel");
    	//if object property
    	if(EditConfigurationUtils.isObjectProperty(EditConfigurationUtils.getPredicateUri(vreq), vreq)){
	    	Individual objectIndividual = EditConfigurationUtils.getObjectIndividual(vreq);
	    	if(objectIndividual != null) {
	    		//update existing object
	    		editConfiguration.prepareForObjPropUpdate(model);
	    	}  else {
	    		//new object to be created
	            editConfiguration.prepareForNonUpdate( model );
	        }
    	} else {
    	    throw new Error("DefaultObjectPropertyForm does not handle data properties.");
    	}
    }
      
    private boolean isSelectFromExisting(VitroRequest vreq) {
    	String predicateUri = EditConfigurationUtils.getPredicateUri(vreq);
    	if(EditConfigurationUtils.isDataProperty(predicateUri, vreq)) {
    		return false;
    	}
    	ObjectProperty objProp = EditConfigurationUtils.getObjectPropertyForPredicate(vreq, EditConfigurationUtils.getPredicateUri(vreq));
    	return objProp.getSelectFromExisting();
    }
    
    //Additional processing, eg. select from existing
    //This is really process prohibited from search
    private void processProhibitedFromSearch(VitroRequest vreq, HttpSession session, EditConfigurationVTwo editConfig) {
    	if(isSelectFromExisting(vreq)) {
    		// set ProhibitedFromSearch object so picklist doesn't show
            // individuals from classes that should be hidden from list views
    		//TODO: Check how model is retrieved
            OntModel displayOntModel = 
               (OntModel) session.getServletContext()
                    .getAttribute(DISPLAY_ONT_MODEL);
            if (displayOntModel != null) {
                ProhibitedFromSearch pfs = new ProhibitedFromSearch(
                    DisplayVocabulary.SEARCH_INDEX_URI, displayOntModel);
                if( editConfig != null )
                    editConfig.setProhibitedFromSearch(pfs);
            }
    	}
    }
    
  //Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		//range options need to be stored for object property 
		//Store field names
		List<String> objectSelect = new ArrayList<String>();
		objectSelect.add(editConfiguration.getVarNameForObject());
		//TODO: Check if this is the proper way to do this?
		formSpecificData.put("objectSelect", objectSelect);
		editConfiguration.setFormSpecificData(formSpecificData);
	}
        			
	public void addFormSpecificDataForAC(EditConfigurationVTwo editConfiguration, VitroRequest vreq, HttpSession session) throws SolrServerException {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		//Get the edit mode
		formSpecificData.put("editMode", getEditMode(vreq).toString().toLowerCase());
		
		//We also need the type of the object itself
		List<String> types = getRangeTypes(vreq);
        //if types array contains only owl:Thing, the search will not return any results
        //In this case, set an empty array
        if(types.size() == 1 && types.get(0).equals(VitroVocabulary.OWL_THING) ){
        	types = new ArrayList<String>();
        }
		
		formSpecificData.put("objectTypes", StringUtils.join(types, ","));
		
		//Get label for individual if it exists
		if(EditConfigurationUtils.getObjectIndividual(vreq) != null) {
			String objectLabel = EditConfigurationUtils.getObjectIndividual(vreq).getName();
			formSpecificData.put("objectLabel", objectLabel);
		}
		
		//TODO: find out if there are any individuals in the classes of objectTypes
		formSpecificData.put("rangeIndividualsExist", rangeIndividualsExist(session,types) );
		
		formSpecificData.put("sparqlForAcFilter", getSparqlForAcFilter(vreq));
		editConfiguration.setTemplate(acObjectPropertyTemplate);
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	private Object rangeIndividualsExist(HttpSession session, List<String> types) throws SolrServerException {		
    	SolrServer solrServer = SolrSetup.getSolrServer(session.getServletContext());
    	
    	boolean rangeIndividualsFound = false;
    	for( String type:types){
    		//solr for type count.
    		SolrQuery query = new SolrQuery();    
    		query.setQuery( VitroSearchTermNames.RDFTYPE + ":" + type);
    		query.setRows(0);
    		
    		QueryResponse rsp = solrServer.query(query);
    		SolrDocumentList docs = rsp.getResults();
    		if( docs.getNumFound() > 0 ){
    			rangeIndividualsFound = true;
    			break;
    		}    		
    	}
    	
    	return  rangeIndividualsFound;		
	}



	/** get the auto complete edit mode */
	public EditMode getEditMode(VitroRequest vreq) {
		//In this case, the original jsp didn't rely on FrontEndEditingUtils
		//but instead relied on whether or not the object Uri existed
		String objectUri = EditConfigurationUtils.getObjectUri(vreq);
		EditMode editMode = FrontEndEditingUtils.EditMode.ADD;
		if(objectUri != null && !objectUri.isEmpty()) {
			editMode = FrontEndEditingUtils.EditMode.EDIT;
			
		}
		return editMode;
	}
    
	public String getSparqlForAcFilter(VitroRequest vreq) {
		String subject = EditConfigurationUtils.getSubjectUri(vreq);			
		String predicate = EditConfigurationUtils.getPredicateUri(vreq);
		//Get all objects for existing predicate, filters out results from addition and edit
		String query =  "SELECT ?objectVar WHERE { " + 
			"<" + subject + "> <" + predicate + "> ?objectVar .} ";
		return query;
	}

}
