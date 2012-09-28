<#--
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
-->
<#--Assign variables from editConfig-->
<#assign rangeOptions = editConfiguration.pageData.objectVar />
<#-- 
<#assign rangeOptionsExist = false /> 
<#if (rangeOptions?keys?size > 0)>
	<#assign rangeOptionsExist = true/>
</#if>
 -->
 
<#assign rangeOptionsExist = true /> 

<#assign objectTypes = editConfiguration.pageData.objectTypes />
<#assign objectTypesSize = objectTypes?length />
<#assign objectTypesExist = false />
<#assign multipleTypes = false />
<#if (objectTypesSize > 1)>
	<#assign objectTypesExist = true />
</#if>
<#if objectTypes?contains(",")>
	<#assign multipleTypes = true/>
</#if>
<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#assign editMode = editConfiguration.pageData.editMode />
<#assign propertyNameForDisplay = "" />
<#if editConfiguration.objectPropertyNameForDisplay?has_content>
	<#assign propertyNameForDisplay = editConfiguration.objectPropertyNameForDisplay />
</#if>
<#if editMode = "edit" >
	<#assign titleVerb = "Edit" />
	<#assign objectLabel = editConfiguration.pageData.objectLabel />
	<#assign selectedObjectUri = editConfiguration.objectUri />
	<#assign submitButtonText = "Save Change" />
<#else>
	<#assign titleVerb = "Add" >
	<#assign objectLabel = "" />
	<#assign selectedObjectUri = ""/>
	<#assign submitButtonText = "Create Entry" />
</#if>

<#if editConfiguration.formTitle?contains("collaborator") >
    <#assign formTitle = "Select an existing Collaborator for ${editConfiguration.subjectName}" />
<#else>
    <#assign formTitle = editConfiguration.formTitle />
</#if>
<#--In order to fill out the subject-->
<#assign acFilterForIndividuals =  "['" + editConfiguration.subjectUri + "']" />

<h2>${formTitle}</h2>

<#if editConfiguration.propertySelectFromExisting = true>
    <#if rangeOptionsExist  = true >
        <form class="customForm" action = "${submitUrl}">
            <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
            <#if editConfiguration.propertyPublicDescription?has_content>
                <p>${editConfiguration.propertyPublicDescription}</p>
             </#if>     
             
            <#---This section should become autocomplete instead--> 
            <p>
				<label for="object"> ${propertyNameForDisplay?capitalize} Name<span class='requiredHint'> *</span></label>
				<input class="acSelector" size="50"  type="text" id="object" name="objectLabel" acGroupName="object" value="${objectLabel}" />
			</p>
								
			<div class="acSelection" acGroupName="object" > 
				<p class="inline">
					<label>Selected:</label> 
					<span class="acSelectionInfo"></span>
					<a href="" class="verifyMatch"  title="verify match">(Verify this match</a> or 
                    <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
                </p>
                <input class="acUriReceiver" type="hidden" id="objectVar" name="objectVar" value="${selectedObjectUri}" />
			</div>

            <#--The above section should be autocomplete-->
            
            <p>
                <input type="submit" id="submit" value="${submitButtonText}" role="button" disabled="disabled"/>
           
                <span class="or"> or </span>
                <a title="Cancel" class="cancel" href="${cancelUrl}">Cancel</a>
            </p>
        </form>
    <#else>
        <p> There are no entries in the system from which to select.  </p>  
    </#if>
</#if>
<p>&nbsp;</p>
<#if editConfiguration.propertyOfferCreateNewOption = true>
<#include "defaultOfferCreateNewOptionForm.ftl">

</#if>

<#if editConfiguration.propertySelectFromExisting = false && editConfiguration.propertyOfferCreateNewOption = false>
<p>This property is currently configured to prohibit editing. </p>
</#if>


<#if editConfiguration.includeDeletionForm = true>
<#include "defaultDeletePropertyForm.ftl">
</#if>


<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >
<#--Passing in object types only if there are any types returned, otherwise
the parameter should not be passed at all to the solr search.
Also multiple types parameter set to true only if more than one type returned-->
    <script type="text/javascript">	
    var customFormData  = {
        acUrl: '${urls.base}/autocomplete?tokenize=true',
        <#if objectTypesExist = true>
            acTypes: {object: '${objectTypes}'},
        </#if>
        <#if multipleTypes = true>
            acMultipleTypes: 'true',
        </#if>
        editMode: '${editMode}',
        typeName:'${propertyNameForDisplay}',
        acSelectOnly: 'true',
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        acFilterForIndividuals: ${acFilterForIndividuals},
        defaultTypeName: '${propertyNameForDisplay}', // used in repair mode to generate button text
        baseHref: '${urls.base}/individual?uri='
    };
    </script>
<#--
	 edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AutocompleteObjectPropertyFormGenerator
	 edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AddAttendeeRoleToPersonGenerator
-->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithAutocomplete.js"></script>')}
