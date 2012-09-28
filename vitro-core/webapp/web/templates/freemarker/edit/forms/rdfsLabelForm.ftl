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

<#assign actionText = "Add new" />
<#if editConfiguration.dataPropertyStatement?has_content>
    <#assign actionText = "Edit"/>
</#if>
<#assign submitLabel>${actionText} label</#assign>

<h2>${actionText} <em>label</em> for ${editConfiguration.subjectName}</h2>

<#assign literalValues = "${editConfiguration.dataLiteralValuesAsString}" />

<form class="editForm" action = "${submitUrl}" method="post">
    <input type="text" name="${editConfiguration.varNameForObject}" id="label" size="70" value="${literalValues}"} role="input"/>
    <input type="hidden" name="editKey" id="editKey" value="${editKey}" role="input"/>
    <input type="hidden" name="vitroNsProp" value="true" role="input"/>
    
    <p class="submit">
        <input type="submit" id="submit" value="${submitLabel}" role="input"/>
        or <a href="${cancelUrl}" class="cancel" title="cancel">Cancel</a>
    </p>
    
</form>

<#--The original jsp included a delete form for deleting rdfs label.  
If required, deletion can be supported but it does not appear that is required currently. 
-->