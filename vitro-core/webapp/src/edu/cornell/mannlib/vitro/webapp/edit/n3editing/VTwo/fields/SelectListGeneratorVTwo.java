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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

public class SelectListGeneratorVTwo {
    
    static Log log = LogFactory.getLog(SelectListGeneratorVTwo.class);
    
    public static Map<String,String> getOptions(
            EditConfigurationVTwo editConfig, 
            String fieldName, 
            WebappDaoFactory wDaoFact){

        
        if( editConfig == null ){
            log.error( "fieldToSelectItemList() must be called with a non-null EditConfigurationVTwo ");
            return Collections.emptyMap();
        }
        if( fieldName == null ){
            log.error( "fieldToSelectItemList() must be called with a non-null fieldName");
            return Collections.emptyMap();
        }                            
        
        FieldVTwo field = editConfig.getField(fieldName);
        if (field==null) {
            log.error("no field \""+fieldName+"\" found from editConfig.");
            return Collections.emptyMap();
        }
        
        if( field.getFieldOptions() == null ){
            return Collections.emptyMap();
        }
        
        try {
            return field.getFieldOptions().getOptions(editConfig,fieldName,wDaoFact);
        } catch (Exception e) {
            log.error("Error runing getFieldOptionis()",e);
            return Collections.emptyMap();
        }
    }
   
      
    //Methods to sort the options map 
    // from http://forum.java.sun.com/thread.jspa?threadID=639077&messageID=4250708
    public static Map<String,String> getSortedMap(Map<String,String> hmap){
        // first make temporary list of String arrays holding both the key and its corresponding value, so that the list can be sorted with a decent comparator
        List<String[]> objectsToSort = new ArrayList<String[]>(hmap.size());
        for (String key:hmap.keySet()) {
            String[] x = new String[2];
            x[0] = key;
            x[1] = hmap.get(key);
            objectsToSort.add(x);
        }
        Collections.sort(objectsToSort, new MapPairsComparator());

        HashMap<String,String> map = new LinkedHashMap<String,String>(objectsToSort.size());
        for (String[] pair:objectsToSort) {
            map.put(pair[0],pair[1]);
        }
        return map;
    }

    private static class MapPairsComparator implements Comparator<String[]> {
        public int compare (String[] s1, String[] s2) {
            Collator collator = Collator.getInstance();
            if (s2 == null) {
                return 1;
            } else if (s1 == null) {
                return -1;
            } else {
            	if ("".equals(s1[0])) {
            		return -1;
            	} else if ("".equals(s2[0])) {
            		return 1;
            	}
                if (s2[1]==null) {
                    return 1;
                } else if (s1[1] == null){
                    return -1;
                } else {
                    return collator.compare(s1[1],s2[1]);
                }
            }
        }
    }    
}
