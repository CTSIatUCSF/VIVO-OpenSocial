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

package edu.cornell.mannlib.vitro.webapp.controller.json;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.jena.VClassGroupCache;

/**
 *
 */
public class GetVClassesForVClassGroup extends JsonObjectProducer {
	private static final Log log = LogFactory
			.getLog(GetVClassesForVClassGroup.class);

	public GetVClassesForVClassGroup(VitroRequest vreq) {
		super(vreq);
	}

	@Override
	protected JSONObject process() throws Exception {                
        JSONObject map = new JSONObject();           
        String vcgUri = vreq.getParameter("classgroupUri");
        if( vcgUri == null ){
            throw new Exception("no URI passed for classgroupUri");
        }
        
        VClassGroupCache vcgc = VClassGroupCache.getVClassGroupCache(ctx);
        VClassGroup vcg = vcgc.getGroup(vcgUri);
        if( vcg == null ){
            throw new Exception("Could not find vclassgroup: " + vcgUri);
        }        
                        
        ArrayList<JSONObject> classes = new ArrayList<JSONObject>(vcg.size());
        for( VClass vc : vcg){
            JSONObject vcObj = new JSONObject();
            vcObj.put("name", vc.getName());
            vcObj.put("URI", vc.getURI());
            vcObj.put("entityCount", vc.getEntityCount());
            classes.add(vcObj);
        }
        map.put("classes", classes);                
        map.put("classGroupName", vcg.getPublicName());
        map.put("classGroupUri", vcg.getURI());

        return map;
    }

}
