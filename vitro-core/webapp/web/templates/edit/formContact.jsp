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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="editingForm">



<c:set var="onSubmit">
   <c:out value="${formOnSubmit}" default="return true;"/>
</c:set>

<c:set var="action">
    <c:out value="${editAction}" default="doEdit"/>
</c:set>


<form id="editForm" name="editForm" action="${action}" method="post" onsubmit="${onSubmit}">
    <input type="hidden" name="_epoKey" value="${epoKey}" />


		<h2>${title}</h2>
			<c:choose>
				<c:when test='${_action == "insert"}'>
					<h3 class="blue">Creating New Record</h3>
				</c:when>
				<c:otherwise>
					<h3 class="blue">Editing Existing Record</h3>
				</c:otherwise>
			</c:choose>
		<!--<span class="entryFormHeadInstructions">(<sup>*</sup> Required Fields)</span>-->


	
<span class="warning">${globalErrorMsg}</span>
	
	<jsp:include page="${formJsp}"/>
	<br />

			<c:choose>
				<c:when test='${_action == "insert"}'>
					<input id="primaryAction" type="submit" class="submit" name="_insert" value="Create new record"/>
				</c:when>
				<c:otherwise>		
    				<input id="primaryAction" type="submit" class="submit" name="_update" value="Save changes"/>
                    <c:if test="${ ! (_cancelButtonDisabled == 'disabled') }">	
				        <!--<input type="submit" class="delete" name="_delete" onclick="return confirmDelete();" value="Delete"/>-->
                    </c:if>
				</c:otherwise>
			</c:choose>
			
			<!--<input type="reset" class="delete" value="Reset"/>-->
			
            <c:choose>
                <c:when test="${!empty formOnCancel}">
                    <input type="submit" class="delete" name="_cancel" onclick="${formOnCancel}" value="Cancel"/> 
                </c:when>
                <c:otherwise>
		            <input type="submit" class="delete" name="_cancel" value="Cancel"/>
                </c:otherwise>
            </c:choose>


</form>

</div><!--editingform-->

