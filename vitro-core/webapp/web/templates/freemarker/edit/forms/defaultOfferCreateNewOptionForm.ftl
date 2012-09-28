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
 
 <#if rangeOptionsExist  = true >
        <p>If you don't find the appropriate entry on the selection list above:</p>
 <#else>
        <p>Please create a new entry.</p>           
 </#if>
 
 <#if editConfiguration.objectUri?has_content>
    <#assign objectUri = editConfiguration.objectUri>
 <#else>
    <#assign objectUri = ""/>
 </#if>
 
<#assign typesList = editConfiguration.offerTypesCreateNew />
<form class="editForm" action="${editConfiguration.mainEditUrl}" role="input" />        
    <input type="hidden" value="${editConfiguration.subjectUri}" name="subjectUri" role="input" />  
    <input type="hidden" value="${editConfiguration.predicateUri}" name="predicateUri" role="input" />  
    <input type="hidden" value="${objectUri}" name="objectUri" role="input" />      
    <input type="hidden" value="create" name="cmd" role="input" />     
        
    <select id="typeOfNew" name="typeOfNew" role="selection">
    <#assign typeKeys = typesList?keys />
    <#list typeKeys as typeKey>
        <option value="${typeKey}" role="option"> ${typesList[typeKey]} </option>
    </#list>
    </select>
    
    <input type="submit" id="offerCreate" class="submit"  value="Add a new item of this type" role="button" />  
    <#if rangeOptionsExist  = false >
        <span class="or"> or </span>
        <a title="Cancel" class="cancel" href="${cancelUrl}">Cancel</a>
    </#if>
</form>          

               