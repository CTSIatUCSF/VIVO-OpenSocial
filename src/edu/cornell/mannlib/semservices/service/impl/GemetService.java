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

package edu.cornell.mannlib.semservices.service.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fao.gilw.aims.webservices.AgrovocWS;
import org.fao.gilw.aims.webservices.AgrovocWSServiceLocator;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.semservices.util.XMLUtils;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;

public class GemetService implements ExternalConceptService  {
   protected final Log logger = LogFactory.getLog(getClass());
   private final String GemetWS_address = "http://www.eionet.europa.eu/gemet/";
   private final String narrowerUri = "http://www.w3.org/2004/02/skos/core%23narrower";
   private final String broaderUri = "http://www.w3.org/2004/02/skos/core%23broader";
   private final String relatedUri = "http://www.w3.org/2004/02/skos/core%23related";
   private final String definitionUri = "http://www.w3.org/2004/02/skos/core%23definition";
   private final String prefLabelUri = "http://www.w3.org/2004/02/skos/core%23prefLabel";
   private final String scopeNoteUri = "http://www.w3.org/2004/02/skos/core%23scopeNote";
   private final String altLabelUri = "http://www.w3.org/2004/02/skos/core%23altLabel";
   private final String exampleUri = "http://www.w3.org/2004/02/skos/core%23example";
   private final String acronymLabelUri = "http://www.w3.org/2004/02/skos/core%23acronymLabel";

   public List<Concept> processResults(String term) throws Exception {
      List<Concept> conceptList = processConceptsAndRelatedMatchingKeyword(term);
      return conceptList;

   }

   /**
    * @param results
    * @return
    * By default, concepts set with best match = true
    */
   private List<Concept> processOutput(String results) {
     return processOutput(results, "true");

   }
   
   private List<Concept> processOutput(String results, String bestMatch) {
	  List<Concept> conceptList = new ArrayList<Concept>();
      try {
         JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON( results );
	   
			for (int i = 0; i < jsonArray.size(); i++) {
				Concept concept = new Concept();
				concept
						.setDefinedBy("http://www.eionet.europa.eu/gemet/gemetThesaurus");
				concept.setBestMatch(bestMatch);
				JSONObject json = jsonArray.getJSONObject(i);
				String uri = getJsonValue(json, "uri");

				concept.setUri(uri);
				concept.setConceptId(uri);
				if (json.has("preferredLabel")) {
					JSONObject preferredLabelObj = json
							.getJSONObject("preferredLabel");
					if (preferredLabelObj.has("string")) {
						concept.setLabel(getJsonValue(preferredLabelObj,
								"string"));
					}
				}
				if (json.has("definition")) {
					JSONObject definitionObj = json.getJSONObject("definition");
					if (definitionObj.has("string")) {
						concept.setDefinition(getJsonValue(definitionObj,
								"string"));
					}
				}
				conceptList.add(concept);

			}
         

      } catch (Exception ex ) {
         ex.printStackTrace();
         logger.error("Could not get concepts", ex);
      }
      return conceptList;
   }

   /**
    * Get a string from a json object or an empty string if there is no value for the given key
   * @param obj
   * @param key
   * @return
   */
  protected String getJsonValue(JSONObject obj, String key) {
      if (obj.has(key)) {
         return obj.getString(key);
      } else {
         return new String("");
      }
   }


   protected String getAvailableLangs(String concept_uri) throws Exception {
      String result = new String();
      String serviceUrl = GemetWS_address + "getAvailableLanguages" +
      "?concept_uri=" + concept_uri;
      result = getGemetResults(serviceUrl);
      return result;
   }

   protected String getConcept(String concept_uri) throws Exception {
      String result = new String();
      String serviceUrl = GemetWS_address + "getConcept" +
      "?concept_uri=" + concept_uri +
      "&language=en";
      result = getGemetResults(serviceUrl);
      return result;
   }
   protected String getAllTranslationsForConcept(String concept_uri, String property) throws Exception  {
      String result = new String();
      String property_uri = new String();
      if (property.equals("definition")) {
         property_uri = definitionUri;
      } else if (property.equals("preferredLabel")) {
         property_uri = prefLabelUri;
      } else if (property.equals("scopeNote")) {
         property_uri = scopeNoteUri;
      } else if (property.equals("nonPreferredLabels")) {
         property_uri = altLabelUri;
      } else if (property.equals("example")) {
         property_uri = exampleUri;
      } else if (property.equals("acronymLabel")) {
         property_uri = acronymLabelUri;
      }

      String serviceUrl = GemetWS_address + "getAllTranslationsForConcept" +
      "?concept_uri=" + concept_uri +
      "&property_uri=" + property_uri +
      "&language=en";
      result = getGemetResults(serviceUrl);
      return result;
   }


   protected String getRelatedConcepts(String concept_uri, String relation) throws Exception {
      String result = new String();
      String relation_uri = new String();
      if (relation.equals("broader")) {
         relation_uri = broaderUri;
      } else if (relation.equals("narrower")) {
         relation_uri = narrowerUri;
      } else if (relation.equals("related")) {
         relation_uri = relatedUri;
      }
      String serviceUrl = GemetWS_address + "getRelatedConcepts" +
      "?concept_uri=" + concept_uri +
      "&relation_uri=" + relation_uri +
      "&language=en";
      result = getGemetResults(serviceUrl);
      return result;
   }



   protected String getConceptsMatchingKeyword(String keyword) throws Exception {
      String result = new String();
      String serviceUrl = GemetWS_address + "getConceptsMatchingKeyword" +
      "?keyword="  + URLEncoder.encode(keyword) +
      "&search_mode=0" +
      "&thesaurus_uri=http://www.eionet.europa.eu/gemet/concept/" +
      "&language=en";

      result = getGemetResults(serviceUrl);
      return result;

   }
   
   //Get concepts matching keyword plus any related concepts
   protected List<Concept> processConceptsAndRelatedMatchingKeyword(String keyword) throws Exception {
	      String result = getConceptsMatchingKeyword(keyword);
	      //iterate through each of the concepts and add related concepts a well
	      List<Concept> bestMatchConceptList = processOutput(result);
	      List<Concept> relatedConceptList = new ArrayList<Concept>();
	      for(Concept c: bestMatchConceptList) {
	    	  String conceptUri = c.getUri();
	    	  String resultsRelated = getRelatedConcepts(conceptUri, "related");
	    	  relatedConceptList.addAll(processOutput(resultsRelated, "false"));
	      }
	      bestMatchConceptList.addAll(relatedConceptList);
	      return bestMatchConceptList;
	   }

   protected String getGemetResults(String url) throws Exception {
      String results = new String();
      //System.out.println("url: "+url);
      try {

         StringWriter sw = new StringWriter();
         URL serviceUrl = new URL(url);

         BufferedReader in = new BufferedReader(new InputStreamReader(serviceUrl.openStream()));
         String inputLine;
         while ((inputLine = in.readLine()) != null) {
            sw.write(inputLine);
         }
         in.close();

         results = sw.toString();

      } catch (Exception ex) {
         logger.error("error occurred in servlet", ex);
         throw ex;
      }
      return results;
   }

}
