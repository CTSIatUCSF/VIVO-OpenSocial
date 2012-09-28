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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeIntervalValidationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.DateTimeWithPrecisionVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;
import edu.cornell.mannlib.vitro.webapp.utils.generators.EditModeUtils;

public class DateTimeIntervalFormGenerator extends
		BaseEditConfigurationGenerator implements EditConfigurationGenerator {

	final static String vivoCore = "http://vivoweb.org/ontology/core#";
	final static String toDateTimeInterval = vivoCore + "dateTimeInterval";
	final static String intervalType = vivoCore + "DateTimeInterval";
	final static String intervalToStart = vivoCore+"start";
	final static String intervalToEnd = vivoCore + "end";
	final static String dateTimeValue = vivoCore + "dateTime";
	final static String dateTimeValueType = vivoCore + "DateTimeValue";
	final static String dateTimePrecision = vivoCore + "dateTimePrecision";
	
	@Override
	public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
			HttpSession session) {
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
        
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("dateTimeIntervalForm.ftl");
        
        conf.setVarNameForSubject("subject");
        conf.setVarNameForPredicate("toDateTimeInterval");
        conf.setVarNameForObject("intervalNode");
        
        conf.setN3Optional(Arrays.asList(n3ForStart, n3ForEnd));
        
        conf.addNewResource("intervalNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("startNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        conf.addNewResource("endNode", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        conf.addSparqlForExistingLiteral(
        		"startField-value", existingStartDateQuery);
        conf.addSparqlForExistingLiteral(
        		"endField-value", existingEndDateQuery);
        conf.addSparqlForExistingUris(
        		"intervalNode", existingIntervalNodeQuery);
        conf.addSparqlForExistingUris("startNode", existingStartNodeQuery);
        conf.addSparqlForExistingUris("endNode", existingEndNodeQuery);
        conf.addSparqlForExistingUris(
        		"startField-precision", existingStartPrecisionQuery);
        conf.addSparqlForExistingUris(
        		"endField-precision", existingEndPrecisionQuery);
        
        FieldVTwo startField = new FieldVTwo().setName("startField");
        		startField.setEditElement(new DateTimeWithPrecisionVTwo(startField, 
        				VitroVocabulary.Precision.SECOND.uri(), 
        				VitroVocabulary.Precision.NONE.uri()));

        FieldVTwo endField = new FieldVTwo().setName("endField");
        		endField.setEditElement(new DateTimeWithPrecisionVTwo(endField, 
        		        VitroVocabulary.Precision.SECOND.uri(), 
            			VitroVocabulary.Precision.NONE.uri()));
        
        conf.addField(startField);
        conf.addField(endField);
        //Need to add validators
        conf.addValidator(new DateTimeIntervalValidationVTwo("startField","endField"));
        //Adding additional data, specifically edit mode
        addFormSpecificData(conf, vreq);
        //Prepare
        prepare(vreq, conf);
        return conf;
        
	}
	
	final static String n3ForStart = 
	    "?subject <" + toDateTimeInterval + "> ?intervalNode . \n" +    
	    "?intervalNode  a <" + intervalType + "> . \n" + 
	    "?intervalNode <" + intervalToStart + "> ?startNode . \n" +    
	    "?startNode a <" + dateTimeValueType + "> . \n" +
	    "?startNode  <" + dateTimeValue + "> ?startField-value . \n" +
	    "?startNode  <" + dateTimePrecision + "> ?startField-precision . \n";
	
	final static String n3ForEnd = 
        "?subject <" + toDateTimeInterval + "> ?intervalNode . \n" +       
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n" +
        "?endNode  <" + dateTimeValue + "> ?endField-value . \n" +
        "?endNode  <" + dateTimePrecision + "> ?endField-precision .";
	
	final static String existingStartDateQuery =
        "SELECT ?existingDateStart WHERE { \n" +     
        "?subject <" + toDateTimeInterval + "> ?existingIntervalNode . \n" +     
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "?startNode a <" + dateTimeValueType + "> . \n" +
        "?startNode <" + dateTimeValue + "> ?existingDateStart }";

	final static String existingEndDateQuery = 
        "SELECT ?existingEndDate WHERE { \n" +
        "?subject <" + toDateTimeInterval + "> ?existingIntervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n " +
        "?endNode <" + dateTimeValue + "> ?existingEndDate . }";
	
	final static String existingIntervalNodeQuery = 
        "SELECT ?existingIntervalNode WHERE { \n" +
        "?subject <" + toDateTimeInterval + "> ?existingIntervalNode . \n" +
        "?existingIntervalNode a <" + intervalType + "> . }";
	
	final static String existingStartNodeQuery =
		"SELECT ?existingStartNode WHERE { \n" +
        "?subject <" + toDateTimeInterval + "> ?existingIntervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?existingStartNode . \n" + 
        "?existingStartNode a <" + dateTimeValueType + "> .}  ";
	
	final static String existingEndNodeQuery = 
        "SELECT ?existingEndNode WHERE { \n" + 
        "?subject <" + toDateTimeInterval + "> ?existingIntervalNode . \n" +
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?existingEndNode . \n" + 
        "?existingEndNode a <" + dateTimeValueType + "> .} ";
	
	final static String existingStartPrecisionQuery = 
        "SELECT ?existingStartPrecision WHERE { \n" +    
        "?subject <" + toDateTimeInterval + "> ?existingIntervalNode . \n" +      
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToStart + "> ?startNode . \n" +
        "?startNode a <" + dateTimeValueType + "> . \n" +          
        "?startNode <" + dateTimePrecision + "> ?existingStartPrecision . }";
	
	final static String existingEndPrecisionQuery =
		"SELECT ?existingEndPrecision WHERE { \n" +
        "?subject <" + toDateTimeInterval + "> ?existingIntervalNode . \n" +      
        "?intervalNode a <" + intervalType + "> . \n" +
        "?intervalNode <" + intervalToEnd + "> ?endNode . \n" +
        "?endNode a <" + dateTimeValueType + "> . \n" +          
        "?endNode <" + dateTimePrecision + "> ?existingEndPrecision . }";

    //Adding form specific data such as edit mode
    public void addFormSpecificData(EditConfigurationVTwo editConfiguration, VitroRequest vreq) {
    	HashMap<String, Object> formSpecificData = new HashMap<String, Object>();
    	formSpecificData.put("editMode", getEditMode(vreq).name().toLowerCase());
    	editConfiguration.setFormSpecificData(formSpecificData);
    }

	public EditMode getEditMode(VitroRequest vreq) {
		//In this case, the original jsp didn't rely on FrontEndEditingUtils
		//but instead relied on whether or not the object Uri existed
		String objectUri = EditConfigurationUtils.getObjectUri(vreq);
		EditMode editMode = FrontEndEditingUtils.EditMode.ADD;
		if(objectUri != null && !objectUri.isEmpty()) {
			editMode = FrontEndEditingUtils.EditMode.EDIT;
			
		}
		return editMode;
	}
}
