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

<#-- 
    Template for the page that displays the StartupStatus on request. 
    
    Unlike the "raw" page, this one assumes that the usual Freemarker framework is in place.
-->

<#macro statusItem item>
    <#if item.level = "FATAL">
        <#assign color = "error" >
    <#elseif item.level = "WARNING">
        <#assign color = "warning" >
    <#elseif item.level = "INFO">
        <#assign color = "info" >
    <#elseif item.level = "NOT_EXECUTED">
        <#assign color = "not_executed" >
    <#else>
        <#assign color = "" >
    </#if>

    <li class="item ${color}" role="listitem">
        <h4>${item.level}: ${item.shortSourceName}</h4>
        
        <ul class="item-spec" role="navigation">
            <li role="listitem">${item.message}</li>
            <li role="listitem">${item.sourceName}</li>
            <#if item.cause?has_content>
            <li role="listitem"><pre>${item.cause}</pre></li>
            </#if>
        </ul>
    </li>
</#macro>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/startupStatus.css" />')}

<#if status.errorItems?has_content>
    <h2>Fatal error</h2>
    
    <p>${applicationName} detected a fatal error during startup.</p>
   
    <ul id="startup-trace" cellspacing="0" class="trace" role="navigation">
    <#list status.errorItems as item>
      <@statusItem item=item />
    </#list>
    </ul>
</#if>

<#if status.warningItems?has_content>
    <h2>Warning</h2>
    
    <p>${applicationName} issued warnings during startup.</p>
    
    <ul id="startup-trace" cellspacing="0" class="trace" role="navigation"><#list status.warningItems as item>
      <@statusItem item=item />
    </#list>
    </ul>
</#if>

<h2>Startup trace</h2>

<p>The full list of startup events and messages.</p>

<ul id="startup-trace" cellspacing="0" class="trace" role="navigation">
      <#list status.statusItems as item>
          <@statusItem item=item />
      </#list>
</ul>
