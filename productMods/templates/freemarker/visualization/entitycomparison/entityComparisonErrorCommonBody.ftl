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

<#assign standardVisualizationURLRoot ="/visualization">
<#assign shortVisualizationURLRoot ="/vis">

<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">

<#if organizationLocalName?has_content >
    <#assign temporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/${otherVisType}/${organizationLocalName}'>
<#else>
    <#assign temporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/${otherVisType}/?uri=${organizationURI}'>
</#if>

<div id="error-container">

<h1 id="noPubsOrGrants-header">${organizationLabel}</h1>

    <h3 id="alternative-vis-info">${textForCurrentEntityComparisonType?capitalize} Temporal Graph 
        <span id="noPubsOrGrants-span">|&nbsp;<a  href="${temporalGraphURL}" title="view">view ${textForOtherEntityComparisonType} temporal graph</a></span>
    </h3>
    <div id="error-body">
        <p>This organization has neither sub-organizations nor people with 
        <span id="comparison-parameter-unavailable-label">${textForCurrentEntityComparisonType}</span> in the system. 
        Please visit the full ${organizationLabel} <a href="${organizationVivoProfileURL}" title="profile page">profile page</a> for a more complete overview.</p>
    </div>

</div>