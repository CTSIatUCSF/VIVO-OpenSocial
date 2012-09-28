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

package edu.cornell.mannlib.vitro.webapp.services.shortview;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.cornell.mannlib.vitro.webapp.services.shortview.FakeApplicationOntologyService.ShortViewConfigException;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

/**
 * Set up the ShortViewService.
 */
public class ShortViewServiceSetup implements ServletContextListener {
	private static final String ATTRIBUTE_NAME = ShortViewService.class
			.getName();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		StartupStatus ss = StartupStatus.getBean(ctx);

		FakeApplicationOntologyService faker;
		try {
			faker = new FakeApplicationOntologyService(ctx);
		} catch (ShortViewConfigException e) {
			ss.warning(this, "Failed to load the shortview_config.n3 file -- "
					+ e.getMessage(), e);
			faker = new FakeApplicationOntologyService();
		}

		ShortViewServiceImpl svs = new ShortViewServiceImpl(faker);
		ctx.setAttribute(ATTRIBUTE_NAME, svs);

		ss.info(this,
				"Started the Short View Service with a ShortViewServiceImpl");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		sce.getServletContext().removeAttribute(ATTRIBUTE_NAME);
	}

	public static ShortViewService getService(ServletContext ctx) {
		return (ShortViewService) ctx.getAttribute(ATTRIBUTE_NAME);
	}
}
