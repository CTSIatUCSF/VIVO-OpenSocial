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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeListener;

/**
 * A ChangeListener that forwards events to a Jena ModelChangedListener 
 * @author bjl23
 *
 */
public class JenaChangeListener implements ChangeListener {

    private static final Log log = LogFactory.getLog(JenaChangeListener.class);
    
    private ModelChangedListener listener;
    private Model m = ModelFactory.createDefaultModel();
    
    public JenaChangeListener(ModelChangedListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void addedStatement(String serializedTriple, String graphURI) {
        listener.addedStatement(parseTriple(serializedTriple));
    }

    @Override
    public void removedStatement(String serializedTriple, String graphURI) {
        listener.removedStatement(parseTriple(serializedTriple));
    }

    @Override
    public void notifyEvent(String graphURI, Object event) {
        log.debug("event: " + event.getClass());
        listener.notifyEvent(m, event);
    }
    
    // TODO avoid overhead of Model
    private Statement parseTriple(String serializedTriple) {
        try {
            Model m = ModelFactory.createDefaultModel();
            m.read(new ByteArrayInputStream(
                    serializedTriple.getBytes("UTF-8")), null, "N3");
            StmtIterator sit = m.listStatements();
            if (!sit.hasNext()) {
                throw new RuntimeException("no triple parsed from change event");
            } else {
                Statement s = sit.nextStatement();
                if (sit.hasNext()) {
                    log.warn("More than one triple parsed from change event");
                }
                return s;
            }         
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }

}
