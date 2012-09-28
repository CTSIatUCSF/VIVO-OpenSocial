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

package edu.cornell.mannlib.vitro.webapp.rdfservice.impl;

import java.io.InputStream;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeListener;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeSet;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;

/**
 * An RDFServiceFactory that always returns the same RDFService object
 * @author bjl23
 *
 */
public class RDFServiceFactorySingle implements RDFServiceFactory {

    private RDFService rdfService;
    
    public RDFServiceFactorySingle(RDFService rdfService) {
        this.rdfService = new UnclosableRDFService(rdfService);
    }
    
    @Override
    public RDFService getRDFService() {
        return this.rdfService;
    }
    
    @Override 
    public RDFService getShortTermRDFService() {
        return this.rdfService;
    }
    
    @Override
    public void registerListener(ChangeListener listener) throws RDFServiceException {
        this.rdfService.registerListener(listener);
    }
    
    @Override
    public void unregisterListener(ChangeListener listener) throws RDFServiceException {
        this.rdfService.unregisterListener(listener);
    }
    
    public class UnclosableRDFService implements RDFService {
        
        private RDFService s;
        
        public UnclosableRDFService(RDFService rdfService) {
            this.s = rdfService;
        }

        @Override
        public boolean changeSetUpdate(ChangeSet changeSet)
                throws RDFServiceException {
            return s.changeSetUpdate(changeSet);
        }

        @Override
        public void newIndividual(String individualURI, String individualTypeURI)
                throws RDFServiceException {
            s.newIndividual(individualURI, individualTypeURI);
        }

        @Override
        public void newIndividual(String individualURI,
                String individualTypeURI, String graphURI)
                throws RDFServiceException {
            s.newIndividual(individualURI, individualTypeURI, graphURI);
        }

        @Override
        public InputStream sparqlConstructQuery(String query,
                ModelSerializationFormat resultFormat)
                throws RDFServiceException {
            return s.sparqlConstructQuery(query, resultFormat);
        }

        @Override
        public InputStream sparqlDescribeQuery(String query,
                ModelSerializationFormat resultFormat)
                throws RDFServiceException {
            return s.sparqlDescribeQuery(query, resultFormat);
        }

        @Override
        public InputStream sparqlSelectQuery(String query,
                ResultFormat resultFormat) throws RDFServiceException {
            return s.sparqlSelectQuery(query, resultFormat);
        }

        @Override
        public boolean sparqlAskQuery(String query) throws RDFServiceException {
            return s.sparqlAskQuery(query);
        }

        @Override
        public List<String> getGraphURIs() throws RDFServiceException {
            return s.getGraphURIs();
        }

        @Override
        public void getGraphMetadata() throws RDFServiceException {
            s.getGraphMetadata();
        }

        @Override
        public String getDefaultWriteGraphURI() throws RDFServiceException {
            return s.getDefaultWriteGraphURI();
        }

        @Override
        public void registerListener(ChangeListener changeListener)
                throws RDFServiceException {
            s.registerListener(changeListener);
        }

        @Override
        public void unregisterListener(ChangeListener changeListener)
                throws RDFServiceException {
            s.unregisterListener(changeListener);
        }

        @Override
        public ChangeSet manufactureChangeSet() {
            return s.manufactureChangeSet();
        }

        @Override
        public void close() {
            // Don't close s.  It's being used by everybody.
        }
        
    }
    

}
