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
package edu.cornell.mannlib.vitro.webapp.utils.dataGetter;

import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.jena.JenaIngestController;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VitroJenaModelMaker;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VitroJenaSDBModelMaker;

public abstract class DataGetterBase implements DataGetter {
   
    /** 
     * Get the model to use based on a model URI.
     */
    protected Model getModel(ServletContext context, VitroRequest vreq , String modelName) {
        //if not set use jenaOntModel from the request
        if( StringUtils.isEmpty(modelName) ){
            return vreq.getJenaOntModel();
        }else if(REQUEST_DISPLAY_MODEL.equals(modelName)){
            return vreq.getDisplayModel();
        }else if( REQUEST_JENA_ONT_MODEL.equals(modelName)){
            return vreq.getJenaOntModel();            
        }else if( CONTEXT_DISPLAY_MODEL.equals(modelName)){
            return (Model)context.getAttribute( DisplayVocabulary.DISPLAY_ONT_MODEL);            
        }else if( ! StringUtils.isEmpty( modelName)){           
            Model model = JenaIngestController.getModel( modelName, vreq, context);
            if( model == null )
                throw new IllegalAccessError("Cannot get model <" + modelName +"> for DataGetter.");
            else
                return model;
        }else{                    
            //default is just the JeanOntModel from the vreq.
            return vreq.getJenaOntModel();
        }
    }

   public final static String REQUEST_DISPLAY_MODEL = "vitro:requestDisplayModel";   
   public final static String REQUEST_JENA_ONT_MODEL = "vitro:requestJenaOntModel";
   public final static String CONTEXT_DISPLAY_MODEL =  "vitro:contextDisplayModel";
   
}
