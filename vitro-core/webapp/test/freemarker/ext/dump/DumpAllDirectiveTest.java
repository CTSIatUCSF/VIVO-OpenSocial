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

package freemarker.ext.dump;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import freemarker.core.Environment;
import freemarker.ext.dump.BaseDumpDirective.DateType;
import freemarker.ext.dump.BaseDumpDirective.Key;
import freemarker.ext.dump.BaseDumpDirective.Type;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class DumpAllDirectiveTest {

    private Template template;

    @Before
    public void setUp() {
        Configuration config = new Configuration();
        String templateStr = "";
        try {
            template = new Template("template", new StringReader(templateStr), config);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Turn off log messages to console
        Logger.getLogger(BaseDumpDirective.class).setLevel(Level.OFF);
    }

    @Test 
    public void dumpDataModel() {
 
        Map<String, Object> dataModel = new HashMap<String, Object>();
        
        String stringName = "dog";
        String stringVal = "Rover";
        dataModel.put(stringName, stringVal);
        
        String boolName = "isLoggedIn";
        boolean boolVal = true;
        dataModel.put(boolName, boolVal);
        
        String intName = "tabCount";
        int intVal = 7;
        dataModel.put(intName, intVal);
        
        String dateName = "now";
        Date dateVal = new Date();
        dataModel.put(dateName, dateVal);
        
        String listName = "fruit";
        List<String> listVal = new ArrayList<String>();
        listVal.add("apples");
        listVal.add("bananas");
        listVal.add("oranges");
        dataModel.put(listName, listVal);
        
        SortedMap<String, Object> expectedDump = new TreeMap<String, Object>();
        
        Map<String, Object> expectedStringDump = new HashMap<String, Object>();
        expectedStringDump.put(Key.TYPE.toString(), Type.STRING);
        expectedStringDump.put(Key.VALUE.toString(), stringVal);
        expectedDump.put(stringName, expectedStringDump);
        
        Map<String, Object> expectedBoolDump = new HashMap<String, Object>();
        expectedBoolDump.put(Key.TYPE.toString(), Type.BOOLEAN);
        expectedBoolDump.put(Key.VALUE.toString(), boolVal);
        expectedDump.put(boolName, expectedBoolDump);
        
        Map<String, Object> expectedIntDump = new HashMap<String, Object>();
        expectedIntDump.put(Key.TYPE.toString(), Type.NUMBER);
        expectedIntDump.put(Key.VALUE.toString(), intVal);
        expectedDump.put(intName, expectedIntDump);
        
        Map<String, Object> expectedDateDump = new HashMap<String, Object>();
        expectedDateDump.put(Key.TYPE.toString(), Type.DATE);
        expectedDateDump.put(Key.DATE_TYPE.toString(), DateType.UNKNOWN);
        expectedDateDump.put(Key.VALUE.toString(), dateVal);
        expectedDump.put(dateName, expectedDateDump);
        
        Map<String, Object> expectedListDump = new HashMap<String, Object>();
        expectedListDump.put(Key.TYPE.toString(), Type.SEQUENCE);
        List<Map<String, Object>> listItemsExpectedDump = new ArrayList<Map<String, Object>>(listVal.size());
        for ( String str : listVal ) {
            Map<String, Object> itemDump = new HashMap<String, Object>();
            itemDump.put(Key.TYPE.toString(), Type.STRING);
            itemDump.put(Key.VALUE.toString(), str);
            listItemsExpectedDump.add(itemDump);            
        }
        expectedListDump.put(Key.VALUE.toString(), listItemsExpectedDump);
        expectedDump.put(listName, expectedListDump);
        
        SortedMap<String, Object> dump = getDump(dataModel);
        assertEquals(expectedDump, dump);
        
        // Test sorting of the data model
        List<String> expectedKeys = new ArrayList<String>(expectedDump.keySet());
        List<String> actualKeys = new ArrayList<String>(dump.keySet());
        assertEquals(expectedKeys, actualKeys);
        
    }
    
    
    /////////////////////////// Private helper methods ///////////////////////////

    private SortedMap<String, Object> getDump(Map<String, Object> dataModel) {
        try {
            Environment env = template.createProcessingEnvironment(dataModel, new StringWriter());
            return new DumpAllDirective().getDataModelDump(env);     
        } catch (Exception e) {
            fail(e.getMessage());
            return null;
        }             
    }    

}
