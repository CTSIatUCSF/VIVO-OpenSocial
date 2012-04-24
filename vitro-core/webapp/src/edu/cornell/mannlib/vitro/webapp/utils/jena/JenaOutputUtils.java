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

package edu.cornell.mannlib.vitro.webapp.utils.jena;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.beans.Ontology;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;

public class JenaOutputUtils {
	
	private static final Log log = LogFactory.getLog(JenaOutputUtils.class.getName());
		
	public static void setNameSpacePrefixes(Model model, WebappDaoFactory wadf) {
		
		if (model == null) {
			log.warn("input model is null");
			return;
		}
		
		Map<String,String> prefixes = new HashMap<String,String>();
		List<Ontology> ontologies = wadf.getOntologyDao().getAllOntologies();
		Iterator<Ontology> iter = ontologies.iterator();
		String namespace = null;
		String prefix = null;
		
		prefixes.put("vitro", "http://vitro.mannlib.cornell.edu/ns/vitro/0.7#");
		while (iter.hasNext()) {
			Ontology ontology = iter.next();
            			
			namespace = ontology.getURI(); // this method returns the namespace
            if (namespace == null || namespace.isEmpty()) {
            	log.warn("ontology with empty namespace found");
            	continue;
            }
            
            prefix = ontology.getPrefix();
            if (prefix == null || prefix.isEmpty()) {
            	log.debug("no prefix found for namespace: " + namespace);
            	continue;
            }
            
			prefixes.put(prefix,namespace);
		}
	    
		model.setNsPrefixes(prefixes);
		return;
	}	
}
