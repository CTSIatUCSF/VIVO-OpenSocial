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
var map;
var downloader;
var currentVisMode;
var currentController;
var visModeControllers = {};
var responseContainerID = "map-of-science-response";
var ERROR_DISPLAY_WIDGET = '';

var loadingScreenTimeout;

/*
 * This method will setup the options for loading screen & then activate the 
 * loading screen.
 */
function setupLoadingScreen() {
	
    $.blockUI.defaults.overlayCSS = { 
            backgroundColor: '#fff', 
            opacity: 1.0
        };
        
    $.blockUI.defaults.css.width = '500px';
    $.blockUI.defaults.css.height = '100px';
    $.blockUI.defaults.css.border = '0px';
    
    $("#" + responseContainerID).block({
        message: '<div id="loading-data-container"><h3><img id="data-loading-icon" src="' + loadingImageLink 
        			+ '" />&nbsp;Loading data for <i>' 
        			+ entityLabel
        			+ '</i></h3></div>'
    });
    
    clearTimeout(loadingScreenTimeout);
    
    loadingScreenTimeout = setTimeout(function() {
    	$("#loading-data-container")
    		.html('<h3><img id="refresh-page-icon" src="' 
    				+ refreshPageImageLink 
	    			+ '" />&nbsp;Data for <i>' + entityLabel
	    			+ '</i> is now being refreshed. The visualization will load as soon as we are done computing, ' 
	    			+ 'or you can search or browse other data in VIVO and come back in a few minutes.</h3>')
	    	.css({'cursor': 'pointer'});
    }, 10 * 1000);
}

function initMap() {
	var gMap = google.maps;
	var centerLatLng = new google.maps.LatLng(55, -10);
	
	var mapOptions = {
		center: centerLatLng,
		zoom: 1,
		streetViewControl: false,
		mapTypeControlOptions: {
		  mapTypeIds: []
		}
	};
	
	var mapAreaId = $("#map_area");
	map = new gMap.Map(mapAreaId[0], mapOptions);
	
	var mapName = 'Scimap';
	createScimapType(map, mapName);
	map.setMapTypeId(mapName);
	
	downloader = new DownloadManager();
	
}

function initVisModeController() {
	var controller = getVisModeController(ENTITY_VIS_MODE);
	switchVisMode(controller.visMode);
	initVisModeTypeButton();
	initGlobalToolTips();
	currentController.loadData(scienceMapDataURL, false);
}

function helper() {
	/* override helper function to avoid reload script */
}

/* Using .load instead of .ready due to issue with IE and Google Maps API */
$(window).load(function() {
	
	ERROR_DISPLAY_WIDGET = new ErrorDisplayWidget({
		containerID: 'error-container'
	});
	
	setupLoadingScreen();
	initMap();
	initVisModeController();
});