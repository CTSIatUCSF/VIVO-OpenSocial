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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalysisContextImpl;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestAnalyzer;
import edu.cornell.mannlib.vitro.webapp.controller.individual.IndividualRequestInfo;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual.IndividualTemplateModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.DefaultObjectWrapper;

public class ExportQrCodeController extends FreemarkerHttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ExportQrCodeController.class);
    private static final String TEMPLATE_DEFAULT = "foaf-person--exportQrCode.ftl";

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {
        	Individual individual = getIndividualFromRequest(vreq);
            
            DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
            wrapper.setExposureLevel(BeansWrapper.EXPOSE_SAFE);
            
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("individual", wrapper.wrap(new IndividualTemplateModel(individual, vreq)));
            
            return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
        } catch (Throwable e) {
            log.error(e, e);
            return new ExceptionResponseValues(e);
        }
    }

	private Individual getIndividualFromRequest(VitroRequest vreq) {
		IndividualRequestInfo requestInfo = new IndividualRequestAnalyzer(vreq,
				new IndividualRequestAnalysisContextImpl(vreq)).analyze();
		return requestInfo.getIndividual();
	}

	@Override
    protected String getTitle(String siteName, VitroRequest vreq) {
        try {
            return "Export QR Code for " + getIndividualFromRequest(vreq).getRdfsLabel();
        } catch (Throwable e) {
            log.error(e, e);
            return "There was an error in the system. The individual could not be found";
        }
    }

}
