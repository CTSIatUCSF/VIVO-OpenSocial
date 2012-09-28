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

<#-- Macros and functions for form controls -->


<#-- Output: html notifying the user that the browser is an unsupported version -->
<#macro unsupportedBrowser  urlsBase>
<div id="ie67DisableWrapper">
    <div id="ie67DisableContent">
	    <img src="${urlsBase}/images/iconAlertBig.png" alt="Alert Icon"/>
	    <p>This form is not supported in versions of Internet Explorer below version 8. Please upgrade your browser, or
	    switch to another browser, such as FireFox.</p>
    </div>
</div>
</#macro>

<#-- After selecting an individual via autocomplete, display highlighted and with verify link -->
<#macro acSelection urlsBase inputName inputId acGroupName inputValue labelValue="">
<div class="acSelection" acGroupName="${acGroupName}">
    <p class="inline">
        <label>${labelValue}</label>
        <span class="acSelectionInfo"></span>
        <a href="${urlsBase}/individual?uri=" class="verifyMatch" title="verify match">(Verify this match</a> or 
        <a href="#" class="changeSelection" id="changeSelection">change selection)</a>
        </p>
        <input class="acUriReceiver" type="hidden" id="${inputId}" name="${inputName}" value="${inputValue}" />
        <!-- Field value populated by JavaScript -->
</div>
</#macro>

<#--Given an edit configuration template object, get the current value for a uri field using the field name-->


<#function getEditConfigLiteralValue config varName>
	<#local literalValues = config.existingLiteralValues >
	<#if (literalValues?keys?seq_contains(varName)) && (literalValues[varName]?size > 0)>
		<#return literalValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Given an edit configuration template object, get the current value for a literal field using the field name-->

<#function getEditConfigUriValue config varName>
 	<#local uriValues = config.existingUriValues />
  <#if (uriValues?keys?seq_contains(varName)) && (uriValues[varName]?size > 0)>
		<#return uriValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Now check whether a given value returns either a uri or a literal value, if one empty then use other and
return - returns empty string if no value found-->
<#function getEditConfigValue config varName>
	<#local returnValue = getEditConfigUriValue(config, varName) />
	<#if (returnValue?length = 0)>
		<#local returnValue = getEditConfigLiteralValue(config, varName) />
	</#if>
	<#return returnValue>
</#function>


<#--Given edit submission object find values-->
<#function getEditSubmissionLiteralValue submission varName>
	<#local literalValues = submission.literalsFromForm >
	<#if (literalValues?keys?seq_contains(varName)) && (literalValues[varName]?size > 0)>
		<#return literalValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Given an edit configuration template object, get the current value for a literal field using the field name-->

<#function getEditSubmissionUriValue submission varName>
 	<#local uriValues = submission.urisFromForm />
  <#if (uriValues?keys?seq_contains(varName)) && (uriValues[varName]?size > 0)>
		<#return uriValues[varName][0] >
	</#if>
	<#return "">
</#function>

<#--Get edit submission value for either literal or uri-->
<#function getEditSubmissionValue submission varName>
	<#local returnValue = getEditSubmissionUriValue(submission, varName) />
	<#if (returnValue?length = 0)>
		<#local returnValue = getEditSubmissionLiteralValue(submission, varName) />
	</#if> 
	<#return returnValue>
</#function>

<#--Get the value for the form field, checking edit submission first and then edit configuration-->
<#function getFormFieldValue submission config varName>
	<#local returnValue = "">
	<#if submission?has_content && submission.submissionExists = true>
		<#local returnValue = getEditSubmissionValue(submission varName)>
	<#else>
		<#local returnValue = getEditConfigValue(config varName)>
	</#if>
	<#return returnValue>
</#function>

<#--Check if submission error exists for a field name-->
<#function submissionErrorExists editSubmission fieldName>
	<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
		<#if editSubmission.validationErrors?keys?seq_contains(fieldName)>
			<#return true>
		</#if>
	</#if>
	<#return false>
</#function>
