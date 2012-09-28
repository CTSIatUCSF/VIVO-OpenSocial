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
<#import "lib-vivo-form.ftl" as lvf>

<#assign existingConcepts = editConfiguration.pageData.existingConcepts/>
<#assign userDefinedConceptUrl = editConfiguration.pageData.userDefinedConceptUrl/>
<#assign sources = editConfiguration.pageData.searchServices/>
<#assign inversePredicate = editConfiguration.pageData.inversePredicate />

<#--If edit submission exists, then retrieve validation errors if they exist-->
<#if editSubmission?has_content && editSubmission.submissionExists = true && editSubmission.validationErrors?has_content>
	<#assign submissionErrors = editSubmission.validationErrors/>
</#if>


<h2>Manage Concepts</h2>
    

<#if submissionErrors?has_content>
    <section id="error-alert" role="alert">
        <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
        <p>
        <#--below shows examples of both printing out all error messages and checking the error message for a specific field-->
        <#list submissionErrors?keys as errorFieldName>
        	${errorFieldName} :  ${submissionErrors[errorFieldName]}
        </#list>
       
        
        </p>
    </section>
</#if>

<@lvf.unsupportedBrowser urls.base/>

<div class="noIE67">

    
    
<ul id="existingConcepts" >
      
    <script type="text/javascript">
        var existingConceptsData = [];
    </script>
    
    <#list existingConcepts as existingConcept>
        <li class="existingConcept">
              
            <span class="concept">

                <span class="conceptWrapper">
                   <span class="conceptLabel"> ${existingConcept.conceptLabel} 
                   	<#if existingConcept.vocabURI?has_content && existingConcept.vocabLabel?has_content>
                   		(${existingConcept.vocabLabel})
                   	</#if>
                   </span> 
                </span>
                &nbsp;<a href="${urls.base}/edit/primitiveRdfEdit" class="remove">Remove</a>
            </span>
        </li>    
        
        <script type="text/javascript">
            existingConceptsData.push({
                "conceptNodeUri": "${existingConcept.conceptURI}",
                "conceptLabel": "${existingConcept.conceptLabel}"      
            });
        </script>         

      </#list>    

</ul>
       

<#if (existingConcepts?size = 0) >   
        <p>There are currently no concepts specified.</p>
<#else>
        &nbsp;
</#if>

<div id="showAddForm">
    
    <input type="submit" value="Add Concept" id="showAddFormButton" name="showAddFormButton">  or 
    <a class="cancel" href="${cancelUrl}&url=/individual">Return to Profile Page</a>
</div> 
    <form id="addConceptForm" class="customForm" action="${submitUrl}">
		<#assign checkedSource = false />
	<h4 class="services">External Vocabulary Services</h4>
    <#list sources?keys as sourceUri>
    		<#assign thisSource = sources[sourceUri]/>
        <input type="radio"  name="source" value="${sourceUri}" role="radio" <#if checkedSource = false><#assign checkedSource = true/>checked="checked"</#if>>
        <label class="inline" for="${thisSource.label}"> <a href="${thisSource.url}">${thisSource.label}</a> &nbsp;(${thisSource.description})</label>
        <br />
    </#list>
    <p class="inline-search">
        <input type="text" id="searchTerm" label="Search" class="acSelector" size="35" />
        <input type="button" class="submit concept-search" id="searchButton" name="searchButton" value="Search"/>&nbsp;
    </p><span id="createOwnOne"> or &nbsp;<a href="${userDefinedConceptUrl}" >Create your own concept&nbsp;</a></span>
    <input type="hidden" id="conceptNode" name="conceptNode" value=""/> <!-- Field value populated by JavaScript -->
    <input type="hidden" id="conceptLabel" name="conceptLabel" value="" />  <!-- Field value populated by JavaScript -->
		<input type="hidden" id="conceptSource" name="conceptSource" value="" /> <!-- Field value populated by JavaScript -->
    <div id="selectedConcept" name="selectedConcept" class="acSelection">
        <p class="inline">
        </p>
        <!-- Search results populated by JavaScript -->
    </div>
    <div id="errors" name="errors"></div>
    
    <input type="hidden" name="editKey" id="editKey" value="${editKey}"/>
    <p class="submit">
        <input type="submit" id="submit" name="submit" value="Add Selected Concept" />
        
    </p>
    <div id="createOwnTwo"><br />
        <a href="${userDefinedConceptUrl}" > Can't find the concept you want? Create your own.</a>
        
    </div>	
        <p>
            <span class="or"> or </span><a class="cancel" href="${cancelUrl}&url=/individual">Return to Profile Page</a>
        </p>
    </form>
</div>



    <script type="text/javascript">
    var customFormData = {
        dataServiceUrl: '${urls.base}/conceptSearchService',
        subjectUri: '${editConfiguration.subjectUri}',
        predicateUri: '${editConfiguration.predicateUri}',
        inversePredicateUri: '${inversePredicate}'
    };
    </script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/addConcept.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/browserUtils.js"></script>')}
${scripts.add('<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/addConcept.js"></script>')}




