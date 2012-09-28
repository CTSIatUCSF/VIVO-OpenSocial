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
<#assign rangeOptions = editConfiguration.pageData.object />
<#assign rangeOptionsExist = false />
<#if (rangeOptions?keys?size > 0)>
    <#assign rangeOptionsExist = true/>
</#if>

<h2>${editConfiguration.formTitle}</h2>


    <#if rangeOptionsExist  = true >
        <#assign rangeOptionKeys = rangeOptions?keys />
        <form class="editForm" action = "${submitUrl}">
            <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input" />                
            <select id="object" name="object" role="select">
                <#list rangeOptionKeys as key>
                 <option value="${key}" <#if editConfiguration.objectUri?has_content && editConfiguration.objectUri = key>selected</#if> role="option">${rangeOptions[key]}</option>
                </#list>
            </select>
            
            <p>
                <input type="submit" id="submit" value="${editConfiguration.submitLabel}" role="button "/>
                <span class="or"> or </span>
                <a title="Cancel" class="cancel" href="${cancelUrl}">Cancel</a>
            </p>
        </form>
    <#else>
        <p> There are no Classes in the system from which to select.  </p>  
    </#if>

<#if editConfiguration.propertyOfferCreateNewOption = true>
<#include "defaultOfferCreateNewOptionForm.ftl">

</#if>

<#if editConfiguration.propertySelectFromExisting = false && editConfiguration.propertyOfferCreateNewOption = false>
<p>This property is currently configured to prohibit editing. </p>
</#if>

<#if editConfiguration.includeDeletionForm = true>
<#include "defaultDeletePropertyForm.ftl">
</#if>

