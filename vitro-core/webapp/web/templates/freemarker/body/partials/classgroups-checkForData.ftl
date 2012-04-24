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

<#assign populatedClassGroups = 0 />

<#list classGroups as classGroup>
    <#-- Check to see if any of the class groups have individuals -->
    <#if (classGroup.individualCount > 0)>
        <#assign populatedClassGroups = populatedClassGroups + 1 />
    </#if>
</#list>
<#if (populatedClassGroups == 0)>
    <#assign noData = true />
<#else>
    <#assign noData = false />
</#if>

<#assign noDataNotification>    
    <#if user.loggedIn>
        <#if user.authorizedToRebuildSearchIndex>
            <span class="contentNote">
                <h4>Expecting content?</h4>
                <p>Try <a title="Rebuild the search index for this site" href="${urls.base}/SearchIndex">rebuilding the search index</a>.</p>
            </span>
        </#if>
    <#else>
        <span class="contentNote">
            <p>Please <a href="${urls.login}" title="log in to manage this site">log in</a> to manage content.</p>
        </span>
    </#if>
    
    <h3>There is currently no content in the system, or you need to create class groups and assign your classes to them.</h3>
    
    <#if user.loggedIn && user.hasSiteAdminAccess>
        <p>You can <a href="${urls.siteAdmin}" title="Manage content">add content and manage this site</a> from the Site Administration page.</p>
    </#if>
</#assign>