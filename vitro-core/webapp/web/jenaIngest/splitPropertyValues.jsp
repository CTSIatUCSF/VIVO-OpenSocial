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

<%@ page import="com.hp.hpl.jena.ontology.Individual" %>
<%@ page import="com.hp.hpl.jena.ontology.OntModel" %>
<%@ page import="com.hp.hpl.jena.rdf.model.ModelMaker" %>
<%@ page import="com.hp.hpl.jena.shared.Lock" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.requestedAction.usepages.UseAdvancedDataToolsPages" %>
<% request.setAttribute("requestedActions", new UseAdvancedDataToolsPages()); %>
<vitro:confirmAuthorization />

    <h2><a class="ingestMenu" href="ingest">Ingest Menu</a> > Split Property Value Strings into Multiple Property Values</h2>

    <form action="ingest" method="get">
        <input type="hidden" name="action" value="splitPropertyValues"/>
    <h3>Select Source Models</h3>
    <ul>
		  <li><input type="checkbox" name="sourceModelName" value="vitro:jenaOntModel"/>webapp model</li>
		  <li><input type="checkbox" name="sourceModelName" value="vitro:baseOntModel"/>webapp assertions</li>
          <c:forEach var="modelName" items="${modelNames}">
              <li> <input type="checkbox" name="sourceModelName" value="${modelName}"/>${modelName}</li>
          </c:forEach>
    </ul>

	
	<input type="text" name="propertyURI"/>
    <p>Property URI for which Values Should Be Split</p>	

	<input type="text" name="splitRegex"/>
    <p>Regex Pattern on which To Split</p>
	
	<input type="text" name="newPropertyURI"/>
    <p>Property URI To Be Used with the Newly-Split Values</p>
	
	<h3></h3>
	
	<p>
	<input type="checkbox" name="trim" value="true"/> trim bordering whitespace
	</p>
	
    <h3>Select Destination Model</h3>

    <select name="destinationModelName">
        <c:forEach var="modelName" items="${modelNames}">
            <option value="${modelName}"/>${modelName}</option>
        </c:forEach>
    </select>

    <input id="submit" type="submit" value="Split Values"/>
