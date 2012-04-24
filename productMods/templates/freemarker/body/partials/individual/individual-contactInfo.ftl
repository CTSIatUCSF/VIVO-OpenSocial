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

<#-- Contact info on individual profile page -->

<#-- Primary Email -->    
<@emailLinks "${core}primaryEmail" />

<#-- Additional Emails --> 
<@emailLinks "${core}email" />   
  
<#-- Phone --> 
<#assign phone = propertyGroups.pullProperty("${core}phoneNumber")!>
<#if phone?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
    <@p.addLinkWithLabel phone editable />
    <#if phone.statements?has_content> <#-- if there are any statements -->
        <ul id="individual-phone" role="list">
            <#list phone.statements as statement>
                <li role="listitem">                           
                   <img class ="icon-phone  middle" src="${urls.images}/individual/phoneIcon.gif" alt="phone icon" />${statement.value}
                    <@p.editingLinks "${phone.localName}" statement editable />
                </li>
            </#list>
        </ul>
    </#if>
</#if>

<#macro emailLinks property>
    <#assign email = propertyGroups.pullProperty(property)!>    
    <#if property == "${core}primaryEmail">
        <#local listId = "primary-email">
        <#local label = "Primary Email">
    <#else>
        <#local listId = "additional-emails">
        <#local label = "Additional Emails">
    </#if>     
    <#if email?has_content> <#-- true when the property is in the list, even if not populated (when editing) -->
        <@p.addLinkWithLabel email editable label/>
        <#if email.statements?has_content> <#-- if there are any statements -->
            <ul id="${listId}" class="individual-emails" role="list">
                <#list email.statements as statement>
                    <li role="listitem">
                        <img class ="icon-email middle" src="${urls.images}/individual/emailIcon.gif" alt="email icon" />
                        <a class="email" href="mailto:${statement.value}" title="email">${statement.value}</a>
                        <@p.editingLinks "${email.localName}" statement editable />
                    </li>
                </#list>
            </ul>
        </#if>
    </#if>
</#macro>