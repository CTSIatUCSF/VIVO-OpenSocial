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
  
// Sets up event listeners so that the submit button gets enabled only if the user has changed
// an existing value.
//
// Used with both the userAccounts--myAccounts.ftl and userAccounts--edit.ftl.
    
$(document).ready(function(){

    var theForm = $('form').last();
    var theSubmitButton = theForm.find(':submit');

    theSubmitButton.addClass("disabledSubmit");
    
    function disableSubmit() {
        theSubmitButton.removeAttr('disabled');
        theSubmitButton.removeClass("disabledSubmit");
    }

    $('input').each(function() {
        if ( $(this).attr('type') != 'submit' && $(this).attr('name') != 'querytext') {
            $(this).change(function() {
                disableSubmit()
            });
            $(this).bind("propertychange", function() {
                disableSubmit();
            });
            $(this).bind("input", function() {
                disableSubmit()
            });
        }
    });
    $('select').each(function() {
        $(this).change(function() {
            disableSubmit()
        });
    });
    
    $('.remove-proxy').click(function(){
        disableSubmit()
    })
});

