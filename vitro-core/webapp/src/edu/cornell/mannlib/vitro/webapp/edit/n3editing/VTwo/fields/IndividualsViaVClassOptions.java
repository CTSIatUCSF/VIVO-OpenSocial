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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.WebappDaoFactoryJena;
import edu.cornell.mannlib.vitro.webapp.dao.jena.pellet.PelletListener;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;

public class IndividualsViaVClassOptions implements FieldOptions {

    public static final String LEFT_BLANK = "";
    private List<String> vclassURIs;    
    private String defaultOptionLabel;    

    public IndividualsViaVClassOptions(String ... vclassURIs) throws Exception {
        super();
        
        if (vclassURIs==null )
            throw new Exception("vclassURIs must not be null or empty ");
                
        this.vclassURIs = new ArrayList<String>(vclassURIs.length);
        for(int i=0;i<vclassURIs.length;i++){
            if( vclassURIs[i] != null && !vclassURIs[i].trim().isEmpty() )
                this.vclassURIs.add(vclassURIs[i]);
        }                              
    }

    public FieldOptions setDefaultOptionLabel(String label){
        this.defaultOptionLabel = label;
        return this;
    }
    
    @Override
    public Map<String, String> getOptions(
            EditConfigurationVTwo editConfig, 
            String fieldName, 
            WebappDaoFactory wDaoFact) throws Exception {              
        
        Map<String, Individual> individualMap = new HashMap<String, Individual>();

        for( String vclassURI : this.vclassURIs){
            individualMap.putAll(  getIndividualsForClass( vclassURI, wDaoFact) );
        }

        //sort the individuals 
        List<Individual> individuals = new ArrayList<Individual>();
        individuals.addAll(individualMap.values());
        Collections.sort(individuals);

        Map<String, String> optionsMap = new HashMap<String,String>();
        
        if (defaultOptionLabel != null) {
            optionsMap.put(LEFT_BLANK, defaultOptionLabel);
        }

        if (individuals.size() == 0) {                        
            optionsMap.putAll( notFoundMsg() );
        } else {
            for (Individual ind : individuals) {                
                if (ind.getURI() != null) {
                    optionsMap.put(ind.getURI(), ind.getName().trim());
                }
            }
        }
        return optionsMap;
    }
    
    
    private Map<? extends String, ? extends String> notFoundMsg() {
        String msg = "No individuals found for "+ (vclassURIs.size() > 1?"types":"type");
        for( String uri : vclassURIs ){
            msg += " " + uri;
        }
        return Collections.singletonMap("", msg);
    }

    protected Map<String,Individual> getIndividualsForClass(String vclassURI, WebappDaoFactory wDaoFact ){
        Map<String, Individual> individualMap = new HashMap<String, Individual>();
        IndividualDao indDao = wDaoFact.getIndividualDao();
        
        List<Individual> indsForClass= indDao.getIndividualsByVClassURI(vclassURI, -1, -1);                          
        for (Individual ind : indsForClass) {
            if (ind.getURI() != null) {
                individualMap.put(ind.getURI(), ind);
            }
        }
        
        // if reasoning isn't available, we will also need to add
        // individuals asserted in subclasses
        individualMap.putAll( addWhenMissingInference(vclassURI, wDaoFact));        

        return individualMap;        
    }
    
    protected boolean isReasoningAvailable( WebappDaoFactory wDaoFact){
        boolean inferenceAvailable = false;
        if (wDaoFact instanceof WebappDaoFactoryJena) {
            PelletListener pl = ((WebappDaoFactoryJena) wDaoFact).getPelletListener();
            if (pl != null && pl.isConsistent() && !pl.isInErrorState()
                    && !pl.isReasoning()) {
                inferenceAvailable = true;
            }
        }
        return inferenceAvailable;
    }
    
    protected Map<String, Individual> addWhenMissingInference( String classUri , WebappDaoFactory wDaoFact ){
        boolean inferenceAvailable = isReasoningAvailable(wDaoFact);    
        Map<String,Individual> individualMap = new HashMap<String,Individual>();
        if ( !inferenceAvailable ) {
            for (String subclassURI : wDaoFact.getVClassDao().getAllSubClassURIs(classUri)) {
                for (Individual ind : wDaoFact.getIndividualDao().getIndividualsByVClassURI(subclassURI, -1, -1)) {
                    if (ind.getURI() != null) {                       
                        individualMap.put(ind.getURI(), ind);
                    }
                }
            }
        }
        return individualMap;   
    }    
}


