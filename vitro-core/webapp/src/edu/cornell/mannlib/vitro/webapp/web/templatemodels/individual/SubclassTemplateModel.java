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

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.individual;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

public class SubclassTemplateModel extends BaseTemplateModel implements Comparable<SubclassTemplateModel> { 
    
    private final VClass vclass;
    private final List<ObjectPropertyStatementTemplateModel> statements;
    
    SubclassTemplateModel(VClass vclass, List<ObjectPropertyStatementTemplateModel> statements) {
        // NB vclass may be null. If the statements don't belong to any subclass, a dummy SubclassTemplateModel
        // is created with a null vclass, so that the data can be presented in a uniform way to the template.
        this.vclass = vclass; 
        this.statements = statements;
    }

    @Override
    public int compareTo(SubclassTemplateModel other) {
        
        if (other == null) {
            return -1;
        }
        
        VClass vclassOther = other.getVClass();
        if (vclass == null) {
            return vclassOther == null ? 0 : 1;
        }
        if (vclassOther == null) {
            return -1;
        }
        
        int rank = vclass.getDisplayRank();
        int rankOther = vclassOther.getDisplayRank();
        
        int intCompare = 0;
        // Values < 1 are undefined and go at end, not beginning
        if (rank < 1) {
            intCompare = rankOther < 1 ? 0 : 1;
        } else if (rankOther < 1) {
            intCompare = -1;
        } else {           
            intCompare = ((Integer)rank).compareTo(rankOther);
        }

        if (intCompare != 0) {
            return intCompare;        
        }
        
        // If display ranks are equal, sort by name     
        String name = getName();
        String nameOther = vclassOther.getName();
               
        if (name == null) {
            return nameOther == null ? 0 : 1;
        } 
        if (nameOther == null) {
            return -1;
        }
        return name.compareToIgnoreCase(nameOther);

    }
    
    protected VClass getVClass() {
        return vclass;
    }
    
    /* Accessor methods for templates */
    
    public String getName() {
        return vclass == null ? "" : vclass.getName();
    }
    
    public List<ObjectPropertyStatementTemplateModel> getStatements() {
        return statements;
    }
    
}
