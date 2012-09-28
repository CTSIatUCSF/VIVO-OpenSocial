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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.Property;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

public class PropertyGroupTemplateModel extends BaseTemplateModel {

    private static final Log log = LogFactory.getLog(PropertyGroupTemplateModel.class); 
    
    private final String name;
    private final List<PropertyTemplateModel> properties;
      
    PropertyGroupTemplateModel(VitroRequest vreq, PropertyGroup group, 
            Individual subject, boolean editing, 
            List<DataProperty> populatedDataPropertyList, List<ObjectProperty> populatedObjectPropertyList) {

        this.name = group.getName();
        
        List<Property> propertyList = group.getPropertyList();
        properties = new ArrayList<PropertyTemplateModel>(propertyList.size());
        for (Property p : propertyList)  {
            if (p instanceof ObjectProperty) {
                ObjectProperty op = (ObjectProperty)p;
                properties.add(ObjectPropertyTemplateModel.getObjectPropertyTemplateModel(op, subject, vreq, editing, populatedObjectPropertyList));
            } else {
                properties.add(new DataPropertyTemplateModel((DataProperty)p, subject, vreq, editing, populatedDataPropertyList));
            }
        }
    }

    protected boolean isEmpty() {
        return properties.isEmpty();
    }
    
    protected void remove(PropertyTemplateModel ptm) {
        properties.remove(ptm);
    }
    
    
    /* Accessor methods for templates */
    // Add this so it's included in dumps for debugging. The templates will want to display
    // name using getName(String)
    public String getName() {
        return name;
    }
    
    public String getName(String otherGroupName) {
        String displayName = name;
        if (displayName == null) {
            displayName = "";
        } else if (displayName.isEmpty()) {
            displayName = otherGroupName;
        } 
        return displayName;
    }
    
    public List<PropertyTemplateModel> getProperties() {
        return properties;
    }
    

}
