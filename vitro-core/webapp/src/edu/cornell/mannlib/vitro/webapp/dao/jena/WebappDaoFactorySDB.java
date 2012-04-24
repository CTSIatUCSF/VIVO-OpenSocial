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
import java.util.HashSet;

import org.apache.commons.dbcp.BasicDataSource;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;

import edu.cornell.mannlib.vitro.webapp.dao.DataPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactoryConfig;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.SimpleReasonerSetup;

public class WebappDaoFactorySDB extends WebappDaoFactoryJena {
	
	public static final String UNION_GRAPH = "urn:x-arq:UnionGraph"; 
    private SDBDatasetMode datasetMode = SDBDatasetMode.ASSERTIONS_AND_INFERENCES;
    
	/**
	 * For use when any database connection associated with the Dataset
	 * is managed externally
	 */
	public WebappDaoFactorySDB(OntModelSelector ontModelSelector, 
                               Dataset dataset) {
		super(ontModelSelector);
		this.dwf = new StaticDatasetFactory(dataset);
	}
	
    /**
     * For use when any database connection associated with the Dataset
     * is managed externally
     */
	public WebappDaoFactorySDB(OntModelSelector ontModelSelector, 
	                            Dataset dataset, 
	                            WebappDaoFactoryConfig config) {
		super(ontModelSelector, config);
        this.dwf = new StaticDatasetFactory(dataset);
	}
	
    /**
     * For use when any Dataset access should get a temporary DB connection
     * from a pool
     */
    public WebappDaoFactorySDB(OntModelSelector ontModelSelector, 
                                BasicDataSource bds,
                                StoreDesc storeDesc,
                                WebappDaoFactoryConfig config) {
        super(ontModelSelector, config);
        this.dwf = new ReconnectingDatasetFactory(bds, storeDesc);
    }
    
    /**
     * For use when any Dataset access should get a temporary DB connection
     * from a pool, and access to the inference graph needs to be specified.
     */
    public WebappDaoFactorySDB(OntModelSelector ontModelSelector, 
                                BasicDataSource bds,
                                StoreDesc storeDesc,
                                WebappDaoFactoryConfig config,
                                SDBDatasetMode datasetMode) {
        super(ontModelSelector, config);
        this.dwf = new ReconnectingDatasetFactory(bds, storeDesc);
        this.datasetMode = datasetMode;
    }
   
    
    public WebappDaoFactorySDB(WebappDaoFactorySDB base, String userURI) {
        super(base.ontModelSelector);
        this.ontModelSelector = base.ontModelSelector;
        this.config = base.config;
        this.userURI = userURI;
        this.dwf = base.dwf;
    }
	
	@Override
    public IndividualDao getIndividualDao() {
        if (entityWebappDao != null)
            return entityWebappDao;
        else
            return entityWebappDao = new IndividualDaoSDB(
                    dwf, datasetMode, this);
    }
	
	@Override
	public DataPropertyStatementDao getDataPropertyStatementDao() {
		if (dataPropertyStatementDao != null) 
			return dataPropertyStatementDao;
		else
			return dataPropertyStatementDao = new DataPropertyStatementDaoSDB(
			        dwf, datasetMode, this);
	}
	
	@Override
	public ObjectPropertyStatementDao getObjectPropertyStatementDao() {
		if (objectPropertyStatementDao != null) 
			return objectPropertyStatementDao;
		else
			return objectPropertyStatementDao = 
			    new ObjectPropertyStatementDaoSDB(dwf, datasetMode, this);
	}
	
	@Override
	public VClassDao getVClassDao() {
		if (vClassDao != null) 
			return vClassDao;
		else
			return vClassDao = new VClassDaoSDB(dwf, datasetMode, this);
	}
	
	public WebappDaoFactory getUserAwareDaoFactory(String userURI) {
        return new WebappDaoFactorySDB(this, userURI);
    }
	
	public enum SDBDatasetMode {
	    ASSERTIONS_ONLY, INFERENCES_ONLY, ASSERTIONS_AND_INFERENCES
	}
	
	public static String getFilterBlock(String[] graphVars, 
	                                    SDBDatasetMode datasetMode) {
	    StringBuffer filterBlock = new StringBuffer();
	    for (int i = 0; i < graphVars.length; i++) {
	        switch (datasetMode) {
	            case ASSERTIONS_ONLY :  
	                    filterBlock.append("FILTER (")
	                        .append("(!bound(").append(graphVars[i])
	                        .append(")) || (")
	                        .append(graphVars[i])
	                        .append(" != <")
	                        .append(JenaDataSourceSetupBase.JENA_INF_MODEL)
	                        .append("> ")
	                        .append("&& ").append(graphVars[i]).append(" != <")
	                        .append(JenaDataSourceSetupBase.JENA_TBOX_INF_MODEL)
	                        .append(">")
	                        .append("&& ").append(graphVars[i]).append(" != <")
                            .append(SimpleReasonerSetup.JENA_INF_MODEL_REBUILD)
                            .append(">")
                            .append("&& ").append(graphVars[i]).append(" != <")
                            .append(SimpleReasonerSetup.JENA_INF_MODEL_SCRATCHPAD)
                            .append(">")
	                        .append(") ) \n");
	                    break;
	            case INFERENCES_ONLY :  
                    filterBlock.append("FILTER (")
                        .append("(!bound(").append(graphVars[i])
                        .append(")) || (")
                        .append(graphVars[i])
                        .append(" = <")
                        .append(JenaDataSourceSetupBase.JENA_INF_MODEL)
                        .append("> || ").append(graphVars[i])
                        .append(" = <")
                        .append(JenaDataSourceSetupBase.JENA_TBOX_INF_MODEL)
                        .append(">) )\n");
                    break;
	            default:
	                break;
	        }
	    }
	    return filterBlock.toString();
	}
	
	private class ReconnectingDatasetFactory implements DatasetWrapperFactory {
	    
	    private BasicDataSource _bds;
	    private StoreDesc _storeDesc;
	    
	    public ReconnectingDatasetFactory(BasicDataSource bds, 
                                          StoreDesc storeDesc) {
	        _bds = bds;
	        _storeDesc = storeDesc;
	    }
	    
	    public DatasetWrapper getDatasetWrapper() {
	        try {
                Connection sqlConn = _bds.getConnection();
                SDBConnection conn = new SDBConnection(sqlConn) ;
                Store store = SDBFactory.connectStore(conn, _storeDesc);
                Dataset dataset = SDBFactory.connectDataset(store);
                return new DatasetWrapper(dataset, conn);
            } catch (SQLException sqe) {
                throw new RuntimeException(
                		"Unable to connect to database", sqe);
            }
	    }
	    
	}    
	
}
