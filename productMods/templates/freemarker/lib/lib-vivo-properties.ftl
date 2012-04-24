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

<#-- Macros related to the display of vivo ontology properties -->

<#import "lib-properties.ftl" as p>

<#assign core = "http://vivoweb.org/ontology/core#">

<#-- Display preferredTitle if it exists; otherwise display mostSpecificTypes -->
<#macro displayTitle individual>
    <#if individual.preferredTitle?has_content>
        <span class="display-title">${individual.preferredTitle}</span>
    <#else>
        <@p.mostSpecificTypes individual />
    </#if>
</#macro>

<#-- core:webpage
     
     Note that this macro has a side-effect in the call to propertyGroups.pullProperty().
-->

<#macro webpages propertyGroups editable linkListClass="individual-urls">
    <#local webpage = propertyGroups.pullProperty("${core}webpage")!>

    <#if webpage?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <nav role="navigation">
            <#local label = "Web Pages">  
            <@p.addLinkWithLabel webpage editable label />           
            <#if webpage.statements?has_content> <#-- if there are any statements -->
                <#include "lib-vivo-property-webpage.ftl">
            </#if>
        </nav>
    </#if>
</#macro>
