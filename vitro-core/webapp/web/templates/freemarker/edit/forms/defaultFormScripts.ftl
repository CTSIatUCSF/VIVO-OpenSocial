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

<#-- Template to setup and call scripts for form editing -->

<#-- Like original jsp, allow the passing of variables -->
<#assign defaultHeight="200" />
<#assign defaultWidth="75%" />
<#assign defaultButton="bold,italic,underline,separator,link,bullist,numlist,separator,sub,sup,charmap,separator,undo,redo,separator,code"/>   
<#assign defaultToolbarLocation = "top" />
<#if !height?has_content>
	<#assign height=defaultHeight/>
</#if>

<#if !width?has_content>
	<#assign width=defaultWidth />
</#if>

<#if !buttons?has_content>
	<#assign buttons = defaultButton />
</#if>

<#if !toolbarLocation?has_content>
	<#assign toolbarLocation = defaultToolbarLocation />
</#if>

<#-- Set up data -->
<script type="text/javascript">
    var customFormData = {
    	tinyMCEData : {
                theme : "advanced",
                mode : "textareas",
                theme_advanced_buttons1 : "${buttons}",
                theme_advanced_buttons2 : "",
                theme_advanced_buttons3 : "",
                theme_advanced_toolbar_location : "${toolbarLocation}",
                theme_advanced_toolbar_align : "left",
                theme_advanced_statusbar_location : "bottom",
                theme_advanced_path : false,
                theme_advanced_resizing : true,
                height : "${height}",
                width  : "${width}",
                valid_elements : "a[href|name|title],br,p,i,em,cite,strong/b,u,sub,sup,ul,ol,li",
                fix_list_elements : true,
                fix_nesting : true,
                cleanup_on_startup : true,
                gecko_spellcheck : true,
                forced_root_block: false
                //forced_root_block : 'p',
                // plugins: "paste",
                // theme_advanced_buttons1_add : "pastetext,pasteword,selectall",
                // paste_create_paragraphs: false,
                // paste_create_linebreaks: false,
                // paste_use_dialog : true,
                // paste_auto_cleanup_on_paste: true,
                // paste_convert_headers_to_strong : true
                // save_callback : "customSave",
                // content_css : "example_advanced.css",
                // extended_valid_elements : "a[href|target|name]",
                // plugins : "table",
                // theme_advanced_buttons3_add_before : "tablecontrols,separator",
                // invalid_elements : "li",
                // theme_advanced_styles : "Header 1=header1;Header 2=header2;Header 3=header3;Table Row=tableRow1", // Theme specific setting CSS classes
            }
         };
</script>

<#-- Script to enable browsing individuals within a class -->
<#--'<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.scrollTo-min.js"></script>',-->
${scripts.add('<script type="text/javascript" src="${urls.base}/js/tiny_mce/tiny_mce.js"></script>',
'<script type="text/javascript" src="${urls.base}/js/tiny_mce/jquery-tinymce.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/edit/initTinyMce.js"></script>')}