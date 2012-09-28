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
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTION); %>
<vitro:confirmAuthorization />

    <h2><a class="ingestMenu" href="ingest">Ingest Menu</a> > Process Property Value Strings</h2>

    <form action="ingest" method="get"i>
        <input type="hidden" name="action" value="processStrings"/>

    <input type="text" style="width:80%;" name="className"/>
    <p>String processor class</p>
    
    <input type="text" name="methodName"/>
    <p>String processor method</p>

    <input type="text" name="propertyName"/>
    <p>Property URI</p>

    <input type="text" name="newPropertyName"/>
    <p>New Property URI</p>

    <select name="destinationModelName">
    <c:forEach var="modelName" items="${modelName}">
        <option value="${modelName}"/>${modelName}</option>
    </c:forEach>
    </select>
    <input type="checkbox" name="processModel" value="TRUE"/> apply changes directly to this model
    <p>model to use</p>
   
    <select name="additionsModel">
		<option value="">none</option>
		<forEach var="modelName" items="${modelNames}">
            <option value="${modelName}">${modelName}</option>
        </forEach>
	</select>
    <p>model in which to save added statements</p>

    <select name="retractionsModel">
		<option value="">none</option>
		<c:forEach var="modelName" items="${modelNames}">
            <option value="${modelName}">${modelName}</option>
        </c:forEach>
	</select>
    <p>model in which to save retracted statements</p>

    <input class="submit" type="submit" value="Process property values"/>
