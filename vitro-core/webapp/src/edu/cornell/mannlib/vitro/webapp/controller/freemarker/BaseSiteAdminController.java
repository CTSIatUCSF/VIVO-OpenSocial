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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.Option;
import edu.cornell.mannlib.vedit.util.FormUtils;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.PelletListener;
import edu.cornell.mannlib.vitro.webapp.search.controller.IndexController;
import edu.cornell.mannlib.vitro.webapp.startup.StartupStatus;

public class BaseSiteAdminController extends FreemarkerHttpServlet {
	
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(BaseSiteAdminController.class);
    protected static final String TEMPLATE_DEFAULT = "siteAdmin-main.ftl";

    public static final Actions REQUIRED_ACTIONS = SimplePermission.SEE_SITE_ADMIN_PAGE.ACTIONS;
    
    @Override
	protected Actions requiredActions(VitroRequest vreq) {
    	return REQUIRED_ACTIONS;
	}

	@Override
	public String getTitle(String siteName, VitroRequest vreq) {
        return siteName + " Site Administration";
	}

    @Override
    protected ResponseValues processRequest(VitroRequest vreq) {
        
        Map<String, Object> body = new HashMap<String, Object>();        

        body.put("dataInput", getDataInputData(vreq));
        body.put("siteConfig", getSiteConfigData(vreq));        
        body.put("indexCacheRebuild", getIndexCacheRebuildUrls(vreq));     
        body.put("ontologyEditor", getOntologyEditorData(vreq));
        body.put("dataTools", getDataToolsUrls(vreq));

        return new TemplateResponseValues(TEMPLATE_DEFAULT, body);
    }
    
    protected Map<String, String> getIndexCacheRebuildUrls(VitroRequest vreq) {
        
        Map<String, String> urls = new HashMap<String, String>();

        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.USE_MISCELLANEOUS_ADMIN_PAGES.ACTIONS)) {
            urls.put("recomputeInferences", UrlBuilder.getUrl("/RecomputeInferences"));     
            urls.put("rebuildClassGroupCache", UrlBuilder.getUrl("/browse?clearcache=1"));
        }
        
		if (PolicyHelper.isAuthorizedForActions(vreq, IndexController.REQUIRED_ACTIONS)) {
			urls.put("rebuildSearchIndex", UrlBuilder.getUrl("/SearchIndex"));
		}
		
        return urls;
    }

    protected Map<String, Object> getDataInputData(VitroRequest vreq) {
    
        Map<String, Object> map = new HashMap<String, Object>();
        
		if (PolicyHelper.isAuthorizedForActions(vreq,
				SimplePermission.DO_BACK_END_EDITING.ACTIONS)) {

            map.put("formAction", UrlBuilder.getUrl("/editRequestDispatch"));
            
            WebappDaoFactory wadf = vreq.getFullWebappDaoFactory();
            
            // Create map for data input entry form options list
            List<VClassGroup> classGroups = wadf.getVClassGroupDao().getPublicGroupsWithVClasses(true,true,false); // order by displayRank, include uninstantiated classes, don't get the counts of individuals
    
            Set<String> seenGroupNames = new HashSet<String>();
            
            Iterator<VClassGroup> classGroupIt = classGroups.iterator();
            LinkedHashMap<String, List<Option>> orderedClassGroups = new LinkedHashMap<String, List<Option>>(classGroups.size());
            while (classGroupIt.hasNext()) {
                VClassGroup group = classGroupIt.next();            
                List<Option> opts = FormUtils.makeOptionListFromBeans(group.getVitroClassList(),"URI","PickListName",null,null,false);
                if( seenGroupNames.contains(group.getPublicName() )){
                    //have a duplicate classgroup name, stick in the URI
                    orderedClassGroups.put(group.getPublicName() + " ("+group.getURI()+")", opts);
                }else if( group.getPublicName() == null ){
                    //have an unlabeled group, stick in the URI
                    orderedClassGroups.put("unnamed group ("+group.getURI()+")", opts);
                }else{
                    orderedClassGroups.put(group.getPublicName(),opts);
                    seenGroupNames.add(group.getPublicName());
                }             
            }
            
            map.put("groupedClassOptions", orderedClassGroups);
        }
        return map;
    }
    
    protected Map<String, Object> getSiteConfigData(VitroRequest vreq) {

        Map<String, Object> data = new HashMap<String, Object>();
        
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.MANAGE_USER_ACCOUNTS.ACTIONS)) {
        	data.put("userAccounts", UrlBuilder.getUrl("/accountsAdmin"));
        }
 
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.MANAGE_PROXIES.ACTIONS)) {
        	data.put("manageProxies", UrlBuilder.getUrl("/manageProxies"));
        }
        
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.EDIT_SITE_INFORMATION.ACTIONS)) {
            data.put("siteInfo", UrlBuilder.getUrl("/editForm", "controller", "ApplicationBean"));
        }
        
        //TODO: Add specific permissions for page management
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.MANAGE_MENUS.ACTIONS)) {
            data.put("menuManagement", UrlBuilder.getUrl("/individual",
                    "uri", "http://vitro.mannlib.cornell.edu/ontologies/display/1.1#DefaultMenu",
                    "switchToDisplayModel", "true"));
            data.put("pageManagement", UrlBuilder.getUrl("/pageList"));
        }
        
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.SEE_STARTUP_STATUS.ACTIONS)) {
        	data.put("startupStatus", UrlBuilder.getUrl("/startupStatus"));
        	data.put("startupStatusAlert", !StartupStatus.getBean(getServletContext()).allClear());
        }
        
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.LOGIN_DURING_MAINTENANCE.ACTIONS)) {
            data.put("restrictLogins", UrlBuilder.getUrl("/admin/restrictLogins"));
        }
        
        return data;
    }
    
    protected Map<String, Object> getOntologyEditorData(VitroRequest vreq) {

        Map<String, Object> map = new HashMap<String, Object>();
 
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.EDIT_ONTOLOGY.ACTIONS)) {
            
            String pelletError = null;
            String pelletExplanation = null;
            Object plObj = getServletContext().getAttribute("pelletListener");
            if ( (plObj != null) && (plObj instanceof PelletListener) ) {
                PelletListener pelletListener = (PelletListener) plObj;
                if (!pelletListener.isConsistent()) {
                    pelletError = "INCONSISTENT ONTOLOGY: reasoning halted.";
                    pelletExplanation = pelletListener.getExplanation();
                } else if ( pelletListener.isInErrorState() ) {
                    pelletError = "An error occurred during reasoning. Reasoning has been halted. See error log for details.";
                }
            }
    
            if (pelletError != null) {
                Map<String, String> pellet = new HashMap<String, String>();
                pellet.put("error", pelletError);
                if (pelletExplanation != null) {
                    pellet.put("explanation", pelletExplanation);
                }
                map.put("pellet", pellet);
            }
                    
            Map<String, String> urls = new HashMap<String, String>();
            
            urls.put("ontologies", UrlBuilder.getUrl("/listOntologies"));
            urls.put("classHierarchy", UrlBuilder.getUrl("/showClassHierarchy"));
            urls.put("classGroups", UrlBuilder.getUrl("/listGroups"));
            urls.put("dataPropertyHierarchy", UrlBuilder.getUrl("/showDataPropertyHierarchy"));
            urls.put("propertyGroups", UrlBuilder.getUrl("/listPropertyGroups"));            
            urls.put("objectPropertyHierarchy", UrlBuilder.getUrl("/showObjectPropertyHierarchy", new ParamMap("iffRoot", "true")));
            map.put("urls", urls);
        }
        
        return map;
    }

    protected Map<String, String> getDataToolsUrls(VitroRequest vreq) {

        Map<String, String> urls = new HashMap<String, String>();
        
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTIONS)) {            
            urls.put("ingest", UrlBuilder.getUrl("/ingest"));
            urls.put("rdfData", UrlBuilder.getUrl("/uploadRDFForm"));
            urls.put("rdfExport", UrlBuilder.getUrl("/export"));
            urls.put("sparqlQueryBuilder", UrlBuilder.getUrl("/admin/sparqlquerybuilder"));
        }
        if (PolicyHelper.isAuthorizedForActions(vreq, SimplePermission.USE_SPARQL_QUERY_PAGE.ACTIONS)) {            
        	urls.put("sparqlQuery", UrlBuilder.getUrl("/admin/sparqlquery"));
        }
        
        return urls;
    }

}
