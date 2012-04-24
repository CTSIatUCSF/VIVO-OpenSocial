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

<#-- Template for creating an account for the first time an external user logs in. -->

<h3>First time log in</h3>

    <#if errorEmailIsEmpty??>
        <#assign errorMessage = "You must supply an email address." />
    </#if>
    
    <#if errorEmailInUse??>
        <#assign errorMessage = "An account with that email address already exists." />
    </#if>
    
    <#if errorEmailInvalidFormat??>
        <#assign errorMessage = "'${emailAddress}' is not a valid email address." />
    </#if>
    
    <#if errorFirstNameIsEmpty??>
        <#assign errorMessage = "You must supply a first name." />
    </#if>
    
    <#if errorLastNameIsEmpty??>
        <#assign errorMessage = "You must supply a last name." />
    </#if>
    
    <#if errorMessage?has_content>
        <section id="error-alert" role="alert">
            <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon"/>
            <p>${errorMessage}</p>
        </section>
    </#if>

<section id="first-time-login" role="region">
    <p>Please provide your contact information to finish creating your account.</p>

    <form method="POST" action="${formUrls.firstTimeExternal}" class="customForm" role="my account">
        <input type="hidden" name="externalAuthId" value="${externalAuthId}" role="input" />
        <input type="hidden" name="afterLoginUrl" value="${afterLoginUrl}" role="input" />
    
        <label for="first-name">First name<span class="requiredHint"> *</span></label> 
        <input type="text" name="firstName" value="${firstName}" id="first-name" role="input" />

        <label for="last-name">Last name<span class="requiredHint"> *</span></label> 
        <input type="text" name="lastName" value="${lastName}" id="last-name" role="input" />

        <label for="email-address">Email address<span class="requiredHint"> *</span></label>
        <input type="text" name="emailAddress" value="${emailAddress}" id="email-address" role="input" />

        <#if emailIsEnabled??>
            <p class="note">
                Note: An email will be sent to the address entered above notifying 
                that an account has been created.
            </p>
        </#if>

        <p><input type="submit" name="submit" value="Create account" class="submit"/> or <a class="cancel" href="${urls.home}" title="cancel">Cancel</a></p>
    </form>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/account/account.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}