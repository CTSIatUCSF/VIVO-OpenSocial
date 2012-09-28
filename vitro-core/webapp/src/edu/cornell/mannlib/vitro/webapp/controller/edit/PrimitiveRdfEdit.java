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

package edu.cornell.mannlib.vitro.webapp.controller.edit;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.ajax.VitroAjaxController;
import edu.cornell.mannlib.vitro.webapp.dao.jena.DependentResourceDeleteJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.StandardModelSelector;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.processEdit.EditN3Utils;

public class PrimitiveRdfEdit extends VitroAjaxController {

    private static final long serialVersionUID = 1L;

    //Using the same setup as primitive delete
    @Override
    protected Actions requiredActions(VitroRequest vreq) {
    	return SimplePermission.USE_BASIC_AJAX_CONTROLLERS.ACTIONS;
    }
    
    @Override
    protected void doRequest(VitroRequest vreq,
            HttpServletResponse response) throws ServletException, IOException {
        
        //Test error case
        /*
        if (1==1) {
            doError(response, "Test error", 500);
            return;
        } */
        
        /* Predefined values for RdfFormat are "RDF/XML", 
         * "N-TRIPLE", "TURTLE" (or "TTL") and "N3". null represents 
         * the default language, "RDF/XML". "RDF/XML-ABBREV" is a synonym for "RDF/XML" */
        String format = vreq.getParameter("RdfFormat");      
        if( format == null )            
            format = "N3";      
        if ( ! ("N-TRIPLE".equals(format) || "TURTLE".equals(format) || "TTL".equals(format)
                || "N3".equals(format)|| "RDF/XML-ABBREV".equals(format) || "RDF/XML".equals(format) )){
            doError(response,"RdfFormat was not recognized.",500);
            return;
        }
        
        //parse RDF 
        Set<Model> additions= null;
        try {
            additions = parseRdfParam(vreq.getParameterValues("additions"),format);
        } catch (Exception e) {
            doError(response,"Error reading RDF, set log level to debug for this class to get error messages in the server logs.",HttpStatus.SC_BAD_REQUEST);
            return;
        }
                        
        Set<Model> retractions = null;
        try {
            retractions = parseRdfParam(vreq.getParameterValues("retractions"),format);
        } catch (Exception e) {
            doError(response,"Error reading RDF, set log level to debug for this class to get error messages in the server logs.",HttpStatus.SC_BAD_REQUEST);
            return;
        }

        String editorUri = EditN3Utils.getEditorUri(vreq);           
        try {
			Model a = mergeModels(additions);
			Model r = mergeModels(retractions);

			Model toBeAdded = a.difference(r);
			Model toBeRetracted = r.difference(a);

			Model depResRetractions = DependentResourceDeleteJena
					.getDependentResourceDeleteForChange(toBeAdded,
							toBeRetracted, getWriteModel(vreq));
			toBeRetracted.add(depResRetractions);
        	processChanges(editorUri, getWriteModel(vreq), toBeAdded, toBeRetracted);
        } catch (Exception e) {
            doError(response,e.getMessage(),HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }           
        
    }
    
	/** Package access to allow for unit testing. */
	void processChanges(String editorUri, Model writeModel,
			Model toBeAdded, Model toBeRetracted) throws Exception {
		Lock lock = null;
		log.debug("Model to be retracted is");
		StringWriter sw = new StringWriter();
		toBeRetracted.write(sw, "N3");
		log.debug(sw.toString());
		try {
			lock = writeModel.getLock();
			lock.enterCriticalSection(Lock.WRITE);
			if( writeModel instanceof OntModel){
			    ((OntModel)writeModel).getBaseModel().notifyEvent(new EditEvent(editorUri, true));
			}
			writeModel.add(toBeAdded);
			writeModel.remove(toBeRetracted);
		} catch (Throwable t) {
			throw new Exception("Error while modifying model \n" + t.getMessage());
		} finally {
		    if( writeModel instanceof OntModel){
		        ((OntModel)writeModel).getBaseModel().notifyEvent(new EditEvent(editorUri, false));
		    }
			lock.leaveCriticalSection();
		}
	}

    /**
     * Convert the values from a parameters into RDF models.
     * 
     * Package access to allow for unit testing.
     * 
     * @param parameters - the result of request.getParameters(String)
     * @param format - a valid format string for Jena's Model.read()
     */
    Set<Model> parseRdfParam(String[] parameters, String format) throws Exception{
        Set<Model> models = new HashSet<Model>();               
        for( String param : parameters){
            try{
                StringReader reader = new StringReader(param);
                Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();          
                model.read(reader, null, format);
                models.add(model);
            }catch(Error ex){
                log.error("Error reading RDF as " + format + " in " + param);
                throw new Exception("Error reading RDF, set log level to debug for this class to get error messages in the sever logs.");
            }
        }
        return models;
    }

    private Model getWriteModel(VitroRequest vreq){
    	return StandardModelSelector.selector.getModel(vreq,getServletContext());  
    }

	/** Package access to allow for unit testing. */
	Model mergeModels(Set<Model> additions) {
		Model a = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
		for (Model m : additions) {
			a.add(m);
		}
		return a;
	}

    Log log = LogFactory.getLog(PrimitiveRdfEdit.class.getName());
}
