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

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.auth.policy.PolicyHelper;
import edu.cornell.mannlib.vitro.webapp.beans.BaseResourceBean.RoleLevel;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.Route;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

/** 
 * Represents the property statement list for a single property of an individual.
 */
public abstract class PropertyTemplateModel extends BaseTemplateModel {

    private static final Log log = LogFactory.getLog(PropertyTemplateModel.class); 
    
    protected final VitroRequest vreq;
    protected final String subjectUri;
    protected final String propertyUri;
    private final String localName;

    protected Map<String, Object> verboseDisplay;
    protected String addUrl;
    
    private String name;

    PropertyTemplateModel(Property property, Individual subject, VitroRequest vreq) {
        this.vreq = vreq;
        subjectUri = subject.getURI(); 
        propertyUri = property.getURI();
        localName = property.getLocalName();        
        log.debug("Local name for property " + propertyUri + ": " + localName);
        setVerboseDisplayValues(property);
        addUrl = "";
        
        // Do in subclass constructor. The label has not been set on the property, and the
        // means of getting the label differs between object and data properties.
        // this.name = property.getLabel();
    }
    
    protected void setVerboseDisplayValues(Property property) {  
        
        // No verbose display for vitro and vitro public properties.
        // This models previous behavior. In theory the verbose display can be provided, but we may not want
        // to give anyone access to these properties, since the application is dependent on them.
        String namespace = property.getNamespace();        
        if (VitroVocabulary.vitroURI.equals(namespace) || VitroVocabulary.VITRO_PUBLIC.equals(namespace)) {
            return;
        }
        
        Boolean verboseDisplayValue = (Boolean) vreq.getSession().getAttribute("verbosePropertyDisplay");
        if ( ! Boolean.TRUE.equals(verboseDisplayValue))  {
            return;
        }
        
		if (!PolicyHelper.isAuthorizedForActions(vreq,
				SimplePermission.SEE_VERBOSE_PROPERTY_INFORMATION.ACTIONS)) {
            return;
        }
        
        verboseDisplay = new HashMap<String, Object>();
        
        RoleLevel roleLevel = property.getHiddenFromDisplayBelowRoleLevel();
        String roleLevelLabel = roleLevel != null ? roleLevel.getLabel() : "";
        verboseDisplay.put("displayLevel", roleLevelLabel);

        roleLevel = property.getProhibitedFromUpdateBelowRoleLevel();
        roleLevelLabel = roleLevel != null ? roleLevel.getLabel() : "";
        verboseDisplay.put("updateLevel", roleLevelLabel);   
        
        verboseDisplay.put("localName", property.getLocalNameWithPrefix());
        verboseDisplay.put("displayRank", getPropertyDisplayTier(property));
       
        String editUrl = UrlBuilder.getUrl(getPropertyEditRoute(), "uri", property.getURI());
        verboseDisplay.put("propertyEditUrl", editUrl);
    }
    
    protected abstract int getPropertyDisplayTier(Property p);
    protected abstract Route getPropertyEditRoute();
    
    protected void setName(String name) {
        this.name = name;
    }
    
    /* Template properties */
    
    public abstract String getType();
    
    public String getName() {
        return name;
    }

    public String getLocalName() {
        return localName;
    }
    
    public String getUri() {
        return propertyUri;
    }
    
    public String getAddUrl() {
        //log.info("addUrl=" + addUrl);
        return (addUrl != null) ? addUrl : "";
    }
    
    public Map<String, Object> getVerboseDisplay() {
        return verboseDisplay;
    }
 
}
