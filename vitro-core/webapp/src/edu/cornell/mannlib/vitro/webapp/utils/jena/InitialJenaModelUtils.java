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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.ResourceUtils;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;

public class InitialJenaModelUtils {

	private static final String INIT_DATA_DIRECTORY = "/WEB-INF/init-data";
	
	private static final Log log = LogFactory.getLog(InitialJenaModelUtils.class);
	
	public static Model loadInitialModel(ServletContext ctx, String defaultNamespace) {
	
		OntModel initialModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		
		try {
			JenaDataSourceSetupBase.readOntologyFilesInPathSet(INIT_DATA_DIRECTORY, ctx, initialModel);
		} catch (Throwable t) {
			log.warn("Unable to read initial model data from " + INIT_DATA_DIRECTORY, t);
		}
	
		if (initialModel.size() == 0) {
			return initialModel;
		}
		
		//find and rename portal object
		//currently, only a single portal is permitted in the initialization data
		Resource portalResource = null;
		ClosableIterator<Resource> portalResIt = initialModel.listSubjectsWithProperty(RDF.type, initialModel.getResource(VitroVocabulary.PORTAL));
		try {
			if (portalResIt.hasNext()) {
				Resource portalRes = portalResIt.next();
				if (portalRes.isAnon()) {
					portalResource = portalRes;
				}
			}
		} finally {
			portalResIt.close();
		}
		if (portalResource != null) {
			ResourceUtils.renameResource(portalResource, defaultNamespace + "portal1");
		}
		
		return initialModel;
		
	}
	
}
