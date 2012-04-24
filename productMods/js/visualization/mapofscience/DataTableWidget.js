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

var DataTableWidget = Class.extend({
	
	dom: {
		firstFilterLabel: "554 Subdisciplines",
		secondFilterLabel: "13 Disciplines",
		
		searchBarParentContainerClass: "searchbar",
		paginationContainerClass: "paginatedtabs",
		containerID: "main-science-areas-table-container",
		footerID: "main-science-areas-table-footer",
		firstFilterID: "first-filter",
		secondFilterID: "second-filter",
		filterOptionClass: "filter-option",
		activeFilterClass: "active-filter",
		filterInfoIconClass: "filterInfoIcon",
		
		percentMappedInfoID: "percent-mapped-info"
	},
	init: function(sciMapWidget) {
		var me = this;
		me.sciMapWidget = sciMapWidget;
		me.widgetType = "MAIN_SCIENCE_AREAS";
		me.currentSelectedFilter = SCIMAP_TYPE.SUBDISCIPLINE;
		me.subdisciplineInfo = {};
		me.disciplineInfo = {};
		me.widget = '';
		me.percentMappedDiv = $("#" + me.dom.percentMappedInfoID);
		me.tableDiv = $('<div />');
		$("#" + me.dom.containerID).append(this.tableDiv);
		
		$.each(DISCIPLINES, function(index, item) {
			var emptyScienceAreaElement = {
				publicationCount: 0,
				label: item.label		
			};
			me.disciplineInfo[index] = emptyScienceAreaElement;
		});
		
		$.each(SUBDISCIPLINES, function(index, item) {
			var emptyScienceAreaElement = {
				publicationCount: 0,
				label: item.label		
			};
			me.subdisciplineInfo[index] = emptyScienceAreaElement;
		});
	},
	loadJsonData: function(data) {
		
		var me = this;
		me.uri = data.uri;
		me.label = data.label;
		me.pubsWithNoJournals = data.pubsWithNoJournals;
		me.pubsWithInvalidJournals = data.pubsWithInvalidJournals;
		me.pubsMapped = data.pubsMapped;
		me.type = data.type;
		
		$.each(data.subdisciplineActivity, function(subdiscipline, density) {
			
			me.subdisciplineInfo[subdiscipline].publicationCount = density;
			
			var currentSubdisciplinesDiscipline = SUBDISCIPLINES[subdiscipline].discipline;
			
			if (me.disciplineInfo[currentSubdisciplinesDiscipline]) {
				
				me.disciplineInfo[currentSubdisciplinesDiscipline].publicationCount = 
					me.disciplineInfo[currentSubdisciplinesDiscipline].publicationCount + density; 
			} 
		});
		
		$(".hide-dom-on-init").show();
		me.setupView();
	},
	hasKey: function(key) {
		return (this.keyToMarkerManagers.hasOwnProperty(key));
	},
	show: function(key) {
		this.tableDiv.show();
		this.percentMappedDiv.show();
	},
	hide: function(key) {
		this.tableDiv.hide();
		this.percentMappedDiv.hide();
	},
	cleanView: function() {
		this.hide();
	},
	initView: function() {
		this.show();
		this.changeFilter(this.currentSelectedFilter);
	},
	parseIDIntoScienceTypeAreaID: function(rawID) {
		
		var type = rawID.substring(0, rawID.indexOf("-"));
		var area = rawID.substring(rawID.indexOf("-") + 1);
		
		return [type, area];
	},
	setupView: function() {
		
		var me = this;
		
		var dom = me.dom;
		var filter = $('<div class="science-areas-filter">' +
				'<span id="' + dom.firstFilterID + '" class="' + dom.filterOptionClass + ' ' + dom.activeFilterClass + '">' + dom.firstFilterLabel + '</span> | ' +
		    	'<span id="' + dom.secondFilterID + '" class="' + dom.filterOptionClass + '">' + dom.secondFilterLabel + '</span>' +
		    	'<img class="'+ dom.filterInfoIconClass +'" id="imageIconTwo" src="'+ infoIconUrl +'" alt="information icon" title="" /></div>');
		me.tableDiv.append(filter);
		createToolTip($("#imageIconTwo"), $('#toolTipTwo').html(), "topLeft");
		initFilter(dom);
		
		var table = $('<table>');
		table.attr('id', 'datatable');
		table.addClass('datatable-table');
		
		var thead = $('<thead>');
		var tr = $('<tr>');
		
		var levelOfScienceAreaTH = $('<th>');
		levelOfScienceAreaTH.html('Level of Science Area');
		
		var scienceAreasTH = $('<th>');
		scienceAreasTH.attr("id", "science-areas-th");
		if (this.currentSelectedFilter === SCIMAP_TYPE.SUBDISCIPLINE ) {
			scienceAreasTH.html('Subdisciplines');
		} else {
			scienceAreasTH.html('Disciplines');
		}
		
		var activityCountTH = $('<th>');
		activityCountTH.html('# of pubs.');
		activityCountTH.attr("id", "activity-count-column");

		var percentageActivityTH = $('<th>');
		percentageActivityTH.html('% of activity');
		percentageActivityTH.attr("id", "percentage-activity-column");

		tr.append(levelOfScienceAreaTH);
		tr.append(scienceAreasTH);
		tr.append(activityCountTH);
		tr.append(percentageActivityTH);
		
		thead.append(tr);
		
		table.append(thead);
		
		var tbody = $('<tbody>');
		
		var rowsToInsert = [];
		var i = 0;
		
		$.each(me.disciplineInfo, function(index, item) {
			rowsToInsert[i++] = '<tr id="' + SCIMAP_TYPE.DISCIPLINE + '-' + index + '" style="color:' + DISCIPLINES[index].color + ';"><td>' + SCIMAP_TYPE.DISCIPLINE + '</td>';
			rowsToInsert[i++] = '<td>' + item.label + '</td>';
			rowsToInsert[i++] = '<td>' + item.publicationCount.toFixed(1) + '</td>';
			rowsToInsert[i++] = '<td>' + (100 * (item.publicationCount / me.pubsMapped)).toFixed(1) + '</td></tr>';
		});
		
		
		$.each(me.subdisciplineInfo, function(index, item) {
			rowsToInsert[i++] = '<tr id="' + SCIMAP_TYPE.SUBDISCIPLINE + '-' + index + '" style="color:' + DISCIPLINES[SUBDISCIPLINES[index].discipline].color + ';"><td>' + SCIMAP_TYPE.SUBDISCIPLINE + '</td>';
			rowsToInsert[i++] = '<td>' + item.label + '</td>';
			rowsToInsert[i++] = '<td>' + item.publicationCount.toFixed(1) + '</td>';
			rowsToInsert[i++] = '<td>' + (100 * (item.publicationCount / me.pubsMapped)).toFixed(1) + '</td></tr>';
		});
		
		tbody.append(rowsToInsert.join(''));

		table.append(tbody);
		me.tableDiv.append(table);
		
		table.children("tbody").children("tr").mouseenter(function() {
			
			var params = me.parseIDIntoScienceTypeAreaID($(this).attr("id"));
			me.sciMapWidget.mouseIn(params[0], params[1]);
		});
		
		table.children("tbody").children("tr").mouseleave(function() {

			var params = me.parseIDIntoScienceTypeAreaID($(this).attr("id"));
			me.sciMapWidget.mouseOut(params[0], params[1]);
		});
		
		/*
		 * GMAIL_STYLE_PAGINATION_CONTAINER_CLASS, ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER has to be declared 
		 * for the filter & pagination to work properly.
		 * */
		GMAIL_STYLE_PAGINATION_CONTAINER_CLASS = me.dom.paginationContainerClass;
		ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER = me.currentSelectedFilter;
		
		if($.inArray(disciplineOrSubdisciplineDataTableFilter, $.fn.dataTableExt.afnFiltering) < 0) {
			$.fn.dataTableExt.afnFiltering.push(disciplineOrSubdisciplineDataTableFilter);
		}
		
		me.widget = table.dataTable({
		    "sDom": '<"' + me.dom.searchBarParentContainerClass 
		    			+ '"f><"filterInfo"i><"' 
		    			+ me.dom.paginationContainerClass + '"p><"table-separator"><"datatablewrapper"t>',
		    "aaSorting": [
		        [2, "desc"], [1,'asc']
		    ],
		    "asStripClasses": [],
		    "aoColumns": [{ "bVisible": false, "bSearchable": false },
		                  null,
		                  null,
		                  null], 
		    "iDisplayLength": 13,
		    "bInfo": true,
		    "oLanguage": {
				"sInfo": "_START_ - _END_ of _TOTAL_",
				"sInfoEmpty": "No matching science areas found",
				"sInfoFiltered": ""
			},
		    "sPaginationType": "gmail_style",
		    "fnDrawCallback": function () {
		    	
		        /* We check whether max number of allowed comparisions (currently 10) is reached
		         * here as well becasue the only function that is guaranteed to be called during 
		         * page navigation is this. No need to bind it to the nav-buttons becuase 1. It is over-ridden
		         * by built-in navigation events & this is much cleaner.
		         * */
//		        checkIfColorLimitIsReached();
		    }
		});
		
		
		var searchInputBox = $("." + me.dom.searchBarParentContainerClass).find("input[type=text]");
		searchInputBox.css("width", "140px");
		searchInputBox.after("<span id='reset-search' title='Clear search query'>X</span>" 
								+ "<img class='filterInfoIcon' id='searchInfoIcon' src='" + infoIconUrl + "' alt='information icon' title='' />");
		$("#reset-search").live('click', function() {
			me.widget.fnFilter("");
		});
		createToolTip($("#searchInfoIcon"), $('#searchInfoTooltipText').html(), "topLeft");
		
		var csvButton = '<hr class="subtle-hr"/><div id="main-science-areas-table-footer"><a id="csv" href="' +
						entityMapOfScienceSubDisciplineCSVURL + 
						'" class="map-of-science-links">Save All as CSV</a></div>';
		this.tableDiv.append(csvButton);
		
		var totalPublications = me.pubsWithNoJournals + me.pubsWithInvalidJournals + me.pubsMapped;
		$("#mapped-publications").text(addCommasToNumber(me.pubsMapped));
		$("#percent-mapped").text((100 * me.pubsMapped / totalPublications).toFixed(2));
		$("#total-publications").text(addCommasToNumber(totalPublications));
		
	},
	changeFilter: function(filterType) {
		var me = this;
		if (filterType === SCIMAP_TYPE.SUBDISCIPLINE) {
			
			$("#science-areas-th").html("Subdisciplines");
			if (me.widget) {
				me.widget.fnSettings()._iDisplayLength = 10;
			}
			me.currentSelectedFilter = SCIMAP_TYPE.SUBDISCIPLINE;
			$("a#csv").attr("href", entityMapOfScienceSubDisciplineCSVURL);
			
		} else {
			
			$("#science-areas-th").html("Disciplines");
			me.currentSelectedFilter = SCIMAP_TYPE.DISCIPLINE;
			if (me.widget) {
				me.widget.fnSettings()._iDisplayLength = 13;
			}
			$("a#csv").attr("href", entityMapOfScienceDisciplineCSVURL);
			
		}
		
		ACTIVE_DISCIPLINE_SUBDISCIPLINE_FILTER = me.currentSelectedFilter;

		if (me.widget) {
			me.widget.fnDraw();
		}
	}
});