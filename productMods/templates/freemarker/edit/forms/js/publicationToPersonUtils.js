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


var publicationToPersonUtils = {

    onLoad: function(href, blankSentinel) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        this.autoDateLabel.hide();
        this.baseHref = href;
        this.sentinel = blankSentinel;
        $.extend(this, vitro.customFormUtils);
        this.displayFieldsForType();

        if ( this.findValidationErrors() ) {
            this.resetLastNameLabel();
        }
        
    },

    initObjectReferences: function() {
    
        this.form = $('#addpublicationToPerson');
        this.pubTitle = $('input#title');
        this.collection = $('#collection');
        this.book = $('#book');
        this.presentedAt = $('#conference');
        this.proceedingsOf = $('#event');
        this.editor = $('#editor');
        this.editorUri = $('#editorUri');
        this.firstName = $('#firstName');
        this.lastName = $('#lastName');
        this.publisher = $('#publisher');
        this.locale = $('#locale');
        this.volume = $('#volume');
        this.volLabel = $('#volLabel');
        this.number = $('#number');
        this.nbrLabel = $('#nbrLabel');
        this.chapterNbr = $('#chapterNbr');
        this.chapterNbrLabel = $('#chapterNbrLabel');
        this.issue = $('#issue');
        this.issueLabel = $('#issueLabel');
        this.startPage = $('#startPage');
        this.sPLabel = $('#sPLabel');
        this.endPage = $('#endPage');
        this.ePLabel = $('#ePLabel');
        this.typeSelector = $('#typeSelector');
        this.cancel = $('.cancel');
        this.fullViewOnly = $('.fullViewOnly');
        this.autoDateLabel = null;
        
        this.form.find('label').each(function() {
            if ( $(this).attr('for') == "dateTime-year") {
                publicationToPersonUtils.autoDateLabel = $(this);
            }
        });

        this.pubAcSelection = $('div#pubAcSelection');
        this.fieldsForNewPub = $('#fieldsForNewPub');
        this.changeLink = this.pubAcSelection.children('p').children('#changeSelection');
        
        // may not need this
        this.firstName.attr('disabled', '');
        
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.typeSelector.change(function() {
            // controls the fieldsForNewPub div. If the user selects an existing pub/title, 
            // this div gets hidden. 
            publicationToPersonUtils.showFieldsForPub(); 
            // after a cancel, the first reset of the type selector resulted in all fields being displayed.
            // by delaying this function just slightly, the timing issue between this js and the 
            // customFormWithAutocomplete js is resolved 
            window.setTimeout('publicationToPersonUtils.displayFieldsForType()', 60);
        });
        
        // we need the delay in the next two functions to ensure the correct timing after the user
        // selects the ac item. The .change handles a mouse click; .blur an arrow key and tab selection
        this.pubTitle.change( function(objEvent) {
           window.setTimeout('publicationToPersonUtils.hideFieldsForPub()', 180); 
        });
        
        this.pubTitle.blur( function(objEvent) {
           window.setTimeout('publicationToPersonUtils.hideFieldsForPub()', 180); 
        });
        
        this.changeLink.click( function() {
           publicationToPersonUtils.showFieldsForPub(); 
        });
        
        this.form.submit(function() {
            publicationToPersonUtils.resolveEditorNames();
        });            
                
    },
    
    hideFieldsForPub: function() {
       if ( this.pubAcSelection.attr('class').indexOf('userSelected') != -1 ) {
           this.fieldsForNewPub.slideUp(250);
       }
    },
    
    showFieldsForPub: function() {
        this.fieldsForNewPub.show();
    },
    
    resetAcSelection: function(groupName) {
        var $acSelection = this.form.find("div.acSelection[acGroupName='" + groupName + "']");
        this.hideFields($acSelection);
        $acSelection.removeClass('userSelected');
        $acSelection.find("input.acUriReceiver").val(this.sentinel);
        $acSelection.find("span").text('');
        $acSelection.find("a.verifyMatch").attr('href', this.baseHref);
    },
    
    getAcUriReceiverVal: function(groupName) {
        var $collectionDiv = this.form.find("div.acSelection[acGroupName='" + groupName + "']");
        return $collectionDiv.find('input#'+ groupName + 'Uri').val();
    },

    hideAllFields: function() {
        this.collection.parent('p').hide();
        this.book.parent('p').hide();
        this.presentedAt.parent('p').hide();
        this.proceedingsOf.parent('p').hide();
        this.editor.parent('p').hide();
        this.publisher.parent('p').hide();
        this.locale.parent('p').hide();
        this.volume.hide();
        this.volLabel.hide();
        this.number.hide();
        this.nbrLabel.hide();
        this.issue.hide();
        this.issueLabel.hide();
        this.chapterNbr.hide();
        this.chapterNbrLabel.hide();
        this.startPage.parent('p').hide();
        this.sPLabel.parent('p').hide();
    },

    displayFieldsForType: function() {
        // hide everything, then show what's needed based on type
        // simpler in the event the user changes the type
        this.hideAllFields();
        var selectedType = this.typeSelector.find(':selected').text();

        if ( selectedType == 'Academic Article' || selectedType == 'Article' || selectedType == 'Editorial Article' || selectedType == 'Review') {
            // if the user has changed type, keep any relevant values and display the 
            // acSelection as appropriate
            var ckForVal = this.getAcUriReceiverVal('collection');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.collection.parent('p').show();
            }
            this.volume.show();
            this.volLabel.show();
            this.issue.show();
            this.issueLabel.show();
            this.startPage.parent('p').show();
            this.sPLabel.parent('p').show();
            
            // if the user has changed type, ensure that irrelevant fields are cleared
            // and reset an acSelection divs
            if ( this.book.val() != '' && this.book.val().substring(0, 18) != "Select an existing" ) {
                this.book.val('');
                this.resetAcSelection('book');
            }
            if ( this.editor.val() != ''  && this.editor.val().substring(0, 18) != "Select an existing" ) {
                this.editor.val('');
                this.resetAcSelection('editor');
            }
            if ( this.publisher.val() != ''  && this.publisher.val().substring(0, 18) != "Select an existing" ) {
                this.publisher.val('');
                this.resetAcSelection('publisher');
            }
            if ( this.presentedAt.val() != ''  && this.presentedAt.val().substring(0, 18) != "Select an existing" ) {
                this.presentedAt.val('');
                this.resetAcSelection('conference');
            }
            if ( this.proceedingsOf.val() != ''  && this.proceedingsOf.val().substring(0, 18) != "Select an existing" ) {
                this.proceedingsOf.val('');
                this.resetAcSelection('event');
            }
                        
            this.locale.val('');
            this.number.val('');
            this.chapterNbr.val('');
        }
        else if ( selectedType == 'Chapter' ) {
            // if the user has changed type, keep any relevant values and display the 
            // acSelection as appropriate
            var ckForVal = this.getAcUriReceiverVal('book');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.book.parent('p').show();
            }
            ckForVal = this.getAcUriReceiverVal('editor');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.editor.parent('p').show();
            }
            ckForVal = this.getAcUriReceiverVal('publisher');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.publisher.parent('p').show();
            }
            
            this.locale.parent('p').show();            
            this.volume.show();
            this.volLabel.show();
            this.chapterNbr.show();
            this.chapterNbrLabel.show();
            this.startPage.parent('p').show();
            this.sPLabel.parent('p').show();
            
            // if the user is changing type, ensure that irrelevant fields are cleared
            // and reset an acSelection divs
            if ( this.collection.val() != ''  && this.collection.val().substring(0, 18) != "Select an existing" ) {
                this.collection.val('');
                this.resetAcSelection('collection');                
            }
            if ( this.presentedAt.val() != ''  && this.presentedAt.val().substring(0, 18) != "Select an existing" ) {
                this.presentedAt.val('');
                this.resetAcSelection('conference');
            }
            if ( this.proceedingsOf.val() != ''  && this.proceedingsOf.val().substring(0, 18) != "Select an existing" ) {
                this.proceedingsOf.val('');
                this.resetAcSelection('event');
            }
                                    
            this.number.val('');
            this.issue.val('');
            this.startPage.val('');
            this.endPage.val('');
        }
        else if ( selectedType == 'Book' || selectedType == 'Edited Book' ) {
            // if the user has changed type, keep any relevant values and display the 
            // acSelection as appropriate
            var ckForVal = this.getAcUriReceiverVal('editor');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.editor.parent('p').show();
            }
            ckForVal = this.getAcUriReceiverVal('publisher');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.publisher.parent('p').show();
            }
            
            this.locale.parent('p').show();            
            this.volume.show();
            this.volLabel.show();

            // if the user is changing type, ensure that irrelevant fields are cleared
            // and reset an acSelection divs
            if ( this.collection.val() != ''  && this.collection.val().substring(0, 18) != "Select an existing" ) {
                this.collection.val('');
                this.resetAcSelection('collection');                
            }
            if ( this.presentedAt.val() != ''  && this.presentedAt.val().substring(0, 18) != "Select an existing" ) {
                this.presentedAt.val('');
                this.resetAcSelection('conference');
            }
            if ( this.proceedingsOf.val() != ''  && this.proceedingsOf.val().substring(0, 18) != "Select an existing" ) {
                this.proceedingsOf.val('');
                this.resetAcSelection('event');
            }
                        
            this.number.val('');
            this.issue.val('');
            this.startPage.val('');
            this.endPage.val('');
            this.chapterNbr.val('');
        }
        else if ( selectedType == 'Conference Paper' ) {
            // if the user has changed type, keep any relevant values and display the 
            // acSelection as appropriate
            var ckForVal = this.getAcUriReceiverVal('collection');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.collection.parent('p').show();
            }
            ckForVal = this.getAcUriReceiverVal('conference');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.presentedAt.parent('p').show();
            }
            
            this.startPage.parent('p').show();
            this.sPLabel.parent('p').show();

            // if the user is changing type, ensure that irrelevant fields are cleared
            // and reset an acSelection divs
            if ( this.book.val() != '' && this.book.val().substring(0, 18) != "Select an existing" ) {
                this.book.val('');
                this.resetAcSelection('book');
            }
            if ( this.editor.val() != '' && this.editor.val().substring(0, 18) != "Select an existing" ) {
                this.editor.val('');
                this.resetAcSelection('editor');
            }
            if ( this.publisher.val() != '' && this.publisher.val().substring(0, 18) != "Select an existing" ) {
                this.publisher.val('');
                this.resetAcSelection('publisher');
            }
            if ( this.proceedingsOf.val() != '' && this.proceedingsOf.val().substring(0, 18) != "Select an existing" ) {
                this.proceedingsOf.val('');
                this.resetAcSelection('event');
            }

            this.number.val('');
            this.issue.val('');
            this.startPage.val('');
            this.endPage.val('');
            this.chapterNbr.val('');
        }
        else if ( selectedType == 'Conference Poster' || selectedType == 'Speech') {
            // if the user has changed type, keep any relevant values and display the 
            // acSelection as appropriate
            var ckForVal = this.getAcUriReceiverVal('conference');
            if ( ckForVal == '' || ckForVal == this.sentinel ) {
                this.presentedAt.parent('p').show();
            }

            // if the user is changing type, ensure that irrelevant fields are cleared
            // and reset an acSelection divs
            if ( this.collection.val() != '' && this.collection.val().substring(0, 18) != "Select an existing" ) {
                this.collection.val('');
                this.resetAcSelection('collection');                
            }
            if ( this.book.val() != '' && this.book.val().substring(0, 18) != "Select an existing" ) {
                this.book.val('');
                this.resetAcSelection('book');
            }
            if ( this.editor.val() != '' && this.editor.val().substring(0, 18) != "Select an existing" ) {
                this.editor.val('');
                this.resetAcSelection('editor');
            }
            if ( this.publisher.val() != '' && this.publisher.val().substring(0, 18) != "Select an existing" ) {
                this.publisher.val('');
                this.resetAcSelection('publisher');
            }
            if ( this.proceedingsOf.val() != '' && this.proceedingsOf.val().substring(0, 18) != "Select an existing" ) {
                this.proceedingsOf.val('');
                this.resetAcSelection('event');
            }

            this.number.val('');
            this.issue.val('');
            this.startPage.val('');
            this.endPage.val('');
            this.chapterNbr.val('');
        }
        else {

            // if the user is changing type, ensure that irrelevant fields are cleared
            // and reset an acSelection divs
            if ( this.collection.val() != '' && this.collection.val().substring(0, 18) != "Select an existing" ) {
                this.collection.val('');
                this.resetAcSelection('collection');                
            }
            if ( this.book.val() != '' && this.book.val().substring(0, 18) != "Select an existing" ) {
                this.book.val('');
                this.resetAcSelection('book');
            }
            if ( this.editor.val() != '' && this.editor.val().substring(0, 18) != "Select an existing" ) {
                this.editor.val('');
                this.resetAcSelection('editor');
            }
            if ( this.publisher.val() != '' && this.publisher.val().substring(0, 18) != "Select an existing" ) {
                this.publisher.val('');
                this.resetAcSelection('publisher');
            }
            if ( this.proceedingsOf.val() != '' && this.proceedingsOf.val().substring(0, 18) != "Select an existing" ) {
                this.proceedingsOf.val('');
                this.resetAcSelection('event');
            }
            if ( this.presentedAt.val() != '' && this.presentedAt.val().substring(0, 18) != "Select an existing" ) {
                this.presentedAt.val('');
                this.resetAcSelection('conference');
            }

            this.volume.val('');
            this.number.val('');
            this.issue.val('');
            this.startPage.val('');
            this.endPage.val('');
            this.chapterNbr.val('');     
        }
        
     },

     resolveEditorNames: function() {
         var firstName,
             lastName,
             name;

         // If editorUri contains the sentinel value, we need to process the name fields
         // otherwise, disable them so they are not submitted
         if (this.editor.parent('p').is(':visible') ) {
             if ( this.editor.val().indexOf('Select an existing') != -1 ) {
                 this.editor.val('');
             }
             if ( this.editorUri.val() == '' || this.editorUri.val() == this.sentinel ) {
                 firstName = this.firstName.val();
                 lastName = this.editor.val();
                 name = lastName;
                 if (firstName) {
                     name += ', ' + firstName;
                 }            
                 this.editor.val(name);
                 this.lastName.val(lastName);
             } 
             else {
                 this.disableNameFields();
             }
        }
        else {
            this.disableNameFields();
        }
        
     },
     
     disableNameFields: function() {
         this.firstName.attr('disabled', 'disabled');
         this.lastName.attr('disabled', 'disabled');
     },

     resetLastNameLabel: function() {
         var indx = this.editor.val().indexOf(", ");
         if ( indx != -1 ) {
             var temp = this.editor.val().substr(0,indx);
             this.editor.val(temp);
         }
     }
     
}