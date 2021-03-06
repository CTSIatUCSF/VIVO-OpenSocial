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

var EntityTablesWidget = Class.extend({
	init: function(sciMapWidget) {
		this.sciMapWidget = sciMapWidget;
		this.keyToDataTable = {};
		this.container = $('<div>');
		$("#subEntityTableArea").append(this.container);
		$("#subEntityTableArea").show();
	},
	initView: function(key) {
		this.container.show();
	},
	cleanView: function(key) {
		this.container.hide();
	},
	loadJsonData: function(data) {
	},
	loadEntity: function(data, color) {
		var key = data.label;
		var dataTable = this.getDataTable(key);
		if (dataTable == null) {
			dataTable = this._createDataTable(key, data, color);
		}
		dataTable.initView();
	},
	unloadEntity: function(key) {
		this._removeDataTable(key);
	},
	getDataTable: function(key) {
		return this.keyToDataTable[key];
	},
	_removeDataTable: function(key) {
		var dataTable = this.getDataTable(key);
		if (dataTable) {
			dataTable.cleanView();
			delete this.keyToDataTable[key];
		}
	},
	_createDataTable: function(key, data, color) {
		dataTable = new SimpleDataTableWidget({
			sciMapWidget: this.sciMapWidget, 
			container: this.container
			});
		data.color = color;
		dataTable.loadJsonData(data);
		this.keyToDataTable[key] = dataTable;
		return dataTable;
	}
});