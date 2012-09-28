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

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Adds static Strings that may be useful for forms that are part of VIVO.
 *  
 * @author bdc34
 *
 */
public abstract class VivoBaseGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {

    final static String vivoCore ="http://vivoweb.org/ontology/core#" ;
    final static String rdfs =VitroVocabulary.RDFS ;
    final static String foaf = "http://xmlns.com/foaf/0.1/";
    final static String type =VitroVocabulary.RDF_TYPE ;            
    final static String label =rdfs+"label" ;
    final static String bibo = "http://purl.org/ontology/bibo/";
    
    final static String trainingClass = vivoCore+"EducationalTraining" ;    
    final static String degreeClass =vivoCore+"AcademicDegree" ;    
    final static String majorFieldPred =vivoCore+"majorField" ;
    final static String deptPred =vivoCore+"departmentOrSchool" ;
    final static String infoPred =vivoCore+"supplementalInformation" ;
    final static String degreeEarned =vivoCore+"degreeEarned" ;
    final static String degreeOutcomeOf =vivoCore+"degreeOutcomeOf" ;
    final static String trainingAtOrg =vivoCore+"trainingAtOrganization" ;
    final static String authorRankPredicate = vivoCore + "authorRank";
    final static String linkedAuthorPredicate = vivoCore + "linkedAuthor";
    
    final static String dateTimeValue =vivoCore+"dateTime";
    final static String dateTimeValueType =vivoCore+"DateTimeValue";
    final static String dateTimePrecision =vivoCore+"dateTimePrecision";

    final static String toInterval =vivoCore+"dateTimeInterval";
    final static String intervalType =vivoCore+"DateTimeInterval";
    final static String intervalToStart =vivoCore+"start";
    final static String intervalToEnd =vivoCore+"end";

    final static String orgClass ="http://xmlns.com/foaf/0.1/Organization" ;
    final static String personClass = foaf + "Person";
    
}
