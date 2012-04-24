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

var SimpleDataTableWidget = Class.extend({
	
	dom: {
		paginationContainerClass : "subpaginatedtabs"
	},
	init: function(options) {
		var me = this;
		me.options = options;
		me.sciMapWidget = options.sciMapWidget;
		me.currentSelectedFilter = COMPARISON_TYPE.SUBDISCIPLINE;
		me.widget = '';
		me.tableDiv = $('<div />');
		me.addToContainer();
	},
	loadJsonData: function(data) {
		
		var me = this;
		me.uri = data.uri;
		me.label = data.label;
		me.pubsWithNoJournals = data.pubsWithNoJournals;
		me.pubsWithInvalidJournals = data.pubsWithInvalidJournals;
		me.pubsMapped = data.pubsMapped;
		me.type = data.type;
		me.color = data.color;
		me.subdisciplineActivity = data.subdisciplineActivity;
		me.setupView();
	},
	addToContainer: function() {
		this.options.container.append(this.tableDiv);
	},
	removeFromContainer: function() {
		this.tableDiv.remove();
	},
	hasKey: function(key) {
		return (this.keyToMarkerManagers.hasOwnProperty(key));
	},
	show: function(key) {
		this.tableDiv.show();
	},
	hide: function(key) {
		this.tableDiv.hide();
	},
	cleanView: function() {
		this.removeFromContainer();
	},
	initView: function() {
		this.addToContainer(this.tableDiv);
		this.show();
	},
	setupView: function() {
		
		var me = this;
		
		me.tableDiv.addClass("subEntityTable");
		
		/* Create filter */
		entityVivoProfileURLPrefix
		var organizationHeader = $('<div><a class="suborganization-title" href="' + 
				entityVivoProfileURLPrefix + me.uri +'">' + 
				truncateText(me.label, 23) + '</a><a href="' + entityMapOfScienceURLPrefix + 
				me.uri + '"><img class="drillDownIcon" src="' + 
				drillDownIconUrl + '" alt="drill down" title="drill down" /></a></div>');
		me.tableDiv.append(organizationHeader);
		
		/* Create table */
		var table = $('<table>');
		table.attr('id', 'entityDatatable');
		table.addClass('entity-datatable-table');
		
		/* Create table header */
		var thead = $('<thead>');
		var tr = $('<tr>');
		
		/*var levelOfScienceAreaTH = $('<th>');
		levelOfScienceAreaTH.html('Level of Science Area');*/
		
		var scienceAreasTH = $('<th>');
		scienceAreasTH.attr("id", "entity-science-areas-th");
		scienceAreasTH.html('Subdisciplines');
		
		var activityCountTH = $('<th width="53">');
		activityCountTH.html('# of pubs.');

		//tr.append(levelOfScienceAreaTH);
		tr.append(scienceAreasTH);
		tr.append(activityCountTH);
		
		thead.append(tr);
		table.append(thead);
		
		/* Create tbody and contents */
		var tbody = $('<tbody>');
		
		var rowsToInsert = [];
		var i = 0;
		
		$.each(me.subdisciplineActivity, function(index, density) {
			rowsToInsert[i++] = '<tr id="' + index + '">';
			rowsToInsert[i++] = '<td style="color:' + me.color + ';">' + truncateText(SUBDISCIPLINES[index].label, 20) + '</td>';
			rowsToInsert[i++] = '<td style="color:' + me.color + ';">' + density.toFixed(1) + '</td></tr>';
		});
		
		tbody.append(rowsToInsert.join(''));
		table.append(tbody);
		me.tableDiv.append(table);
		
		/* Register events */
		table.children("tbody").children("tr").mouseenter(function() {
			me.sciMapWidget.mouseInNode(me.type, me.label, $(this).attr("id"));
		});
		
		table.children("tbody").children("tr").mouseleave(function() {
			me.sciMapWidget.mouseOutNode(me.type, me.label, $(this).attr("id"));
		});
		
		/* Init DataTable object */
		me.widget = table.dataTable({
		    "sDom": '<"' + me.dom.paginationContainerClass + '"p><"table-separator"><"datatablewrapper"t>',
		    "aaSorting": [
		        [1, "desc"], [0,'asc']
		    ],
		    "asStripClasses": [],
		    "aoColumns": [{ "bSearchable": false },
		                  { "bSearchable": false }], 
		    "iDisplayLength": 10,
		    "bInfo": true,
		    "bFilter": false,
		    "oLanguage": {
				"sInfo": "_START_ - _END_ of _TOTAL_",
				"sInfoEmpty": "No matching science areas found",
				"sInfoFiltered": ""
			},
		    "sPaginationType": "gmail_style",
		    "fnDrawCallback": function () {
		    }
		});
		
		/* Create csv download button */
		var csvButton = '<hr class="subtle-hr" /><div id="main-science-areas-table-footer" style="background-color:' + me.color + ';"><a href="' +
						entityMapOfScienceSubDisciplineCSVURLPrefix + me.uri +
						'" class="map-of-science-links">Save All as CSV</a></div>';
		me.tableDiv.append(csvButton);
		
		/* Create mapping statistic result */
		var totalPublications = me.pubsWithNoJournals + me.pubsWithInvalidJournals + me.pubsMapped;
		var mappedText = '<a class="mapped-result" href="' + entityUnmappedJournalsCSVURLPrefix + me.uri + '">' + 
						(100 * me.pubsMapped / totalPublications).toFixed(2) + '% mapped</a>';
		me.tableDiv.append($(mappedText));
		me.widget.fnDraw();
	}
});

function truncateText(text, len) {

	var trunc = text;
	
	if (text.length > len) {
		trunc = text.substring(0, len);
		trunc = trunc.replace(/\w+$/, '') + '<font title="' + text + '">...</font>'
	}

	return trunc;
}