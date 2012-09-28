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
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.semservices.bo.Concept;
import edu.cornell.mannlib.semservices.service.ExternalConceptService;
import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;

/**
 * @author jaf30
 *
 */
public class UMLSService implements ExternalConceptService {
   protected final Log logger = LogFactory.getLog(getClass());
   private static final String submissionUrl = "http://link.informatics.stonybrook.edu/MeaningLookup/MlServiceServlet?";
   private static final String baseUri = "http://link.informatics.stonybrook.edu/umls/CUI/";

   public List<Concept> processResults(String term) throws Exception{
      String results = null;
      String dataUrl = submissionUrl + "textToProcess="
            + URLEncoder.encode(term) + "&format=json";

      try {

         StringWriter sw = new StringWriter();
         URL rss = new URL(dataUrl);

         BufferedReader in = new BufferedReader(new InputStreamReader(rss.openStream()));
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
      List<Concept> conceptList = processOutput(results);
      return conceptList;
   }

   /**
    * @param results
    * @return
    */
   private List<Concept> processOutput(String results) {

      List<Concept> conceptList = new ArrayList<Concept>();
      List<String> bestMatchIdList = new ArrayList<String>();
      String bestMatchId = new String();
      try {
         JSONObject json = (JSONObject) JSONSerializer.toJSON( results );

         if (json.has("Best Match")) {
            //System.out.println("Best Match");

            JSONArray bestMatchArray = json.getJSONArray("Best Match");
            int len = bestMatchArray.size();
            if (len > 1) {
               System.out.println("Found this many best matches: "+ len);
            }
            int i;
            for (i = 0; i < len; i++) {
               JSONObject o = bestMatchArray.getJSONObject(i);
               //System.out.println(o.toString());
               Concept concept = new Concept();
               concept.setDefinedBy("http://link.informatics.stonybrook.edu/umls");
               concept.setBestMatch("true");
               String cui = getJsonValue(o, "CUI");
               bestMatchIdList.add(cui);

               concept.setConceptId(cui);
               concept.setLabel(getJsonValue(o, "label"));
               concept.setType(getJsonValue(o, "type"));
               concept.setDefinition(getJsonValue(o, "definition"));
               concept.setUri(baseUri + cui);
               conceptList.add(concept);
            }
         }
         if (json.has("All")) {

            JSONArray allArray = json.getJSONArray("All");
            int len = allArray.size();
            //System.out.println("size of best match array: "+ len);
            int i;
            for (i = 0; i < len; i++) {
               JSONObject o = allArray.getJSONObject(i);
               //System.out.println(o.toString());
               Concept concept = new Concept();
               concept.setDefinedBy("http://link.informatics.stonybrook.edu/umls");
               String cui = getJsonValue(o, "CUI");
               concept.setConceptId(cui);

               concept.setLabel(getJsonValue(o, "label"));
               concept.setType(getJsonValue(o, "type"));
               concept.setDefinition(getJsonValue(o, "definition"));
               concept.setUri(baseUri + cui);
               // prevent duplicate concepts in list
               if (! bestMatchIdList.contains(cui)) {
                  concept.setBestMatch("false");
                  conceptList.add(concept);
               }
            }
         }
      } catch (Exception ex ) {
         ex.printStackTrace();
         logger.error("Could not get concepts", ex);
      }
      return conceptList;

      //
      // now serialize the list of Concepts to a JSON String
      //
      //JSONObject jsonObject = null;
      //jsonObject = BeanToJsonSerializer.serializeToJsonObject(conceptList);
      //System.out.println(jsonObject.toString());
      //return jsonObject.toString();

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

}
