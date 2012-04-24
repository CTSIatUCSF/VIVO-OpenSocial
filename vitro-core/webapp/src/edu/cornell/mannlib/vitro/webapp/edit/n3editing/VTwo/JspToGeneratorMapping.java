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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.RDFSLabelGenerator;

public class JspToGeneratorMapping {
    static Log log = LogFactory.getLog( JspToGeneratorMapping.class );
    
    public static Map<String,String> jspsToGenerators;
    
    static{
        jspsToGenerators = new HashMap<String,String>();
        Map<String, String> map = jspsToGenerators;        
        
        // vitro forms:
//        map.put("autoCompleteDatapropForm.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AutoCompleteDatapropFormGenerator.class.getName());
//        map.put("autoCompleteObjPropForm.jsp",
//                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.AutoCompleteObjPropFormGenerator.class.getName());
        map.put("datapropStmtDelete.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultDeleteGenerator.class.getName());
        map.put("dateTimeIntervalForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DateTimeIntervalFormGenerator.class.getName());
        map.put("dateTimeValueForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DateTimeValueFormGenerator.class.getName());
        map.put("defaultAddMissingIndividualForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultAddMissingIndividualFormGenerator.class.getName());
        map.put("defaultDatapropForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultDataPropertyFormGenerator.class.getName());
        map.put("defaultObjPropForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultObjectPropertyFormGenerator.class.getName());
        map.put("newIndividualForm.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.NewIndividualFormGenerator.class.getName());
        map.put("propDelete.jsp",
                edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.DefaultDeleteGenerator.class.getName());
        map.put("rdfsLabelForm.jsp",
                RDFSLabelGenerator.class.getName());
        
        //add in the vivo mappings if they exist
        Object object = null;
        try {
            Class classDefinition = 
                Class.forName("edu.cornell.mannlib.vitro.webapp.edit.n3editing.N3TransitionToV2Mapping");
            object = classDefinition.newInstance();
            Map<String,String> vivoJspsToGenerators = (Map) object;
            if( vivoJspsToGenerators != null )
            map.putAll( vivoJspsToGenerators );
           
        } catch (Throwable th){
            log.error( "could not load VIVO jsp mappings",th );
        }                                       
    }
}
