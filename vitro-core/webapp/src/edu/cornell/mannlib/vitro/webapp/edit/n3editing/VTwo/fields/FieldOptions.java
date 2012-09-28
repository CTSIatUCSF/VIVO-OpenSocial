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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields;

import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

/**
 * Represents an object that can return a list of options
 * for an HTML select list.
 * 
 * @author bdc34 
 *        
 */
public interface FieldOptions {
    
    /**
     * Any object that are needed to get the options should
     * be passed in the constructor of the implementation.
     * 
     * @return return a map of value->label for the options. 
     * Should never return null.
     * 
     * @throws Exception 
     */
    public Map<String,String> getOptions(
            EditConfigurationVTwo editConfig, 
            String fieldName, 
            WebappDaoFactory wDaoFact) throws Exception;
}

/*
 * lic enum OptionsType {
LITERALS,x

HARDCODED_LITERALS,
STRINGS_VIA_DATATYPE_PROPERTY, 

INDIVIDUALS_VIA_OBJECT_PROPERTY, x

INDIVIDUALS_VIA_VCLASS, 

CHILD_VCLASSES, x

CHILD_VCLASSES_WITH_PARENT,
VCLASSGROUP,
FILE, 
UNDEFINED, x
DATETIME, 
DATE,
TIME
*/