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
var ScinodePolygon = CirclePolygon.extend({
	init: function(options) {
		this._super(options);
		this.hide();
	},
	setValue: function(value) {
		this.polygon.value = value;
	},
	getValue: function() {
		return this.polygon.value;
	},
	setSize: function(size) {
		this.setRadius(size);
		this.setZIndex(-size);
	},
	focus: function() {
		this.setOptions({strokeWeight: 1.2, strokeColor: '#000'});
	},
	unfocus: function() {
		this.setOptions({strokeWeight: 1.0, strokeColor: '#808080'});
	},
	setContent: function(content) {
		this.polygon.content = content;
	},
	registerEvents : function() {
		var me = this;
		var polygon = me.polygon;
		me._super();
		
		me.registerEvent(addClickListener(polygon, function() {
			INFO_WINDOW.setPosition(this.center);
			var content = this.content;
			INFO_WINDOW.setContent(content);
			INFO_WINDOW.open(this.map);
		}));
		
		me.registerEvent(addMouseOverListener(polygon, function() {
			me.focus();
		}));
		
		me.registerEvent(addMouseOutListener(polygon, function() {
			me.unfocus();
		}));
	}
});

function createScinodeMarker(map, label, value, radius, color, latlng) {
	var circleOptions = {
		label: label,
		value: value,
		strokeColor: '#808080',
		strokeOpacity: 1.0,
		strokeWeight: 1.0,
		fillColor: color,
		fillOpacity: 0.9,
		map: map,
		center: latlng,
		zIndex: -radius,
		radius: radius // min: 10000, max: 2500000
	};
	
	return new ScinodePolygon(circleOptions);
}

