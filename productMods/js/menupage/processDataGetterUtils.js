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

//This class is responsible for the product-specific form processing/content selection that might be possible
//Overrides the usual behavior of selecting the specific JavaScript class needed to convert the form inputs
//into a JSON object for submission based on the page content type
//VIVO Specific version includes individuals for classes data getter in addition to the others
//The internal class specific processor class is in VIVO, while all other javascript files are in Vitro

var processDataGetterUtils = {
		dataGetterProcessorMap:{"browseClassGroup": processClassGroupDataGetterContent, 
								"sparqlQuery": processSparqlDataGetterContent, 
								"fixedHtml":processFixedHTMLDataGetterContent,
								"internalClass":processInternalClassDataGetterContent},
	    selectDataGetterType:function(pageContentSection) {
			var contentType = pageContentSection.attr("contentType");
			//The form can provide "browse class group" as content type but need to check
			//whether this is in fact individuals for classes instead
			if(contentType == "browseClassGroup") {
				//Is ALL NOT selected and there are other classes, pick one
				//this SHOULD be an array
				var allClassesSelected = pageContentSection.find("input[name='allSelected']:checked");
				var isInternalSelected = pageContentSection.find("input[name='display-internalClass']:checked");
				//If all NOT selected then need to pick a different content type OR if internal class selected
				if( isInternalSelected.length > 0 || allClassesSelected.length == 0) {
					contentType = "internalClass";
				}
			} 
			
			return contentType;
	    },
	    isRelatedToBrowseClassGroup:function(contentType) {
	    	return (contentType == "browseClassGroup" || contentType == "internalClass");
	    },
	    getContentTypeForCloning:function(contentType) {
	    	if(contentType == "browseClassGroup" || contentType == "internalClass") {
	    		return "browseClassGroup";
	    	} 
	    	return contentType;
	    }
};