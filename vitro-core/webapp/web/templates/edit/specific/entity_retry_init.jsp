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

<%@ taglib prefix="form" uri="http://vitro.mannlib.cornell.edu/edit/tags" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

    <tr class="editformcell" id="entityNameTr">
        <td valign="bottom" id="entityNameTd" colspan="1">
			<b>Individual Name</b><br/>
                <input type="text" name="field1Value" value="<form:value name="Name"/>" size="80" maxlength="255" />
            <c:set var="NameError"><form:error name="Name"/></c:set>
            <c:if test="${!empty NameError}">
                <span class="notice"><c:out value="${NameError}"/></span>
            </c:if>
        </td>
        <td valign="top" id="displayStatusTd" colspan="1">
			<b>Display Status</b><br/>
		    <select name="StatusId" >
				<form:option name="StatusId"/>
			</select>
        </td>
    </tr>
    <tr class='editformcell' id='GenericTypeTr'>
        <td valign="top" id="genericTypeTd" colspan="2">
			<b>Generic Type<br/>
			<select id="VclassId" name="VClassId" onChange="update();">
				<% // need to implement form:optgroup %>
					<form:option name="VClassId"/>
			</select>
        </td>
    </tr>
    

