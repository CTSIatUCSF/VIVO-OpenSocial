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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * Accepts multiple vclasses and returns individuals which correspond to the
 * intersection of those classes (i.e. have all those types)
 */
public class GetSolrIndividualsByVClasses extends JsonObjectProducer {
	private static final Log log = LogFactory
		.getLog(GetSolrIndividualsByVClasses.class);

	public GetSolrIndividualsByVClasses(VitroRequest vreq) {
		super(vreq);
	}

	@Override
	protected JSONObject process() throws Exception {
    log.debug("Executing retrieval of individuals by vclasses");
        VClass vclass=null;
        log.debug("Retrieving solr individuals by vclasses");
        // Could have multiple vclass ids sent in
        String[] vitroClassIdStr = vreq.getParameterValues("vclassId");  
        if ( vitroClassIdStr != null && vitroClassIdStr.length > 0){    
        	for(String vclassId: vitroClassIdStr) {
        		log.debug("Iterating throug vclasses, using VClass " + vclassId);
                vclass = vreq.getWebappDaoFactory().getVClassDao().getVClassByURI(vclassId);
                if (vclass == null) {
                    log.error("Couldn't retrieve vclass ");   
                    throw new Exception ("Class " + vclassId + " not found");
                }   
        	}
        }else{
            log.error("parameter vclassId URI parameter expected but not found");
            throw new Exception("parameter vclassId URI parameter expected ");
        }
        List<String> vclassIds = Arrays.asList(vitroClassIdStr);
        return JsonServlet.getSolrIndividualsByVClasses(vclassIds, vreq, ctx);
    }

}
