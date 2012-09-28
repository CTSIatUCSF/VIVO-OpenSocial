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

package edu.cornell.mannlib.vitro.webapp.filters;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * No matter what URL is requested, check to see whether the StartupStatus
 * contains errors or warnings. If it does, hijack the request to show the
 * StartupStatus display page.
 * 
 * If the status only contains warnings, this only happens once. Subsequent
 * requests will display normally. However, if the status contains a fatal
 * error, this filter will hijack every request, and will not let you proceed.
 */
public class StartupStatusDisplayFilter implements Filter {
	private static final String TEMPLATE_PATH = "/templates/freemarker/body/admin/startupStatus-displayRaw.ftl";

	private ServletContext ctx;
	private StartupStatus ss;
	private boolean statusAlreadyDisplayed;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ctx = filterConfig.getServletContext();
		ss = StartupStatus.getBean(ctx);
		statusAlreadyDisplayed = false;
	}

	@Override
	public void destroy() {
		// nothing to do.
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		if (ss.allClear() || (!isFatal() && statusAlreadyDisplayed)) {
			chain.doFilter(req, resp);
			return;
		}

		displayStartupStatus(req, resp);
		statusAlreadyDisplayed = true;
	}

	private void displayStartupStatus(ServletRequest req, ServletResponse resp) throws IOException,
			ServletException {
		HttpServletResponse hResp = (HttpServletResponse) resp;

		try {
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			bodyMap.put("status", ss);
			bodyMap.put("showLink", !isFatal());
			bodyMap.put("contextPath", getContextPath());
			bodyMap.put("applicationName", getApplicationName());			
						
	        HttpServletRequest httpreq = (HttpServletRequest) req;
	        String url = "";
	        
	        String path = httpreq.getRequestURI();
	        if( path != null ){
	        	url = path;
	        }
	        
	        String query = httpreq.getQueryString();
	        if( !StringUtils.isEmpty( query )){
	        	url = url + "?" + query;
	        }	
	        
			bodyMap.put("url", url );

			hResp.setContentType("text/html;charset=UTF-8");
			hResp.setStatus(SC_INTERNAL_SERVER_ERROR);
			Template tpl = loadFreemarkerTemplate();
			tpl.process(bodyMap, hResp.getWriter());
		} catch (TemplateException e) {
			throw new ServletException("Problem with Freemarker Template", e);
		}
	}

	private String getContextPath() {
		String cp = ctx.getContextPath();
		if ((cp == null) || cp.isEmpty()) {
			return "The application";
		} else {
			return cp;
		}
	}

	private Object getApplicationName() {
		String name = "";
		try {
			ApplicationBean app = ApplicationBean.getAppBean(ctx);
			name = app.getApplicationName();
		} catch (Exception e) {
			// deal with problems below
		}

		if ((name != null) && (!name.isEmpty())) {
			return name;
		} else {
			return getContextPath();
		}
	}

	private Template loadFreemarkerTemplate() throws IOException {
		Configuration cfg = new Configuration();
		cfg.setTemplateLoader(new WebappTemplateLoader(ctx));
		return cfg.getTemplate(TEMPLATE_PATH);
	}

	private boolean isFatal() {
		return !ss.getErrorItems().isEmpty();
	}
}
