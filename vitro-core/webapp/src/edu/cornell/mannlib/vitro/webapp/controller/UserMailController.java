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

package edu.cornell.mannlib.vitro.webapp.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.email.FreemarkerEmailFactory;

/**
 *  Controller for comments ("contact us") page
 *  * @author bjl23
 */
public class UserMailController extends VitroHttpServlet{

    @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
	public void doGet( HttpServletRequest request, HttpServletResponse response )
    throws IOException, ServletException {
        super.doGet(request,response);
        VitroRequest vreq = new VitroRequest(request);
        try {
        //this try block passes any errors to error.jsp
            if (!FreemarkerEmailFactory.isConfigured(request)) {
                request.setAttribute("title", "Mail All Users Form");
                request.setAttribute("bodyJsp", "/contact_err.jsp");// <<< this is where the body gets set
				request.setAttribute("ERR",
						"This application has not yet been configured to send mail. "
								+ "Email properties must be specified in the configuration properties file.");
                RequestDispatcher errd = request.getRequestDispatcher(Controllers.BASIC_JSP);
                errd.forward(request, response);
            }
            ApplicationBean appBean=vreq.getAppBean();

            request.setAttribute("siteName", appBean.getApplicationName());
            request.setAttribute("scripts","/js/commentsForm.js");

            if (request.getHeader("Referer") == null)
                request.getSession().setAttribute("commentsFormReferer","none");
            else
                request.getSession().setAttribute("commentsFormReferer",request.getHeader("Referer"));

            request.setAttribute("title", appBean.getApplicationName()+" Mail Users Form");
            request.setAttribute("bodyJsp", "/templates/parts/emailUsers.jsp");// <<< this is where the body gets set

            RequestDispatcher rd =
                request.getRequestDispatcher(Controllers.BASIC_JSP);
            rd.forward(request, response);

        } catch (Throwable e) {
            // This is how we use an error.jsp
            //it expects javax.servlet.jsp.jspException to be set to the
            //exception so that it can display the info out of that.
            request.setAttribute("javax.servlet.jsp.jspException", e);
            RequestDispatcher rd = request.getRequestDispatcher("/error.jsp");
            rd.include(request, response);
        }
    }
}
