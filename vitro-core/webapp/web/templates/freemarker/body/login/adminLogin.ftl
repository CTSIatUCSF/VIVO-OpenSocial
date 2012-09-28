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

<#-- Template for login using internal vitro account (even when external auth is enabled). Accessible at /admin/login -->

<section id="internalLogin" role="region">
    <h2>Internal Login</h2>

    <#if errorNoEmail??>
        <#assign errorMessage = "No email supplied." />
    </#if>
    
    <#if errorNoPassword??>
        <#assign errorMessage = "No password supplied." />
    </#if>
    
    <#if errorLoginDisabled??>
        <#assign errorMessage = "User logins are temporarily disabled while the system is being maintained." />
    </#if>
    
    <#if errorLoginFailed??>
        <#assign errorMessage = "Email or Password was incorrect." />
    </#if>
    
    <#if errorNewPasswordWrongLength??>
        <#assign errorMessage = "Password must be between 6 and 12 characters." />
    </#if>
    
    <#if errorNewPasswordsDontMatch??>
        <#assign errorMessage = "Passwords do not match." />
    </#if>
    
    <#if errorNewPasswordMatchesOld??>
        <#assign errorMessage = "Your new password must be different from your existing password." />
    </#if>
    
    <#if errorMessage?has_content>
        <section id="error-alert" role="alert">
            <img src="${urls.images}/iconAlert.png" width="24" height="24" alert="Error alert icon"/>
            <p>${errorMessage}</p>
        </section>
    </#if>
    
    <#if !newPasswordRequired??>
        <p>Enter the email address and password for your internal Vitro account.</p>
    <#else>
        <p>You must change your password to log in.</p>
    </#if>

	<form method="post" action="${controllerUrl}">
        <#if newPasswordRequired??>
            <label for="newPassword">New Password</label>
            <input name="newPassword" id="newPassword" class="text-field" type="password" required autofocus />
            
            <p class="password-note">Minimum of 6 characters in length.</p>
            
            <label for="confirmPassword">Confirm Password</label>
            <input id="confirmPassword" name="confirmPassword" class="text-field" type="password" required />
            
            <input id="email" name="email" type="hidden" value="${email!}" />
            <input id="password" name="password" type="hidden" value="${password!}" />
        <#else>
            <label for="email">Email</label>
            <input id="email" name="email" class="text-field focus" type="text" value="${email!}" required autofocus />

        	<label for="password">Password</label>
            <input id="password" name="password" class="text-field" type="password" required />
        </#if>

		<p class="submit"><input name="loginForm" type="submit" class="green button" value="Log in"/></p>
	</form>
</section>