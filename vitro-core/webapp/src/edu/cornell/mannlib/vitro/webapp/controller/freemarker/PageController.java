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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.DataGetter;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.DataGetterUtils;
/**
 * Controller for getting data for pages defined in the display model. 
 * 
 * This controller passes these variables to the template: 
 * page: a map with information about the page from the display model.
 * pageUri: the URI of the page that identifies the page in the model 
 *  (note that this is not the URL address of the page).
 *    
 * See implementations of PageDataGetter for more variables. 
 */
public class PageController extends FreemarkerHttpServlet{
    private static final Log log = LogFactory.getLog(PageController.class);
    
    protected final static String DEFAULT_TITLE = "Page";        
    protected final static String DEFAULT_BODY_TEMPLATE = "emptyPage.ftl";     

    protected static final String DATA_GETTER_MAP = "pageTypeToDataGetterMap";
 
    @Override
    protected ResponseValues processRequest(VitroRequest vreq) throws Exception {
                   
        Map<String,Object> mapForTemplate = new HashMap<String,Object>();                                
        Map<String,Object>page;
        
        //figure out what page we are trying to get            
        String pageUri = getPageUri( vreq );
        if( StringUtils.isEmpty( pageUri ) )
            return doNoPageSpecified(vreq);              
        else           
            mapForTemplate.put("pageUri", pageUri);
        
        //try to get the page RDF from the model
        try{
            page =  vreq.getWebappDaoFactory().getPageDao().getPage(pageUri);                
            mapForTemplate.put( "page", page);
            if( page.containsKey("title") ){
                mapForTemplate.put("title", page.get("title"));
            }
        }catch( Throwable th){
            return doNotFound(vreq);
        }
        
        //executePageDataGetters( pageUri, vreq, getServletContext(), mapForTemplate );
        //these should all be data getters now
        executeDataGetters( pageUri, vreq, mapForTemplate);

        mapForTemplate.putAll( getPageControllerValues( pageUri, vreq, getServletContext(), mapForTemplate));
        
        ResponseValues rv = new TemplateResponseValues(getTemplate( mapForTemplate ), mapForTemplate);            
        return rv;       
    }

    private void executeDataGetters(String pageUri, VitroRequest vreq, Map<String, Object> mapForTemplate) 
    throws Exception {
        List<DataGetter> dgList = DataGetterUtils.getDataGettersForPage(vreq, vreq.getDisplayModel(), pageUri);
                        
        for( DataGetter dg : dgList){            
            Map<String,Object> moreData = dg.getData(mapForTemplate);            
            if( moreData != null ){
                mapForTemplate.putAll(moreData);
            }
        }                       
    }
/*
    private void executePageDataGetters(String pageUri, VitroRequest vreq, ServletContext context, Map<String, Object> mapForTemplate) 
    throws Exception{                
        mapForTemplate.putAll( DataGetterUtils.getDataForPage(pageUri, vreq, context) );        
    }
*/
    /**
     * Add any additional values to the template variable map that are related to the page.
     * For example, editing links.
     */
    private Map<String,Object> getPageControllerValues(
            String pageUri, VitroRequest vreq, ServletContext servletContext,
            Map<String, Object> mapForTemplate) {
        Map<String,Object> map = new HashMap<String,Object>();
        
        //Add editing link for page if authorized        
        Map<String,Object> pageMap = (Map<String, Object>) mapForTemplate.get("page");        
        if( PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.MANAGE_MENUS.ACTIONS) ){
            String editPageUrl = UrlBuilder.getIndividualProfileUrl(pageUri, vreq);            
            editPageUrl = UrlBuilder.addParams(editPageUrl, DisplayVocabulary.SWITCH_TO_DISPLAY_MODEL , "1");            
            pageMap.put("URLToEditPage", editPageUrl);
        }        
            
        return map;
    }
    
    private String getTemplate(Map<String, Object> mapForTemplate) {
        //first try to get the body template from the display model RDF
        if( mapForTemplate.containsKey("page") ){
            Map page = (Map) mapForTemplate.get("page");
            if( page != null && page.containsKey("bodyTemplate")){
                return (String) page.get("bodyTemplate");
            }
        }
        //next, try to get body template from the data getter values
        if( mapForTemplate.containsKey("bodyTemplate") ){
            return (String) mapForTemplate.get("bodyTemplate");            
        }
        
        //Nothing? then use a default empty page
        return DEFAULT_BODY_TEMPLATE;        
    }


    private ResponseValues doError(VitroRequest vreq) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title","Page could not be created");
        body.put("errorMessage", "There was an error while creating the page, please check the logs.");        
        return new TemplateResponseValues(Template.TITLED_ERROR_MESSAGE.toString(), body, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private ResponseValues doNotFound(VitroRequest vreq) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title","Page Not Found");
        body.put("errorMessage", "The page was not found in the system.");        
        return new TemplateResponseValues(Template.TITLED_ERROR_MESSAGE.toString(), body, HttpServletResponse.SC_NOT_FOUND);
    }


    private ResponseValues doNoPageSpecified(VitroRequest vreq) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("title","No page URI specified");
        body.put("errorMessage", "Could not generate page beacause it was unclear what page was being requested.  A URL mapping may be missing.");        
        return new TemplateResponseValues(Template.TITLED_ERROR_MESSAGE.toString(), body, HttpServletResponse.SC_NOT_FOUND);
    }
    
    /**
     * Gets the page URI from the request.  The page must be defined in the display model.  
     * @throws Exception 
     */
    private String getPageUri(VitroRequest vreq) throws Exception {
        // get URL without hostname or servlet context
        //bdc34: why are we getting this?
        String url = vreq.getRequestURI().substring(vreq.getContextPath().length());
        
        // Check if there is a page URI in the request.  
        // This would have been added by a servlet Filter.
        String pageURI = (String) vreq.getAttribute("pageURI");        
        return pageURI;        
    }
      
    
    public static void putPageUri(HttpServletRequest req, String pageUri){
        req.setAttribute("pageURI", pageUri);
    }  
    
    
}
