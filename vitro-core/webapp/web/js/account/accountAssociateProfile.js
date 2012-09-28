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

var associateProfileFields = {
    onLoad: function() {
        if (this.disableFormInUnsupportedBrowsers()) {
            return;
        }        

        this.mixIn();
        this.initObjectReferences();                 
        this.bindEventListeners();
        this.setInitialState();       
    },

    disableFormInUnsupportedBrowsers: function() {       
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
    },

    mixIn: function() {
        $.extend(this, associateProfileFieldsData);
    },
    
    initObjectReferences: function() {
        this.form = $('#userAccountForm');
        
        // The external auth ID field and messages
        this.externalAuthIdField = $('#externalAuthId');
        this.externalAuthIdInUseMessage = $('#externalAuthIdInUse');

        // We have an associated profile
        this.associatedArea = $('#associated');
        this.associatedProfileNameSpan = $('#associatedProfileName');
        this.verifyAssociatedProfileLink = $('#verifyProfileLink');
        this.changeAssociatedProfileLink = $('#changeProfileLink');
        this.associatedProfileUriField = $('#associatedProfileUri')
        
        // We want to associate a profile
        this.associationOptionsArea = $('#associationOptions');
        this.associateProfileNameField = $('#associateProfileName');
        this.newProfileClassSelector = $('#newProfileClassUri');
        
        // Container <div> elements to provide background shading -- tlw72
        this.associateProfileBackgroundOneArea = $('#associateProfileBackgroundOne');
    },

    bindEventListeners: function() {
        this.idCache = {};
        this.externalAuthIdField.change(function() {
            associateProfileFields.externalAuthIdFieldHasChanged();
        }); 
        this.externalAuthIdField.keyup(function() {
            associateProfileFields.externalAuthIdFieldHasChanged();
        }); 
        this.externalAuthIdField.bind("propertychange", function() {
            associateProfileFields.externalAuthIdFieldHasChanged();
        });  
        this.externalAuthIdField.bind("input", function() {
            associateProfileFields.externalAuthIdFieldHasChanged();
        });
              
        this.verifyAssociatedProfileLink.click(function() {
            associateProfileFields.openVerifyWindow();
            return false;
        });   
        
        this.changeAssociatedProfileLink.click(function() {
            associateProfileFields.showAssociatingOptionsArea();
            return false;
        });   
        
        this.newProfileClassSelector.change(function() {
            associateProfileFields.newProfileClassHasChanged();
        });
        
        this.acCache = {};  
        this.associateProfileNameField.autocomplete({
            minLength: 3,
            source: function(request, response) {
                if (request.term in associateProfileFields.acCache) {
                    response(associateProfileFields.acCache[request.term]);
                    return;
                }
                $.ajax({
                    url: associateProfileFields.ajaxUrl,
                    dataType: 'json',
                    data: {
                        action: "autoCompleteProfile",
                        term: request.term,
                        externalAuthId: associateProfileFields.externalAuthIdField.val()
                    }, 
                    complete: function(xhr, status) {
                        var results = jQuery.parseJSON(xhr.responseText);
                        associateProfileFields.acCache[request.term] = results;  
                        response(results);
                    }
                });
            },
            select: function(event, ui) {
                associateProfileFields.showAssociatedProfileArea(ui.item.label, ui.item.uri, ui.item.url); 
            }
        });

        
    },
    
    setInitialState: function() {
        if (this.externalAuthIdField.val().length == 0) {
            this.hideAllOptionals();
        } else if (this.associatedProfileInfo) {
            this.showAssociatedProfileArea(this.associatedProfileInfo.label, this.associatedProfileInfo.uri, this.associatedProfileInfo.url);
        } else {
            this.showAssociatingOptionsArea();
        }
    },
    
    externalAuthIdFieldHasChanged: function() {
        var externalAuthId = this.externalAuthIdField.val();
        
        if (externalAuthId.length == 0) {
            this.hideAllOptionals();
            return;
        }
        
        if (externalAuthId in this.idCache) {
            var results = this.idCache[externalAuthId];
            this.applyAjaxResultsForExternalAuthIdField(results)
            return;
        }

        $.ajax({
            url: associateProfileFields.ajaxUrl,
            dataType: "json",
            data: {
                action: "checkExternalAuth",
                userAccountUri: associateProfileFields.userUri,
                externalAuthId: externalAuthId
            },
            complete: function(xhr, status) {
                var results = $.parseJSON(xhr.responseText);
                associateProfileFields.idCache[externalAuthId] = results;  
                associateProfileFields.applyAjaxResultsForExternalAuthIdField(results);
            }
        });
    },

    applyAjaxResultsForExternalAuthIdField: function(results) {
        if (results.idInUse) {
            this.showExternalAuthInUseMessage()
        } else if (results.matchesProfile) {
            this.showAssociatedProfileArea(results.profileLabel, results.profileUri, results.profileUrl)
        } else {
            this.showAssociatingOptionsArea();
        }
    },
        
    openVerifyWindow: function() {
        window.open(this.verifyUrl, 'verifyMatchWindow', 'width=640,height=640,scrollbars=yes,resizable=yes,status=yes,toolbar=no,menubar=no,location=no');
    },
    
    newProfileClassHasChanged: function() {
        if (this.newProfileClassSelector.val().length == 0) {
            this.associateProfileNameField.attr("disabled","");
        } else {
            this.associateProfileNameField.val('');
            this.associateProfileNameField.attr("disabled","disabled");
        }
    },
    
    hideAllOptionals: function() {
        this.hideExternalAuthInUseMessage();
        this.hideAssociatedProfileArea();
        this.hideAssociatingOptionsArea();
    },
    
    hideExternalAuthInUseMessage: function() {
        this.externalAuthIdInUseMessage.hide();
    },
    
    hideAssociatedProfileArea: function() {
        this.associatedArea.hide();
        this.associateProfileBackgroundOneArea.css("background-color","#fff");
        this.associateProfileBackgroundOneArea.css("border","none");
        this.associatedProfileUriField.val('');
    },
    
    hideAssociatingOptionsArea: function() {
        this.associationOptionsArea.hide();
        this.associateProfileBackgroundOneArea.css("background-color","#fff");
        this.associateProfileBackgroundOneArea.css("border","none");
        this.associateProfileNameField.val('');
        this.newProfileClassSelector.get(0).selectedIndex = 0;
    },
    
    showExternalAuthInUseMessage: function() {
        this.hideAssociatedProfileArea();
        this.hideAssociatingOptionsArea();

        this.externalAuthIdInUseMessage.show();
    },
    
    showAssociatedProfileArea: function(name, uri, url) {
        this.hideExternalAuthInUseMessage();
        this.hideAssociatingOptionsArea();

        if (this.associationEnabled) {
            this.associatedProfileNameSpan.html(name);
            this.associatedProfileUriField.val(uri);
            this.verifyUrl = url;
            this.associatedArea.show();
            this.associateProfileBackgroundOneArea.css("background-color","#f1f2ee");
            this.associateProfileBackgroundOneArea.css("border","1px solid #ccc");
        }
    },
    
    showAssociatingOptionsArea: function() {
        this.hideExternalAuthInUseMessage();
        this.hideAssociatedProfileArea();

        if (this.associationEnabled) {
            this.newProfileClassHasChanged();
            this.associationOptionsArea.show();
            this.associateProfileBackgroundOneArea.css("background-color","#f1f2ee");
            this.associateProfileBackgroundOneArea.css("border","1px solid #ccc");
        }
    }
    
}
 
$(document).ready(function() {   
    associateProfileFields.onLoad();
}); 
        
