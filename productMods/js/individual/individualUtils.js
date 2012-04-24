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

$(document).ready(function(){
    
    // "more"/"less" HTML truncator for showing more or less content in data property core:overview
    $('.overview-value').truncate({max_length: 500});
    
    $.fn.exists = function () {
        return this.length !== 0;
    }
    
    $.fn.moreLess = function () {
        $(this).each
    }
    
    var togglePropDisplay = {
        showMore: function($toggleLink, $itemContainer) {
            $toggleLink.click(function() {
                $itemContainer.show();
                $(this).attr('href', '#show less content');
                $(this).text('less');
                togglePropDisplay.showLess($toggleLink, $itemContainer);
                return false;
            });
        },
        
        showLess: function($toggleLink, $itemContainer) {
            $toggleLink.click(function() {
                $itemContainer.hide();
                $(this).attr('href', '#show more content');
                $(this).text('more...');
                togglePropDisplay.showMore($toggleLink, $itemContainer);
                return false;
            });
        }
    };
    
    // var $propList = $('.property-list').not('>li>ul');
    var $propList = $('.property-list:not(:has(>li>ul))');
    $propList.each(function() {
        var $additionalItems = $(this).find('li:gt(4)');
        if ( $additionalItems.exists() ) {
            // create container for additional elements
            var $itemContainer = $('<div class="additionalItems" />').appendTo(this);
            
            // create toggle link
            var $toggleLink = $('<a class="more-less" href="#show more content">more...</a>').appendTo(this);
            
            $additionalItems.appendTo($itemContainer);
            
            $itemContainer.hide();
            
            togglePropDisplay.showMore($toggleLink, $itemContainer);
        }
    });
    
    var $subPropList = $('.subclass-property-list');
    $subPropList.each(function() {
        var $additionalItems = $(this).find('li:gt(4)');
        if ( $additionalItems.exists() ) {
            // create container for additional elements
            var $itemContainer = $('<div class="additionalItems" />').appendTo(this);
            
            // create toggle link
            var $toggleLink = $('<a class="more-less" href="#show more content">more...</a>').appendTo(this);
            
            $additionalItems.appendTo($itemContainer);
            
            $itemContainer.hide();
            
            togglePropDisplay.showMore($toggleLink, $itemContainer);
        }
    });
    
    var $subPropSibs = $subPropList.closest('li').last().nextAll();
    var $subPropParent = $subPropList.closest('li').last().parent();
    var $additionalItems = $subPropSibs.slice(3);
    if ( $additionalItems.length > 0 ) {
        // create container for additional elements
        var $itemContainer = $('<div class="additionalItems" />').appendTo($subPropParent);
        
        // create toggle link
        var $toggleLink = $('<a class="more-less" href="#show more content">more...</a>').appendTo($subPropParent);
        
        $additionalItems.appendTo($itemContainer);
        
        $itemContainer.hide();
        
        togglePropDisplay.showMore($toggleLink, $itemContainer);
    }
    
    // Change background color button when verbose mode is off
    $('a#verbosePropertySwitch:contains("Turn off")').addClass('verbose-off');
    
    // Reveal vCard QR code when QR icon is clicked
    $('#qrIcon, .qrCloseLink').click(function() {
        $('#qrCodeImage').toggleClass('hidden');
        return false;
    });
});
