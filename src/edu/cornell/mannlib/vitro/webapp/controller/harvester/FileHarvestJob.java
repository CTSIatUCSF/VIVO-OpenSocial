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

package edu.cornell.mannlib.vitro.webapp.controller.harvester; 

import java.io.File;


/**
 * Handles specifics of a file harvest.  
 * @author mbarbieri
 *
 */
interface FileHarvestJob {

    /**
     * Checks to make sure the uploaded file can be handled by this job (for instance, are we looking for a CSV file with specific columns?)
     * @param file the uploaded file to check
     * @return null if success, message to be returned to the user if failure
     */
    String validateUpload(File file);

    /**
     * Gets the path on the server of the file which the user can download to serve as a guide for what to upload.
     * @return the path on the server of the file which the user can download to serve as a guide for what to upload.
     */
    String getTemplateFilePath();

    /**
     * Gets the console script which can be used to run the harvest job. 
     * @return the console script which can be used to run the harvest job
     */
    String getScript();

    /**
     * The path to the file containing the RDF/XML triples that get added to VIVO.
     * @return the path to the file containing the RDF/XML triples that get added to VIVO
     */
    String getAdditionsFilePath();

    /**
     * A heading to be shown at the top of the page.
     * @return a heading to be shown at the top of the page
     */
    String getPageHeader();

    /**
     * A heading to be shown above the area where links to profiles of newly-harvested entities are listed. 
     * @return a heading to be shown above the area where links to profiles of newly-harvested entities are listed
     */
    String getLinkHeader();
    
    /**
     * Get an array of fully-qualified rdf:type values.  When the harvest run is complete, any new entities which have an rdf:type represented
     * in this array will have a link displayed on the page allowing the user to visit the new profile. 
     * @return an array of types to be used in links
     */
    String[] getRdfTypesForLinks();
    
    /**
     * Get the HTML to be shown on the page immediately next to the "Download" button for the template.
     * @return the HTML to be shown on the page immediately next to the "Download" button for the template.
     */
    String getTemplateDownloadHelp();
    
    /**
     * Get the HTML to be shown in the collapsible "Help" area in the "Fill in data" section of the page.
     * @return the HTML to be shown in the collapsible "Help" area in the "Fill in data" section of the page.
     */
    String getTemplateFillInHelp();
    
    /**
     * Get the message to show to the user if there are no newly-harvested entities to show them.
     * @return the message to show to the user if there are no newly-harvested entities to show them
     */
    String getNoNewDataMessage();
}

