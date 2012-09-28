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

//Process sparql data getter and provide a json object with the necessary information
var processSparqlDataGetterContent = {
	dataGetterClass:null,
	//can use this if expect to initialize from elsewhere
	initProcessor:function(dataGetterClass) {
		this.dataGetterClass =dataGetterClass;
	},
	processPageContentSection:function(pageContentSection) {
		
		var variableValue = pageContentSection.find("input[name='saveToVar']").val();
		var queryValue = pageContentSection.find("textarea[name='query']").val();
		queryValue = processSparqlDataGetterContent.encodeQuotes(queryValue);
		var queryModel = pageContentSection.find("input[name='queryModel']").val();

		//query model should also be an input
		//set query model to query model here - vitro:contentDisplayModel
		var returnObject = {saveToVar:variableValue, query:queryValue, dataGetterClass:this.dataGetterClass, queryModel:queryModel};
		return returnObject;
	},
	//For an existing set of content where form is already set, fill in the values 
	populatePageContentSection:function(existingContentObject, pageContentSection) {
		var saveToVarValue = existingContentObject["saveToVar"];
		var queryValue = existingContentObject["query"];
		//replace any encoded quotes with escaped quotes that will show up as quotes in the textarea
		queryValue = processSparqlDataGetterContent.replaceEncodedWithEscapedQuotes(queryValue);
		var queryModelValue = existingContentObject["queryModel"];
		
		
		//Now find and set value
		pageContentSection.find("input[name='saveToVar']").val(saveToVarValue);
		pageContentSection.find("textarea[name='query']").val(queryValue);
		pageContentSection.find("input[name='queryModel']").val(queryModelValue);
	},
	//For the label of the content section for editing, need to add additional value
	retrieveContentLabel:function() {
		return "SPARQL Query Results";
	},
	//For the label of the content section for editing, need to add additional value
	retrieveAdditionalLabelText:function(existingContentObject) {
		var saveToVarValue = existingContentObject["saveToVar"];
		return saveToVarValue;
	},
    //Validation on form submit: Check to see that class group has been selected 
    validateFormSubmission: function(pageContentSection, pageContentSectionLabel) {
    	var validationError = "";
    	//Check that query and saveToVar have been input
    	var variableValue = pageContentSection.find("input[name='saveToVar']").val();
    	if(variableValue == "") {
    		validationError += pageContentSectionLabel + ": You must supply a variable to save query results. <br />"
    	}
    	if(processSparqlDataGetterContent.stringHasSingleQuote(variableValue)) {
    		validationError += pageContentSectionLabel + ": The variable name should not have an apostrophe . <br />";
    	}
    	if(processSparqlDataGetterContent.stringHasDoubleQuote(variableValue)) {
    		validationError += pageContentSectionLabel + ": The variable name should not have a double quote . <br />";
    	}
    	//Check that query  model does not have single or double quotes within it
    	//Uncomment this/adapt this when we actually allow display the query model input
    	/*
    	var queryModelValue = pageContentSection.find("input[name='queryModel']").val();
    	if(processSparqlDataGetterContent.stringHasSingleQuote(queryModelValue)) {
    		validationError += pageContentSectionLabel + ": The query model should not have an apostrophe . <br />";

    	}
    	if(processSparqlDataGetterContent.stringHasDoubleQuote(queryModelValue)) {
    		validationError += pageContentSectionLabel + ": The query model should not have a double quote . <br />";
	
    	}*/
    	
		var queryValue = pageContentSection.find("textarea[name='query']").val();
		if(queryValue == "") {
			validationError += pageContentSectionLabel + ": You must supply a Sparql query. <br />";
		}
    	return validationError;
    },
    encodeQuotes:function(inputStr) {
    	return inputStr.replace(/'/g, '&#39;').replace(/"/g, '&quot;');
    },
    //For the variable name, no single quote should be allowed
    //This can be extended for other special characters
    stringHasSingleQuote:function(inputStr) {
    	return(inputStr.indexOf("'") != -1);
    },
    stringHasDoubleQuote:function(inputStr) {
    	return(inputStr.indexOf("\"") != -1);
    },
    replaceEncodedWithEscapedQuotes: function(inputStr) {

    	return inputStr.replace(/&#39;/g, "\'").replace(/&quot;/g, "\"");
    }
		
		
};