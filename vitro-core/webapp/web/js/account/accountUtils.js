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
  
// Change form actions in account main page
function changeAction(form, url) {
    form.action = url;
    return true;
} 
    
$(document).ready(function(){

    //Accounts per page
    //Hide if javascript is enabled
    $('input[name="accounts-per-page"]').addClass('hidden');
    
    $('.accounts-per-page').change(function() {
        // ensure both accounts-per-page select elements are
        // set to the same value before submitting
        var selectedValue = $(this).val();
        $('.accounts-per-page').val(selectedValue);
        $('#account-display').submit();
    });
    
    //Delete accounts
    //Show is javascript is enabled
    $('input:checkbox[name=delete-all]').removeClass('hidden');
    
    $('input:checkbox[name=delete-all]').click(function(){
         if ( this.checked ) {
         // if checked, select all the checkboxes
         $('input:checkbox[name=deleteAccount]').attr('checked','checked');
            
         } else {
         // if not checked, deselect all the checkboxes
           $('input:checkbox[name=deleteAccount]').removeAttr('checked');
         }
    });
    
    $('input:checkbox[name=deleteAccount]').click(function(){
        $('input:checkbox[name=delete-all]').removeAttr('checked');
    });
      
    // Confirmation alert for account deletion in userAccounts-list.ftl template
    $('input[name="delete-account"]').click(function(){
        var countAccount = $('input:checkbox[name=deleteAccount]:checked').length;
        if (countAccount == 0){
            return false;
        }else{
            var answer = confirm( 'Are you sure you want to delete ' + ((countAccount > 1) ? 'these accounts' : 'this account') +'?');
            return answer;
        }
    });
    
    //Select role and filter
    $('#roleFilterUri').bind('change', function () {
        var url = $(this).val(); // get selected value
        if (url) { // require a URL
            window.location = url; // redirect
        }
        return false;
    });
});