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
var ENTITY_VIS_MODE = "ENTITY";
var COMPARISON_VIS_MODE = "COMPARISON";

var dataMarket = {};

var VisModeController = Class.extend({
	init: function(map) {
		this.visMode = ENTITY_VIS_MODE;
		this.isUnloaded = true;
		this.initWidgets(map);
	},
	initWidgets: function(map) {
		this.widgets = {};
	},
	needLoaded: function() {
		return this.isUnloaded;
	},
	loadData: function(url, sync) {
		
		// Download data from server and add to markerManager if not gotten already
		var me = this;
		if (me.isUnloaded) {
			// Lazy loading
			if (!dataMarket[url]) {
				if (sync) {
					downloader.downloadAndWait(url, function(data) {
						dataMarket[url] = data;
						me.loadJsonData(me, data);
					});
				} else {
					downloader.download(url, function(data) {
						dataMarket[url] = data;
						me.loadJsonData(me, data);
					});
				}
			} else {
				me.loadJsonData(me, dataMarket[url]);
			}
		} // end if
	},
	loadJsonData: function(me, data) {
		
		$("#" + responseContainerID).unblock();
		
		if (ERROR_DISPLAY_WIDGET.isErrorConditionTriggered(data)) {
			$("#map-of-science-response").hide();
			ERROR_DISPLAY_WIDGET.show(ENTITY_TYPE, data);
			return;
		}
		
		data = data[0];
		
		$.each(me.widgets, function(i, widget) {
			widget.loadJsonData(data);
		});
		//me.initToolTipInfo();
		me.isUnloaded = false;
	},
	initView: function() {
		$.each(this.widgets, function(i, widget) {
			widget.initView();
		});
	},
	// key can be discippline or subdiscipline
	show: function(key) {
		$.each(this.widgets, function(i, widget) {
			widget.show(key);
		});
	},
	hide: function(key) {
		$.each(this.widgets, function(i, widget) {
			widget.hide(key);
		});
	},
	cleanView: function() {
		$.each(this.widgets, function(i, widget) {
			widget.cleanView();
		});
	},
	changeFilter: function(value) {
		var type = this.getFilterType(value);
		
		$.each(this.widgets, function(i, widget) {
			widget.changeFilter(type);
		});
	},
});

var EntityVisModeController = VisModeController.extend({
	init: function(map) {
		this._super(map);
		this.visMode = ENTITY_VIS_MODE;
	},
	getFilterType: function(value) {
		if (value === 1) {
			return SCIMAP_TYPE.SUBDISCIPLINE;
		}
		return SCIMAP_TYPE.DISCIPLINE;
	},
	initWidgets: function(map) {
		var widgets = {};
		widgets['scimap'] = new ScimapWidget(map);
		widgets['sci_area_table'] = new DataTableWidget(widgets['scimap']);
		
		this.widgets = widgets;
	}
});

var ComparisonVisModeController = VisModeController.extend({
	init: function(map) {
		this._super(map);
		this.visMode = COMPARISON_VIS_MODE;
	},
	getFilterType: function(value) {
		if (value === 1) {
			return COMPARISON_TYPE.ORGANIZATION;
		}
		return COMPARISON_TYPE.PERSON;
	},
	initWidgets: function(map) {
		var widgets = {};
		widgets['scimap'] = new ComparisonScimapWidget(map);
		widgets['entity_area_table'] = new EntityTablesWidget(widgets['scimap']);
		widgets['sci_area_table'] = new ComparisonDataTableWidget(widgets['scimap'], widgets['entity_area_table']);
		
		this.widgets = widgets;
	}
});