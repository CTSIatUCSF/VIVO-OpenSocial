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

<#-- Template for adding a publication to a foaf:Persons -->

<#import "lib-vivo-form.ftl" as lvf>

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />

<#--assign htmlForElements = editConfiguration.pageData.htmlForElements ! {}/-->

<#--drop down options for a field are included in page data with that field name-->
<#assign pubTypeLiteralOptions = editConfiguration.pageData.pubType />
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#--In case of submission error, may already have publication type or title - although latter not likely, but storing values to be on safe side-->
<#assign publicationTypeValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "pubType") />
<#assign titleValue = lvf.getFormFieldValue(editSubmission, editConfiguration, "title") />
<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Publication">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Publication">
        <#assign disabledVal=""/>
</#if>

<h2>${titleVerb}&nbsp;publication entry for ${editConfiguration.subjectName}</h2>

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	${submissionErrors[errorFieldName]} <br/>
        </#list>
        
        </p>
    </section>
</#if>


<#assign requiredHint = "<span class='requiredHint'> *</span>" />
<#assign yearHint     = "<span class='hint'>(YYYY)</span>" />

<#if editMode = "error">
 <div>This form is unable to handle the editing of this position because it is associated with 
      multiple Position individuals.</div>      
<#else>

<section id="addPublicationToPerson" role="region">        
    
<@lvf.unsupportedBrowser urls.base/>
<form id="addpublicationToPerson" class="customForm noIE67" action="${submitUrl}"  role="add/edit publication">
        
        <#--TODO: Check if possible to have existing publication options here in order to select-->
    <p class="inline"><label for="typeSelector">Publication Type ${requiredHint}</label>
        <select id="typeSelector" name="pubType" <#if (disabledVal?length > 0)>disabled="${disabledVal}"</#if> >
             <option value="" <#if (publicationTypeValue?length = 0)>selected="selected"</#if>>Select one</option>
             <#list pubTypeLiteralOptions?keys as key>
                 <option value="${key}" <#if (publicationTypeValue = key)>selected="selected"</#if>>${pubTypeLiteralOptions[key]}</option>
             </#list>
        </select>
    </p>
    <div class="fullViewOnly">        
        <p>
            <label for="relatedIndLabel">Title ${requiredHint}</label>
            <input class="acSelector" size="50"  type="text" id="relatedIndLabel" name="title" 
            <#if (disabledVal?length > 0)>disabled="${disabledVal}"</#if> value="" />
        </p>

        <div class="acSelection">
            <p class="inline">
                <label>Selected Publication:</label>
                <span class="acSelectionInfo"></span>
                <a href="${urls.base}/individual?uri=" class="verifyMatch" title"verify match">(Verify this match)</a>
            </p>
            <input class="acUriReceiver" type="hidden" id="pubUri" name="pubUri" value="" />
        </div>
        
   </div>
       <p class="submit">
            <input type="hidden" name = "editKey" value="${editKey}"/>
            <input type="submit" id="submit" value="${submitButtonText}"/><span class="or"> or </span><a class="cancel" href="${cancelUrl}">Cancel</a>
       </p>

       <p id="requiredLegend" class="requiredHint">* required fields</p>
    </form>


<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >

    <script type="text/javascript">
    var customFormData  = {
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        acUrl: '${urls.base}/autocomplete?tokenize=true',
        submitButtonTextType: 'simple',
        editMode: '${editMode}',
        defaultTypeName: 'publication' // used in repair mode to generate button text
    };
    </script>
</section>
</#if>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customFormWithAutocomplete.css" />')}


 ${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/edit/forms/js/customFormWithAutocomplete.js"></script>')}
