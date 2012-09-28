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

<#-- Template for Site Administration site configuration panel -->

<#if siteConfig?has_content>
    <section class="pageBodyGroup" role="region">
        <h3>Site Configuration</h3>
        
        <ul role="navigation">
            <#if siteConfig.internalClass?has_content>
                <li role="listitem"><a href="${siteConfig.internalClass}" title="Institutional internal class">Institutional internal class</a></li>
            </#if>     
            
            <#if siteConfig.manageProxies?has_content>
                <li role="listitem"><a href="${siteConfig.manageProxies}" title="Manage profile editing">Manage profile editing</a></li>
            </#if>  
            
            <#if siteConfig.pageManagement?has_content>
                <li role="listitem"><a href="${siteConfig.pageManagement}" title="Page management">Page management</a></li>
            </#if>        
            
            <#if siteConfig.menuManagement?has_content>
                <li role="listitem"><a href="${siteConfig.menuManagement}" title="Menu ordering">Menu ordering</a></li>
            </#if>      
            
            <#if siteConfig.restrictLogins?has_content>
                <li role="listitem"><a href="${siteConfig.restrictLogins}" title="Restrict Logins">Restrict Logins</a></li>
            </#if>
            
            <#if siteConfig.siteInfo?has_content>
                <li role="listitem"><a href="${siteConfig.siteInfo}" title="Site information">Site information</a></li>
            </#if>
            
            <#if siteConfig.startupStatus?has_content>
                <li role="listitem">
                    <a href="${siteConfig.startupStatus}" title="Startup status">Startup status</a>
                    <#if siteConfig.startupStatusAlert>
                        <img id="alertIcon" src="${urls.images}/iconAlert.png" width="20" height="20" alert="Error alert icon" />
                    </#if>
                </li>
            </#if>   
            
             <#if siteConfig.userAccounts?has_content>
                <li role="listitem"><a href="${siteConfig.userAccounts}" title="User accounts">User accounts</a></li>
             </#if>        
        </ul>
    </section>
</#if>
