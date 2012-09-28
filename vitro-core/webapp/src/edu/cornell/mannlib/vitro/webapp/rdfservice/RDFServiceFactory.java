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

package edu.cornell.mannlib.vitro.webapp.rdfservice;

public interface RDFServiceFactory {

    /**
     * @return RDFService - an RDFService instance
     */
    public RDFService getRDFService();
    
    /**
     * Returns an instance of RDFService that may not support being left idle
     * for long periods of time.  RDFService instances returned by this method
     * should be immediately used and closed, not stored in (for example) session
     * or context attributes.
     * 
     * This method exists to enable performance improvements resulting from a
     * lack of need to handle database connection or other service timeouts and
     * reconnects.
     * 
     * The results provided by RDFService instances returned by this method must 
     * be identical to those provided by instances returned by getRDFService().  
     *   
     * @return RDFService - an RDFService instance
     */
    public RDFService getShortTermRDFService();
    
    /**
     * Registers a listener to listen to changes in any graph in
     * the RDF store.  Any RDFService objects returned by this factory
     * should notify this listener of changes.
     * 
     * @param changeListener - the change listener
     */
    public void registerListener(ChangeListener changeListener) throws RDFServiceException;
    
    /**
     * Unregisters a listener from listening to changes in the RDF store.
     * Any RDFService objects returned by this factory should notify
     * this listener of changes.
     * 
     * @param changeListener - the change listener
     */
    public void unregisterListener(ChangeListener changeListener) throws RDFServiceException;
    
}
