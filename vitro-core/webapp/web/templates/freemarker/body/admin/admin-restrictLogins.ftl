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

<#-- Template for restricting (or opening) access to logins. -->

<h2>Restrict Logins</h2>
    <#if messageAlreadyRestricted??>
        <#assign errorMessage = "Logins are already restricted." />
    </#if>
    
    <#if messageAlreadyOpen??>
        <#assign errorMessage = "Logins are already not restricted." />
    </#if>
    
    <#if errorMessage?has_content>
        <section id="error-alert" role="alert">
            <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon" />
            <p>${errorMessage}</p>
        </section>
    </#if>

    <#if messageRestricting??>
        <#assign successMessage = "Logins are now restricted." />
    </#if>
    
    <#if messageOpening??>
        <#assign successMessage = "Logins are no longer restricted." />
    </#if>

    <#if successMessage?has_content>
        <section class="success">
            <p>${successMessage}</p>
        </section>
    </#if>


<section id="restrict-login" role="region">
    <#if restricted == true>
        <h4>Logins are restricted</h4>
        <p><a href="${openUrl}" title="Remove Restrictions">Remove Restrictions</a></p>
    <#else>
        <h4>Logins are open to all</h4>
        <p><a href="${restrictUrl}" title="Restrict Logins">Restrict Logins</a></p>
    </#if>
</section>
