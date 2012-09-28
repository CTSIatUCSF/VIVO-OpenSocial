<%--
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
--%>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTION); %>
<vitro:confirmAuthorization />

<h2>Ingest Menu</h2>

<ul class="ingestMenu">
    <li><a href="ingest?action=connectDB" title="Connect to a Jena database">Connect DB</a></li>
    <li><a href="ingest?action=listModels"title="Manage all available Jena models">Manage Jena Models</a></li>
    <li><a href="ingest?action=subtractModels" title="Subtract one model from another and save difference to new model">Subtract One Model from Another</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="ingest?action=csv2rdf" title="Convert a CSV file to RDF in preparation for ingest">Convert CSV to RDF</a></li>
    <li><a href="jenaXmlFileUpload" title="Convert an XML file to RDF in preparation for ingest">Convert XML to RDF</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="ingest?action=executeSparql" title="Run a SPARQL CONSTRUCT query and apply results to an available model">Execute SPARQL CONSTRUCT</a></li>
    <li><a href="ingest?action=generateTBox" title="Generate TBox from assertions data">Generate TBox</a></li>
    <li><a href="ingest?action=renameBNodes" title="Convert blank nodes to named resources">Name Blank Nodes</a></li>
    <li><a href="ingest?action=smushSingleModel" title="Convert all existing URIs for a resource to a single URI">Smush Resources</a></li>
    <li><a href="ingest?action=mergeResources" title="Merge two resources into one">Merge Resources</a></li>
    <li><a href="ingest?action=renameResource" title="Change the namespace of resources currently in a specified namespace">Change Namespace of Resources</a></li> 
    <li><a href="ingest?action=processStrings" title="Process property value strings">Process Property Value Strings</a></li>
    <li><a href="ingest?action=splitPropertyValues" title="Split property value strings into multiple property values using a regular expression pattern">Split Property Value Strings into Multiple Property Values</a></li>
</ul>

<ul class="ingestMenu">
    <li><a href="harvester/harvest?job=csvPerson" title="Use the harvester to ingest person data from CSV files">Harvest Person Data from CSV</a></li>
    <li><a href="harvester/harvest?job=csvGrant" title="Use the harvester to ingest grant data from CSV files">Harvest Grant Data from CSV</a></li>
    <li><a href="ingest?action=executeWorkflow" title="Execute an RDF-encoded ingest workflow">Execute Workflow</a></li>
</ul>