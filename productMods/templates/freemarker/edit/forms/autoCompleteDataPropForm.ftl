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

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
    <#assign submissionErrors = editSubmission.validationErrors/>
</#if>

<#assign sparqlForAcFilter = editConfiguration.pageData.sparqlForAcFilter />
<#assign editMode = editConfiguration.pageData.editMode />
<#assign propertyPublicName = editConfiguration.propertyPublicName/>
<h2>${editConfiguration.formTitle}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        
        <#list submissionErrors?keys as errorFieldName>
            ${submissionErrors[errorFieldName]}
        </#list>
                        
        </p>
    </section>
</#if>

<#assign literalValues = "${editConfiguration.dataLiteralValuesAsString}" />

<form class="customForm" action = "${submitUrl}" method="post">
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
    <#if editConfiguration.dataPredicatePublicDescription?has_content>
       <label for="${editConfiguration.dataLiteral}"><p class="propEntryHelpText">${editConfiguration.dataPredicatePublicDescription}</p></label>
    </#if>   
	
    <p>
		<input class="acSelector" size="50"  type="text" id="literal" name="literal" value="${literalValues}" />
	</p>
								
	<div class="acSelection"> 
	<p class="inline">
	<label>Selected:</label> 
	<span class="acSelectionInfo"></span> 
	
	<a href="#" class="cancel">(Change selection)</a> 
	</p>
	</div>
    <br />

    <input type="submit" id="submit" value="${editConfiguration.submitLabel}" role="button"/>
    <span class="or"> or </span>
    <a title="Cancel" href="${cancelUrl}">Cancel</a>

</form>

<#if editConfiguration.includeDeletionForm = true>
<#include "defaultDeletePropertyForm.ftl">
</#if>
<#--Not including defaultFormScripts.ftl which would trigger tinyMce-->
<#assign sparqlQueryUrl = "${urls.base}/ajax/sparqlQuery" >
<#--Passing in object types only if there are any types returned, otherwise
the parameter should not be passed at all to the solr search.
Also multiple types parameter set to true only if more than one type returned-->
    <script type="text/javascript">	
    var customFormData  = {
        acUrl: '${urls.base}/dataautocomplete?',
      	property: '${editConfiguration.predicateUri}',
        submitButtonTextType: 'simple',
        editMode: '${editMode}', //Change this to check whether adding or editing
        sparqlForAcFilter: '${sparqlForAcFilter}',
        sparqlQueryUrl: '${sparqlQueryUrl}',
        defaultTypeName: '${propertyPublicName}'
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}
 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customFormWithAutocomplete.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>',             
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/customFormWithDataAutocomplete.js"></script>')}
