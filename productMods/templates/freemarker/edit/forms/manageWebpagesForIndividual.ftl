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

<#-- Custom form for managing web pages for individuals -->

<#if (editConfiguration.pageData.webpages?size > 1) >
  <#assign ulClass="class='dd'">
<#else>
  <#assign ulClass="">
</#if>

<#assign baseEditWebpageUrl=editConfiguration.pageData.baseEditWebpageUrl!"baseEditWebpageUrl is undefined">
<#assign deleteWebpageUrl=editConfiguration.pageData.deleteWebpageUrl!"deleteWebpageUrl is undefined">
<#assign showAddFormUrl=editConfiguration.pageData.showAddFormUrl!"showAddFormUrl is undefined">

<#if (editConfiguration.pageData.subjectName??) >
<h2><em>${editConfiguration.pageData.subjectName}</em></h2>
</#if>

<h3>Manage Web Pages</h3>
       
<script type="text/javascript">
    var webpageData = [];
</script>

<#if !editConfiguration.pageData.webpages?has_content>
    <p>This individual currently has no web pages specified. Add a new web page by clicking on the button below.</p>
</#if>

<ul id="webpageList" ${ulClass} role="list">
    <#list editConfiguration.pageData.webpages as webpage>
        <li class="webpage" role="listitem">
            <#if webpage.anchor??>
                <#assign anchor=webpage.anchor >
            <#else>
                <#assign anchor=webpage.url >
            </#if>
            
            <span class="webpageName">
                <a href="${webpage.url}" title="webpage url">${anchor}</a>
            </span>
            <span class="editingLinks">
                <a href="${baseEditWebpageUrl}&objectUri=${webpage.link?url}" class="edit" title="edit web page link">Edit</a> | 
                <a href="${urls.base}${deleteWebpageUrl}" class="remove" title="delete web page link">Delete</a> 
            </span>
        </li>    
        
        <script type="text/javascript">
            webpageData.push({
                "webpageUri": "${webpage.link}"              
            });
        </script>      
    </#list>  
</ul>

<section id="addAndCancelLinks" role="section">
    <#-- There is no editConfig at this stage, so we don't need to go through postEditCleanup.jsp on cancel.
         These can just be ordinary links, rather than a v:input element, as in 
         addAuthorsToInformationResource.jsp. -->   
    <a href="${showAddFormUrl}" id="showAddForm" class="button green" title="add new web page">Add New Web Page</a>
       
    <a href="${cancelUrl}" id="returnToIndividual" class="return" title="return to individual">Return to Individual</a>
    <img id="indicator" class="indicator hidden" src="${urls.base}/images/indicatorWhite.gif" />
</section>


<script type="text/javascript">
var customFormData = {
    rankPredicate: '${editConfiguration.pageData.rankPredicate}',
    reorderUrl: '${urls.base}/edit/reorder'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/customForm.css" />',
                  '<link rel="stylesheet" href="${urls.base}/templates/freemarker/edit/forms/css/manageWebpagesForIndividual.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/utils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/customFormUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/templates/freemarker/edit/forms/js/manageWebpagesForIndividual.js"></script>')}