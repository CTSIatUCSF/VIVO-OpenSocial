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

var manageLabels = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
    
            this.mixIn();               
            this.initPage();
            
            var selectedRadio;       
        },

    mixIn: function() {

        // Get the custom form data from the page
        $.extend(this, customFormData);
    },

    // Initial page setup. Called only at page load.
    initPage: function() {
        
        $('input#submit').attr('disabled', 'disabled');
        $('input#submit').addClass('disabledSubmit');
        this.bindEventListeners();
                       
    },
    
    bindEventListeners: function() {
               
        $('input:radio').click( function() {
            manageLabels.selectedRadio = $(this);
            $('input#submit').attr('disabled', '');
            $('input#submit').removeClass('disabledSubmit');            
        });

        $('input#submit').click( function() {
             manageLabels.processLabel(manageLabels.selectedRadio);
        });

    },
                      
    processLabel: function(selectedRadio) {
        
        // PrimitiveDelete only handles one statement, so we have to use PrimitiveRdfEdit to handle multiple
        // retractions if they exist. But PrimitiveRdfEdit also handles assertions, so pass an empty string
        // for "additions"
        var add = "";
        var retract = "";
        
        $('input:radio').each( function() {
            if ( !$(this).is(':checked') ) {
                retract += " <" + manageLabels.individualUri + "> <http://www.w3.org/2000/01/rdf-schema#label> "
                                + "\"" + $(this).attr('id') + "\"" + $(this).attr('tagOrType') + " ." ;
            }
        });

        retract = retract.substring(0,retract.length -1);

        $.ajax({
            url: manageLabels.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: selectedRadio, // context for callback
            complete: function(request, status) {
                
                if (status == 'success') {
                    window.location = $('a.cancel').attr('href');
                }
                else {
                    alert('Error processing request: the unchecked labels could not be deleted.');
                    selectedRadio.removeAttr('checked');
                }
            }
        });        

    },

};

$(document).ready(function() {   
    manageLabels.onLoad();
}); 
