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
 * The scaler is used for scaling based on the predefined minimum value and
 * maximum value. You also can control the returned maximum scale and minimum 
 * scale.
 */
var Scaler = Class.extend({ 
	init: function(options) {
		this.options = $.extend({}, this.options, options);
	},
	options: {
		scaleFunc: ReallySimpleAreaScale,
		minValue: 0.0,
		maxValue: 0.0,
		minScale: 0.0,
		maxScale: 1.0
	},
	getScale: function(value) {
		var o = this.options;
		var scale = o.scaleFunc(value, o.minValue, o.maxValue, o.minScale, o.maxScale);
		if (scale > o.maxScale) {
			scale = maxScale;
		} else if (scale < o.minScale) {
			scale = minScale;
		}
		return scale;
	}
});

/* Scaling that ignore minScale and maxScale */
function ReallySimpleAreaScale(value, minValue, maxValue, minScale, maxScale) {
	return maxScale * Math.sqrt(value / maxValue);
}

/* Scaling that cares about minScale and maxScale */
function SimpleAreaScale(value, minValue, maxValue, minScale, maxScale) {
	if (maxValue != minValue) {
		var scale = minScale; 
		if (value > minValue) {
			var valueDiff = maxValue - minValue;
			var areaScale = value / valueDiff;
			scale = Math.sqrt(areaScale);
		}
		return scale;
	} else {
		return maxScale;
	}
}

/**
 * SizeCoder use scaler to scale its interested size based on the given
 * scaler strategy.
 */
var CircleSizeCoder = Class.extend({
	init: function(options) {
		this.options = $.extend({}, this.options, options);
	},
	options: {
		minRadius: 0,
		maxRadius: 25.0,
		scaler: new Scaler({})
	},
	getSize: function(value) {
		var o = this.options;
		var radius = o.scaler.getScale(value) * o.maxRadius;
		
		if (radius < o.minRadius) {
			radius = o.minRadius;
		} else if (radius > o.maxRadius) {
			radius = o.maxRadius;
		}
		
		return radius;
	},
	getMaxValue: function() {
		return this.options.scaler.options.maxValue;
	}
});

