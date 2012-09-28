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

<#-- Template viewing the authorization mechanisms: current identifiers, factories, policies, etc. -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/showAuth.css" />')}

<h2>Authorization Info</h2>

<section id="show-auth" role="region">
    <h4>Current user</h4>
    <table summary="Information about the current user">
    <#if currentUser?has_content>
            <tr><th>URI:</th><td>${currentUser.uri}</td></tr>
            <tr><th>First name:</th><td>${currentUser.firstName}</td></tr>
            <tr><th>Last name:</th><td>${currentUser.lastName}</td></tr>
            <tr><th>Email:</th><td>${currentUser.emailAddress}</td></tr>
            <tr><th>External Auth ID:</th><td>${currentUser.externalAuthId}</td></tr>
            <tr><th>Login count:</th><td>${currentUser.loginCount}</td></tr>
            <#list currentUser.permissionSetUris as role>
                <tr><th>Role:</th><td>${role}</td></tr>
            </#list>
    <#else>
        <tr><th>Not logged in</th></tr>
    </#if>
    </table>
   
    <h4>Identifiers:</h4>
    <table summary="Identifiers">
        <#list identifiers as identifier>
            <tr>
                <td>${identifier}</td>
            </tr>
        </#list>
    </table>

    <h4>
        AssociatedIndividuals: 
        <#if matchingProperty??>
            (match by ${matchingProperty})
        <#else>
            (matching property is not defined)
        </#if>
    </h4>
    <table summary="Associated Individuals">
        <#if associatedIndividuals?has_content>
            <#list associatedIndividuals as associatedIndividual>
                <tr>
                    <td>${associatedIndividual.uri}</td>
                    <#if associatedIndividual.editable>
                        <td>May edit</td>
                    <#else>
                        <td>May not edit</td>
                    </#if>
                </tr>
            </#list>
        <#else>
            <tr><td>none</td></tr>
        </#if>
    </table>

    <h4>Identifier factories:</h4>
    <table summary="Active Identifier Factories">
        <#list factories as factory>
            <tr>
                <td>${factory}</td>
            </tr>
        </#list>
    </table>

    <h4>Policies:</h4>
    <table summary="Policies" width="100%">
        <#list policies as policy>
            <tr>
                <td>${policy}</td>
            </tr>
        </#list>
    </table>

    <h4>Authenticator:</h4>
    <table summary="Authenticator" width="100%">
        <tr>
            <td>${authenticator}</td>
        </tr>
    </table>
</section>
