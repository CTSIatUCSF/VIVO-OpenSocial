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

package edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.search.VitroSearchTermNames;

public class NameBoost implements DocumentModifier {

    /** 
     * These are the fields in the solr Document that
     * are related to the name.  If you modify the schema,
     * please consider if you need to change this list
     * of name fields to boost. 
     */
    static final VitroSearchTermNames term = new VitroSearchTermNames();
    String[] fieldsToBoost = {term.NAME_RAW,term.NAME_LOWERCASE,term.NAME_UNSTEMMED,term.NAME_STEMMED};
    
    
    final float boost;
    
    public NameBoost(float boost){
        this.boost = boost;
    }
    
    @Override
    public void modifyDocument(Individual individual, SolrInputDocument doc,
            StringBuffer addUri) {
        
        for( String fieldName : fieldsToBoost){
            SolrInputField field = doc.getField(fieldName);
            if( field != null ){                                
                field.setBoost(field.getBoost() + boost);
            }
        }        
    }

    @Override
    public void shutdown() {
        // do nothing.
    }

}
