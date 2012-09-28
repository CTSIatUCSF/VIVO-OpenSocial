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
package edu.cornell.mannlib.vitro.webapp.search.solr;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;

import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;

import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.CalculateParameters;
import edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding.DocumentModifier;

public class VivoDocumentModifiers implements javax.servlet.ServletContextListener{
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        ServletContext context = sce.getServletContext();
		RDFServiceFactory rdfServiceFactory = RDFServiceUtils.getRDFServiceFactory(context);
        
        Dataset dataset = DatasetFactory.create(ModelContext.getJenaOntModel(context));
        
        /* put DocumentModifiers into servlet context for use later in startup by SolrSetup */        
        
        List<DocumentModifier> modifiers = new ArrayList<DocumentModifier>();                                        
        modifiers.add(new CalculateParameters(dataset));        //
        modifiers.add(new VivoAgentContextNodeFields(rdfServiceFactory));
        modifiers.add(new VivoInformationResourceContextNodeFields(rdfServiceFactory));
        
        context.setAttribute("DocumentModifiers", modifiers);
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing.        
    }    
}
