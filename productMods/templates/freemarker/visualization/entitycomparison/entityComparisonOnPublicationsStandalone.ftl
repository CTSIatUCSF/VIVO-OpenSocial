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

<#-- The Order of each element in this file is very important. Do not make any changes to it unless making
corresponding changes in the included Templates. -->

<#assign currentParameter = "publication">

<script language="JavaScript" type="text/javascript">

var currentParameter = "${currentParameter}";

</script>

<#include "entityComparisonSetup.ftl">

<#assign temporalGraphDownloadFileLink = '${temporalGraphDownloadCSVCommonURL}&vis=entity_comparison'>
<#assign temporalGraphDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=entity_comparison&uri=${organizationURI}&vis_mode=json">

<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
<!--

/*
This is used in util.js to print grant temporal graph links for all sub-organizations.
*/    
var temporalGraphCommonURL = subOrganizationPublicationTemporalGraphCommonURL;

var temporalGraphDataURL = '${temporalGraphDataURL}';

$(document).ready(function () {

	options = {
		responseContainer: $("div#temporal-graph-response"),
		bodyContainer: $("#body"),
		errorContainer: $("#error-container"),
		dataURL: temporalGraphDataURL	
	};
	
	renderTemporalGraphVisualization(options);

});

// -->
</script>

<#assign currentParameterObject = publicationParameter>

<div id="temporal-graph-response">

<#include "entityComparisonBody.ftl">

<#-- 
Right now we include the error message by default because currently I could not devise any more smarted solution. By default
the CSS of the #error-container is display:none; so it will be hidden unless explicitly commanded to be shown which we do in 
via JavaScript.
-->
<#include "entityPublicationComparisonError.ftl">

</div>