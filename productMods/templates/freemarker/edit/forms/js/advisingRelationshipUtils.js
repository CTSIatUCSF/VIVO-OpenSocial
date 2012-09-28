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

var advisingRelUtils = {
        
    onLoad: function(subject,blankSentinel) {
        this.subjName = '';
        if ( subject ) { this.subjName = subject; }
        
        this.sentinel = '';
        if ( blankSentinel ) { this.sentinel = blankSentinel; }

        this.initObjectReferences();                 
        this.bindEventListeners();
        
        $.extend(this, vitro.customFormUtils);

        if ( this.findValidationErrors() ) {
            this.resetLastNameLabel();
        }
    },

    initObjectReferences: function() {
    
    this.form = $('#personHasAdvisingRelationship');
    this.adRelshiplabel = $('#advisingRelLabel');
    this.advisee = $('#advisee');
    this.subjArea = $('#SubjectArea');
    this.firstName = $('#firstName');
    this.lastName = $('#lastName');
    this.adviseeUri = $('#adviseeUri');
    this.subjAreaUri = $('#subjAreaUri');
    this.saveAdviseeLabel = $('#saveAdviseeLabel');
    this.adviseeAcSelection = $('div#adviseeAcSelection');
    

    // may not need this
    this.firstName.attr('disabled', '');
    
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        //we want to use the advisee label in the relationship label.
        // since the former gets cleared on submit in some cases, store
        //the value in a hidden field and map to relationship label
        this.advisee.change( function(objEvent) {
           window.setTimeout('advisingRelUtils.mapAdviseeValue()', 180); 
        });
        this.advisee.blur( function(objEvent) {
           window.setTimeout('advisingRelUtils.mapAdviseeValue()', 180); 
        });
        
        
        this.form.submit(function() {
            advisingRelUtils.resolveAdviseeNames();
            advisingRelUtils.buildAdvisingRelLabel();
        });            
    },
    
    mapAdviseeValue: function() {
       if ( this.adviseeAcSelection.attr('class').indexOf('userSelected') != -1 ) {
           this.saveAdviseeLabel.val(this.advisee.val());
       }
    },
    resolveAdviseeNames: function() {
        var firstName,
            lastName,
            name;

        // If selecting an existing person, don't submit name fields
        if (this.adviseeUri.val() == '' || this.adviseeUri.val() == this.sentinel ) {
            firstName = this.firstName.val();
            lastName = this.advisee.val();
            
            name = lastName;
            if (firstName) {
                name += ', ' + firstName;
            }            
            this.advisee.val(name);
            this.lastName.val(lastName);
        } 
        else {
            this.firstName.attr('disabled', 'disabled');
            this.lastName.attr('disabled', 'disabled');
        }

    },    

    buildAdvisingRelLabel: function() {
        if ( this.advisee.val() != "" ) {
            this.adRelshiplabel.val(this.subjName + " advising " + this.advisee.val());
        }
        else if ( this.saveAdviseeLabel.val() != "" ){
            this.adRelshiplabel.val(this.subjName + " advising " + this.saveAdviseeLabel.val());
        }
        else {
            this.adRelshiplabel.val(this.subjName + " advising relationship");
        }
    },

    resetLastNameLabel: function() {
        var indx = this.advisee.val().indexOf(", ");
        if ( indx != -1 ) {
            var temp = this.advisee.val().substr(0,indx);
            this.advisee.val(temp);
        }
    }
    
} 
