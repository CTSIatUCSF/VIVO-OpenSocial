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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing;

import java.util.HashMap;
import java.util.Map;

public class N3TransitionToV2Mapping extends HashMap<String, String>{        
    public N3TransitionToV2Mapping(){
        Map<String,String> map = this;
        
        // vivo forms:

        map.put("addAuthorsToInformationResource.jsp",                
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddAuthorsToInformationResourceGenerator.class.getName());
        map.put("manageWebpagesForIndividual.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.ManageWebpagesForIndividualGenerator.class.getName());
        map.put("newIndividualForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.NewIndividualFormGenerator.class.getName());
        map.put("organizationHasPositionHistory.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.OrganizationHasPositionHistoryGenerator.class.getName());
        map.put("personHasEducationalTraining.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.PersonHasEducationalTraining.class.getName());
        map.put("personHasPositionHistory.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.PersonHasPositionHistoryGenerator.class.getName());                
        map.put("addGrantRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddGrantRoleToPersonGenerator.class.getName());
        map.put("addEditWebpageForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddEditWebpageFormGenerator.class.getName());
        map.put("addPublicationToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddPublicationToPersonGenerator.class.getName());

//        map.put("terminologyAnnotation.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.TerminologyAnnotationGenerator.class.getName());
//        
//        map.put("redirectToPublication.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.RedirectToPublicationGenerator.class.getName());
//        map.put("unsupportedBrowserMessage.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.UnsupportedBrowserMessage.class.getName());
//        
        // vivo 2 stage role forms:

        map.put("addAttendeeRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddAttendeeRoleToPersonGenerator.class.getName());
        map.put("addClinicalRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddClinicalRoleToPersonGenerator.class.getName());
        map.put("addEditorRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddEditorRoleToPersonGenerator.class.getName());
        map.put("addHeadOfRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddHeadOfRoleToPersonGenerator.class.getName());
        map.put("addMemberRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddMemberRoleToPersonGenerator.class.getName());
        map.put("addOrganizerRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddOrganizerRoleToPersonGenerator.class.getName());
        map.put("addOutreachRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddOutreachProviderRoleToPersonGenerator.class.getName());
        map.put("addPresenterRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddPresenterRoleToPersonGenerator.class.getName());                
        map.put("addResearcherRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddResearcherRoleToPersonGenerator.class.getName());
        map.put("addReviewerRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddReviewerRoleToPersonGenerator.class.getName());
        map.put("addRoleToPersonTwoStage.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddRoleToPersonTwoStageGenerator.class.getName());
        map.put("addServiceProviderRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddServiceProviderRoleToPersonGenerator.class.getName());
        map.put("addTeacherRoleToPerson.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddTeacherRoleToPersonGenerator.class.getName());
    
    }
}
