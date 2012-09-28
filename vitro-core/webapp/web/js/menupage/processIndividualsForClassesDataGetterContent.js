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

var processIndividualsForClassesDataGetterContent = {
	dataGetterClass:null,
	//can use this if expect to initialize from elsewhere
	initProcessor:function(dataGetterClassInput) {
		this.dataGetterClass = dataGetterClassInput;
	},
	//Do we need a separate content type for each of the others?
	processPageContentSection:function(pageContentSection) {
		//Will look at classes etc. 
		var classGroup = pageContentSection.find("select[name='selectClassGroup']").val();
		//query model should also be an input
		//Get classes selected
		var classesSelected = [];
		pageContentSection.find("input[name='classInClassGroup']:checked").each(function(){
			//Need to make sure that the class is also saved as a URI
			classesSelected.push($(this).val());
		});
		var returnObject = {classGroup:classGroup, classesSelectedInClassGroup:classesSelected, dataGetterClass:this.dataGetterClass};
		return returnObject;
	},
	//For the label of the content section for editing, need to add additional value
	retrieveContentLabel:function() {
		return processClassGroupDataGetterContent.retrieveContentLabel();
	},
	//For an existing set of content where form is already set, fill in the values 
	populatePageContentSection:function(existingContentObject, pageContentSection) {
		//select class group in dropdown and append the classes within that class group
		processClassGroupDataGetterContent.populatePageContentSection(existingContentObject, pageContentSection);
		var classesSelected = existingContentObject["classesSelectedInClassGroup"];
		var numberSelected = classesSelected.length;
		var i;
		//Uncheck all since default is checked
		pageContentSection.find("input[name='classInClassGroup']").removeAttr("checked");
		for(i = 0; i < numberSelected; i++) {
			var classSelected = classesSelected[i];
			pageContentSection.find("input[name='classInClassGroup'][value='" + classSelected + "']").attr("checked", "checked");
		}
		//If number of classes selected is not equal to total number of classes, uncheck all
		
		var results =existingContentObject["results"];
		if(results != null && results.classGroupName != null) {
	    	var resultsClasses = results["classes"];
	    	if(resultsClasses != null) {
	    		var numberClasses = resultsClasses.length;
	    		if(numberClasses != numberSelected) {
	    			pageContentSection.find("input[name='allSelected']").removeAttr("checked");
	    		}
	    	}
		}
	},
	//For the label of the content section for editing, need to add additional value
	retrieveAdditionalLabelText:function(existingContentObject) {
		return processClassGroupDataGetterContent.retrieveAdditionalLabelText(existingContentObject);
	},
	 //Validation on form submit: Check to see that class group has been selected 
    validateFormSubmission: function(pageContentSection, pageContentSectionLabel) {
    	return processClassGroupDataGetterContent.validateFormSubmission(pageContentSection, pageContentSectionLabel);
    }
		
}