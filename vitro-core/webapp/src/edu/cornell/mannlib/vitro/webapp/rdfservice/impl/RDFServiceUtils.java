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

package edu.cornell.mannlib.vitro.webapp.rdfservice.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.resultset.ResultSetFormat;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService.ModelSerializationFormat;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService.ResultFormat;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceFactory;

public class RDFServiceUtils {

	static Log log = LogFactory.getLog(RDFServiceUtils.class);
	
    private static final String RDFSERVICEFACTORY_ATTR = 
            RDFServiceUtils.class.getName() + ".RDFServiceFactory";
    
       
    public static RDFServiceFactory getRDFServiceFactory(ServletContext context) {
        Object o = context.getAttribute(RDFSERVICEFACTORY_ATTR);
        return (o instanceof RDFServiceFactory) ? (RDFServiceFactory) o : null;
    }
    
    public static void setRDFServiceFactory(ServletContext context, 
            RDFServiceFactory factory) {
        context.setAttribute(RDFSERVICEFACTORY_ATTR, factory);
    }
    
    public static InputStream toInputStream(String serializedRDF) {
        try {
            return new ByteArrayInputStream(serializedRDF.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Model parseModel(InputStream in, ModelSerializationFormat format) {
        Model model = ModelFactory.createDefaultModel();
        model.read(in, null,
                getSerializationFormatString(format));
        return model;
    }
    
    public static ResultSetFormat getJenaResultSetFormat(ResultFormat resultFormat) {
        switch(resultFormat) {
            case JSON:
                return ResultSetFormat.syntaxJSON;
            case CSV:
                return ResultSetFormat.syntaxCSV;
            case XML:
                return ResultSetFormat.syntaxXML;
            case TEXT:
                return ResultSetFormat.syntaxText;
            default:
                throw new RuntimeException("unsupported ResultFormat");
        }
    }
    
    public static String getSerializationFormatString(RDFService.ModelSerializationFormat format) {
        switch (format) {
            case RDFXML: 
                return "RDF/XML";
            case N3: 
                return "N3";
            case NTRIPLE:
                return "N-TRIPLE";
            default: 
                throw new RuntimeException("unexpected format in getSerializationFormatString");
        }
    }
    
    public static ModelSerializationFormat getSerializationFormatFromJenaString(String jenaString) {
        if ("N3".equals(jenaString) || "TTL".equals(jenaString) 
                || "TURTLE".equals(jenaString)) {
            return ModelSerializationFormat.N3;
        } else if ("N-TRIPLE".equals(jenaString)) {
            return ModelSerializationFormat.NTRIPLE;
        } else if ("RDF/XML".equals(jenaString) 
                || "RDF/XML-ABBREV".equals(jenaString)) {
            return ModelSerializationFormat.RDFXML;
        } else {
            throw new RuntimeException("unrecognized format " + jenaString);
        }
    }
    
    public static RDFService getRDFService(VitroRequest vreq) {
        return getRDFServiceFactory(
                vreq.getSession().getServletContext()).getRDFService();
    }

    public static ResultSet sparqlSelectQuery(String query, RDFService rdfService) {
    	
    	ResultSet resultSet = null;
    	
        try {
            InputStream resultStream = rdfService.sparqlSelectQuery(query, RDFService.ResultFormat.JSON);
            resultSet = ResultSetFactory.fromJSON(resultStream);
            return resultSet;
        } catch (RDFServiceException e) {        	
            log.error("error executing sparql select query: " + e.getMessage());
        }
        
        return resultSet;
    }    
}
