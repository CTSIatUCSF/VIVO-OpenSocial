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
function switchMarkerManager(id) {

	markerManager = getMarkerManager(id);
	if(isActiveMarkerManager(markerManager)) {
		markerManager.addAllToMap();
		
		if(activeMarkerManager) {
			activeMarkerManager.removeAllFromMap();
		}
		
		/* switch to target marker manager */
		activeMarkerManager = markerManager;
	}
}

function createVisModeController(visMode) {
	if (visMode === ENTITY_VIS_MODE) {
		var controller = new EntityVisModeController(map);
		visModeControllers[controller.visMode] = controller;
	}
	
	if (visMode === COMPARISON_VIS_MODE) {
		var controller = new ComparisonVisModeController(map);
		visModeControllers[controller.visMode] = controller;
		controller.loadData(scienceMapDataURL, false);
	}
}

function isActiveVisMode(visMode) {
	return (currentVisMode === visMode);
}

function getVisModeController(visMode){
	if (visModeControllers[visMode] == null) {
		createVisModeController(visMode);
	}
	return visModeControllers[visMode];
}

function switchVisMode(visMode) {
	if (!isActiveVisMode(visMode)) {
		if (currentController) {
			currentController.cleanView();
		}
		currentController = getVisModeController(visMode);
		currentVisMode = visMode;
		currentController.initView();
	}
}

function initFilter(dom) {
	
	// Switch filter handling
	$("." + dom.filterOptionClass).live('click', function() { 
		var obj = $(this);
		if (!obj.hasClass(dom.activeFilterClass)) {
			var checked = obj.attr('id');
			if (checked === dom.secondFilterID) {
				$("#" + dom.firstFilterID).removeClass(dom.activeFilterClass);
				currentController.changeFilter(2);
				
			} else if (checked === dom.firstFilterID) {
				$("#" + dom.secondFilterID).removeClass(dom.activeFilterClass);
				currentController.changeFilter(1);
			}
				
			obj.addClass(dom.activeFilterClass);	
		}
	});
	
	$("#" + dom.firstFilterID).trigger('click');
}

function initVisModeTypeButton() {
	// Switch vis mode handling
	var viewTypeRadio = "input[name='view-type']";
	$(viewTypeRadio).change( function() {
		var visMode = $(viewTypeRadio+ ":checked").val();
		switchVisMode(visMode);
	});
	
	/* Init default filter */
	$(viewTypeRadio+ ":eq(0)").click();
}

function initGlobalToolTips() {

	createToolTip($("#imageIconOne"), $('#toolTipOne').html(), "topLeft");
	createToolTip($("#exploreInfoIcon"), $('#exploreTooltipText').html(), "topLeft");
	createToolTip($("#compareInfoIcon"), $('#compareTooltipText').html(), "topLeft");
	createToolTip($("#imageIconThree"), $('#toolTipThree').html(), "topRight");
}

function createToolTip(target, tipText, tipLocation) {
	target.qtip({
        content: {
            text: tipText
        },
        position: {
            corner: {
                target: 'center',
                tooltip: tipLocation
            }
        },
        show: {
            when: {
                event: 'mouseover'
            }
        },
        hide: {
            fixed: true // Make it fixed so it can be hovered over
        },
        style: {
            padding: '6px 6px',
            // Give it some extra padding
            width: 500,
            textAlign: 'left',
            backgroundColor: '#ffffc0',
            fontSize: '.7em',
            padding: '6px 10px 6px 10px',
            lineHeight: '14px'
        }
    });
}