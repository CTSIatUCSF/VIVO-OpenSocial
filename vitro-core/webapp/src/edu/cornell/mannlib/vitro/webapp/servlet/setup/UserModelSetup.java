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
package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelSynchronizer;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;


/** 
 * Setup the user account model.  If it does not exist in the 
 * database, create and populate it. 
 */
public class UserModelSetup extends JenaDataSourceSetupBase 
                                           implements ServletContextListener {
    protected static String AUTHPATH = BASE + "auth/";
    
    private static final Log log = LogFactory.getLog(
            UserModelSetup.class.getName());

    @Override   
    public void contextInitialized(ServletContextEvent sce) {       
        ServletContext ctx = sce.getServletContext();
        StartupStatus ss = StartupStatus.getBean(ctx);                
    
        DataSource bds = getApplicationDataSource(ctx);
        if( bds == null ){
            ss.fatal(this, "A DataSource must be setup before ModelSetup "+
                    "is run. Make sure that JenaPersistentDataSourceSetup runs before "+
                    "ModelSetup.");
            return;
        }
        
        setupUserAccountModel(bds, ctx, ss);
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // Does nothing.        
    }

    private void setupUserAccountModel (DataSource bds, ServletContext ctx ,StartupStatus ss){             
        try {
            Model userAccountsDbModel = makeDBModel(bds,
                    JENA_USER_ACCOUNTS_MODEL, DB_ONT_MODEL_SPEC, ctx);
            OntModel userAccountsModel =
                ModelFactory.createOntologyModel( MEM_ONT_MODEL_SPEC);
    
            // This is used in Selenium testing, when we load user accounts from a file.
        	if (userAccountsDbModel.isEmpty()) {
        		readOntologyFilesInPathSet(AUTHPATH, ctx, userAccountsDbModel);
        	}

        	userAccountsModel.add(userAccountsDbModel);
            userAccountsModel.getBaseModel().register(
                    new ModelSynchronizer(userAccountsDbModel));
            ctx.setAttribute("userAccountsOntModel", userAccountsModel);
                        
        } catch (Throwable t) {
            log.error("Unable to load user accounts model from DB", t);
            ss.fatal(this, "Unable to load user accounts model from DB", t);
        }        
    }
}
