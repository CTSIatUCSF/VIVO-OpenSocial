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
 * Marker Object for that hold external information - data. Please refer to the
 * Google.map.MakerOptions for options' details
 */

var Marker = Class.extend({
	init : function(options) {
		this.options = $.extend({}, this.options, options);
		this.marker = createGoogleMarker(this.options);
		this.hide();
		this.registerEvents();
	},
	options : {
		value : 0,
		map : null,
		icon : null,
		position : null,
		content : null
	},
	addToMap : function() {
		this.marker.setMap(this.options.map);
		this.registerEvents();
	},
	removeFromMap : function() {
		this.marker.setMap(null);
		this.unregisterEvents();
	},
	show : function() {
		this.marker.setVisible(true);
	},
	hide : function() {
		this.marker.setVisible(false);
	},
	setIcon : function(icon) {
		this.marker.setIcon(icon);
	},
	setZIndex: function(zIndex){
		this.marker.setZIndex(zIndex);
	},
	setTitle : function(title) {
		this.marker.title = title;
	},
	registerEvents : function() {
		var handlers = new Array();
		var marker = this.marker;
		handlers.push(addClickListener(marker, function() {
			updateIFrame(this.url);
		}));
		this.handlers = handlers;
	},
	unregisterEvents : function() {
		if (this.handlers) {
			$.each(this.handlers, function(i, handler) {
				removeListener(handler);
			});
			this.handlers = null;
		}
	}
});
