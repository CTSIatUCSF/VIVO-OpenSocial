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


var awardReceiptUtils = {

    onLoad: function(mode, subjectName, href) {
        this.initObjectReferences();                 
        this.bindEventListeners();
        this.baseHref = href;
        this.editMode = mode;
        $.extend(this, vitro.customFormUtils);
        // in edit mode copy the year awarded to the displayed input element
        if ( this.editMode == "edit"  ) {
            this.hiddenOrgDiv = $('div#hiddenOrgLabel');
            this.displayedYear.val(this.yearAwarded.val());
            if ( this.org.val() != '' ) {
                window.setTimeout('awardReceiptUtils.hiddenOrgDiv.removeClass("hidden")', 100);
                window.setTimeout('awardReceiptUtils.orgAcSelection.hide()', 100);
            }
        }
        this.subjectName = subjectName;
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasAwardOrHonor');
    this.recLabel = $('#awardReceiptLabel');
    this.award = $('#award');
    this.awardDisplay = $('#awardDisplay');
    this.org = $('#org');
    this.yearAwarded = $('#yearAwarded-year');
    this.displayedYear = $('#yearAwardedDisplay');
    this.awardAcSelection = $('div#awardAcSelection');
    this.orgAcSelection = $('div#orgAcSelection');
    this.orgUriReceiver = $('input#orgUri');
    this.changeLink = this.awardAcSelection.children('p').children('#changeSelection');

    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        // the delay ensures that the function is called after the ac selection is completed
        this.award.change( function(objEvent) {
           window.setTimeout('awardReceiptUtils.hideConferredBy()', 180); 
        });
        
        this.award.blur( function(objEvent) {
           window.setTimeout('awardReceiptUtils.hideConferredBy()', 180); 
        });

        this.form.submit(function() {
            awardReceiptUtils.setYearAwardedValue();
            awardReceiptUtils.buildAwardReceiptLabel();
        });    
    
        this.changeLink.click( function() {
           awardReceiptUtils.showConferredBy(); 
        });
    },
    
    hideConferredBy: function() {
        if ( this.awardAcSelection.attr('class').indexOf('userSelected') != -1 ) {
            this.org.parent('p').hide();
            this.org.val('');
            this.resetAcSelection();       }
    },

    showConferredBy: function() {
        this.org.val('Select an existing Organization or create a new one.');
        this.org.addClass('acSelectorWithHelpText');
        this.org.parent('p').show();
        if ( this.editMode == "edit" ) {
            this.hiddenOrgDiv.hide();
        }
        this.resetAcSelection();
    },

    resetAcSelection: function() {
        var $acSelection = $("div.acSelection[acGroupName='org']");
        
        if ( this.orgUriReceiver.val() != '' ) {
            this.hideFields($acSelection);
            $acSelection.removeClass('userSelected');
            $acSelection.find("span.acSelectionInfo").text('');
            $acSelection.find("a.verifyMatch").attr('href', this.baseHref);
        }
    },

    buildAwardReceiptLabel: function() {
        var rdfsLabel = "";
        if ( this.editMode == "edit"  ) {
            rdfsLabel = this.awardDisplay.val();
        }
        else {
            rdfsLabel = this.award.val();
        }
        if ( this.yearAwarded.val().length ) {
            rdfsLabel += " (" + this.subjectName + ' - ' + this.yearAwarded.val() + ")";
        }
        else {
            rdfsLabel += " (" + this.subjectName + ")";
        }
        this.recLabel.val(rdfsLabel);
    },

    setYearAwardedValue: function() {
        this.yearAwarded.val(this.displayedYear.val());
    }
       
}