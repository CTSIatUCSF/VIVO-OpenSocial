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

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

public class FreemarkerConfigurationLoader {
	private static final Log log = LogFactory
			.getLog(FreemarkerConfigurationLoader.class);

	private static final Map<String, FreemarkerConfiguration> themeToConfigMap = new HashMap<String, FreemarkerConfiguration>();

	public static FreemarkerConfiguration getConfig(VitroRequest vreq) {
		String themeDir = getThemeDir(vreq.getAppBean());
		return getConfigForTheme(themeDir, vreq.getAppBean(), vreq.getSession().getServletContext());
	}

	private static String getThemeDir(ApplicationBean appBean) {
		if (appBean == null) {
			log.error("Cannot get themeDir from null application bean");
			return null;
		}

		String themeDir = appBean.getThemeDir();
		if (themeDir == null) {
			log.error("themeDir is null");
			return null;
		}

		return themeDir.replaceAll("/$", "");
	}

	/**
	 * The Configuration is theme-specific because:
	 * 
	 * 1. The template loader is theme-specific, since it specifies a theme
	 * directory to load templates from.
	 * 
	 * 2. Some shared variables are theme-specific.
	 */
	private static FreemarkerConfiguration getConfigForTheme(String themeDir,
			ApplicationBean appBean, ServletContext context) {
		synchronized (themeToConfigMap) {
			if (themeToConfigMap.containsKey(themeDir)) {
				return themeToConfigMap.get(themeDir);
			} else {
				FreemarkerConfiguration config = new FreemarkerConfiguration(
						themeDir, appBean, context);
				themeToConfigMap.put(themeDir, config);
				return config;
			}
		}
	}

}