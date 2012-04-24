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

package edu.cornell.mannlib.vitro.webapp.utils.generators;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils;
import edu.cornell.mannlib.vitro.webapp.utils.FrontEndEditingUtils.EditMode;

public class EditModeUtils {
	private static Log log = LogFactory.getLog(EditModeUtils.class);

    
    /* *************** Static utility methods used in role-based generators *********** */
	public static EditMode getEditMode(VitroRequest vreq, List<String> possiblePredicates) {
    	//We're making some assumptions here: That there is only one role objec tot one activity object
    	//pairing, i.e. the same role object can't be related to a different activity object
    	//That said, there should only be one role to Activity predicate linking a role to an activity
    	//So if 
    	Individual object = EditConfigurationUtils.getObjectIndividual(vreq);
    	boolean foundErrorMode = false;
    	int numberEditModes = 0;
    	int numberRepairModes = 0;
    	int numberPredicates = possiblePredicates.size();
    	for(String predicate:possiblePredicates) {
    		EditMode mode = FrontEndEditingUtils.getEditMode(vreq, object, predicate);
    		log.debug("Checking edit mode for " + predicate + " and retrieved " + mode.toString());
    		//Any error  mode should result in error
    		if(mode == EditMode.ERROR) {
    			log.debug("Edit mode is error for " + predicate);
    			foundErrorMode = true;
    			break;
    		}
    		if(mode == EditMode.EDIT) {
    			log.debug("Edit mode is EDIT for " + predicate);
    			numberEditModes++;
    		}
    		else if(mode == EditMode.REPAIR) {
    			log.debug("Edit mode is REPAIR for " + predicate);
    			numberRepairModes++;
    		}
    		
    	}
    	log.debug("Number of edit editModes " + numberEditModes);
    	log.debug("Number of repair editModes " + numberRepairModes);
    	log.debug("Found error mode: " + foundErrorMode);
    	//if found an error or if more than one edit mode returned, incorrect

    	if(foundErrorMode || numberEditModes > 1) 
    	{
    		return EditMode.ERROR;
    	}
    	EditMode mode = EditMode.ADD;
    	//if exactly one edit mode found, then edit mode
    	if(numberEditModes == 1) {
    		mode = EditMode.EDIT;
    	}
    	//if all modes are repair, this means that all of them have zero statements returning
    	//which is incorrect
    	if(numberRepairModes == numberPredicates) {
    		mode = EditMode.REPAIR;
    	}    	
    	//otherwise all the modes are Add and Add will be returned
    	return mode;
	}
	
	public static boolean isAddMode(EditMode mode) {
    	return (mode == EditMode.ADD);
    }
    
    public static boolean isEditMode(EditMode mode) {
    	return (mode == EditMode.EDIT);
    }
    
   public static boolean isRepairMode(EditMode mode) {
    	return (mode == EditMode.REPAIR);
    }

}
