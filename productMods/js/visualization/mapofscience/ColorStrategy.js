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
var ColorStrategy = Class.extend ({
	getColor: function(key) {
	}
});

var SingleColorStrategy = ColorStrategy.extend ({
	init: function(defaultColor) {
		this.defaultColor = '#000000';
		if (defaultColor) {
			this.defaultColor = defaultColor;
		}
	},
	getColor: function(key) {
		return this.defaultColor;
	}
});

var DisciplineColorStrategy = SingleColorStrategy.extend ({
	init: function(defaultColor) {
		this._super(defaultColor);
	},
	getColor: function(key) {
		var color = DISCIPLINES[key].color;
		if (color) {
			return color;
		} else {
			return this._super(key);
		}
	}
});

var SubdisciplineColorStrategy = ColorStrategy.extend ({
	init: function(defaultColor) {
		this.colorStrategy = new DisciplineColorStrategy(defaultColor);
	},
	getColor: function(key) {
		var mapKey = SUBDISCIPLINES[key].discipline;
		return this.colorStrategy.getColor(mapKey);
	}
});

// Todo: Stop coding until comparison view, might need to use Temporal Line Graph solution
var AutoAssignColorStrategy = SingleColorStrategy.extend ({
	init: function(defaultColor, ColorList) {
		this._super(defaultColor);
	},
	getColor: function(key) {
	}
});

