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

package edu.cornell.mannlib.vitro.webapp.auth.permissions;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Load the initial configuration of PermissionSets and Permissions.
 * 
 * The UserAccounts model must be created before this runs.
 * 
 * For now, we just use the four hard-coded "roles".
 */
public class PermissionSetsLoader implements ServletContextListener {
	private static final Log log = LogFactory
			.getLog(PermissionSetsLoader.class);

	public static final String URI_SELF_EDITOR = "http://permissionSet-1";
	public static final String URI_EDITOR = "http://permissionSet-4";
	public static final String URI_CURATOR = "http://permissionSet-5";
	public static final String URI_DBA = "http://permissionSet-50";
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		StartupStatus ss = StartupStatus.getBean(ctx);

		try {
			String ns = ConfigurationProperties.getBean(ctx).getProperty(
					"Vitro.defaultNamespace");

			OntModel model = ModelContext.getBaseOntModelSelector(ctx)
					.getUserAccountsModel();

			ModelWrapper wrapper = new ModelWrapper(model);
			wrapper.createPermissionSet(URI_SELF_EDITOR, "Self Editor");
			wrapper.createPermissionSet(URI_EDITOR, "Editor");
			wrapper.createPermissionSet(URI_CURATOR, "Curator");
			wrapper.createPermissionSet(URI_DBA, "Site Admin");
		} catch (Exception e) {
			ss.fatal(this, "could not run PermissionSetsLoader" + e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to tear down.
	}

	private static class ModelWrapper {
		private final OntModel model;

		private final Property typeProperty;
		private final Property labelProperty;
		private final Resource permissionSet;

		public ModelWrapper(OntModel model) {
			this.model = model;

			typeProperty = model.createProperty(VitroVocabulary.RDF_TYPE);
			labelProperty = model.createProperty(VitroVocabulary.LABEL);
			permissionSet = model.createResource(VitroVocabulary.PERMISSIONSET);
		}

		public void createPermissionSet(String uri, String label) {
			model.enterCriticalSection(Lock.WRITE);
			try {
				Resource r = model.createResource(uri);
				model.add(r, typeProperty, permissionSet);
				model.add(r, labelProperty, label);
				log.debug("Created permission set: '" + uri + "', '" + label
						+ "'");
			} finally {
				model.leaveCriticalSection();
			}
		}
	}
}
