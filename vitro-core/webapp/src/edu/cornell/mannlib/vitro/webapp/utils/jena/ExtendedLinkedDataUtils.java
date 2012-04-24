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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.ResourceUtils;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.cornell.mannlib.vitro.webapp.beans.ApplicationBean;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;

public class ExtendedLinkedDataUtils {
	
	private static final Log log = LogFactory.getLog(ExtendedLinkedDataUtils.class.getName());
	
	public static Model createModelFromQueries(ServletContext sc, String rootDir, OntModel sourceModel, String subject) {
		
		Model model = ModelFactory.createDefaultModel(); 
		
		Set<String> pathSet = sc.getResourcePaths(rootDir);
		
		if (pathSet == null) {
		  log.warn(rootDir + " not found.");
		  return model;
		}
		
		for ( String path : pathSet ) {
            File file = new File(sc.getRealPath(path));	
            if (file.isDirectory()) {           	
            	model.add(createModelFromQueries(sc, path, sourceModel, subject));
            } else if (file.isFile()) { 
    			if (!path.endsWith(".sparql")) {
    				log.warn("Ignoring file " + path + " because the file extension is not sparql.");
    				continue;
    			}
            	model.add(createModelFromQuery(file, sourceModel, subject));
            } else {
            	log.warn("path is neither a directory nor a file " + path);
            }
		} // end - for
				
		return model;
	}	
	
	public static Model createModelFromQuery(File sparqlFile, OntModel sourceModel, String subject) {
		
		Model model = ModelFactory.createDefaultModel(); 
						
		BufferedReader reader = null;
		
		try {
			try {
				reader = new BufferedReader(new FileReader(sparqlFile));
				StringBuffer fileContents = new StringBuffer();
				String ln;
			
				while ( (ln = reader.readLine()) != null) {
					fileContents.append(ln).append('\n');
				}		
						
				String query = fileContents.toString();
				String subjectString = "<" + subject + ">";
				query = query.replaceAll("PERSON_URI", subjectString);
				
				Query q = QueryFactory.create(query, Syntax.syntaxARQ);
				QueryExecution qe = QueryExecutionFactory.create(q, sourceModel);
				qe.execConstruct(model);
		   	} catch (Exception e) {
				log.error("Unable to process file " + sparqlFile.getAbsolutePath(), e);
			} finally {
				reader.close();
			}
		} catch (IOException ioe) {
			// this is for the reader.close above
			log.warn("Exception while trying to close file: " + sparqlFile.getAbsolutePath(), ioe);
		}			
				
		return model;
	}	
}
