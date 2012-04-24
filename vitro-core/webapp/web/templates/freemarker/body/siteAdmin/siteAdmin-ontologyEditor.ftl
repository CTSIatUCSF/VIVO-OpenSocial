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

<#-- Template for Site Administration Ontology Editor -->

<#if ontologyEditor?has_content>
    <section class="pageBodyGroup" role="region">
        <h3>Ontology Editor</h3>
        
        <#if ontologyEditor.pellet?has_content>
            <div class="notice">
                <p>${ontologyEditor.pellet.error}</p>
                <#if ontologyEditor.pellet.explanation?has_content>
                    <p>Cause: ${ontologyEditor.pellet.explanation}</p>
                </#if>
            </div>
        </#if>
        
        <ul role="navigation">
            <li role="listitem">
                <a href="${ontologyEditor.urls.ontologies}" title="Ontology list">Ontology list</a></h4>
            </li>
        </ul>
    
        <h4>Class Management</h4>
        
        <ul role="navigation">
            <li role="listitem"><a href="${ontologyEditor.urls.classHierarchy}" title="Class hierarchy">Class hierarchy</a></li>
            <li role="listitem"><a href="${ontologyEditor.urls.classGroups}" title="Class groups">Class groups</a></li>
        </ul>
        
        <h4>Property Management</h4>
        
        <ul role="navigation">
            <li role="listitem"><a href="${ontologyEditor.urls.objectPropertyHierarchy}" title="Object property hierarchy">Object property hierarchy</a></li>
            <li role="listitem"><a href="${ontologyEditor.urls.dataPropertyHierarchy}" title="Data property hierarchy">Data property hierarchy</a></li>
            <li role="listitem"><a href="${ontologyEditor.urls.propertyGroups}" title="Property groups">Property groups</a></li>
        </ul>
        
    </section>
</#if>