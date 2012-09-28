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

<#-- Custom object property statement view for http://vivoweb.org/ontology/core#mailingAddress. 
    
     This template must be self-contained and not rely on other variables set for the individual page, because it
     is also used to generate the property statement during a deletion.  
 -->
<#import "lib-datetime.ftl" as dt>
<@showAdvisorIn statement />

<#-- Use a macro to keep variable assignments local; otherwise the values carry over to the
     next statement -->
<#macro showAdvisorIn statement>
    <#-- It's possible that advisorIn relationships were created before the custom form and only have
         an rdfs:label. So check to see if there's an advisee first. If not, just display the label.  -->
    <#local linkedIndividual>
        <#if statement.advisee??>
            <#if statement.degreeLabel?? || statement.dateTimeStart?? || statement.dateTimeEnd?? >
                <a href="${profileUrl(statement.uri("advisee"))}" title="advisee label">${statement.adviseeLabel!}</a>,
            <#else>
                <a href="${profileUrl(statement.uri("advisee"))}" title="advisee label">${statement.adviseeLabel!}</a>
            </#if>
            <#if statement.degreeLabel??>
                ${statement.degreeAbbr!statement.degreeLabel!} 
                <#if statement.dateTimeStart?? || statement.dateTimeEnd?? >&nbsp;candidate,<#else>&nbsp;candidate</#if>
            </#if>
        <#elseif statement.advisoryLabel??>
            <a href="${profileUrl(statement.uri("advisory"))}" title="advisory label">${statement.advisoryLabel!statement.localName}</a>
        </#if>
    </#local>

    ${linkedIndividual}    <@dt.yearIntervalSpan "${statement.dateTimeStart!}" "${statement.dateTimeEnd!}" />
 </#macro>