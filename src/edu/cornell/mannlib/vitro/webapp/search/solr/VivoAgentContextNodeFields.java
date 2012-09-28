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
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.ContextNodeFields;

import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.ContextNodeFields;

/**
 * Class that adds text from context nodes to Solr Documents for 
 * foaf:Agent individuals.
 */
public class VivoAgentContextNodeFields extends ContextNodeFields{
    
    static List<String> queriesForAgent = new ArrayList<String>();    
    
    public VivoAgentContextNodeFields(RDFServiceFactory rdfServiceFactory){        
        super(queriesForAgent,rdfServiceFactory);
    }
      
  protected static final String prefix = 
        "prefix owl: <http://www.w3.org/2002/07/owl#> "
      + " prefix vitroDisplay: <http://vitro.mannlib.cornell.edu/ontologies/display/1.1#>  "
      + " prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
      + " prefix core: <http://vivoweb.org/ontology/core#>  "
      + " prefix foaf: <http://xmlns.com/foaf/0.1/> "
      + " prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> "
      + " prefix localNav: <http://vitro.mannlib.cornell.edu/ns/localnav#>  "
      + " prefix bibo: <http://purl.org/ontology/bibo/>  ";
  

  //queries for foaf:Agent
  static {
      
      /*  Position */
    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:hrJobTitle ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:involvedOrganizationName ?ContextNodeProperty . }");       
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:positionInOrganization ?i . ?i rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Position . " +
            " ?c core:titleOrRole ?ContextNodeProperty .  }");
    
    /* HR Job Title */
    
    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?HRJobTitle) as ?hrJobTitle)  " +
            "(str(?InvolvedOrganizationName) as ?involvedOrganizationName) " +
            "(str(?PositionInOrganization) as ?positionInOrganization) " +
            "(str(?TitleOrRole) as ?titleOrRole) WHERE {" 
            
            + "?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:Position . "
            
            + " OPTIONAL { ?c core:hrJobTitle ?HRJobTitle . } . "
            + " OPTIONAL { ?c core:involvedOrganizationName ?InvolvedOrganizationName . } ."            
            + " OPTIONAL { ?c core:positionInOrganization ?i . ?i rdfs:label ?PositionInOrganization .  } . "
            + " OPTIONAL { ?c core:titleOrRole ?TitleOrRole . } . "
            + " }");
    
    /* Advisor */
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:advisee ?d . ?d rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:degreeCandidacy ?e . ?e rdfs:label ?ContextNodeProperty . }");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?label) as ?adviseeLabel) WHERE {" +
            " ?uri rdf:type foaf:Agent  ." +            
            " ?c rdf:type core:Relationship . " +
            " ?c core:advisor ?uri . " +
            " ?c core:advisee ?d . ?d rdfs:label ?label .}" );
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?label) as ?advisorLabel) WHERE {" +
            " ?uri rdf:type foaf:Agent  ." +            
            " ?c rdf:type core:Relationship . " +
            " ?c core:advisee ?uri . " +
            " ?c core:advisor ?d . ?d rdfs:label ?label .}" );
    
    /* Author */
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:linkedAuthor ?f . " +            
            " ?f rdfs:label ?ContextNodeProperty . " +
            " FILTER( ?f != ?uri  ) " +
            "}");
    
    queriesForAgent.add(prefix +        "SELECT " +
            "(str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {" +
            " ?uri rdf:type foaf:Agent  ; ?b ?c . " +
            " ?c rdf:type core:Relationship . " +
            " ?c core:linkedInformationResource ?h . ?h rdfs:label ?ContextNodeProperty . }");
    
    /* Award */        

    queriesForAgent.add(prefix +
            "SELECT " +
            "(str(?AwardLabel) as ?awardLabel) " +
            "(str(?AwardConferredBy) as ?awardConferredBy)  " +
            "(str(?Description) as ?description)   " +                        
            "WHERE {"            
            + " ?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:AwardReceipt . "            
            + " OPTIONAL { ?c rdfs:label ?AwardLabel . } . "
            + " OPTIONAL { ?c core:awardConferredBy ?d . ?d rdfs:label ?AwardConferredBy . } . "
            + " OPTIONAL { ?c core:description ?Description . } . "
            + " }");
    
    /* Role In Organization */
    
    queriesForAgent.add(prefix +
            "SELECT (str(?OrganizationLabel) as ?organizationLabel)  WHERE {" 
            + "?uri rdf:type foaf:Agent  ; ?b ?c . "
            + " ?c rdf:type core:Role ; core:roleIn ?Organization ."
            + " ?Organization rdfs:label ?OrganizationLabel . "
            + " }");    
                
    /* Academic Degree / Educational Training */
    
    queriesForAgent.add(prefix + 
            "SELECT  " +
            "(str(?AcademicDegreeLabel) as ?academicDegreeLabel) " +
            "(str(?AcademicDegreeAbbreviation) as ?academicDegreeAbbreviation) " +
            "(str(?MajorField) as ?majorField) " +
            "(str(?DepartmentOrSchool) as ?departmentOrSchool) " +
            "(str(?TrainingAtOrganizationLabel) as ?trainingAtOrganizationLabel) WHERE {"
                
                + " ?uri rdf:type foaf:Agent ; ?b ?c . "
                + " ?c rdf:type core:EducationalTraining . "
                  
                +  "OPTIONAL { ?c core:degreeEarned ?d . ?d rdfs:label ?AcademicDegreeLabel ; core:abbreviation ?AcademicDegreeAbbreviation . } . "
                +  "OPTIONAL { ?c core:majorField ?MajorField .} ."           
                + " OPTIONAL { ?c core:departmentOrSchool ?DepartmentOrSchool . }"            
                + " OPTIONAL { ?c core:trainingAtOrganization ?e . ?e rdfs:label ?TrainingAtOrganizationLabel . } . " 
                +"}");                 
  }
}
