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

var managePublications = {

    /* *** Initial page setup *** */
   
    onLoad: function() {
    
            this.mixIn();               
            this.initPage();       
        },

    mixIn: function() {

        // Get the custom form data from the page
        $.extend(this, customFormData);
    },

    // Initial page setup. Called only at page load.
    initPage: function() {

        this.initPublicationData();
       
        this.bindEventListeners();
                       
    },

    // On page load, associate data with each list item. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // items.
    initPublicationData: function() {
        $('.pubCheckbox').each(function(index) {
            $(this).data(publicationData[index]);  
        });
    },
    
    bindEventListeners: function() {

        $('.pubCheckbox').click(function() {
            managePublications.processPublication(this);
            //return false;
        });
               
    },
                      
    processPublication: function(publication) {
        
        var add = "";
        var retract = "";
        var n3String = "<" + $(publication).data('authorshipUri') + "> <http://vivoweb.org/ontology/core#hideFromDisplay> \"true\" ." ;

        if ( $(publication).is(':checked') ) {
            add = n3String;
        }
        else {
            retract = n3String;
        } 
        
        $.ajax({
            url: managePublications.processingUrl,
            type: 'POST', 
            data: {
                additions: add,
                retractions: retract
            },
            dataType: 'json',
            context: publication, // context for callback
            complete: function(request, status) {
            
                if (status === 'success') {
                    window.status = "The publication will has been successfully excluded from the profile page."; 

                } else {
                    alert('Error processing request: the publication cannot be excluded from the profile page.');
                    $(publication).removeAttr('checked');
                }
            }
        });        
    },

};

$(document).ready(function() {   
    managePublications.onLoad();
}); 
