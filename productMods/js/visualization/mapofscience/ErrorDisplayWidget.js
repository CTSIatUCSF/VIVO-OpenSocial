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

var ErrorDisplayWidget = Class.extend({
	
	container: '',
	body: '',
	bodyID: 'error-body',
	messagePlaceholderID: 'variable-error-text',
	
	init: function(opts) {
	
		this.container = $("#" + opts.containerID);
		this.body = this.container.find("#" + this.bodyID);
	}, 
	
	isErrorConditionTriggered: function(responseData) {
		
		if (responseData.error) {
			return true;
		}
		
		if (responseData[0].pubsMapped === 0) {
			return true;
		}
		
		return false;
	},
	
	show: function(errorForType, responseData) {
		
		var isZeroPublicationsCase = responseData.error ? true : false;
		var newErrorMessage = "";
		
		/*
		 * This means that the organization or person has zero publications. 
		 * */
		if (isZeroPublicationsCase) {
			
			newErrorMessage += "No publications in the system have been attributed to this " + errorForType.toLowerCase() + ".";
			
		} else {
		/*
		 * This means that the organization or person has publications but none of them are mapped.
		 * Change the default text.
		 * */
			newErrorMessage += this._getUnScienceLocatedErrorMessage(errorForType, responseData[0]);
		} 

		/*
		 * Now replace the error message with the newly created one.
		 * */
		this.body.find("#" + this.messagePlaceholderID).html(newErrorMessage);	
		
		this.container.show();
	},
	
	_getUnScienceLocatedErrorMessage: function(errorForType, responseData) {
		
		var totalPublications = responseData.pubsWithNoJournals + responseData.pubsWithInvalidJournals;
		var newErrorMessage = "";

		if (totalPublications > 1) {
			newErrorMessage = "None of the " + totalPublications + " publications attributed to this " 
			+ errorForType.toLowerCase() + " have been 'science-located'.";	
			
		} else {
			newErrorMessage = "The publication attributed to this " 
				+ errorForType.toLowerCase() + " has not been 'science-located'.";
		}
		
		
		newErrorMessage += "<ul class='error-list'>";
		
		if (responseData.pubsWithNoJournals && responseData.pubsWithNoJournals > 0) {
			
			var publicationsText = (responseData.pubsWithNoJournals > 1) ? "publications" : "publication";
			
			newErrorMessage += "<li>" + responseData.pubsWithNoJournals + " " + publicationsText + " have no journal" 
				+ " information.</li>"
				
		}
		
		if (responseData.pubsWithInvalidJournals && responseData.pubsWithInvalidJournals > 0) {
			
			var publicationsText = (responseData.pubsWithInvalidJournals > 1) ? "publications" : "publication";
			
			newErrorMessage += "<li>" + responseData.pubsWithInvalidJournals + " " + publicationsText + " " 
			+ " could not be matched with a map location using their journal information.</li>" 				
		}
		
		newErrorMessage += "</ul>";
		
		return newErrorMessage;
	},
	
	hide: function() {
		this.container.hide();
	}
	
});