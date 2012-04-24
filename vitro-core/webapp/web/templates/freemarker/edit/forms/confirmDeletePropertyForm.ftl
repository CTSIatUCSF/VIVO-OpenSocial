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

<#assign toBeDeletedClass = "dataProp" />

<#if editConfiguration.objectProperty = true>
    <#assign toBeDeletedClass = "objProp" />
    <#if editConfiguration.objectStatementDisplay?has_content>
    	<#assign statement = editConfiguration.objectStatementDisplay />
    	<#--Reviewer and editor role list views required object property template model object for property-->
    	<#assign property = editConfiguration.objectPropertyStatementDisplayPropertyModel />
    </#if>
<#else>
	<#assign statement = editConfiguration.dataStatementDisplay />
</#if>

<#assign deletionTemplateName = editConfiguration.deleteTemplate/>

<form action="${editConfiguration.deleteProcessingUrl}" method="get">
    <h2>Are you sure you want to delete the following entry from <em>${editConfiguration.propertyName}</em>?</h2>
    
    <p class="toBeDeleted ${toBeDeletedClass}">
        <#if editConfiguration.objectProperty = true>
            <#if statement?has_content>
                <#include deletionTemplateName />
            </#if>
        <#else>
            ${statement}
        </#if>
    </p>
    
    <input type="hidden" name="subjectUri"   value="${editConfiguration.subjectUri}" role="input" />
    <input type="hidden" name="predicateUri" value="${editConfiguration.predicateUri}" role="input" />
    
    <#if editConfiguration.dataProperty = true>
        <input type="hidden" name="datapropKey" value="${editConfiguration.datapropKey}" role="input" />
        <input type="hidden" name="vitroNsProp" value="${editConfiguration.vitroNsProperty}" role="input" />
    <#else>
        <input type="hidden" name="objectUri"    value="${editConfiguration.objectUri}" role="input" />
    </#if>
    
   <br />
    <#if editConfiguration.objectProperty = true>
    <p class="submit">
    </#if>
        <input type="submit" id="submit" value="Delete" role="button"/>
        or 
        <a class="cancel" title="Cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
    <#if editConfiguration.objectProperty = true>
    </p>
    </#if>
</form>