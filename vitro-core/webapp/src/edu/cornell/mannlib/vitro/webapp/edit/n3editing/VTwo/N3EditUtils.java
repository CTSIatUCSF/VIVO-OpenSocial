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

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatementImpl;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.ModelChangePreprocessor;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.RdfLiteralHash;

public class N3EditUtils {

    
    /**
     * Execute any modelChangePreprocessors in the editConfiguration; 
     */
    public static void preprocessModels(
            AdditionsAndRetractions changes, 
            EditConfigurationVTwo editConfiguration, 
            VitroRequest request){

        List<ModelChangePreprocessor> modelChangePreprocessors = editConfiguration.getModelChangePreprocessors();
        if ( modelChangePreprocessors != null ) {
            for ( ModelChangePreprocessor pp : modelChangePreprocessors ) {
                //these work by side effect
                pp.preprocess( changes.getRetractions(), changes.getAdditions(), request );
            }
        }                   
    }
    


    /** 
     * Process Entity to Return to - substituting uris etc. 
     * May return null.  */
    public static String processEntityToReturnTo(
            EditConfigurationVTwo configuration, 
            MultiValueEditSubmission submission, 
            VitroRequest vreq) {      
        String returnTo = null;
        
        //usually the submission should have a returnTo that is
        // already substituted in with values during ProcessRdfForm.process()
        if( submission != null && submission.getEntityToReturnTo() != null 
                && !submission.getEntityToReturnTo().trim().isEmpty()){
            returnTo = submission.getEntityToReturnTo();            
        }else{
            //If submission doesn't have it, do the best that we can do.
            //this will not have the new resource URIs.
            List<String> entityToReturnTo = new ArrayList<String>();
            String entity = configuration.getEntityToReturnTo();
            entityToReturnTo.add(entity);
            EditN3GeneratorVTwo n3Subber = configuration.getN3Generator();
        
            //Substitute URIs and literals from form
            n3Subber.subInMultiUris(submission.getUrisFromForm(), entityToReturnTo);
            n3Subber.subInMultiLiterals(submission.getLiteralsFromForm(), entityToReturnTo);
        
            //TODO: this won't work to get new resoruce URIs,
            //must the same new resources as in ProcessRdfForm.process
            //setVarToNewResource(configuration, vreq);
            //entityToReturnTo = n3Subber.subInMultiUris(varToNewResource, entityToReturnTo);
            
            returnTo = entityToReturnTo.get(0);
        }

        //remove brackets from sub in of URIs 
        if(returnTo != null) {            
            returnTo = returnTo.trim().replaceAll("<","").replaceAll(">","");       
        }
        return returnTo;
    }
    
    /**
     * If the edit was a data property statement edit, then this updates the EditConfiguration to
     * be an edit of the new post-edit statement.  This allows a back button to the form to get the
     * edit key and be associated with the new edit state.
     * TODO: move this to utils
     */
    public static void updateEditConfigurationForBackButton(
            EditConfigurationVTwo editConfig,
            MultiValueEditSubmission submission, 
            VitroRequest vreq, 
            Model writeModel) {
        
        //now setup an EditConfiguration so a single back button submissions can be handled
        //Do this if data property
        if(EditConfigurationUtils.isDataProperty(editConfig.getPredicateUri(), vreq)) {
            EditConfigurationVTwo copy = editConfig.copy();
            
            //need a new DataPropHash and a new editConfig that uses that, and replace 
            //the editConfig used for this submission in the session.  The same thing
            //is done for an update or a new insert since it will convert the insert
            //EditConfig into an update EditConfig.                       
             
            DataPropertyStatement dps = new DataPropertyStatementImpl();
            List<Literal> submitted = submission.getLiteralsFromForm().get(copy.getVarNameForObject());
            if( submitted != null && submitted.size() > 0){
                for(Literal submittedLiteral: submitted) {
                    dps.setIndividualURI( copy.getSubjectUri() );
                    dps.setDatapropURI( copy.getPredicateUri() );
                    dps.setDatatypeURI( submittedLiteral.getDatatypeURI());
                    dps.setLanguage( submittedLiteral.getLanguage() );
                    dps.setData( submittedLiteral.getLexicalForm() );
                   
                    copy.setDatapropKey( RdfLiteralHash.makeRdfLiteralHash(dps) );                    
                    copy.prepareForDataPropUpdate(writeModel, vreq.getWebappDaoFactory().getDataPropertyDao());                    
                }
                EditConfigurationVTwo.putConfigInSession(copy,vreq.getSession());
            }
        }
        
    }    
    
   
}
