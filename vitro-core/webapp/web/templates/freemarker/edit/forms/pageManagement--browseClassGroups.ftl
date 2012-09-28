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
<#--Browse Class Groups Section-->
<#-----------Variable assignment-------------->
<#--Requires Menu action be defined in parent template-->

<#assign classGroup = pageData.classGroup />
<#assign classGroups = pageData.classGroups />
<#-- some additional processing here which shows or hides the class group selection and classes based on initial action-->
<#assign existingClassGroupStyle = " " />
<#assign selectClassGroupStyle = 'class="hidden"' />
<#-- Reveal the class group and hide the class selects if adding a new menu item or editing an existing menu item with an empty class group (no classes)-->
<#-- Menu action needs to be sent from  main template-->
<#if menuAction == "Add" || !classGroup?has_content>
    <#assign existingClassGroupStyle = 'class="hidden"' />
    <#assign selectClassGroupStyle = " " />
</#if>


<#--HTML Portion-->
 <section id="browseClassGroup" class="contentSectionContainer">
                       
                <section id="selectContentType" name="selectContentType" ${selectClassGroupStyle} role="region">     
                    
                    <label for="selectClassGroup">Class Group<span class="requiredHint"> *</span></label>
                    <select name="selectClassGroup" id="selectClassGroup" role="combobox">
                        <option value="-1" role="option">Select one</option>
                        <#list classGroups as aClassGroup>
                            <option value="${aClassGroup.URI}"  role="option">${aClassGroup.publicName}</option>
                        </#list>
                    </select>
                </section>
                
                
                <section id="classesInSelectedGroup" name="classesInSelectedGroup" ${existingClassGroupStyle}>
                    <#-- Select classes in a class group -->    
                    <p id="selectClassesMessage" name="selectClassesMessage">Select content to display<span class="requiredHint"> *</span></p>

                    <#include "pageManagement--classIntersections.ftl">

                    <ul id="selectedClasses" name="selectedClasses" role="menu">
                        <#--Adding a default class for "ALL" in case all classes selected-->
                        <li class="ui-state-default" role="menuitem">
                            <input type="checkbox" name="allSelected" id="allSelected" value="all" checked="checked" />
                            <label class="inline" for="All"> All</label>
                        </li>
                        <#list classGroup as classInClassGroup>
                        <li class="ui-state-default" role="menuitem">
                            <input type="checkbox" id="classInClassGroup" name="classInClassGroup" value="${classInClassGroup.URI}" checked="checked" />
                            <label class="inline" for="${classInClassGroup.name}"> ${classInClassGroup.name}</label>
                            <span class="ui-icon-sortable"></span> 
                        </li>
                        </#list>
                    </ul><br />
                    <input  type="button" id="doneWithContent" class="doneWithContent" name="doneWithContent" value="Save this content" />
                    <#if menuAction == "Add">
                        <span id="cancelContent"> or <a class="cancel" href="javascript:"  id="cancelContentLink" >Cancel</a></span>
                    </#if>
                </section>
            </section>
 <#--Include JavaScript specific to the types of data getters related to this content-->           
 <#include "pageManagement--browseClassGroupsScripts.ftl">           