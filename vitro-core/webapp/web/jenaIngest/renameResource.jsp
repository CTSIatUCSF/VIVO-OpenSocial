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

<%@ page import="com.hp.hpl.jena.rdf.model.ModelMaker"%>
<%@ page import="com.hp.hpl.jena.rdf.model.Model"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%@taglib prefix="vitro" uri="/WEB-INF/tlds/VitroUtils.tld" %>
<%@page import="edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission" %>
<% request.setAttribute("requestedActions", SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTION); %>
<vitro:confirmAuthorization />

<script type="text/javascript" src="js/jquery.js"></script>

<h2><a class="ingestMenu" href="ingest">Ingest Menu</a> > Change Namespace of Resources</h2>

<p>This tool will change all resources in the supplied "old namespace" 
to be in the "new namespace."  Additionally, if the local names do not
already follow the established "n" + random integer naming convention, 
they will be updated to this format.</p>

<p>This tool operates on the main web application model only, not on any 
   of the additional Jena models.</p>

<c:if test="${!empty errorMsg}">
    <p class="notice">${errorMsg}</p>
</c:if>

<form id="takeuri" action="ingest" method="get">
<input type="hidden" name="action" value="renameResource"/>
<p>Old namespace: <input id="uri1" type="text" size="52" name="oldNamespace" value="${oldNamespace}" /></p>
<p>New namespace: <input id="uri2" type="text" size="52" name="newNamespace" value="${newNamespace}" /></p>
<p><input id="submit" type="submit" name="submit" value="Change namespace" /></p>
</form>