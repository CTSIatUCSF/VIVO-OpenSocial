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

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SDBGraphConnectionGenerator {

	private final static Log log = LogFactory.getLog(
			SDBGraphConnectionGenerator.class);
	
	private BasicDataSource ds = null;
	private Connection connection = null;
	
	public SDBGraphConnectionGenerator(BasicDataSource dataSource) {
		this.ds = dataSource;
	}
	
	public Connection generateConnection() throws SQLException {
        if ( this.connection == null ) {
            this.connection = ds.getConnection();
        } else if ( this.connection.isClosed() ) {
            try {
                this.connection.close();
            } catch (SQLException e) {                  
                // The connection will throw an "Already closed"
                // SQLException that we need to catch.  We need to 
                // make this extra call to .close() in order to make
                // sure that the connection is returned to the pool.
                // This depends on the particular behavior of version
                // 1.4 of the Apache Commons connection pool library.
                // Earlier versions threw the exception right away,
                // making this impossible. Future versions may do the
                // same.
            }
            this.connection = ds.getConnection();
        }
        return connection;
	}
	
}
