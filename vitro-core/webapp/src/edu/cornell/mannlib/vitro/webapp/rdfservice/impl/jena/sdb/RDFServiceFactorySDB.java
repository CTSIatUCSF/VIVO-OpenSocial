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

package edu.cornell.mannlib.vitro.webapp.rdfservice.impl.jena.sdb;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.sdb.StoreDesc;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeListener;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;

public class RDFServiceFactorySDB implements RDFServiceFactory {

    private final static Log log = LogFactory.getLog(RDFServiceFactorySDB.class);
    
    private DataSource ds;
    private StoreDesc storeDesc;
    private RDFService longTermRDFService;
    
    public RDFServiceFactorySDB(DataSource dataSource, StoreDesc storeDesc) {
        this.ds = dataSource;
        this.storeDesc = storeDesc;
        this.longTermRDFService = new RDFServiceSDB(dataSource, storeDesc);
    }
    
    @Override
    public RDFService getRDFService() {
        return this.longTermRDFService;
    }

    @Override
    public RDFService getShortTermRDFService() {
        try {
            RDFService rdfService = new RDFServiceSDB(ds.getConnection(), storeDesc);
            for (ChangeListener cl : ((RDFServiceSDB) longTermRDFService)
                    .getRegisteredListeners() ) {
                rdfService.registerListener(cl);    
            }
            return rdfService;
        } catch (Exception e) {
            log.error(e,e);
            throw new RuntimeException(e);
        }
    } 

    @Override
    public void registerListener(ChangeListener changeListener)
            throws RDFServiceException {
        this.longTermRDFService.registerListener(changeListener);
    }

    @Override
    public void unregisterListener(ChangeListener changeListener)
            throws RDFServiceException {
        this.longTermRDFService.registerListener(changeListener);
    }

}
