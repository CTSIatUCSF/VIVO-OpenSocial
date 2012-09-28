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

var orgHasPositionUtils = {
        
    onLoad: function(blankSentinel) {
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
    
    this.form = $('#organizationHasPositionHistory');
    this.person = $('#person');
    this.firstName = $('#firstName');
    this.lastName = $('#lastName');
    this.personUri = $('#personUri');    

    // may not need this
    this.firstName.attr('disabled', '');
    
    },
    
    bindEventListeners: function() {
        this.idCache = {};
        
        this.form.submit(function() {
            orgHasPositionUtils.resolvePersonNames();
        });            
    },
    
    resolvePersonNames: function() {
        var firstName,
            lastName,
            name;

        // If selecting an existing person, don't submit name fields
        if (this.personUri.val() == '' || this.personUri.val() == this.sentinel ) {
            firstName = this.firstName.val();
            lastName = this.person.val();
            
            name = lastName;
            if (firstName) {
                name += ', ' + firstName;
            }
            this.person.val(name);
            this.lastName.val(lastName);
        } 
        else {
            this.firstName.attr('disabled', 'disabled');
            this.lastName.attr('disabled', 'disabled');
        }

    },    

    resetLastNameLabel: function() {
        var indx = this.person.val().indexOf(", ");
        if ( indx != -1 ) {
            var temp = this.person.val().substr(0,indx);
            this.person.val(temp);
        }
    }
    
} 
