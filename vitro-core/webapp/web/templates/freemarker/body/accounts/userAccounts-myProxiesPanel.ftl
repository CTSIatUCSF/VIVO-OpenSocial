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

<#-- Template for setting the account reference field, which can also associate a profile with the user account -->

<section id="edit-myProxy" name="proxyProxiesPanel" role="region">
    <h4>Who can edit my profile</h4>
    
    <label for="addProfileEditor">Add profile editor</label>
    <input id="addProfileEditor" type="text" name="proxySelectorAC" class="acSelector" size="35" value="Select an existing last name" role="input" /><span><img class="loading-profileMyAccoount hidden" src="${urls.images}/indicatorWhite.gif" /></span>
    
    <p class="search-status"><span name='proxySelectorSearchStatus' moreCharsText='type more characters' noMatchText='no match'>&nbsp;</span></p>
    <p name="excludeUri" style="display: none">${myAccountUri}<p>
    <p class="selected-editors">Selected editors:</p>
    
    <#-- Magic ul that holds all of the proxy data and the template that shows how to display it. -->
    <ul name="proxyData" role="navigation">
        <#list proxies as proxy>
            <div name="data" style="display: none">
                <p name="uri">${proxy.uri}</p>
                <p name="label">${proxy.label}</p>
                <p name="classLabel">${proxy.classLabel}</p>
                <p name="imageUrl">${proxy.imageUrl}</p>
            </div>
        </#list>

        <#-- 
            Each proxy will be shown using the HTML inside this div.
            It must contain at least:
              -- a link with templatePart="remove"
              -- a hidden input field with templatePart="uriField"  
        -->
        <div name="template" style="display: none">
            <li role="listitem">
                <img class="photo-profile" width="90" alt="%label%" src="%imageUrl%">
                
                <p class="proxy-info">%label% | <span class="class-label">%classLabel%</span>
                    <br />
                    <a class='remove-proxy' href="." templatePart="remove" title="remove selection">Remove selection</a>
                    
                    <input type="hidden" name="proxyUri" value="%uri%" role="input" />
                </p>
            </li>
        </div>
    </ul>
</section>

<script type="text/javascript">
var proxyContextInfo = {
    baseUrl: '${urls.base}',
    ajaxUrl: '${formUrls.proxyAjax}'
};
</script>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/autocomplete.css" />',
                   '<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/account/proxyUtils.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/account/accountProxyCommon.js"></script>',   
              '<script type="text/javascript" src="${urls.base}/js/account/accountProxyItemsPanel.js"></script>',  
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>')}