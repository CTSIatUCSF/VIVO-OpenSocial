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

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.JenaBaseDao;
import edu.cornell.mannlib.vitro.webapp.filestorage.backend.FileStorage;
import edu.cornell.mannlib.vitro.webapp.filestorage.backend.FileStorageSetup;
import edu.cornell.mannlib.vitro.webapp.filestorage.updater.FileStorageAliasAdder;
import edu.cornell.mannlib.vitro.webapp.filestorage.updater.FileStorageUpdater;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Check that the conditions are met for updating uploaded files. If everything
 * is in place, call the two updaters.
 * 
 * The first updater converts from old-style (pre 1.0) to new-style (post 1.0)
 * file storage.
 * 
 * The second updater insures that all bytestreams store their own alias URLs
 * (post 1.1.1).
 */
public class UpdateUploadedFiles implements ServletContextListener {
	private static final Log log = LogFactory.getLog(UpdateUploadedFiles.class);

	/**
	 * Nothing to do on teardown.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		return;
	}

	/**
	 * Check that the ontology model, the old upload directory, and the file
	 * storage system are all valid. Then do the update.
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		
		try {
			WebappDaoFactory wadf = (WebappDaoFactory) ctx
					.getAttribute("assertionsWebappDaoFactory");
			if (wadf == null) {
				throw new IllegalStateException("Webapp DAO Factory is null. "
						+ "The ServletContext does not contain an attribute "
						+ "for '" + "assertionsWebappDaoFactory" + "'. "
						+ "Does the log contain a previous exception from "
						+ "JenaDataSourceSetup? Is it possible that web.xml "
						+ "is not set up to run JenaDataSourceSetup before "
						+ "UpdateUploadedFiles?");
			}

			OntModel jenaOntModel = (OntModel) ctx
					.getAttribute(JenaBaseDao.ASSERTIONS_ONT_MODEL_ATTRIBUTE_NAME);
			if (jenaOntModel == null) {
				throw new IllegalStateException("Ontology model is null. "
						+ "The ServletContext does not contain an attribute "
						+ "for '"
						+ JenaBaseDao.ASSERTIONS_ONT_MODEL_ATTRIBUTE_NAME
						+ "'. "
						+ "Does the log contain a previous exception from "
						+ "JenaDataSourceSetup? Is it possible that web.xml "
						+ "is not set up to run JenaDataSourceSetup before "
						+ "UpdateUploadedFiles?");
			}

			FileStorage fileStorage = (FileStorage) ctx
					.getAttribute(FileStorageSetup.ATTRIBUTE_NAME);
			if (fileStorage == null) {
				throw new IllegalStateException("File storage system is null. "
						+ "The ServletContext does not contain an attribute "
						+ "for '" + FileStorageSetup.ATTRIBUTE_NAME + "'. "
						+ "Does the log contain a previous exception from "
						+ "FileStorageSetup? Is it possible that web.xml is "
						+ "not set up to run FileStorageSetup before "
						+ "UpdateUploadedFiles?");
			}

			String vitroHomeDirectoryName = ConfigurationProperties
					.getBean(ctx).getProperty(
							FileStorageSetup.PROPERTY_VITRO_HOME_DIR);
			if (vitroHomeDirectoryName == null) {
				throw new IllegalStateException("Upload directory name is null");
			}
			File vitroHomeDirectory = new File(vitroHomeDirectoryName);
			if (!vitroHomeDirectory.exists()) {
				throw new IllegalStateException("Vitro home directory '"
						+ vitroHomeDirectory.getAbsolutePath()
						+ "' does not exist.");
			}
			File uploadDirectory = new File(vitroHomeDirectory,
					FileStorageSetup.FILE_STORAGE_SUBDIRECTORY);
			if (!uploadDirectory.exists()) {
				uploadDirectory.mkdir();
				if (!uploadDirectory.exists()) {
					throw new IllegalStateException(
							"Failed to create the file uploads directory: "
									+ uploadDirectory.getAbsolutePath());
				}
			}

			String vivoDefaultNamespace = ConfigurationProperties.getBean(ctx)
					.getProperty(FileStorageSetup.PROPERTY_DEFAULT_NAMESPACE);
			if (vivoDefaultNamespace == null) {
				throw new IllegalStateException("Default namespace is null.");
			}

			String webappImagePath = ctx.getRealPath("images");
			File webappImageDirectory = (webappImagePath == null) ? null
					: new File(webappImagePath);

			/*
			 * Update from old-style storage to new-style storage.
			 */
			FileStorageUpdater fsu = new FileStorageUpdater(wadf, jenaOntModel,
					fileStorage, uploadDirectory, webappImageDirectory, ctx);
			fsu.update();

			/*
			 * Insure that every FileByteStream object has an alias URL.
			 */
			FileStorageAliasAdder fsaa = new FileStorageAliasAdder(
					jenaOntModel, uploadDirectory, vivoDefaultNamespace);
			fsaa.update();
		} catch (Exception e) {
			log.fatal("Unknown problem", e);
			StartupStatus.getBean(ctx).fatal(this, "Unknown problem", e);
		}
	}
}
