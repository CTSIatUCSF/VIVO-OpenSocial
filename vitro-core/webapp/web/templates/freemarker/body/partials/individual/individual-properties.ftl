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

<#-- Template for property listing on individual profile page -->

<#import "lib-properties.ftl" as p>
<#assign subjectUri = individual.controlPanelUrl()?split("=") >
<#list propertyGroups.all as group>
    <#assign groupName = group.getName(nameForOtherGroup)>
    <#assign verbose = (verbosePropertySwitch.currentValue)!false>
    
    <section class="property-group" role="region">
        <nav class="scroll-up" role="navigation">
            <a href="#branding" title="scroll up">
                <img src="${urls.images}/individual/scroll-up.gif" alt="scroll to property group menus" />
            </a>
        </nav>
        
        <#-- Display the group heading --> 
        <#if groupName?has_content>
    		<#--the function replaces spaces in the name with underscores, also called for the property group menu-->
        	<#assign groupNameHtmlId = p.createPropertyGroupHtmlId(groupName) >
            <h2 id="${groupNameHtmlId}">${groupName?capitalize}</h2>
        <#else>
            <h2 id="properties">Properties</h2>
        </#if>
        
        <#-- List the properties in the group -->
        <#list group.properties as property>
            <article class="property" role="article">
                <#-- Property display name -->
                <#if property.localName == "authorInAuthorship" && editable  >
                    <h3 id="${property.localName}">${property.name} <@p.addLink property editable /> <@p.verboseDisplay property /> 
                        <a id="managePubLink" class="manageLinks" href="${urls.base}/managePublications?subjectUri=${subjectUri[1]!}" title="manage publications" <#if verbose>style="padding-top:10px"</#if> >
                            manage publications
                        </a>
                    </h3>
                <#elseif property.localName == "hasResearcherRole" && editable  >
                <h3 id="${property.localName}">${property.name} <@p.addLink property editable /> <@p.verboseDisplay property /> 
                    <a id="manageGrantLink" class="manageLinks" href="${urls.base}/manageGrants?subjectUri=${subjectUri[1]!}" title="manage grants & projects" <#if verbose>style="padding-top:10px"</#if> >
                        manage grants & projects
                    </a>
                </h3>
                <#elseif property.localName == "organizationForPosition" && editable  >
                <h3 id="${property.localName}">${property.name} <@p.addLink property editable /> <@p.verboseDisplay property /> 
                    <a id="managePeopleLink" class="manageLinks" href="${urls.base}/managePeople?subjectUri=${subjectUri[1]!}" title="manage people" <#if verbose>style="padding-top:10px"</#if> >
                        manage affiliated people
                    </a>
                </h3>
                <#else>
                    <h3 id="${property.localName}">${property.name} <@p.addLink property editable /> <@p.verboseDisplay property /> </h3>
                </#if>
                <#-- List the statements for each property -->
                <ul class="property-list" role="list" id="${property.localName}List">
                    <#-- data property -->
                    <#if property.type == "data">
                        <@p.dataPropertyList property editable />
                    <#-- object property -->
                    <#else>
                        <@p.objectProperty property editable />
                    </#if>
                </ul>
            </article> <!-- end property -->
        </#list>
    </section> <!-- end property-group -->
</#list>
