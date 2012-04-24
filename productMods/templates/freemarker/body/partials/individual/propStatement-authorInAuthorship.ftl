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

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#authorInAuthorship. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
 
<#import "lib-sequence.ftl" as s>
<#import "lib-datetime.ftl" as dt>

<@showAuthorship statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAuthorship statement>

    <#local citationDetails>
        <#if statement.subclass??>
            <#if statement.subclass?contains("Article")>
                <#if statement.journal??>
                    <em>${statement.journal!}</em>.&nbsp;
                    <#if statement.volume?? && statement.startPage?? && statement.endPage??>
                        ${statement.volume!}:${statement.startPage!}-${statement.endPage!}.
                    <#elseif statement.volume?? && statement.startPage??>
                        ${statement.volume!}:${statement.startPage!}.
                    <#elseif statement.volume??>
                        ${statement.volume!}.
                    <#elseif statement.startPage?? && statement.endPage??>
                        ${statement.startPage!}-${statement.endPage!}.
                    <#elseif statement.startPage??>
                        ${statement.startPage!}.
                    </#if>
                </#if>
            <#elseif statement.subclass?contains("Chapter")>
                <#if statement.journal??>
                    <em>${statement.journal!}</em>.
                <#elseif statement.appearsIn??>
                    <em>${statement.appearsIn!}</em>.
                <#elseif statement.partOf??>
                    <em>${statement.partOf!}</em>.
                </#if>
                <#if statement.editor??>
                    Ed.&nbsp;${statement.editor!}.&nbsp;
                </#if>
                <#if statement.locale?? && statement.publisher??>
                    ${statement.locale!}:&nbsp;${statement.publisher!}.
                <#elseif statement.locale??>
                    ${statement.locale!}.
                <#elseif statement.publisher??>
                    ${statement.publisher!}.
                </#if>
                <#if statement.startPage?? && statement.endPage??>
                    ${statement.startPage!}-${statement.endPage!}.
                <#elseif statement.startPage??>
                    ${statement.startPage!}.
                </#if>
            <#elseif statement.subclass?contains("Book")>
                <#if statement.volume??>
                    Vol.&nbsp;${statement.volume!}.&nbsp;
                </#if>
                <#if statement.editor??>
                    Ed.&nbsp;${statement.editor!}.&nbsp;
                </#if>
                <#if statement.locale?? && statement.publisher??>
                    ${statement.locale!}:&nbsp;${statement.publisher!}.
                <#elseif statement.locale??>
                    ${statement.locale!}.
                <#elseif statement.publisher??>
                    ${statement.publisher!}.
                </#if>
            <#else>
                <#if statement.journal??>
                    <em>${statement.journal!}</em>.
                <#elseif statement.appearsIn??>
                    <em>${statement.appearsIn!}</em>.
                <#elseif statement.partOf??>
                    <em>${statement.partOf!}</em>.
                </#if>
                <#if statement.editor??>
                    Ed. ${statement.editor!}.&nbsp;
                </#if>
                <#if statement.startPage?? && statement.endPage??>
                    ${statement.startPage!}-${statement.endPage!}.
                <#elseif statement.startPage??>
                    ${statement.startPage!}.
                </#if>
            </#if>
        </#if>
    </#local>

    <#local resourceTitle>
        <#if statement.infoResource??>
            <#if citationDetails?has_content>
                <a href="${profileUrl(statement.uri("infoResource"))}"  title="resource name">${statement.infoResourceName}</a>.&nbsp;
            <#else>
                <a href="${profileUrl(statement.uri("infoResource"))}"  title="resource name">${statement.infoResourceName}</a>
            </#if>
        <#else>
            <#-- This shouldn't happen, but we must provide for it -->
            <a href="${profileUrl(statement.uri("authorship"))}" title="missing resource">missing information resource</a>
        </#if>
    </#local>

    ${resourceTitle} ${citationDetails} <@dt.yearSpan "${statement.dateTime!}" />

</#macro>
