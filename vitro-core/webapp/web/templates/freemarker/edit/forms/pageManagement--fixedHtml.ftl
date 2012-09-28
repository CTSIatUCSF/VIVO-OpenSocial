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
<#--This contains the template for the fixed HTML content type that is to be cloned and used in page management-->

<section id="fixedHtml" class="contentSectionContainer">
    <label id="fixedHTMLVariableLabel" for="fixedHTMLVariable">Variable Name<span class="requiredHint"> *</span></label>
    <input type="text" name="saveToVar" size="20" value="" id="fixedHTMLSaveToVar" role="input" />
    <label id="fixedHTMLValueLabel" for="fixedHTMLValue">Enter fixed HTML here<span id="fixedHTMLValueSpan"></span><span class="requiredHint"> *</span></label>
    <textarea id="fixedHTMLValue" name="htmlValue" cols="70" rows="15" style="margin-bottom:7px"></textarea><br />
    <input  type="button" id="doneWithContent" name="doneWithContent" value="Save this content" class="doneWithContent" />
    <#if menuAction == "Add">
        <span id="cancelContent"> or <a class="cancel" href="javascript:"  id="cancelContentLink" >Cancel</a></span>
    </#if>
</section>
${scripts.add('<script type="text/javascript" src="${urls.base}/js/menupage/processFixedHTMLDataGetterContent.js"></script>')}
