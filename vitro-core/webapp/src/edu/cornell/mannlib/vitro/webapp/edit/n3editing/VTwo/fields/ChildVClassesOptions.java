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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.vocabulary.OWL;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.VClassDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

public class ChildVClassesOptions implements FieldOptions {

    private static final String LEFT_BLANK = "";
    private String vclassUri;

    private String defaultOptionLabel;
    
    static Log log = LogFactory.getLog(ChildVClassesOptions.class);
    
    public ChildVClassesOptions(String vclassUri) {
        super();        
        this.vclassUri = vclassUri;        
    }
    
    public ChildVClassesOptions setDefaultOptionLabel(String label){
        this.defaultOptionLabel = label;
        return this;
    }

    @Override
    public Map<String, String> getOptions(            
            EditConfigurationVTwo editConfig, 
            String fieldName, 
            WebappDaoFactory wDaoFact) throws Exception{
        // now create an empty HashMap to populate and return
        HashMap <String,String> optionsMap = new LinkedHashMap<String,String>();
        
        // for debugging, keep a count of the number of options populated
        int optionsCount=0;

        
        if (vclassUri==null || vclassUri.equals("")){
            throw new Exception("no vclassUri found for field \""+fieldName+"\" in SelectListGenerator.getOptions() when OptionsType CHILD_VCLASSES specified");
        } else {
            
            // first test to see whether there's a default "leave blank" value specified with the literal options            
            if (defaultOptionLabel!=null) {
                optionsMap.put(LEFT_BLANK, defaultOptionLabel);
            }
            
            // now populate the options                            
            VClassDao vclassDao = wDaoFact.getVClassDao();
            List<String> subClassList = vclassDao.getAllSubClassURIs(vclassUri);
            
            if( subClassList == null || subClassList.size()==0 ) { 
                log.debug("No subclasses of " + vclassUri + " found in the model so only default value from field's literalOptions will be used" );
            } else {                
                for( String subClassUri : subClassList ) {
                    VClass subClass = vclassDao.getVClassByURI(subClassUri);
                    if( subClass != null && !OWL.Nothing.getURI().equals(subClassUri)) {                        
                        optionsMap.put(subClassUri,subClass.getName().trim());                        
                        ++optionsCount;
                    }
                }
            }
        }
        
        log.debug("added "+optionsCount+" options for field \""+fieldName+"\"");
        return optionsMap;
    }

    public String getClassUri(){
        return vclassUri;    
    }
}
