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

package edu.cornell.mannlib.vitro.webapp.startup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Instantiate and run the ServletContextListeners for Vitro, while accumulating
 * messages in StartupStatus.
 * 
 * The startup listeners are stored in a file with one full-qualified class name
 * per line. Blank lines and comment lines (starting with '#') are ignored.
 * 
 * No exception in the listeners should prevent the successful completion.
 * However, an uncaught exception or a fatal error status will cause the
 * StartupStatusDisplayFilter to disply the problem instead of showing the home
 * page (or any other requested page).
 */
public class StartupManager implements ServletContextListener {
	private static final Log log = LogFactory.getLog(StartupManager.class);

	public static final String FILE_OF_STARTUP_LISTENERS = "/WEB-INF/resources/startup_listeners.txt";

	private final List<ServletContextListener> initializeList = new ArrayList<ServletContextListener>();

	/**
	 * These can be instance variables without risk, since contextInitialized()
	 * will only be called once per instance.
	 */
	private ServletContext ctx;
	private StartupStatus ss;

	/**
	 * Build a list of the listeners, and run contextInitialized() on each of
	 * them, at least until we get a fatal error.
	 * 
	 * Each step of this should handle its own exceptions, but we'll wrap the
	 * whole thing in a try/catch just in case.
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ctx = sce.getServletContext();
		ss = StartupStatus.getBean(ctx);

		try {
			findAndInstantiateListeners();

			for (ServletContextListener listener : initializeList) {
				if (ss.isStartupAborted()) {
					ss.listenerNotExecuted(listener);
				} else {
					initialize(listener, sce);
				}
			}
			log.info("Called 'contextInitialized' on all listeners.");
		} catch (Exception e) {
			ss.fatal(this, "Startup threw an unexpected exception.", e);
			log.error("Startup threw an unexpected exception.", e);
		}
	}

	/**
	 * Read the file and instantiate build a list of listener instances.
	 * 
	 * If there is a problem, it will occur and be handled in a sub-method.
	 */
	private void findAndInstantiateListeners() {
		List<String> classNames = readFileOfListeners();

		for (String className : classNames) {
			ServletContextListener listener = instantiateListener(className);
			if (listener != null) {
				initializeList.add(listener);
			}
		}

		checkForDuplicateListeners();
	}

	/**
	 * Read the names of the listener classes.
	 * 
	 * If there is a problem, set a fatal error, and return an empty list.
	 */
	private List<String> readFileOfListeners() {
		List<String> list = new ArrayList<String>();

		InputStream is = null;
		BufferedReader br = null;
		try {
			is = ctx.getResourceAsStream(FILE_OF_STARTUP_LISTENERS);
			br = new BufferedReader(new InputStreamReader(is));

			String line;
			while (null != (line = br.readLine())) {
				String trimmed = line.trim();
				if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
					list.add(trimmed);
				}
			}
		} catch (NullPointerException e) {
			ss.fatal(this, "Unable to locate the list of startup listeners: "
					+ FILE_OF_STARTUP_LISTENERS);
		} catch (IOException e) {
			ss.fatal(this,
					"Failed while processing the list of startup listeners:  "
							+ FILE_OF_STARTUP_LISTENERS, e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}

		log.debug("Classnames of listeners = " + list);
		return list;
	}

	/**
	 * Instantiate a context listener from this class name.
	 * 
	 * If there is a problem, set a fatal error, and return null.
	 */
	private ServletContextListener instantiateListener(String className) {
		try {
			Class<?> c = Class.forName(className);
			Object o = c.newInstance();
			return (ServletContextListener) o;
		} catch (ClassCastException e) {
			ss.fatal(this, "Instance of '" + className
					+ "' is not a ServletContextListener", e);
			return null;
		} catch (Exception e) {
			ss.fatal(this, "Failed to instantiate listener: '" + className
					+ "'", e);
			return null;
		} catch (ExceptionInInitializerError e) {
			ss.fatal(this, "Failed to instantiate listener: '" + className
					+ "'", e);
			return null;
		}
	}

	/**
	 * Call contextInitialized() on the listener.
	 * 
	 * If there is an unexpected exception, set a fatal error.
	 */
	private void initialize(ServletContextListener listener,
			ServletContextEvent sce) {
		try {
			log.debug("Initializing '" + listener.getClass().getName() + "'");
			listener.contextInitialized(sce);
			ss.listenerExecuted(listener);
		} catch (Exception e) {
			ss.fatal(listener, "Threw unexpected exception", e);
		}
	}

	/**
	 * If we have more than one listener from the same class, set a fatal error.
	 */
	private void checkForDuplicateListeners() {
		for (int i = 0; i < initializeList.size(); i++) {
			for (int j = i + 1; j < initializeList.size(); j++) {
				ServletContextListener iListener = initializeList.get(i);
				ServletContextListener jListener = initializeList.get(j);
				if (iListener.getClass().equals(jListener.getClass())) {
					ss.fatal(this,
							("File contains duplicate listener classes: '"
									+ iListener.getClass().getName() + "'"));
				}
			}
		}
	}

	/**
	 * Notify the listeners that the context is being destroyed, in the reverse
	 * order from how they were notified at initialization.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		List<ServletContextListener> destroyList = new ArrayList<ServletContextListener>(
				initializeList);
		Collections.reverse(destroyList);

		for (ServletContextListener listener : destroyList) {
			try {
				log.debug("Destroying '" + listener.getClass().getName() + "'");
				listener.contextDestroyed(sce);
			} catch (Exception e) {
				log.error("Unexpected exception from contextDestroyed() on '"
						+ listener.getClass().getName() + "'", e);
			}
		}
		log.info("Called 'contextDestroyed' on all listeners.");
	}

}
