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
package edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;

/**
 * Exclude individual from search index if
 * it is a member of any of the the types.
 * @author bdc34
 *
 */
public class ExcludeBasedOnType implements SearchIndexExcluder {

	private static final String SKIP_MSG = "skipping due to type.";
	
	/** The add, set and remove methods must keep this list sorted. */
    List<String> typeURIs;
    
    public ExcludeBasedOnType(String ... typeURIs) {    
        setExcludedTypes( typeURIs );
    }

    @Override
    public String checkForExclusion(Individual ind) { 
        if( ind == null ) 
        	return null;
                  	
    	if( typeURIinExcludeList( ind.getVClass() ))
    		return SKIP_MSG;        	
    	
        List<VClass> vclasses = new ArrayList<VClass>();        
        vclasses.addAll( ind.getVClasses()!=null?ind.getVClasses():Collections.EMPTY_LIST );
        vclasses.addAll( ind.getVClasses(true)!=null?ind.getVClasses(true):Collections.EMPTY_LIST );
        
        for( VClass vclz : vclasses){
        	if( typeURIinExcludeList( vclz ))
        		return SKIP_MSG;
        }        
        
        return null;
    }
        
    protected boolean typeURIinExcludeList( VClass vclz){    
    	if( vclz != null && vclz.getURI() != null && !vclz.isAnonymous() ){
    		int pos = Collections.binarySearch(typeURIs, vclz.getURI());
    		return pos >= 0;    			        		        	
        }else{
        	return false;
        }    	
    }
    
    public void setExcludedTypes(String ... typeURIs){        
        setExcludedTypes(Arrays.asList(typeURIs));         
    }
    
    public void setExcludedTypes(List<String> typeURIs){
        synchronized(this){
            this.typeURIs =  new ArrayList<String>(typeURIs) ;
            Collections.sort( this.typeURIs );
        }
    }
    
    protected void addTypeToExclude(String typeURI){
        if( typeURI != null && !typeURI.isEmpty()){
            synchronized(this){
                typeURIs.add(typeURI);
                Collections.sort( this.typeURIs );
            }
        }
    }
    
    protected void removeTypeToExclude(String typeURI){        
        synchronized(this){
            typeURIs.remove(typeURI);            
        }
    }
}
