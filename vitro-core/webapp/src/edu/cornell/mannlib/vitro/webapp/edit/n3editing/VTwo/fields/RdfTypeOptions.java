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
import java.util.List;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

public class RdfTypeOptions implements FieldOptions {

    String[] typeURIs;        
    
    public RdfTypeOptions(String ... superClassURIs) 
    throws Exception {
        super();
        if( superClassURIs == null )
            throw new Exception("superClassURIs must be supplied "+
            		"to constructor.");
        
        this.typeURIs = superClassURIs;        
    }


    @Override
    public Map<String, String> getOptions(
            EditConfigurationVTwo editConfig, 
            String fieldName, 
            WebappDaoFactory wdf) {        
        Map<String,String> uriToLabel = new HashMap<String,String>();
        
        for(int i=0;i<typeURIs.length; i++){
            String uri = typeURIs[i];
            VClass vc = wdf.getVClassDao().getVClassByURI( uri );
            if( vc == null ){
                uriToLabel.put(uri,uri);
                continue;
            }
            
            uriToLabel.put(uri,vc.getPickListName());
            List<String> subclassUris = wdf.getVClassDao().getAllSubClassURIs( uri );
            if( subclassUris == null )
                continue;
            
            for( String subUri : subclassUris ){
                VClass subVc = wdf.getVClassDao().getVClassByURI( subUri );
                if( vc != null ){
                    uriToLabel.put(subUri,subVc.getPickListName());
                }else{
                    uriToLabel.put(subUri,subUri);
                }
            }            
        }
        
        return uriToLabel;
    }

}
