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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.util.iterator.ClosableIterator;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean.ThemeInfo;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

public class ThemeInfoSetup implements ServletContextListener {
	private static final Log log = LogFactory.getLog(ThemeInfoSetup.class);

	// Set default theme based on themes present on the file system
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		StartupStatus ss = StartupStatus.getBean(ctx);

		String themeDirPath = ctx.getRealPath("/themes");
		if (themeDirPath == null) {
			throw new IllegalStateException(
					"Application does not have a /themes directory.");
		}
		File themesBaseDir = new File(themeDirPath);

		List<String> themeNames = getThemeNames(themesBaseDir);
		log.debug("themeNames: " + themeNames);
		if (themeNames.isEmpty()) {
			ss.fatal(this, "The application contains no themes. '"
					+ themesBaseDir.getAbsolutePath()
					+ "' has no child directories.");
		}

		String defaultThemeName = "vitro";
		if (!themeNames.contains(defaultThemeName)) {
			defaultThemeName = themeNames.get(0);
		}
		log.debug("defaultThemeName: " + defaultThemeName);

		String currentThemeName = getCurrentThemeName(ctx);
		log.debug("currentThemeName: " + currentThemeName);
		if ((currentThemeName != null) && (!currentThemeName.isEmpty())
				&& (!themeNames.contains(currentThemeName))) {
			ss.warning(this, "The current theme selection is '"
					+ currentThemeName
					+ "', but that theme is not available. The '"
					+ defaultThemeName + "' theme will be used instead. "
					+ "Go to the Site Admin page and choose "
					+ "\"Site Information\" to select a theme.");
		}

		ApplicationBean.themeInfo = new ThemeInfo(themesBaseDir,
				defaultThemeName, themeNames);
		ss.info(this, ", current theme: " + currentThemeName
				+ "default theme: " + defaultThemeName + ", available themes: "
				+ themeNames);
	}

	/** Get a list of the names of available themes, sorted alphabetically. */
	private List<String> getThemeNames(File themesBaseDir) {
		ArrayList<String> themeNames = new ArrayList<String>();

		for (File child : themesBaseDir.listFiles()) {
			if (child.isDirectory()) {
				themeNames.add(child.getName());
			}
		}

		Collections.sort(themeNames, String.CASE_INSENSITIVE_ORDER);
		return themeNames;
	}

	private String getCurrentThemeName(ServletContext ctx) {
		OntModel ontModel = ModelContext.getBaseOntModelSelector(ctx)
				.getApplicationMetadataModel();

		ontModel.enterCriticalSection(Lock.READ);
		try {
			Property property = ontModel
					.getProperty(VitroVocabulary.PORTAL_THEMEDIR);
			ClosableIterator<RDFNode> nodes = ontModel
					.listObjectsOfProperty(property);
			try {
				if (nodes.hasNext()) {
					String themeDir = ((Literal) nodes.next()).getString();
					return ThemeInfo.themeNameFromDir(themeDir);
				} else {
					return null;
				}
			} finally {
				nodes.close();
			}
		} catch (Exception e) {
			log.error(e, e);
			return null;
		} finally {
			ontModel.leaveCriticalSection();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// nothing to do here
	}

}
