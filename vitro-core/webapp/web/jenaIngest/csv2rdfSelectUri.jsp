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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>

<%@ page import="com.hp.hpl.jena.ontology.Individual" %>
<%@ page import="com.hp.hpl.jena.ontology.OntModel" %>
<%@ page import="com.hp.hpl.jena.rdf.model.ModelMaker" %>
<%@ page import="com.hp.hpl.jena.shared.Lock" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Map.Entry" %>

<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTION); %>
<vitro:confirmAuthorization />

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/jenaIngest/renameNode.js"></script>
<script type="text/javascript">
function selectProperties(){
	document.getElementById("properties").disabled = false;
	document.getElementById("pattern").disabled = false;
}
function disableProperties(){
	document.getElementById("properties").disabled = true;
	document.getElementById("pattern").disabled = true;
}
</script>

    <h2><a class="ingestMenu" href="ingest">Ingest Menu</a> > Convert Blank Nodes to Named Resources</h2>

    <form id="takeuri" action="ingest" method="get">
        <input type="hidden" name="action" value="renameBNodesURISelect"/>

    <h3>Select URI prefix</h3>
   
	<p>URIs will be constructed using the following base string:</p>
	<input id="namespace" type="text" style="width:65%;" name="namespaceEtcStr"/> 

    <p/>
    
    <p>Each resource will be assigned a URI by taking the above string and 
     adding either a random integer, or a string based on the value of one of the
     the properties of the resource</p>
    
    <input type="radio" value="integer" name="concatenate" checked="checked" onclick="disableProperties()">Use random integer</input>
    <br></br>
    <input type="radio" value="pattern" name="concatenate" onclick="selectProperties()">Use pattern based on values of </input> 
  
    
    <% Map<String,LinkedList<String>> propertyMap = (Map) request.getAttribute("propertyMap");
       Set<Entry<String,LinkedList<String>>> set = propertyMap.entrySet();
       Iterator<Entry<String,LinkedList<String>>> itr = set.iterator();
       Entry<String, LinkedList<String>> entry = null;
    %>
   
    <select name="property" id="properties" disabled="disabled">
    <% while(itr.hasNext()){%>
    
    	<%entry = itr.next();
    	Iterator<String> listItr = entry.getValue().iterator();
    	%>
    	<option value ="<%=entry.getKey() %>"><%=entry.getKey()%></option>
    <%}
    %>
    </select>
    <br></br>
    <p>Enter a pattern using $$$ as the placeholder for the value of the property selected above.</p>
    <p>For example, entering dept_$$$ might generate URIs with endings such as dept_Art or dept_Classics.</p>
    <input id="pattern" disabled="disabled" type="text" style="width:35%;" name="pattern"/> 
    
    <input type="hidden" name="destinationModelName" value="${destinationModelName}"/>
    <input type="hidden" name="csv2rdf" value="${csv2rdf}"/>
    
    <c:forEach var="sourceModelValue" items="${sourceModel}">
        <input type="hidden" name="sourceModelName" value="${sourceModelValue}"/>
    </c:forEach>
    
    <p/>
    
    <input class="submit" type="submit" value="Convert CSV"/>
    
    </form>
    
