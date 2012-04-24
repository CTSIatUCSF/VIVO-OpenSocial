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

<jsp:include page="addRoleToPersonTwoStage.jsp">    
	<jsp:param name="roleDescriptor" value="organizer of" />
	<jsp:param name="typeSelectorLabel" value="organizer of" />
	<jsp:param name="showRoleLabelField" value="false" />
	<jsp:param name="buttonText" value="organizer role" />
	<jsp:param name="roleType" value="http://vivoweb.org/ontology/core#OrganizerRole" />	
	<jsp:param name="roleActivityType_optionsType" value="HARDCODED_LITERALS" />
	<jsp:param name="roleActivityType_objectClassUri" value="" /> 
	<jsp:param name="roleActivityType_literalOptions" 
    value='["", "Select type"],
           [ "http://purl.org/NET/c4dm/event.owl#Event", "Event" ],
           [ "http://vivoweb.org/ontology/core#Competition", "Competition" ],
           [ "http://purl.org/ontology/bibo/Conference", "Conference" ],
           [ "http://vivoweb.org/ontology/core#Course", "Course" ],
           [ "http://vivoweb.org/ontology/core#Exhibit", "Exhibit" ],                     
           [ "http://vivoweb.org/ontology/core#Meeting", "Meeting" ],
           [ "http://vivoweb.org/ontology/core#Presentation", "Presentation" ],
           [ "http://vivoweb.org/ontology/core#InvitedTalk", "Invited Talk" ],
           [ "http://purl.org/ontology/bibo/Workshop", "Workshop" ],
           [ "http://vivoweb.org/ontology/core#EventSeries", "Event Series" ],
           [ "http://vivoweb.org/ontology/core#ConferenceSeries", "Conference Series" ],
           [ "http://vivoweb.org/ontology/core#SeminarSeries", "Seminar Series" ],
           [ "http://vivoweb.org/ontology/core#WorkshopSeries", "Workshop Series" ]' />       
</jsp:include>