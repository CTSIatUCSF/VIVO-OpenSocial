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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

public class ConstantFieldOptions implements FieldOptions {

    List<List<String>> options;       
    
    public ConstantFieldOptions(String ... optionPairs) throws Exception {
        super();                
        
        if (optionPairs==null) 
            throw new Exception("Must specify option pairs in ConstantFieldOptions constructor");
        
        if( optionPairs.length % 2 != 0)
            throw new Exception("options must be in pairs of (value,lable)");
        
        options = new ArrayList<List<String>>( optionPairs.length / 2 );        
        for(int i=0; i< optionPairs.length ; i=i+2){
            List<String> pair = new ArrayList<String>(2);                        
            pair.add(optionPairs[i]);
            pair.add(optionPairs[i+1]);                        
            options.add( pair );
        }                
    }


    public ConstantFieldOptions(String fieldName2,
            List<List<String>> optionPairs) throws Exception {
                        
        for(List<String> literalPair: optionPairs ){
            if( literalPair == null)
                throw new Exception("no items in optionPairs may be null.");
            if( literalPair.size() == 0 )
                throw new Exception("no items in optionPairs  may be empty lists.");
            if( literalPair.size() > 2)
                throw new Exception("no items in optionPairs  may be lists longer than 2 items.");                        
        }
                
        options = optionPairs;
    }


    @Override
    public Map<String, String> getOptions(
            EditConfigurationVTwo editConfig, 
            String fieldName, 
            WebappDaoFactory wDaoFact) throws Exception {
        // originally not auto-sorted but sorted now, and empty values not removed or replaced
        HashMap <String,String> optionsMap = new LinkedHashMap<String,String>();
        
        for(Object obj: ((Iterable)options)){
            List<String> literalPair = (List)obj;
            String value=(String)literalPair.get(0);
            if( value != null){  // allow empty string as a value
                String label=(String)literalPair.get(1);
                if (label!=null) { 
                    optionsMap.put(value,label);
                } else {
                    optionsMap.put(value, value);
                }                
            }
        }
        
        return optionsMap;
    }

}
