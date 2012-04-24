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

/*
 * ----------------------------------------------------------------------------
 * proxyItemsPanel
 * ----------------------------------------------------------------------------
 * Display an AJAX-enabled list of proxy-related items (either proxies or 
 * profiles). 
 * 
 * The list may start out with a population of items. items may be added by 
 * selecting them in the auto-complete box. Items may be removed by clicking
 * the "remove" link next to that item.
 * 
 * A hidden field will hold the URI for each item, so when the form is submitted,
 * the controller can determine the list of items.
 * ----------------------------------------------------------------------------
 * You provide:
 *   p -- the DOM element that contains the template and the data. 
 *         It also contains the autocomplete field, with a status element and
 *         perhaps 1 or more excluded URIs
 * ----------------------------------------------------------------------------
 */
function proxyItemsPanel(panel, contextInfo)  {
	var self = this;

	this.itemData = [];

	var dataContainerElement = $("[name='proxyData']", panel).first();
	var autoCompleteField = $("input[name='proxySelectorAC']", panel).first();
	var searchStatusField = $("span[name='proxySelectorSearchStatus']", panel).first();
	var excludedUris =  [];
	$("[name='excludeUri']", panel).each(function(index) {
		excludedUris.push($(this).text());
	});

	var parseTemplate = function(dataContainer) {
		var templateDiv = $("div[name='template']", dataContainer)
		var templateHtml = templateDiv.html();
		templateDiv.remove();
		return templateHtml;
	};
	this.templateHtml = parseTemplate(dataContainerElement);

	this.displayItemData = function() {
		$(".proxyInfoElement", dataContainerElement).remove();
		
		for (i = 0; i < self.itemData.length; i++) {
			self.itemData[i].element().appendTo(dataContainerElement);
		}
	}

	var getItemData = function() {
		return self.itemData;	
	}

	this.removeItem = function(info) {
		var i;
		for (i = 0; i < self.itemData.length; i++) {
			if (self.itemData[i] === info) {
				self.itemData.splice(i, 1);
				break;
			}
		}
		self.displayItemData();
	}

	this.addItemData = function(selection) {
		var info = new itemElement(self.templateHtml, selection.uri, selection.label, 
				selection.classLabel, selection.imageUrl, self.removeItem);
        self.itemData.unshift(info);
        self.displayItemData();
        self.getAdditionalData(self, info, selection.externalAuthId)
	}

	this.getAdditionalData = function(parent, info, externalAuthId) {
		data = info
	    $.ajax({
	        url: contextInfo.ajaxUrl,
	        dataType: 'json',
	        data: {
	        	action: contextInfo.moreInfoAction,
	        	uri: info.uri
	        },
	        complete: function(xhr, status) {
	            var results = $.parseJSON(xhr.responseText);
	            if (results.length > 0) {
	                if ("classLabel" in results[0]) {
	                    info.classLabel = results[0].classLabel;
	                }
	                if ("imageUrl" in results[0]) {
	                	info.imageUrl = results[0].imageUrl;
	                }
	                self.displayItemData();
	            }
	        }
	    });
	}
	
	var parseOriginalData = function() {
		var dataDivs = $("div[name='data']", dataContainerElement)
		var data = [];
		for (i = 0; i < dataDivs.length; i++) {
			var dd = dataDivs[i];
			var uri = $("p[name='uri']", dd).text();
			var label = $("p[name='label']", dd).text();
			var classLabel = $("p[name='classLabel']", dd).text();
			var imageUrl = $("p[name='imageUrl']", dd).text();
			data.push(new itemElement(self.templateHtml, uri, label, classLabel, imageUrl, self.removeItem));
		}
		return data;
	}
	this.itemData = parseOriginalData();

	var setupAutoCompleteFields = function() {
		var parms = {
		    url: contextInfo.ajaxUrl,
		    action: contextInfo.basicInfoAction
		}
		var updateStatus = new statusFieldUpdater(searchStatusField, 3).setText;
		var autocompleteInfo = new proxyAutocomplete(parms, excludedUris, getItemData, self.addItemData, updateStatus)
	    autoCompleteField.autocomplete(autocompleteInfo);
	}
	setupAutoCompleteFields();

	self.displayItemData();
}

function statusFieldUpdater(element, minLength) {
	var emptyText = element.text();
	var moreCharsText = element.attr('moreCharsText');
	var noMatchText = element.attr('noMatchText');

	this.setText = function(searchTermLength, numberOfResults) {
		if (numberOfResults > 0) {
			element.text('');
		} else if (searchTermLength == 0) {
			element.text(emptyText);
		} else if (searchTermLength < minLength) {
			element.text(moreCharsText);
		} else {
			element.text(noMatchText);
		}
	}
}

/*
 * Execute this when the page loads.
 */
$(document).ready(function() {
	var disableFormInUnsupportedBrowsers = function() {
		var disableWrapper = $('#ie67DisableWrapper');

		// Check for unsupported browsers only if the element exists on the page
		if (disableWrapper.length) {
			if (vitro.browserUtils.isIELessThan8()) {
				disableWrapper.show();
				$('.noIE67').hide();
				return true;
			}
		}
		return false;
	};

	/* If we don't support this form in this browser, just stop here. */
	if (disableFormInUnsupportedBrowsers()) {
		return;
	}

	$("section[name='proxyProfilesPanel']").each(function(i) {
		var context = {
			baseUrl: proxyContextInfo.baseUrl,
			ajaxUrl: proxyContextInfo.ajaxUrl,
			basicInfoAction: "getAvailableProfiles",
			moreInfoAction: "moreProfileInfo"
		}
		this["proxyItemsPanel"] = new proxyItemsPanel(this, context);
	});
	
	$("section[name='proxyProxiesPanel']").each(function(i) {
		var context = {
			baseUrl: proxyContextInfo.baseUrl,
			ajaxUrl: proxyContextInfo.ajaxUrl,
			basicInfoAction: "getAvailableProxies",
			moreInfoAction: "moreProxyInfo"
		}
		this["proxyItemsPanel"] = new proxyItemsPanel(this, context);
	});
	
	//Add progress indicator for autocomplete input fields
	
	var progressImage;
	
	$('#addProfileEditor').click(function(event){
        progressImage = $(event.target).closest("section").find(".loading-profileMyAccount")
    });
    
    $('#selectProfileEditors').click(function(event){
        progressImage = $(event.target).closest("section").find(".loading-relateEditor")
    });
    
    $('#selectProfiles').click(function(event){
        progressImage = $(event.target).closest("section").find(".loading-relateProfile")
    });
    
    $('#addProfile').click(function(event){
        progressImage = $(event.target).closest("section").find(".loading-addProfile")
    });
    
    
    $(document).ajaxStart(function(){
      progressImage.removeClass('hidden').css('display', 'inline-block');
    });
    
    $(document).ajaxStop(function(){
      progressImage.hide().addClass('hidden');
    });
});
