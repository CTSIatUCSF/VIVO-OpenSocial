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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyComparator;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.PublicationHasAuthorValidator;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

/**
 * This is a slightly unusual generator that is used by Manage Authors on
 * information resources. 
 *
 * It is intended to always be an add, and never an update. 
 */
public class AddAuthorsToInformationResourceGenerator extends VivoBaseGenerator implements EditConfigurationGenerator {

    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) {
        EditConfigurationVTwo editConfiguration = new EditConfigurationVTwo();    	    	
        initBasics(editConfiguration, vreq);
        initPropertyParameters(vreq, session, editConfiguration);

        //Overriding URL to return to
        setUrlToReturnTo(editConfiguration, vreq);

        //set variable names
        editConfiguration.setVarNameForSubject("infoResource");               
        editConfiguration.setVarNameForPredicate("predicate");      
        editConfiguration.setVarNameForObject("authorshipUri");                          

        // Required N3
        editConfiguration.setN3Required( list( getN3NewAuthorship() ) );    

        // Optional N3 
        editConfiguration.setN3Optional( generateN3Optional());	

        editConfiguration.addNewResource("authorshipUri", DEFAULT_NS_TOKEN);
        editConfiguration.addNewResource("newPerson", DEFAULT_NS_TOKEN);
        editConfiguration.addNewResource("newOrg", DEFAULT_NS_TOKEN);
        
        //In scope
        setUrisAndLiteralsInScope(editConfiguration, vreq);

        //on Form
        setUrisAndLiteralsOnForm(editConfiguration, vreq);

        //Sparql queries
        setSparqlQueries(editConfiguration, vreq);

        //set fields
        setFields(editConfiguration, vreq, EditConfigurationUtils.getPredicateUri(vreq));

        //template file
        editConfiguration.setTemplate("addAuthorsToInformationResource.ftl");
        //add validators
        editConfiguration.addValidator(new PublicationHasAuthorValidator());

        //Adding additional data, specifically edit mode
        addFormSpecificData(editConfiguration, vreq);
        
        editConfiguration.addValidator(new AntiXssValidation());
        
        //NOITCE this generator does not run prepare() since it 
        //is never an update and has no SPARQL for existing
        
        return editConfiguration;
    }
		
	private void setUrlToReturnTo(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		editConfiguration.setUrlPatternToReturnTo(EditConfigurationUtils.getFormUrlWithoutContext(vreq));		
	}
	
	/***N3 strings both required and optional***/
	
	public String getN3PrefixString() {
		return "@prefix core: <" + vivoCore + "> .\n" + 
		 "@prefix foaf: <" + foaf + "> .  \n"   ;
	}
	
	private String getN3NewAuthorship() {
		return getN3PrefixString() + 
		"?authorshipUri a core:Authorship ;\n" + 
        "  core:linkedInformationResource ?infoResource .\n" + 
        "?infoResource core:informationResourceInAuthorship ?authorshipUri .";
	}
	
	private String getN3AuthorshipRank() {
		return getN3PrefixString() +   
        "?authorshipUri core:authorRank ?rank .";
	}
	
	//first name, middle name, last name, and new perseon for new author being created, and n3 for existing person
	//if existing person selected as author
	private List<String> generateN3Optional() {
		return list(
		        getN3NewPersonFirstName() ,
                getN3NewPersonMiddleName(),
                getN3NewPersonLastName(),                
                getN3NewPerson(),
                getN3AuthorshipRank(),
                getN3ForExistingPerson(),
                getN3NewOrg(),
                getN3ForExistingOrg());
		
	}
	
	
	private String getN3NewPersonFirstName() {
		return getN3PrefixString() + 
		"?newPerson foaf:firstName ?firstName .";
	}
	
	private String getN3NewPersonMiddleName() {
		return getN3PrefixString() +  
        "?newPerson core:middleName ?middleName .";
	}
	
	private String getN3NewPersonLastName() {
		return getN3PrefixString() + 
        "?newPerson foaf:lastName ?lastName .";
	}
	
	private String getN3NewPerson() {
		return  getN3PrefixString() + 
        "?newPerson a foaf:Person ;\n" + 
        "<" + RDFS.label.getURI() + "> ?label .\n" + 
        "?authorshipUri core:linkedAuthor ?newPerson .\n" + 
        "?newPerson core:authorInAuthorship ?authorshipUri . ";
	}
	
	private String getN3ForExistingPerson() {
		return getN3PrefixString() + 
		"?authorshipUri core:linkedAuthor ?personUri .\n" + 
		"?personUri core:authorInAuthorship ?authorshipUri .";
	}
	
	private String getN3NewOrg() {
		return  getN3PrefixString() + 
        "?newOrg a foaf:Organization ;\n" + 
        "<" + RDFS.label.getURI() + "> ?orgName .\n" + 
        "?authorshipUri core:linkedAuthor ?newOrg .\n" + 
        "?newOrg core:authorInAuthorship ?authorshipUri . ";
	}
	
	private String getN3ForExistingOrg() {
		return getN3PrefixString() + 
		"?authorshipUri core:linkedAuthor ?orgUri .\n" + 
		"?orgUri core:authorInAuthorship ?authorshipUri .";
	}
	/**  Get new resources	 */
	//A new authorship uri will always be created when an author is added
	//A new person may be added if a person not in the system will be added as author
	 private Map<String, String> generateNewResources(VitroRequest vreq) {					
			
			
			HashMap<String, String> newResources = new HashMap<String, String>();			
			newResources.put("authorshipUri", DEFAULT_NS_TOKEN);
			newResources.put("newPerson", DEFAULT_NS_TOKEN);
			newResources.put("newOrg", DEFAULT_NS_TOKEN);
			return newResources;
		}
	
	/** Set URIS and Literals In Scope and on form and supporting methods	 */   
    private void setUrisAndLiteralsInScope(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	//Uris in scope always contain subject and predicate
    	HashMap<String, List<String>> urisInScope = new HashMap<String, List<String>>();
    	urisInScope.put(editConfiguration.getVarNameForSubject(), 
    			Arrays.asList(new String[]{editConfiguration.getSubjectUri()}));
    	urisInScope.put(editConfiguration.getVarNameForPredicate(), 
    			Arrays.asList(new String[]{editConfiguration.getPredicateUri()}));
    	editConfiguration.setUrisInScope(urisInScope);
    	//no literals in scope    	    
    }
	
    private void setUrisAndLiteralsOnForm(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	List<String> urisOnForm = new ArrayList<String>();    	
    	//If an existing person is being used as an author, need to get the person uri
    	urisOnForm.add("personUri");
    	urisOnForm.add("orgUri");
    	editConfiguration.setUrisOnform(urisOnForm);
    	
    	//for person who is not in system, need to add first name, last name and middle name
    	//Also need to store authorship rank and label of author
    	List<String> literalsOnForm = list("firstName",
    			"middleName",
    			"lastName",
    			"rank",
    			"orgName",
    			"label");
    	editConfiguration.setLiteralsOnForm(literalsOnForm);
    }   
    
    /** Set SPARQL Queries and supporting methods. */        
    private void setSparqlQueries(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {        
        //Sparql queries are all empty for existing values
    	//This form is different from the others that it gets multiple authors on the same page
    	//and that information will be queried and stored in the additional form specific data
        HashMap<String, String> map = new HashMap<String, String>();
    	editConfiguration.setSparqlForExistingUris(new HashMap<String, String>());
    	editConfiguration.setSparqlForExistingLiterals(new HashMap<String, String>());
    	editConfiguration.setSparqlForAdditionalUrisInScope(new HashMap<String, String>());
    	editConfiguration.setSparqlForAdditionalLiteralsInScope(new HashMap<String, String>());
    }
    
    /**
	 * 
	 * Set Fields and supporting methods
	 */
	
	private void setFields(EditConfigurationVTwo editConfiguration, VitroRequest vreq, String predicateUri) {
    	setLabelField(editConfiguration);
    	setFirstNameField(editConfiguration);
    	setMiddleNameField(editConfiguration);
    	setLastNameField(editConfiguration);
    	setRankField(editConfiguration);
    	setPersonUriField(editConfiguration);
    	setOrgUriField(editConfiguration);
    	setOrgNameField(editConfiguration);
    }
	
	private void setLabelField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("label").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
		
	}


	private void setFirstNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("firstName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
		
	}


	private void setMiddleNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("middleName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
		
	}





	private void setLastNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("lastName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
		
	}

	private void setRankField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("rank").
				setValidators(list("nonempty")).
				setRangeDatatypeUri(XSD.xint.toString())
				);
		
	}


	private void setPersonUriField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("personUri")
				//.setObjectClassUri(personClass)
				);
		
	}

	private void setOrgUriField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("orgUri")
				//.setObjectClassUri(personClass)
				);
		
	}
	private void setOrgNameField(EditConfigurationVTwo editConfiguration) {
		editConfiguration.addField(new FieldVTwo().
				setName("orgName").
				setValidators(list("datatype:" + XSD.xstring.toString())).
				setRangeDatatypeUri(XSD.xstring.toString())
				);
		
	}
	//Form specific data
	public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
		HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
		//Get the existing authorships
		formSpecificData.put("existingAuthorInfo", getExistingAuthorships(vreq));
		formSpecificData.put("newRank", getMaxRank(vreq) + 1);
		formSpecificData.put("rankPredicate", authorRankPredicate);
		editConfiguration.setFormSpecificData(formSpecificData);
	}
	
	private List<AuthorshipInfo> getExistingAuthorships(VitroRequest vreq) {
		Individual infoResource = EditConfigurationUtils.getSubjectIndividual(vreq);
	    List<Individual> authorships = infoResource.getRelatedIndividuals(
	    		EditConfigurationUtils.getPredicateUri(vreq));  
	    //TODO: Check if sorted correctly
	    sortAuthorshipIndividuals(authorships);
		
		return getAuthorshipInfo(authorships);
	}

	private List<AuthorshipInfo> getAuthorshipInfo(
			List<Individual> authorships) {
		List<AuthorshipInfo> info = new ArrayList<AuthorshipInfo>();
		 for ( Individual authorship : authorships ) {
			 	String authorshipUri =  authorship.getURI();
			 	String authorshipName = authorship.getName();
			 	String authorUri = "";
			 	String authorName = "";
			 	Individual author = authorship.getRelatedIndividual(linkedAuthorPredicate);
			 	if(author != null) {
			 		authorUri = author.getURI();
			 		authorName = author.getName();
			 	}
			 	AuthorshipInfo aaInfo = new AuthorshipInfo(authorshipUri, authorshipName, authorUri, authorName);
		        info.add(aaInfo);
		 }
		 return info;
	}

	private int getMaxRank(VitroRequest vreq) {
		Individual infoResource = EditConfigurationUtils.getSubjectIndividual(vreq);
	    List<Individual> authorships = infoResource.getRelatedIndividuals(
	    		EditConfigurationUtils.getPredicateUri(vreq)); 
	    sortAuthorshipIndividuals(authorships);
	    int maxRank = 0;
	    for(Individual authorship: authorships) {
	    	DataPropertyStatement rankStmt = authorship.getDataPropertyStatement(authorRankPredicate);
	        if (rankStmt != null) {
	            maxRank = Integer.parseInt(rankStmt.getData());   
	        }
	    }
		return maxRank;
	}




	private void sortAuthorshipIndividuals(List<Individual> authorships) {
		DataPropertyComparator comp = new DataPropertyComparator(authorRankPredicate);
	    Collections.sort(authorships, comp);
	}

	//This is the information about authors the form will require
	public class AuthorshipInfo {
		//This is the authorship node information
		private String authorshipUri;
		private String authorshipName;
		//Author information for authorship node
		private String authorUri;
		private String authorName;
		
		public AuthorshipInfo(String inputAuthorshipUri, 
				String inputAuthorshipName,
				String inputAuthorUri,
				String inputAuthorName) {
			authorshipUri = inputAuthorshipUri;
			authorshipName = inputAuthorshipName;
			authorUri = inputAuthorUri;
			authorName = inputAuthorName;

		}
		
		//Getters - specifically required for Freemarker template's access to POJO
		public String getAuthorshipUri() {
			return authorshipUri;
		}
		
		public String getAuthorshipName() {
			return authorshipName;
		}
		
		public String getAuthorUri() {
			return authorUri;
		}
		
		public String getAuthorName() {
			return authorName;
		}
	}
	
	static final String DEFAULT_NS_TOKEN=null; //null forces the default NS

}
