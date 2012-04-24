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
/**
 * This is the Google MAP API utilities file to make sure all calls to 
 * Google API is correct. Please add all common utilities at here.
 */
var GMAPS = google.maps;
var GEVENT = google.maps.event;
var DEFAULT_POINT = new google.maps.Point(0, 0);

function createMarkerImage(url, width, height) {
	return new GMAPS.MarkerImage(
			url,
		    new GMAPS.Size(width, height), /* set the image viewable window size */
		    DEFAULT_POINT, /* Use this to cut the image */
		    // TODO Fix icons so their center at the right spot. Very low priority.
		    new google.maps.Point(width/2, height/2), /* use this to shift the marker location in pixels. Default */
			new GMAPS.Size(width, height)); /* set the desired image size */
}

function createNoWrapLatLng(lat, lng) {
	return new GMAPS.LatLng(lat, lng, true);
}

function createGoogleCirclePolygon(options) {
	return new GMAPS.Circle(options);
}

function createGooglePolygon(options) {
	return new GMAPS.Polygon(options);
}

function createGoogleMarker(options) {
	return new GMAPS.Marker(options);
}

function createInfoWindow(content, maxWidth) {
	return new GMAPS.InfoWindow({ 
		content: content,
		maxWidth: maxWidth
		});
}

function addMapProjectionChangedListener(map, actionFunction) {
	return GEVENT.addListener(map, 'projection_changed', actionFunction);
}

function addMouseOverListener(marker, actionFunction) {
	return GEVENT.addListener(marker, 'mouseover', actionFunction);
}

function addMouseOutListener(marker, actionFunction) {
	return GEVENT.addListener(marker, 'mouseout', actionFunction);
}

function addClickListener(marker, actionFunction) {
	return GEVENT.addListener(marker, 'click', actionFunction);
}

function removeListener(handler) {
	GEVENT.removeListener(handler);
}
