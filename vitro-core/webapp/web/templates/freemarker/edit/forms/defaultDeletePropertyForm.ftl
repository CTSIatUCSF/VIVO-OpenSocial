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

<form class="deleteForm" action="${editConfiguration.mainEditUrl}" method="get"> 
    <h3 class="delete-entry">Delete this entry?</h3>
          
    <label for="delete"></label>
    
    <input type="hidden" name="subjectUri"   value="${editConfiguration.subjectUri}"/>
    <input type="hidden" name="predicateUri" value="${editConfiguration.predicateUri}"/>
    <input type="hidden" name="cmd"          value="delete"/>
    <input type="hidden" name="editKey" value="${editConfiguration.editKey}"/>
    <#if editConfiguration.dataProperty = true>
        <input type="hidden" name="datapropKey" value="${editConfiguration.datapropKey}" />
        <input type="submit" id="delete" value="Delete" role="button "/>
    </#if>
    
    <#--The original jsp included vinput tag with cancel=empty string for case where both select from existing
    and offer create new option are true below so leaving as Cancel for first option but not second below-->
    <#if editConfiguration.objectProperty = true> 
        <input type="hidden" name="objectUri" value="${editConfiguration.objectUri}"/>    
    
        <#if editConfiguration.propertySelectFromExisting = false && editConfiguration.propertyOfferCreateNewOption = false>
            <p>
                <input type="submit" id="delete" value="Delete" role="button "/>
                <span class="or"> or </span>
                <a title="Cancel" href="${editConfiguration.cancelUrl}">Cancel</a>
            </p> 
        </#if>
        
        <#if editConfiguration.propertySelectFromExisting = true || editConfiguration.propertyOfferCreateNewOption = true>
            <p>
                <input type="submit" id="delete" value="Delete" role="button "/>
            </p>      
        </#if>
    </#if>
</form>
