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
package edu.cornell.mannlib.vitro.webapp.dao.jena;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.ModelSource;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;

/** 
 * ModelSource that will handle specially named Vitro models such
 * as display and t-box. Any other models will be retrieved from
 * the inner ModelSource. 
 * 
 * None of these models will be retrieved
 * from the attributes of a ServletRequest.
 */
public class VitroModelSource implements ModelSource {

    private ModelSource innerSource;
    private ServletContext context;
    
    /** 
     * Each of these values identifies a model in the system.     
     */
    public enum ModelName {
        /** Name for default assertion model. */
        ABOX,
        /** Name of default t-box for default assertion model. */
        TBOX,
        /** Name for default display model related to ABOX and TBOX. */
        DISPLAY,
        /** Name for t-box for DISPLAY. */
        DISPLAY_TBOX,
        /** Name for display model related to DISPLAY and DISPLAY_TBOX. */
        DISPLAY_DISPLAY, 
        /** Name for user accounts model. */
        USER_ACCOUNTS
        
        //may need a way to specify unions of these models and unions of URI models 
    }
    
    public VitroModelSource(ModelSource innerSource, ServletContext context){
        this.innerSource = innerSource;
        this.context = context;
    }
    
    @Override
    public Model getModel(String arg0) {
        ModelName pn = getModelName( arg0 );
        if( pn != null ){
            return getNamedModel(pn);
        }else{
            return innerSource.getModel(arg0);
        }
    }

    @Override
    public Model getModel(String arg0, ModelReader arg1) {
        ModelName pn = getModelName( arg0 );
        if( pn != null ){
            return getNamedModel(pn);
        }else{
            return innerSource.getModel(arg0,arg1);
        }
    }

    @Override
    public Model createDefaultModel() {
        return innerSource.createDefaultModel();
    }

    @Override
    public Model createFreshModel() {
        return innerSource.createFreshModel();
    }

    @Override
    public Model openModel(String arg0) {
        ModelName pn = getModelName( arg0 );
        if( pn != null ){
            return getNamedModel( pn );
        }else{
            return innerSource.openModel(arg0);
        }
    }

    @Override
    public Model openModelIfPresent(String arg0) {
        ModelName pn = getModelName( arg0 );
        if( pn != null ){
            return getNamedModel( pn );
        }else{
            return innerSource.openModelIfPresent(arg0);
        }
    }

    /**
     * This should not return null for any value of pmn in 
     * the enum PrivilegedModelName.
     */
    private Model getNamedModel( ModelName pmn ){
        switch( pmn ){
            case ABOX: 
                return (Model) context.getAttribute("jenaOntModel");
            case TBOX:
                return (Model) context.getAttribute("tboxmodel???");
            case DISPLAY:
                return (Model) context.getAttribute(DisplayVocabulary.DISPLAY_ONT_MODEL );
            case DISPLAY_TBOX:
                return (Model) context.getAttribute(DisplayVocabulary.CONTEXT_DISPLAY_TBOX);
            case DISPLAY_DISPLAY:
                return (Model) context.getAttribute(DisplayVocabulary.CONTEXT_DISPLAY_DISPLAY);
            case USER_ACCOUNTS:
                throw new IllegalArgumentException("getNamedModel() Does not yet handle USER_ACCOUNTS");
            default:
                throw new IllegalArgumentException("getNamedModel() should handle all values for enum PrivilegedModelName");
        }
    }
    
    
    /**
     * Returns null if the string is not a ModelName.
     */
    public static ModelName getModelName( String string ){
        if( StringUtils.isEmpty(string))
            return null;
        
        try{ 
            return ModelName.valueOf( string.trim().toUpperCase());
        }catch(IllegalArgumentException ex){
            //Did not find value in enum ModelName for the string, no problem.
            return null;
        }
    }
}
