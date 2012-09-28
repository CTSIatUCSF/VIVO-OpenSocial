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

<#-- Menu management page (uses individual display mechanism) -->

<#include "individual-setup.ftl">

<#assign hasElement = propertyGroups.pullProperty("${namespaces.display}hasElement")!>

<#assign addNewMenuItemUrl = "${urls.base}/menuManagementController?cmd=add" >

<#if hasElement?has_content>
    <script type="text/javascript">
        var menuItemData = [];
    </script>
    
    <h3>Menu Ordering</h3>
    
    <#-- List the menu items -->
    <ul class="menuItems">
        <#list hasElement.statements as statement>
            <li class="menuItem"><#include "${hasElement.template}"> <span class="controls"><!--p.editingLinks "hasElement" statement editable /--></span></li>
        </#list>
    </ul>
    
    <#-- Link to add a new menu item -->
    <#if editable>
        <#if addNewMenuItemUrl?has_content>
        <form id="pageListForm" action="${urls.base}/editRequestDispatch" method="get">
            <input type="hidden" name="typeOfNew" value="http://vitro.mannlib.cornell.edu/ontologies/display/1.1#Page">              
            <input type="hidden" name="switchToDisplayModel" value="1">
            <input type="hidden" name="editForm" value="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.ManagePageGenerator" role="input">
       		<input type="hidden" name="addMenuItem" value="true" />
       	<input id="submit" value="Add new menu page" role="button" type="submit" >
        
        </form>
            <br />
            <p class="note">Refresh page after reordering menu items</p>
        </#if>
    </#if>
    
    ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual.css" />',
                      '<link rel="stylesheet" href="${urls.base}/css/individual/menuManagement-menuItems.css" />')}
                      
    ${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}
    
    <#assign positionPredicate = "${namespaces.display}menuPosition" />
    
    <script type="text/javascript">
        // <#-- We need the controller to provide ${reorderUrl}. This is where ajax request will be sent on drag-n-drop events. -->
        var menuManagementData = {
            reorderUrl: '${reorderUrl}',
            positionPredicate: '${positionPredicate}'
        };
    </script>
    
    ${scripts.add('<script type="text/javascript" src="${urls.base}/js/individual/menuManagement.js"></script>')}
<#else>
    <p id="error-alert">There was an error in the system. The display:hasElement property could not be retrieved.</p>
</#if>