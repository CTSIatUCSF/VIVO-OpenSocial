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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ExceptionResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.DataGetterUtils;
/**
 * Controller for getting data for pages defined in the display model. 
 * 
 * This controller passes these variables to the template: 
 * page: a map with information about the page from the display model.
 *  
 * See implementations of PageDataGetter for more variables. 
 */
public class PageController extends FreemarkerHttpServlet{
    private static final Log log = LogFactory.getLog(PageController.class);
    
    protected final static String DEFAULT_TITLE = "Page";        
    protected final static String DEFAULT_BODY_TEMPLATE = "menupage.ftl";     

    protected static final String DATA_GETTER_MAP = "pageTypeToDataGetterMap";
 
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        try {            
            // get URL without hostname or servlet context
            String url = vreq.getRequestURI().substring(vreq.getContextPath().length()); 
        
            Map<String,Object> mapForTemplate = new HashMap<String,Object>();            
            String pageUri = "";                     
            Map<String,Object>page;
            try {
                pageUri = getPageUri( vreq , url );
                page = DataGetterUtils.getMapForPage( vreq, pageUri );
                mapForTemplate.put( "page", page);
                if( page.containsKey("title") ){
                    mapForTemplate.put("title", page.get("title"));
                }

            } catch (Throwable th) {
                return doNotFound(vreq);                
            }
            
            try{
                mapForTemplate.putAll( DataGetterUtils.getDataForPage(pageUri, vreq, getServletContext()) );
            } catch( Throwable th){
                log.error(th,th);
                return doError(vreq);
            }
            //set default in case doesn't exist for data getter
            if(!mapForTemplate.containsKey("dataServiceUrlIndividualsByVClass")) {             
            	mapForTemplate.put("dataServiceUrlIndividualsByVClass", UrlBuilder.getUrl("/dataservice?getSolrIndividualsByVClass=1&vclassId="));
            }
            ResponseValues rv = new TemplateResponseValues(getTemplate( mapForTemplate ), mapForTemplate);            
            return rv;
        } catch (Throwable e) {
            log.error(e, e);
            return new ExceptionResponseValues(e);
        }
    }

    private String getTemplate(Map<String, Object> mapForTemplate) {
        if( mapForTemplate.containsKey("page") ){
            Map page = (Map) mapForTemplate.get("page");
            if( page != null && page.containsKey("bodyTemplate"))
                return (String) page.get("bodyTemplate");
            else
                return DEFAULT_BODY_TEMPLATE;
        }else
            return DEFAULT_BODY_TEMPLATE;        
    }


    private ResponseValues doError(VitroRequest vreq) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title","Page could not be created");
        body.put("errorMessage", "There was an error while creating the page, please check the logs.");        
        return new TemplateResponseValues(Template.TITLED_ERROR_MESSAGE.toString(), body, HttpServletResponse.SC_NOT_FOUND);
    }

    private ResponseValues doNotFound(VitroRequest vreq) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title","Page Not Found");
        body.put("errorMessage", "The page was not found in the system.");        
        return new TemplateResponseValues(Template.TITLED_ERROR_MESSAGE.toString(), body, HttpServletResponse.SC_NOT_FOUND);
    }


    /**
     * Gets the page URI from the request.  The page must be defined in the display model.  
     * @throws Exception 
     */
    private String getPageUri(VitroRequest vreq, String url) throws Exception {
        //check if there is a page URI in the request.  This would have
        //been added by a servlet Filter.
        String pageURI = (String) vreq.getAttribute("pageURI");
        if( pageURI != null && ! pageURI.isEmpty() )
            return pageURI;
        else
            throw new Exception("no page found for " + vreq.getRequestURI() );
    }
      
    
    public static void putPageUri(HttpServletRequest req, String pageUri){
        req.setAttribute("pageURI", pageUri);
    }  
    
    
}
