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

package edu.cornell.mannlib.vitro.webapp.config;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Test that gets run at servlet context startup to check for the existence and 
 * validity of properties in the configuration. 
 */
public class ConfigurationPropertiesSmokeTests implements ServletContextListener {

	private static final String PROPERTY_HOME_DIRECTORY = "vitro.home.directory";
	private static final String PROPERTY_DB_URL = "VitroConnection.DataSource.url";
	private static final String PROPERTY_DB_USERNAME = "VitroConnection.DataSource.username";
	private static final String PROPERTY_DB_PASSWORD = "VitroConnection.DataSource.password";
	private static final String PROPERTY_DB_DRIVER_CLASS_NAME = "VitroConnection.DataSource.driver";
	private static final String PROPERTY_DEFAULT_NAMESPACE = "Vitro.defaultNamespace";

	private static final String DEFAULT_DB_DRIVER_CLASS = "com.mysql.jdbc.Driver";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		ConfigurationProperties props = ConfigurationProperties.getBean(ctx);
		StartupStatus ss = StartupStatus.getBean(ctx);

		checkHomeDirectory(ctx, props, ss);
		checkDatabaseConnection(ctx, props, ss);
		checkDefaultNamespace(ctx, props, ss);
	}

	/**
	 * Confirm that: a home directory has been specified; it exists; it is a
	 * directory; it is readable and writable.
	 */
	private void checkHomeDirectory(ServletContext ctx,
			ConfigurationProperties props, StartupStatus ss) {
		String homeDirectoryPath = props.getProperty(PROPERTY_HOME_DIRECTORY);
		if (homeDirectoryPath == null || homeDirectoryPath.isEmpty()) {
			ss.fatal(this, "deploy.properties does not contain a value for '"
					+ PROPERTY_HOME_DIRECTORY + "'");
			return;
		}

		File homeDirectory = new File(homeDirectoryPath);
		if (!homeDirectory.exists()) {
			ss.fatal(this, PROPERTY_HOME_DIRECTORY + " '" + homeDirectoryPath
					+ "' does not exist.");
			return;
		}
		if (!homeDirectory.isDirectory()) {
			ss.fatal(this, PROPERTY_HOME_DIRECTORY + " '" + homeDirectoryPath
					+ "' is not a directory.");
			return;
		}

		if (!homeDirectory.canRead()) {
			ss.fatal(this, PROPERTY_HOME_DIRECTORY + " '" + homeDirectoryPath
					+ "' cannot be read.");
		}
		if (!homeDirectory.canWrite()) {
			ss.fatal(this, PROPERTY_HOME_DIRECTORY + " '" + homeDirectoryPath
					+ "' cannot be written to.");
		}
	}

	/**
	 * Confirm that the URL, Username and Password have been specified for the
	 * Database connection. Confirm that we can load the database driver.
	 * Confirm that we can connect to the database using those properties.
	 */
	private void checkDatabaseConnection(ServletContext ctx,
			ConfigurationProperties props, StartupStatus ss) {
		String url = props.getProperty(PROPERTY_DB_URL);
		if (url == null || url.isEmpty()) {
			ss.fatal(this, "deploy.properties does not contain a value for '"
					+ PROPERTY_DB_URL + "'");
			return;
		}
		String username = props.getProperty(PROPERTY_DB_USERNAME);
		if (username == null || username.isEmpty()) {
			ss.fatal(this, "deploy.properties does not contain a value for '"
					+ PROPERTY_DB_USERNAME + "'");
			return;
		}
		String password = props.getProperty(PROPERTY_DB_PASSWORD);
		if (password == null || password.isEmpty()) {
			ss.fatal(this, "deploy.properties does not contain a value for '"
					+ PROPERTY_DB_PASSWORD + "'");
			return;
		}

		Properties connectionProps = new Properties();
		connectionProps.put("user", username);
		connectionProps.put("password", password);

		String driverClassName = props
				.getProperty(PROPERTY_DB_DRIVER_CLASS_NAME);
		if (driverClassName == null) {
			try {
				Class.forName(DEFAULT_DB_DRIVER_CLASS).newInstance();
			} catch (Exception e) {
				ss.fatal(this, "The default Database Driver failed to load. "
						+ "The driver class is '" + DEFAULT_DB_DRIVER_CLASS
						+ "'", e);
			}
		} else {
			try {
				Class.forName(driverClassName).newInstance();
			} catch (Exception e) {
				ss.fatal(this, "The Database Driver failed to load. "
						+ "The driver class was set by "
						+ PROPERTY_DB_DRIVER_CLASS_NAME + " to be '"
						+ driverClassName + "'", e);
			}
		}

		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, connectionProps);
			conn.close();
		} catch (SQLException e) {
			ss.fatal(this, "Can't connect to the database: " + PROPERTY_DB_URL
					+ "='" + url + "', " + PROPERTY_DB_USERNAME + "='"
					+ username + "'", e);
		}
	}
	
	/**
     * Confirm that the default namespace is specified and a syntactically valid URI.
     */
    private void checkDefaultNamespace(ServletContext ctx,
            ConfigurationProperties props, StartupStatus ss) {
        String ns = props.getProperty(PROPERTY_DEFAULT_NAMESPACE);
        if (ns == null || ns.isEmpty()) {
            ss.fatal(this, "deploy.properties does not contain a value for '"
                    + PROPERTY_DEFAULT_NAMESPACE + "'");
        } else {
            try {
                URI uri = new URI(ns);
            } catch (URISyntaxException e) {
                ss.fatal(this, PROPERTY_DEFAULT_NAMESPACE + " '" + ns + 
                        "' is not a valid URI. " + 
                                (e.getMessage() != null ? e.getMessage() : ""));
            }
        }
    }    
        
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing to do at shutdown
	}

}
