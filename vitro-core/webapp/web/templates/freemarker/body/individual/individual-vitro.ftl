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
<#if !labelCount??>
    <#assign labelCount = 0 >
</#if>

<#-- Default individual profile page template -->
<#--@dumpAll /-->
<section id="individual-intro" class="vcard" role="region">
    <#-- Image -->
    <#assign individualImage>
        <@p.image individual=individual 
                  propertyGroups=propertyGroups 
                  namespaces=namespaces 
                  editable=editable 
                  showPlaceholder="with_add_link" />
    </#assign>
    
    <#if ( individualImage?contains('<img class="individual-photo"') )>
        <#assign infoClass = 'class="withThumb"'/>
    </#if>
    
    <div id="photo-wrapper">${individualImage}</div>
    
    <section id="individual-info" ${infoClass!} role="region">
        <#include "individual-adminPanel.ftl">
        
        <header>
            <#if relatedSubject??>
                <h2>${relatedSubject.relatingPredicateDomainPublic} for ${relatedSubject.name}</h2>
                <p><a href="${relatedSubject.url}" title="return to subject">&larr; return to ${relatedSubject.name}</a></p>                
            <#else>                
                <h1 class="fn">
                    <#-- Label -->
                    <@p.label individual editable labelCount />

                    <#--  Most-specific types -->
                    <@p.mostSpecificTypes individual /><img id="uriIcon" title="${individual.uri}" class="middle" src="${urls.images}/individual/uriIcon.gif" alt="uri icon"/>
                </h1>
            </#if>
        </header>
                
    <#if individualProductExtension??>
        ${individualProductExtension}
    <#else>
            </section> <!-- individual-info -->
        </section> <!-- individual-intro -->
    </#if>

<#assign nameForOtherGroup = "other"> <#-- used by both individual-propertyGroupMenu.ftl and individual-properties.ftl -->

<#-- Property group menu -->
<#include "individual-propertyGroupMenu.ftl">

<#-- Ontology properties -->
<#include "individual-properties.ftl">

<#assign rdfUrl = individual.rdfUrl>

<#if rdfUrl??>
    <script>
        var individualRdfUrl = '${rdfUrl}';
    </script>
</#if>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />')}

${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/qtip/jquery.qtip-1.0.0-rc3.min.js"></script>',
                  '<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/imageUpload/imageUploadUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/individual/individualUriRdf.js"></script>')}
