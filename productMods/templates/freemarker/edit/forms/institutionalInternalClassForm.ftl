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
Institutional Internal Class Form 
To be associated later (upon completion of N3 Refactoring) with 
edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.InstitutionalInternalClassForm.
-->

<h2>Institutional Internal Class</h2>

<section id="introMessage" role="region">
    <p>This class will be used to designate those individuals internal to your institution.</p>
    <p>This will allow you to limit the individuals displayed on your menu pages (People, Research, etc.) 
    to only those within your institution.</p>
</section>

<section role="region">
    <form method="POST" action="${formUrl}" class="customForm">
        <input type="hidden" name="submitForm" id="submitForm" value="true" />
        
        <#--If no local ontologies, display message for user to create a local ontology-->
        <#if ontologiesExist = false>
        <section id="noLocalOntologyExists" role="region">
            <h4>There are currently no recognized local ontologies.</h4>
            <#if defaultNamespace?has_content && defaultNamespace?contains("individual/")>
                <#assign localOntologyNamespace = defaultNamespace?replace("individual/", "") />
                <p>In order for a local ontology to be recognized here, its namespace URI must follow this pattern:</p>
                <blockquote>${localOntologyNamespace}ontology/<em>yourOntologyName</em></blockquote>
            </#if>
            <p>Please create a <a href='${urls.base}/editForm?controller=Ontology' title="new local ontology">new local ontology</a> and then return here to define the institutional internal class.</p>
        </section>
        
        <#--else if local ontologies exist and local classes exist, show drop-down of local classes-->
        <#elseif useExistingLocalClass?has_content> 
        <section id="existingLocalClass" role="region">
            <#--Populated based on class list returned-->
            <label for="existingLocalClasses">Select an existing class from a local extension</label>
       
            <select id="existingLocalClasses" name="existingLocalClasses" role="combobox"<strong></strong>>
                <#assign classUris = existingLocalClasses?keys />
                
                <#--If internal class exists, check against value in drop-down and select option-->
                <#--<option value="" role="option">Select one</option>-->
                <#list classUris as localClassUri>
                    <option value="${localClassUri}" role="option" <#if existingInternalClass = localClassUri>selected</#if> >${existingLocalClasses[localClassUri]}</option>
                </#list>
            </select>
            
            <p>Can't find an appropriate class? Create a <a href="${formUrl}?cmd=createClass" title="create new class">new one</a>.</p>
        </section>

        <#--if parameter to create new class passed or if there are local ontologies but no local classes, show create new class page-->
        <#elseif createNewClass?has_content>
        <section id="createNewLocalClass" role="region">
            <h3>Create a new class</h3>
        
            <label for="localClassName">Name<span class="requiredHint"> *</span></label>
            <input type="text" id="localClassName" name="localClassName" value="" role="input" />
            <p class="note">use capitals for the first letter of each word</p>
    
            <#--If more than one local namespace, generate select-->
            <#if multipleLocalNamespaces = true>
                <label for="existingLocalNamespace">Local Namespace<span class="requiredHint"> *</span></label>
                
                <select id="existingLocalNamespaces" name="existingLocalNamespaces" role="combobox">
                    <#assign namespaceUris = existingLocalNamespaces?keys /> 
                    <#list namespaceUris as existingNamespace>
                        <option value="${existingNamespace}" role="option">${existingLocalNamespaces[existingNamespace]}</option>
                    </#list>
            </select>
            <#else>
            <input type="hidden" id="existingLocalNamespaces" name="existingLocalNamespaces" value="${existingLocalNamespace}" />
            </#if>
        </section>
    
        <#--this case is an error case-->
        <#else>
            <p>Problematic section as above should all have been handled</p>
        </#if>

        <#--only show submit and cancel if ontologies exist-->
        <#if ontologiesExist = true>
            <input type="submit" name="submit-internalClass" value="${submitAction}" class="submit" /> or <a class="cancel" href="${cancelUrl}" title="Cancel">Cancel</a>
        </#if>
    </form>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/institutionalInternalClass.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />')}