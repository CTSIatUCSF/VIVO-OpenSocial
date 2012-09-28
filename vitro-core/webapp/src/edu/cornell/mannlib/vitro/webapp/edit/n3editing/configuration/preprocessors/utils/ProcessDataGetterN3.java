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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.preprocessors.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import javax.servlet.ServletContext;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

//Returns the appropriate n3 based on data getter

public interface ProcessDataGetterN3 {
	public String getClassType();
	public List<String> retrieveN3Required(int counter);
    public List<String> retrieveN3Optional(int counter);
    public List<String >retrieveLiteralsOnForm(int counter);
    
     
    public List<String> retrieveUrisOnForm(int counter);
    public List<FieldVTwo> retrieveFields(int counter);
    public List<String> getLiteralVarNamesBase();
    public List<String> getUriVarNamesBase();
    public String getVarName(String base, int counter);
    public String getDataGetterVar(int counter);
    public String getDataGetterVarName(int counter);
    public List<String> getNewResources(int counter);
    
    //Get Existing values to put in scope
    public Map<String, List<Literal>> retrieveExistingLiteralValues();
    public Map<String, List<String>> retrieveExistingUriValues();
    public void populateExistingValues(String dataGetterURI, int counter, OntModel queryModel);
    public JSONObject getExistingValuesJSON(String dataGetterURI, OntModel queryModel, ServletContext context);
    public String replaceEncodedQuotesWithEscapedQuotes(String inputStr);

}
