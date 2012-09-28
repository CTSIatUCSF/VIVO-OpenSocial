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
  
    var objectPropHierarchyUtils = {
    onLoad: function(urlBase,displayOption,type) {
        this.imagePath = urlBase + "/images/";
        this.propType = type;
        this.initObjects();
        this.expandAll.hide();
//        this.toggleDiv.hide();
        this.checkJsonTree();

        if ( noProps ) {
            this.buildNoPropsHtml();
        }
        else if ( displayOption == "all" ) {
            this.buildAllPropsHtml();
        }
        else if ( displayOption == "group" ) {
            this.buildPropertyGroupHtml();
        }
        else {
            this.buildPropertyHierarchyHtml();
            this.wireExpandLink();
        }
        
        if ( displayOption == "hierarchy" || displayOption == "group") {
            this.expandAll.show();
        }
//        else if ( displayOption == "group" ) {
//            this.toggleDiv.show();
//        }
        this.bindEventListeners();
    },

    initObjects: function() { 
        this.expandAll = $('span#expandAll').find('a');   
        this.classCounter = 1;
        this.expandCounter = 1;
        this.classHtml = "";
        this.clickableSpans = [] ;
        this.form = $('form#classHierarchyForm');
        this.select = $('select#displayOption');
        this.addProperty = $('input#addProperty');
//        this.toggleDiv = $('div#propsToggleDiv');
//        this.toggleSpan = $('span#propsToggle');
//        this.toggleLink = $('span#propsToggle').find('a');
        noProps = new Boolean;
    },

    bindEventListeners: function() {
        if ( this.propType == "object" ) {
            this.select.change(function() {
                if ( objectPropHierarchyUtils.select.val() == "all" ) {
                    objectPropHierarchyUtils.form.attr("action", "listPropertyWebapps");
                }
                else if ( objectPropHierarchyUtils.select.val() == "hierarchy") {
                    objectPropHierarchyUtils.form.attr("action", "showObjectPropertyHierarchy");
                }
                else {
                    objectPropHierarchyUtils.form.attr("action", "listPropertyGroups");
                }
                objectPropHierarchyUtils.form.submit();
            });
         
            this.addProperty.click(function() {
                objectPropHierarchyUtils.form.attr("action", "editForm?controller=Property");
                objectPropHierarchyUtils.form.submit();
            });
        }
        else  {
            this.select.change(function() {
                if ( objectPropHierarchyUtils.select.val() == "all" ) {
                    objectPropHierarchyUtils.form.attr("action", "listDatatypeProperties");
                }
                else if ( objectPropHierarchyUtils.select.val() == "hierarchy" ) {
                    objectPropHierarchyUtils.form.attr("action", "showDataPropertyHierarchy");
                }
                else {
                    objectPropHierarchyUtils.form.attr("action", "listPropertyGroups");
                }
                objectPropHierarchyUtils.form.submit();
            });
         
            this.addProperty.click(function() {
                objectPropHierarchyUtils.form.attr("action", "editForm?controller=Dataprop");
                objectPropHierarchyUtils.form.submit();
            });
        }
        if ( this.propType == "group" ) {
            this.expandAll.click(function() {
            
                if ( objectPropHierarchyUtils.expandAll.text() == "hide properties" ) { 
                    $('td.subclassCell').parent('tr').hide();
                    objectPropHierarchyUtils.expandAll.text("show properties");
                }
                else {
                    $('td.subclassCell').parent('tr').show();
                    objectPropHierarchyUtils.expandAll.text("hide properties");
                }
            });
        }
    },

    checkJsonTree: function() { 
        if ( json.length == 1 ) {
            $.each(json, function() {
                // check to see whether we have a 'no properties' message or an actual json tree
                if ( this.name.indexOf("properties") != -1 && this.data == undefined ) {
                        noProps = true;
                }
                else {
                    noProps = false;
                }
            });
        }
        else {
            noProps = false;
        }
    },

    buildPropertyHierarchyHtml: function() {

        $.each(json, function() {
            $newClassSection = jQuery("<section></section>", {
                id: "classContainer" + objectPropHierarchyUtils.classCounter
            });
            var descendants = "";
            var headerSpan = "";
            var closingTable = "</table>";
            
            if ( this.children.length ) {
                descendants = objectPropHierarchyUtils.getTheChildren(this);
                closingTable = "";
                headerSpan = "<span class='headerSpanPlus' id='headerSpan" + objectPropHierarchyUtils.classCounter 
                              + "' view='less'>&nbsp;</span>";
            }

            objectPropHierarchyUtils.classHtml += "<div>" + this.name + headerSpan + "</div>" + "<table class='classHierarchy' id='classHierarchy" 
                                      + objectPropHierarchyUtils.classCounter + "'>" ;

            objectPropHierarchyUtils.classHtml += "<tr><td class='classDetail'>Local Name:</td><td>" 
                                               + (this.data.internalName.length > 0 ? this.data.internalName : "none" ) + "</td></tr>";

            objectPropHierarchyUtils.classHtml += "<tr><td class='classDetail'>Group:</td><td>" 
                                               + (this.data.group.length > 0 ? this.data.group : "unspecified" ) + "</td></tr>";

            objectPropHierarchyUtils.classHtml += "<tr><td class='classDetail'>Domain Class:</td><td>" 
                                               + (this.data.domainVClass.length > 0 ? this.data.domainVClass : "none" ) + " ";

            objectPropHierarchyUtils.classHtml += "<span class='rangeClass'>Range Class:</span>" 
                                               + (this.data.rangeVClass.length > 0 ? this.data.rangeVClass : "none" ) + "</td></tr>";

            if ( descendants.length > 1 ) {
               descendants = descendants.substring(0, descendants.length - 10);
            }
 
            objectPropHierarchyUtils.classHtml += descendants;

            objectPropHierarchyUtils.classHtml += closingTable;
       //     alert(objectPropHierarchyUtils.classHtml);
            $newClassSection.html(objectPropHierarchyUtils.classHtml);
            $newClassSection.appendTo($('section#container'));
            objectPropHierarchyUtils.makeHeaderSpansClickable(objectPropHierarchyUtils.classCounter);
            objectPropHierarchyUtils.makeSubpropertySpansClickable();
            objectPropHierarchyUtils.clickableSpans = [] ;
            objectPropHierarchyUtils.classHtml = "";
            objectPropHierarchyUtils.classCounter += 1;
        });
    },

    getTheChildren: function(node) {
        var childDetails = "";
        var subclassString = " ";
        var ctr = 0
        $.each(node.children, function() {
            if ( ctr == 0 ) {
                childDetails += "<tr><td class='classDetail'>Subproperties:</td>";
                ctr = ctr + 1;
            }
            else {
                childDetails += "<tr><td></td>" ;
            }

            if ( this.children.length == 1 ) {
                subclassString += "<span style='font-size:0.8em'> (1 subclass)</span>"; 
            }
            else if ( this.children.length > 1 ) {
                subclassString += "<span style='font-size:0.8em'> (" + this.children.length + " subclasses)</span>";
            }

            childDetails += "<td class='subclassCell' colspan='2'><span class='subclassExpandPlus' id='subclassExpand" 
                            + objectPropHierarchyUtils.expandCounter + "'>&nbsp;</span>" 
                            + this.name + subclassString + "</td></tr><tr><td></td><td colspan='2'><table id='subclassTable" 
                            + objectPropHierarchyUtils.expandCounter + "' class='subclassTable'>";

            subclassString = " ";

            objectPropHierarchyUtils.clickableSpans.push('subclassExpand' + objectPropHierarchyUtils.expandCounter);
            
            objectPropHierarchyUtils.expandCounter += 1;
            
            childDetails += "<tr><td class='classDetail'>Local Name:</td><td>" 
                                               + (this.data.internalName.length > 0 ? this.data.internalName : "none" ) + "</td></tr>";

            childDetails += "<tr><td class='classDetail'>Group:</td><td>" 
                                               + (this.data.group.length > 0 ? this.data.group : "unspecified" ) + "</td></tr>";

            childDetails += "<tr><td class='classDetail'>Domain Class:</td><td>" 
                                               + (this.data.domainVClass.length > 0 ? this.data.domainVClass : "none" ) + " ";

            childDetails += "<span class='rangeClass'>Range Class:</span>" 
                                               + (this.data.rangeVClass.length > 0 ? this.data.rangeVClass : "none" ) + "</td></tr>";

            if ( this.children ) {
                var grandChildren = objectPropHierarchyUtils.getTheChildren(this);
                childDetails += grandChildren;
            }
        });
        childDetails += "</table></td></tr>";
        return childDetails;
    },
    
    makeHeaderSpansClickable: function(ctr) {

        var $clickableHeader = $('section#classContainer' + ctr).find('span.headerSpanPlus');

        $clickableHeader.click(function() {
            if ( $clickableHeader.attr('view') == "less" ) {
                $clickableHeader.addClass("headerSpanMinus");
                $('table#classHierarchy' + ctr).find('span.subclassExpandPlus').addClass("subclassExpandMinus");
                $('table#classHierarchy' + ctr).find('table.subclassTable').show();
                $clickableHeader.attr('view', 'more' );
            }
            else {
                $clickableHeader.removeClass("headerSpanMinus");
                $('table#classHierarchy' + ctr).find('span.subclassExpandPlus').removeClass("subclassExpandMinus");
                $('table#classHierarchy' + ctr).find('table.subclassTable').hide();
                $clickableHeader.attr('view', 'less' );
            }
        });
    },//    $('myOjbect').css('background-image', 'url(' + imageUrl + ')');
    
    makeSubpropertySpansClickable: function() {
        $.each(objectPropHierarchyUtils.clickableSpans, function() {
            var currentSpan = this;
            var $clickableSpan = $('section#container').find('span#' + currentSpan);
            var $subclassTable = $('section#container').find('table#subclassTable' + currentSpan.replace("subclassExpand",""));

            $clickableSpan.click(function() {
                if ( $subclassTable.is(':visible') ) {
                    $subclassTable.hide();
                    $subclassTable.find('table.subclassTable').hide();
                    $subclassTable.find('span').removeClass("subclassExpandMinus");
                    $clickableSpan.removeClass("subclassExpandMinus");
                }
                else {
                    $subclassTable.show();
                    $clickableSpan.addClass("subclassExpandMinus");
                }
            });
        });
    },
    
    wireExpandLink: function() {
        this.expandAll.click(function() {
            if ( objectPropHierarchyUtils.expandAll.text() == "expand all" ) {
                objectPropHierarchyUtils.expandAll.text("collapse all");
                $('span.headerSpanPlus').addClass("headerSpanMinus");
                $('table.classHierarchy').find('span.subclassExpandPlus').addClass("subclassExpandMinus");
                $('table.classHierarchy').find('table.subclassTable').show();
                $('section#container').find('span.headerSpanPlus').attr('view','more');
            }
            else {
                objectPropHierarchyUtils.expandAll.text("expand all");
                $('span.headerSpanPlus').removeClass("headerSpanMinus");
                $('table.classHierarchy').find('span.subclassExpandPlus').removeClass("subclassExpandMinus");
                $('table.classHierarchy').find('table.subclassTable').hide();
                $('section#container').find('span.headerSpanPlus').attr('view','less');
            }
        });
    },
     
    buildAllPropsHtml: function() {

        $.each(json, function() {
            $newClassSection = jQuery("<section></section>", {
                id: "classContainer" + objectPropHierarchyUtils.classCounter
            });
            
            objectPropHierarchyUtils.classHtml += "<div>" + this.name + "</div>" + "<table class='classHierarchy' id='classHierarchy" 
                                      + objectPropHierarchyUtils.classCounter + "'>" ;

            objectPropHierarchyUtils.classHtml += "<tr><td class='classDetail'>Local Name:</td><td>" 
                                         + (this.data.internalName.length > 0 ? this.data.internalName : "none" ) + "</td></tr>";

            objectPropHierarchyUtils.classHtml += "<tr><td class='classDetail'>Group:</td><td>" 
                                         + (this.data.group.length > 0 ? this.data.group : "unspecified" ) + "</td></tr>";

            objectPropHierarchyUtils.classHtml += "<tr><td class='classDetail'>Domain Class:</td><td>" 
                                         + (this.data.domainVClass.length > 0 ? this.data.domainVClass : "none" ) + " ";

            objectPropHierarchyUtils.classHtml += "<span class='rangeClass'>Range Class:</span>" 
                                         + (this.data.rangeVClass.length > 0 ? this.data.rangeVClass : "none" ) + "</td></tr>";

            objectPropHierarchyUtils.classHtml += "</table>";

            $newClassSection.html(objectPropHierarchyUtils.classHtml);
            $newClassSection.appendTo($('section#container'));
            objectPropHierarchyUtils.classHtml = "";
            objectPropHierarchyUtils.classCounter += 1;
        });
    },

    buildNoPropsHtml: function() {

        $.each(json, function() {
            $newClassSection = jQuery("<section></section>", {
                id: "classContainer" + objectPropHierarchyUtils.classCounter
            });
            
            objectPropHierarchyUtils.classHtml = "<h4>" + this.name + "</h4>";
            $newClassSection.html(objectPropHierarchyUtils.classHtml);
            $newClassSection.appendTo($('section#container'));
            objectPropHierarchyUtils.classHtml = "";
        });
    },

    buildPropertyGroupHtml: function() {

        $.each(json, function() {
            $newClassSection = jQuery("<section></section>", {
                id: "classContainer" + objectPropHierarchyUtils.classCounter
            });
            var descendants = "";

            if ( this.children.length ) {
                var ctr = 0;
                $.each(this.children, function() {
                    if ( ctr == 0 ) {
                        descendants += "<tr><td class='classDetail'>Properties:</td>";
                        ctr = ctr + 1;
                    }
                    else {
                        descendants += "<tr><td></td>" ;
                    }

                    descendants += "<td class='subclassCell'>" + this.name + "</td></tr>";
//                    descendants += "<tr><td></td><td><table class='innerDefinition'><tr><td>" + this.data.shortDef + "</td></tr></table></td></tr>";

                });
                descendants += "</table></td></tr>";
            }

            objectPropHierarchyUtils.classHtml += "<div>" + this.name + "</div>" + "<table class='classHierarchy' id='classHierarchy" 
                                               + objectPropHierarchyUtils.classCounter + "'>" ;

            if ( this.data.displayRank.length > 0 ) {
                objectPropHierarchyUtils.classHtml += "<tr><td class='classDetail'>Display Rank:</td><td>" + this.data.displayRank + "</td></tr>"
            }
            
            objectPropHierarchyUtils.classHtml += descendants;

            objectPropHierarchyUtils.classHtml += "</table>";
           // alert(objectPropHierarchyUtils.classHtml);
            $newClassSection.html(objectPropHierarchyUtils.classHtml);
            $newClassSection.appendTo($('section#container'));
            objectPropHierarchyUtils.classHtml = "";
            objectPropHierarchyUtils.classCounter += 1;
        });
    }
    
}
