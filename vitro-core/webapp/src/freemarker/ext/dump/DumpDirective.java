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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class DumpDirective extends BaseDumpDirective {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(DumpDirective.class);
    
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body) throws TemplateException, IOException {

        if (loopVars.length != 0) {
            throw new TemplateModelException(
                "The dump directive doesn't allow loop variables.");
        }
        if (body != null) {
            throw new TemplateModelException(
                "The dump directive doesn't allow nested content.");
        }
        
        Object o = params.get("var");
        
        if ( o == null) {
            throw new TemplateModelException(
                "Must specify 'var' argument.");
        }     
        
        if ( !(o instanceof SimpleScalar)) {
            throw new TemplateModelException(
               "Value of parameter 'var' must be a string.");     
        }
        
        String varName = o.toString(); //((SimpleScalar)o).getAsString();   
        Map<String, Object> map = getTemplateVariableDump(varName, env); 
        String title = "Template variable dump";
        dump(map, env, title);   
    }
    
    @Override
    public Map<String, Object> help(String name) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        
        map.put("effect", "Dumps the contents of a template variable.");

        Map<String, String> params = new HashMap<String, String>();
        params.put("var", "name of variable to dump");
        map.put("parameters", params);
        
        List<String> examples = new ArrayList<String>();
        examples.add("<@" + name + " var=\"urls\" />");
        map.put("examples", examples);
        
        return map;

    }
}
