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

<#assign formTitle>
 "${editConfiguration.propertyPublicDomainTitle}" entry for ${editConfiguration.subjectName}
</#assign>
<#if editConfiguration.objectUri?has_content>
    <#assign formTitle>Edit ${formTitle} </#assign>
    <#assign submitLabel>Save changes</#assign>
<#else>
    <#assign formTitle>Create ${formTitle} </#assign>
    <#assign submitLabel>Create "${editConfiguration.propertyPublicDomainTitle}" entry</#assign>
</#if>

<h2>${formTitle}</h2>

<form class="editForm" action="${submitUrl}">
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />
    <input type="text" name="name" id="name" label="name (required)" size="30" role="input" />
    
    <p class="submit">
        <input type="submit" id="submit" value="${submitLabel}" role="submit" />
        <span class="or"> or </span>
        <a title="Cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
    </p>     
</form>