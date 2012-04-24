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

<#assign standardVisualizationURLRoot ="/visualization">
<#assign ajaxVisualizationURLRoot ="/visualizationAjax">
<#assign dataVisualizationURLRoot ="/visualizationData">
<#assign shortVisualizationURLRoot ="/vis">

<#assign organizationURI ="${organizationURI?url}">
<#assign organizationVivoProfileURL = "${urls.base}/individual?uri=${organizationURI}">

<#assign subOrganizationVivoProfileURL = "${urls.base}/individual?">

<#assign subOrganizationGrantTemporalGraphCommonURL = "${urls.base}${shortVisualizationURLRoot}/grant-graph/">
<#assign subOrganizationPublicationTemporalGraphCommonURL = "${urls.base}${shortVisualizationURLRoot}/publication-graph/">


<#if organizationLocalName?has_content >
    
    <#assign organizationPublicationTemporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/publication-graph/${organizationLocalName}'>
    <#assign organizationGrantTemporalGraphURL = "${urls.base}${shortVisualizationURLRoot}/grant-graph/${organizationLocalName}">
    
<#else>

    <#assign organizationPublicationTemporalGraphURL = '${urls.base}${shortVisualizationURLRoot}/publication-graph/?uri=${organizationURI}'>
    <#assign organizationGrantTemporalGraphURL = "${urls.base}${shortVisualizationURLRoot}/grant-graph/?uri=${organizationURI}">

</#if>

<#assign organizationPublicationTemporalGraphDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=entity_comparison&uri=${organizationURI}&vis_mode=json">
<#assign organizationGrantTemporalGraphDataURL = "${urls.base}${dataVisualizationURLRoot}?vis=entity_grant_count&uri=${organizationURI}&vis_mode=json">

<#assign temporalGraphDrillUpIcon = '${urls.images}/visualization/temporalgraph/temporal-drill-up.png'>
<#assign temporalGraphDrillDownIcon = '${urls.images}/visualization/temporalgraph/temporal-drill-down.png'>

<#assign temporalGraphDownloadCSVCommonURL = '${urls.base}${dataVisualizationURLRoot}?uri=${organizationURI}&labelField=label'>

<#assign publicationParameter = {   "name": "publication",
                                    "pluralName": "publications",
                                    "verbName": "published",
                                    "dropDownText": "by Publications", 
                                    "viewLink": "${organizationPublicationTemporalGraphURL}",
                                    "viewBaseLink": "${subOrganizationPublicationTemporalGraphCommonURL}",
                                    "dataLink": "${organizationPublicationTemporalGraphDataURL}",
                                    "csvLink": "${temporalGraphDownloadCSVCommonURL}&vis=entity_comparison", 
                                    "value": "Publications" }>
                                    
<#assign grantParameter = {   "name": "grant",
                              "pluralName": "grants",
                              "verbName": "granted",
                              "dropDownText": "by Grants", 
                              "viewLink": "${organizationGrantTemporalGraphURL}",
                              "viewBaseLink": "${subOrganizationGrantTemporalGraphCommonURL}", 
                              "dataLink": "${organizationGrantTemporalGraphDataURL}",
                              "csvLink": "${temporalGraphDownloadCSVCommonURL}&vis=entity_grant_count",
                              "value": "Grants" }>
                              
<#assign parameterOptions = [publicationParameter, grantParameter]>

<#assign entityCheckboxSelectorDOMClass = "entity-selector-checkbox">
                                    
<#-- Javascript files -->

<#-- Currently we are using the developer build version for both flot & excanvas libraries,
this is because IE 9 complains about certain properties. After testing it seems that dev 
build version is stable enough. If in next couple of days we feel that there are some issues
we will default to using the stable version unless the request comes from IE 9 in which case
we will use rev 293 (dev build version) of the flot & excanvas files.
-->

<script language="JavaScript" type="text/javascript">

var activitiesLabel = {
        singular: 'activity',
        plural: 'activities'
    };
    
</script>

${scripts.add('<!--[if IE]><script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/r293/excanvas.min.js"></script><![endif]-->',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/flot/r293/jquery.flot.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/jquery_plugins/fliptext/jquery.mb.flipText.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.blockUI.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.dataTables.min.js"></script>', 
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/util.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/constants.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/visualization/visualization-helper-functions.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/jquery_plugins/jquery.notify.min.js"></script>')}              

<#-- CSS files -->

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />',
                  '<link rel="stylesheet" href="${urls.base}/js/visualization/entitycomparison/jquery_plugins/datatable/demo_table.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/visualization/entitycomparison/layout.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/visualization/visualization.css" />',
                  '<link rel="stylesheet" href="${urls.base}/css/jquery_plugins/ui.notify.css" />',
                  '<!--[if IE]><link href="${urls.base}/css/visualization/entitycomparison/layout-ie.css" rel="stylesheet" type="text/css" /><![endif]-->',
                  '<!--[if lt IE 8]><link href="${urls.base}/css/visualization/entitycomparison/layout-ie-67.css" rel="stylesheet" type="text/css" /><![endif]-->')}                  
 
<#-- variables passed from server-side code -->
<script language="JavaScript" type="text/javascript">
    
var contextPath = "${urls.base}";
var temporalGraphDrillUpIcon = "${temporalGraphDrillUpIcon}";
var temporalGraphDrillDownIcon = "${temporalGraphDrillDownIcon}";
var subOrganizationVivoProfileURL = "${subOrganizationVivoProfileURL}";

var subOrganizationGrantTemporalGraphCommonURL = "${subOrganizationGrantTemporalGraphCommonURL}";
var subOrganizationPublicationTemporalGraphCommonURL = "${subOrganizationPublicationTemporalGraphCommonURL}";

var jsonString = '${jsonContent!}';
var vivoDefaultNamespace = '${vivoDefaultNamespace!}';
var organizationLabel = '${organizationLabel}';
var organizationVIVOProfileURL = "${organizationVivoProfileURL}";

var loadingImageLink = contextPath + "/images/visualization/ajax-loader-indicator.gif";
var refreshPageImageLink = contextPath + "/images/visualization/refresh-green.png";

var entityCheckboxSelectorDOMClass = "${entityCheckboxSelectorDOMClass}";

var isDataRequestSentViaAJAX = false;

var csvDownloadURL, temporalGraphProcessor;

/*
This has to be declared before making a call to GUI event manager JS.
*/
var COMPARISON_PARAMETERS_INFO = {

<#list parameterOptions as parameter>

    ${parameter.name}: {

    <#list parameter?keys as key>
        ${key}:"${parameter[key]}"<#if key_has_next>,</#if>
    </#list>
    
    }<#if parameter_has_next>,</#if>

</#list>
    
}

</script>

${scripts.add('<script type="text/javascript" src="${urls.base}/js/visualization/entitycomparison/gui-event-manager.js"></script>')}