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

<#-- Template for adding a user account -->

<h3>Reset your Password</h3>

<p>Please enter your new password for ${userAccount.emailAddress}</p>

    <#if errorPasswordIsEmpty??>
        <#assign errorMessage = "No password supplied." />
    </#if>
    
    <#if errorPasswordIsWrongLength??>
        <#assign errorMessage = "Password must be between ${minimumLength} and ${maximumLength} characters." />
    </#if>
    
    <#if errorPasswordsDontMatch??>
        <#assign errorMessage = "Passwords do not match." />
    </#if>
    
    <#if errorMessage?has_content>
        <section id="error-alert" role="alert">
            <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon"/>
            <p>${errorMessage}</p>
        </section>
    </#if>

<section id="reset-password" role="region">
    <form method="POST" action="${formUrls.resetPassword}" class="customForm" role="create password">
        <input type="hidden" name="user" value="${userAccount.emailAddress}" />
        <input type="hidden" name="key"  value="${userAccount.passwordLinkExpiresHash}" />
        
        <label for="new-password">Password<span class="requiredHint"> *</span></label>
        <input type="password" name="newPassword" value="${newPassword}" id="new-password" role="input" />
            
        <p class="note">Minimum of ${minimumLength} characters in length.</p>

        <label for="confirm-password">Confirm Password<span class="requiredHint"> *</span></label>
        <input type="password" name="confirmPassword" value="${confirmPassword}" id="confirm-password" role="input" />

        <p><input type="submit" name="submit" value="Save changes" class="submit" /></p>

        <p class="requiredHint">* required fields</p>
    </form>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/account/account.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}