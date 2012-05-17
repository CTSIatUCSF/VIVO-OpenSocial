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

<#-- Template for displaying paged search results -->

<h2>
    Search results for '${querytext}'
    <#if classGroupName?has_content>limited to type '${classGroupName}'</#if>
    <#if typeName?has_content>limited to type '${typeName}'</#if>
</h2>

<div class="contentsBrowseGroup">

    <#-- Refinement links -->
    <#if classGroupLinks?has_content>
        <div class="searchTOC">
            <h4>Display only</h4>           
            <ul>           
            <#list classGroupLinks as link>
                <li><a href="${link.url}" title="class group link">${link.text}</a></li>
            </#list>
            </ul>           
        </div>
    </#if>

    <#if classLinks?has_content>
        <div class="searchTOC">
            <#if classGroupName?has_content>
                <h4>Limit ${classGroupName} to</h4>
            <#else>
                <h4>Limit to</h4>
            </#if>
            <ul>           
            <#list classLinks as link>
                <li><a href="${link.url}" title="class link">${link.text}</a></li>
            </#list>
            </ul>
        </div>
    </#if>

    <#-- Search results -->
    <ul class="searchhits">
        <#list individuals as individual>
            <li>                        
                <#include "${individual.searchView}">
            </li>
        </#list>
    </ul>
    
		<#-- VIVO OpenSocial Extension by UCSF -->
		<#if openSocial??>
			<#if openSocial.visible>
		        <script type="text/javascript" language="javascript">
		            // find the 'Search' gadget(s).  
		            var searchGadgets = my.findGadgetsAttachingTo("gadgets-search");
		            var keyword = '${querytext}';
		            // add params to these gadgets
		            if (keyword) {
		                for (var i = 0; i < searchGadgets.length; i++) {
		                    var searchGadget = searchGadgets[i];
		                    searchGadget.additionalParams = searchGadget.additionalParams || {};
		                    searchGadget.additionalParams["keyword"] = keyword;
		                }
		            }
		            else {  // remove these gadgets
		                my.removeGadgets(searchGadgets);
		            }            
		        </script>
		
		        <div id="gadgets-search" class="gadgets-gadget-parent"></div>            	
            </#if>	
		</#if>	

    <#-- Paging controls -->
    <#if (pagingLinks?size > 0)>
        <div class="searchpages">
            Pages: 
            <#if prevPage??><a class="prev" href="${prevPage}" title="previous">Previous</a></#if>
            <#list pagingLinks as link>
                <#if link.url??>
                    <a href="${link.url}" title="page link">${link.text}</a>
                <#else>
                    <span>${link.text}</span> <#-- no link if current page -->
                </#if>
            </#list>
            <#if nextPage??><a class="next" href="${nextPage}" title="next">Next</a></#if>
        </div>
    </#if>

</div> <!-- end contentsBrowseGroup -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/search.css" />')}
