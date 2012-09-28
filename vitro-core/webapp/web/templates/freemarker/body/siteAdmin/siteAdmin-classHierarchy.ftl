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

<section role="region">
    
    <h2>${pageTitle!}</h2>

    <#if !displayOption?has_content>
        <#assign displayOption = "asserted">
    </#if>
    <form name="classHierarchyForm" id="classHierarchyForm" action="showClassHierarchy" method="post" role="classHierarchy">
        <label id="displayOptionLabel" class="inline">Display Options</label>
        <select id="displayOption" name="displayOption">
            <option value="asserted" <#if displayOption == "asserted">selected</#if> >Asserted Class Hierarchy</option>
            <option  value="inferred" <#if displayOption == "inferred">selected</#if> >Inferred Class Hierarchy</option>
            <option value="all" <#if displayOption == "all">selected</#if> >All Classes</option>
            <option value="group" <#if displayOption == "group">selected</#if> >Classes by Class Group</option>
        </select>
        <input id="addClass" value="Add New Class" class="form-button" type="submit" />
        <#if displayOption == "group">
                <input type="submit" id="addGroup" class="form-button" value="Add New Group"/>
        </#if>
    </form>
        
    <#if displayOption == "group">
        <div id="expandLink"><span id="expandAll" ><a href="javascript:" title="hide/show subclasses">hide subclasses</a></span></div>
    <#else>
        <div id="expandLink"><span id="expandAll" ><a href="#" title="expand all">expand all</a></span></div>
    </#if>
    <section id="container">

    </section>
</section>
<script language="javascript" type="text/javascript" >
    var json = [${jsonTree!}];
</script>

<script language="javascript" type="text/javascript" >
$(document).ready(function() {
    classHierarchyUtils.onLoad("${urls.base!}","${displayOption!}");
});    
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/classHierarchy.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/siteAdmin/classHierarchyUtils.js"></script>')}
              
