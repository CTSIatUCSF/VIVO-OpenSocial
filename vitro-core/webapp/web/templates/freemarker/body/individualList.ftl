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

<#-- List individuals in the requested class. -->

<#import "lib-list.ftl" as l>

<#include "individualList-checkForData.ftl">

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/browseIndex.css" />')}

<section class="individualList">
    <h2>${title} 
        <#if rdfUrl?has_content>
            <span class="rdfLink"><a class="icon-rdf" href="${rdfUrl}" title="View the ${title} list in RDF format">RDF</a></span>
        </#if>
    </h2>
    <#if subtitle?has_content>
        <h4>${subtitle}</h4>
    </#if>
    
    <#if (!noData)>
        <#if errorMessage?has_content>
            <p>${errorMessage}</p>
        <#else>
            <#assign pagination>
                <#if (pages?has_content && pages?size > 1)>
                    pages:
                    <ul class="pagination">
                        <#list pages as page>
                            <#if page.selected>
                                <li class="selectedNavPage">${page.text}</li>
                            <#else>
                                <#-- RY Ideally the urls would be generated by the controller; see search-pagedResults.ftl -->
                                <li><a href="${urls.base}/individuallist?${page.param}&vclassId=${vclassId?url}" title="page text">${page.text}</a></li>
                            </#if>
                        </#list>
                    </ul>
                </#if>
            </#assign>
            
            ${pagination}
            
            <ul>
                <#list individuals as individual>
                    <li>       
                        <@shortView uri=individual.uri viewContext="index" />
                    </li>
                </#list>
            </ul>
            
            ${pagination}
        </#if>
    <#else>
        ${noDataNotification}
    </#if>
</section> <!-- .individualList -->