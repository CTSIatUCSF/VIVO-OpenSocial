/*
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
*/

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.HashMap;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class AddAttendeeRoleToPersonGenerator extends AddRoleToPersonTwoStageGenerator {
    
    private static String TEMPLATE = "addAttendeeRoleToPerson.ftl";
    
    @Override
    String getTemplate(){ return TEMPLATE; }

    @Override
    String getRoleType() {
        return "http://vivoweb.org/ontology/core#AttendeeRole";
    }
    
    @Override
    public String getRoleActivityTypeObjectClassUri(VitroRequest vreq) {
        //no ClassURI since it uses hard coded literals
        return null;
    }   
    
    @Override
    public RoleActivityOptionTypes getRoleActivityTypeOptionsType() {
        return RoleActivityOptionTypes.HARDCODED_LITERALS;        
    }    
    
    //Editor role involves hard-coded options for the "right side" of the role or activity
    @Override
    protected HashMap<String, String> getRoleActivityTypeLiteralOptions() {
        HashMap<String, String> literalOptions = new HashMap<String, String>();
        literalOptions.put("", "Select type");
        literalOptions.put("http://purl.org/NET/c4dm/event.owl#Event", "Event");
        literalOptions.put("http://vivoweb.org/ontology/core#Competition", "Competition");
        literalOptions.put("http://purl.org/ontology/bibo/Conference", "Conference");
        literalOptions.put("http://vivoweb.org/ontology/core#Course", "Course");
        literalOptions.put("http://vivoweb.org/ontology/core#Exhibit", "Exhibit");                     
        literalOptions.put("http://vivoweb.org/ontology/core#Meeting", "Meeting");
        literalOptions.put("http://vivoweb.org/ontology/core#Presentation", "Presentation");
        literalOptions.put("http://vivoweb.org/ontology/core#InvitedTalk", "Invited Talk");
        literalOptions.put("http://purl.org/ontology/bibo/Workshop", "Workshop");
        literalOptions.put("http://vivoweb.org/ontology/core#EventSeries", "Event Series");
        literalOptions.put("http://vivoweb.org/ontology/core#ConferenceSeries", "Conference Series");
        literalOptions.put("http://vivoweb.org/ontology/core#SeminarSeries", "Seminar Series");
        literalOptions.put("http://vivoweb.org/ontology/core#WorkshopSeries", "Workshop Series");
        return literalOptions;
    }

    @Override   
    boolean isShowRoleLabelField() { 
        return false;  
    }	  
}
