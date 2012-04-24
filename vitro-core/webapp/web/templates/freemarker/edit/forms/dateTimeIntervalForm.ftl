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

<#-- Template for adding/editing time values -->

<#--Retrieve certain edit configuration information-->
<#assign editMode = editConfiguration.pageData.editMode />
<#assign htmlForElements = editConfiguration.pageData.htmlForElements />

<#if editMode == "edit">        
        <#assign titleVerb="Edit">        
        <#assign submitButtonText="Edit Date/Time Value">
        <#assign disabledVal="disabled">
<#else>
        <#assign titleVerb="Create">        
        <#assign submitButtonText="Create Date/Time Value">
        <#assign disabledVal=""/>
</#if>
<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>


<h2>${titleVerb} date time value for ${editConfiguration.subjectName}</h2>

<#--Display error messages if any-->
<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	<#if errorFieldName == "startField">
        	    <#if submissionErrors[errorFieldName]?contains("before")>
        	        The Start interval must be earlier than the End interval.
        	    <#else>
        	        ${submissionErrors[errorFieldName]}
        	    </#if>
        	    <br />
        	<#elseif errorFieldName == "endField">
    	        <#if submissionErrors[errorFieldName]?contains("after")>
    	            The End interval must be later than the Start interval.
    	        <#else>
    	            ${submissionErrors[errorFieldName]}
    	        </#if>
	        </#if>
        </#list>
        </p>
    </section>
</#if>
<form class="customForm" action ="${submitUrl}" class="customForm">
<p></p>
<#--Need to draw edit elements for dates here-->
 <#if htmlForElements?keys?seq_contains("startField")>
	Start&nbsp; ${htmlForElements["startField"]}
 </#if>
 <br /><br />
 <#if htmlForElements?keys?seq_contains("endField")>
	End&nbsp; ${htmlForElements["endField"]}
 </#if>

    <p class="submit">
        <input type="hidden" name="editKey" value="${editKey}" />
        <input type="submit" id="submit" value="${submitButtonText}" role="button" />
    
        <span class="or"> or </span>
    
        <a class="cancel" href="${editConfiguration.cancelUrl}" title="Cancel">Cancel</a>
    </p>
</form>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/edit/forms/css/personHasEducationalTraining.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}