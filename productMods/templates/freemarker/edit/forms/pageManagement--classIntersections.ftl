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

<#assign internalClassUri = editConfiguration.pageData.internalClassUri />
<section id="internal-class" role="region">
    <#if editConfiguration.pageData.internalClass?has_content>
        <#assign enableInternalClass = '' />
        <#assign disableClass = 'class="inline"' />
    <#else>
        <#assign enableInternalClass = '<p class="note">To enable this option, you must first select an <a href="${urls.base}/processInstitutionalInternalClass" title="institutional internal class">institutional internal class</a> for your instance</p>' />
        <#assign disableClass = 'class="disable inline" disabled="disabled"' />
    </#if>

                <input type="checkbox" ${disableClass} name="display-internalClass" value="${internalClassUri}" id="display-internalClass" <#if editConfiguration.pageData.internalClass?has_content && editConfiguration.pageData.isInternal?has_content>checked</#if> role="input" />
    <label ${disableClass} class="inline" for="display-internalClass">Only display <em> </em> within my institution</label>

    ${enableInternalClass}
</section>