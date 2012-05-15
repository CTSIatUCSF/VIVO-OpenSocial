package edu.ucsf.vitro.opensocial;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.FreemarkerHttpServlet;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;

public class GadgetDetailsController extends FreemarkerHttpServlet {
	
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(GadgetDetailsController.class);
    private static final String TEMPLATE_DEFAULT = "gadgetDetails.ftl";
	
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
    	try {
	        Map<String, Object> body = new HashMap<String, Object>();

            body.put("title", "GadgetDetails");            
	        // VIVO OpenSocial Extension by UCSF
	        try {
		        OpenSocialManager openSocialManager = new OpenSocialManager(vreq, "gadgetDetails");
		        body.put(OpenSocialManager.TAG_NAME, openSocialManager);
		        if (openSocialManager.isVisible()) {
		        	body.put("bodyOnload", "my.init();");
		        }
	        } catch (IOException e) {
	            log.error("IOException in doTemplate()", e);
	        } catch (SQLException e) {
	            log.error("SQLException in doTemplate()", e);
	        }	               
	        
	        return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
        
	    } catch (Throwable e) {
	        log.error(e, e);
	        return new ExceptionResponseValues(e);
	    }
    }
    
    @Override
    protected String getTitle(String siteName, VitroRequest vreq) {
    	return "Gadget Details";
    }    

}
