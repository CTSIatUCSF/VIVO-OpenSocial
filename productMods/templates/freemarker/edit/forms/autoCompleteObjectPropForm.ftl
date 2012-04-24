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
<#assign rangeOptionsExist = false />
<#if (rangeOptions?keys?size > 0)>
	<#assign rangeOptionsExist = true/>
</#if>
<#assign objectTypes = editConfiguration.pageData.objectTypes />
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
<#else>
	<#assign titleVerb = "Add" >
	<#assign objectLabel = "" />
		<#assign selectedObjectUri = ""/>
</#if>

<#if editConfiguration.formTitle?contains("collaborator") >
    <#assign formTitle = "Select an existing Collaborator for ${editConfiguration.subjectName}" />
<#else>
    <#assign formTitle = editConfiguration.formTitle />
</#if>

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
								<label for="relatedIndLabel"> ${propertyNameForDisplay?capitalize} Name<span class='requiredHint'> *</span></label>
								<input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="objectLabel" value="${objectLabel}" />
						</p>
								
							<div class="acSelection"> 
							<p class="inline">
							<label>Selected:</label> 
							<span class="acSelectionInfo"></span> 
							<a href="/vivo/individual?uri="
									class="verifyMatch">(Verify this match)</a> 
							<a href="#"
									class="cancel">(Change selection)</a> 
							</p> <input class="acUriReceiver" type="hidden" id="objectVar" name="objectVar" value="${selectedObjectUri}" />
							</div>

            
            <#--The above section should be autocomplete-->
            
            
            <p>
                <input type="submit" id="submit" value="${editConfiguration.submitLabel}" role="button" disabled="disabled"/>
           
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

    <script type="text/javascript">
    var customFormData  = {
        acUrl: '${urls.base}/autocomplete?tokenize=true',
        acType: '${objectTypes}',
        acMultipleTypes: 'true',
        submitButtonTextType: 'simple',
        editMode: '${editMode}',
        typeName:'${propertyNameForDisplay}',
        supportEdit: 'true',
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        defaultTypeName: '${propertyNameForDisplay}' // used in repair mode to generate button text
    };
    </script>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}
