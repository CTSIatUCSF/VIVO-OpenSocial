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
 * core:InformationResource individuals.
 * 
 * @author bdc34
 *
 */
public class VivoInformationResourceContextNodeFields extends ContextNodeFields{
    
    static List<String> queriesForInformationResource = new ArrayList<String>();
    
    public VivoInformationResourceContextNodeFields(RDFServiceFactory rdfServiceFactory){        
        super(queriesForInformationResource, rdfServiceFactory);
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
  

  //queries for core:InformationResource
  static {
      
        /* linked author labels */

        queriesForInformationResource
                .add(prefix
                        + "SELECT (str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {"
                        + " ?uri rdf:type core:InformationResource . "
                        + "?uri core:informationResourceInAuthorship ?a . ?a core:linkedAuthor ?b ."
                        + "?b rdfs:label ?ContextNodeProperty .}");

        /* features */
        
        queriesForInformationResource
                .add(prefix
                        + "SELECT (str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {"
                        + "?uri rdf:type core:InformationResource . "
                        + "?uri core:features ?i . ?i rdfs:label ?ContextNodeProperty ."
                        + "}");

        /* editor */ 
        
        queriesForInformationResource
                .add(prefix
                        + "SELECT (str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {"
                        + "?uri rdf:type core:InformationResource . "
                        + "?uri bibo:editor ?e . ?e rdfs:label ?ContextNodeProperty  ."
                        + "}");

        /* subject area */
        
        queriesForInformationResource
                .add(prefix
                        + "SELECT (str(?ContextNodeProperty) as ?contextNodeProperty) WHERE {"
                        + "?uri rdf:type core:InformationResource . "
                        + "?uri core:hasSubjectArea ?f . ?f rdfs:label ?ContextNodeProperty ."
                        + "}");              
    }

}
