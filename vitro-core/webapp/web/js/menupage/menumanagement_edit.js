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

var menuManagementEdit = {
    onLoad: function() {
        this.initObjects();
        this.bindEventListeners();
        this.toggleClassSelection();
    },
    initObjects: function() {
        this.defaultTemplateRadio = $('input.default-template');
        this.customTemplateRadio = $('input.custom-template');
        this.customTemplate = $('#custom-template');
        this.changeContentType = $('#changeContentType');
        this.selectContentType = $('#selectContentType');
        this.existingContentType = $('#existingContentType');
        this.selectClassGroupDropdown = $('#selectClassGroup');
        this.classesForClassGroup = $('#classesInSelectedGroup');
        this.selectedGroupForPage = $('#selectedContentTypeValue');
        this.allClassesSelectedCheckbox = $('#allSelected');
        this.displayInternalMessage = $('#internal-class label em');
    },
    bindEventListeners: function() {        
        // Listeners for vClass switching
        this.changeContentType.click(function() {
           menuManagementEdit.showClassGroups();
         
           return false;
        });
        this.selectClassGroupDropdown.change(function() {
            menuManagementEdit.chooseClassGroup();
        });
        
        // Listeners for template field
        this.defaultTemplateRadio.click(function(){
            menuManagementEdit.customTemplate.addClass('hidden');
        });
        this.customTemplateRadio.click(function(){
            // If checked, hide this input element
            menuManagementEdit.customTemplate.removeClass('hidden');
        });
        $("form").submit(function () { 
            var validationError = menuManagementEdit.validateMenuItemForm();
            if (validationError == "") {
                   $(this).submit();
               } else{
                   $('#error-alert').removeClass('hidden');
                   $('#error-alert p').html(validationError);
                   $.scrollTo({ top:0, left:0}, 500)
                   return false;
               } 
         });
    },
    updateInternalClassMessage:function(classGroupName) { //User has changed content type 
        //Set content type within internal class message
        this.displayInternalMessage.filter(":first").html(classGroupName);
    },
    showClassGroups: function() { //User has clicked change content type
        //Show the section with the class group dropdown
        this.selectContentType.removeClass("hidden");
        //Hide the "change content type" section which shows the selected class group
        this.existingContentType.addClass("hidden");
        //Hide the checkboxes for classes within the class group
        this.classesForClassGroup.addClass("hidden");
    },
    hideClassGroups: function() { //User has selected class group/content type, page should show classes for class group and 'existing' type with change link
        //Hide the class group dropdown
        this.selectContentType.addClass("hidden");
        //Show the "change content type" section which shows the selected class group
        this.existingContentType.removeClass("hidden");
        //Show the classes in the class group
        this.classesForClassGroup.removeClass("hidden");
        
    },
    toggleClassSelection: function() {
        // Check/unckeck all classes for selection
        $('input:checkbox[name=allSelected]').click(function(){
             if ( this.checked ) {
             // if checked, select all the checkboxes
             $('input:checkbox[name=classInClassGroup]').attr('checked','checked');

             } else {
             // if not checked, deselect all the checkboxes
               $('input:checkbox[name=classInClassGroup]').removeAttr('checked');
             }
        });

        $('input:checkbox[name=classInClassGroup]').click(function(){
            $('input:checkbox[name=allSelected]').removeAttr('checked');
        });
    },
    validateMenuItemForm: function() {
        var validationError = "";
        
        // Check menu name
        if ($('input[type=text][name=menuName]').val() == "") {
            validationError += "You must supply a name<br />";
            }
        // Check pretty url     
        if ($('input[type=text][name=prettyUrl]').val() == "") {
            validationError += "You must supply a pretty URL<br />";
        }
        if ($('input[type=text][name=prettyUrl]').val().charAt(0) != "/") {
            validationError += "The pretty URL must begin with a leading forward slash<br />";
        }
        
        // Check custom template
        if ($('input:radio[name=selectedTemplate]:checked').val() == "custom") {
            if ($('input[name=customTemplate]').val() == "") {
                validationError += "You must supply a template<br />"; 
            }
        }
        
        // if no class group selected, this is an error
        if ($('#selectClassGroup').val() =='-1') {
            validationError += "You must supply a content type<br />"; 
        } else {
            //class group has been selected, make sure there is at least one class selected
            var allSelected = $('input[name="allSelected"]:checked').length;
            var noClassesSelected = $('input[name="classInClassGroup"]:checked').length;
            if (allSelected == 0 && noClassesSelected == 0) {
                //at least one class should be selected
                validationError += "You must select the type of content to display<br />";
            }
        }
      
       
        //check select class group
       
        return validationError;
    },
    chooseClassGroup: function() {        
        var url = "dataservice?getVClassesForVClassGroup=1&classgroupUri=";
        var vclassUri = this.selectClassGroupDropdown.val();
        url += encodeURIComponent(vclassUri);
        //Make ajax call to retrieve vclasses
        $.getJSON(url, function(results) {
  
          if ( results.classes.length == 0 ) {
     
          } else {
              //update existing content type with correct class group name and hide class group select again
              var _this = menuManagementEdit;
              menuManagementEdit.hideClassGroups();
      
              menuManagementEdit.selectedGroupForPage.html(results.classGroupName);
              //update content type in message to "display x within my institution"
              menuManagementEdit.updateInternalClassMessage(results.classGroupName);
              //retrieve classes for class group and display with all selected
              var selectedClassesList = menuManagementEdit.classesForClassGroup.children('ul#selectedClasses');
              
              selectedClassesList.empty();
              selectedClassesList.append('<li class="ui-state-default"> <input type="checkbox" name="allSelected" id="allSelected" value="all" checked="checked" /> <label class="inline" for="All"> All</label> </li>');
              
              $.each(results.classes, function(i, item) {
                  var thisClass = results.classes[i];
                  var thisClassName = thisClass.name;
                  //When first selecting new content type, all classes should be selected
                  appendHtml = ' <li class="ui-state-default">' + 
                          '<input type="checkbox" checked="checked" name="classInClassGroup" value="' + thisClass.URI + '" />' +  
                         '<label class="inline" for="' + thisClassName + '"> ' + thisClassName + '</label>' + 
                          '</li>';
                  selectedClassesList.append(appendHtml);
              });
              menuManagementEdit.toggleClassSelection();
          }
 
        });
    }
};

$(document).ready(function() {   
    menuManagementEdit.onLoad();
});