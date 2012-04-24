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

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo;

import java.io.StringWriter;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * This is a data structure to allow a method to return
 * a pair of Model objects for additions and retractions.
 * 
 */
public class AdditionsAndRetractions {
    Model additions;
    Model retractions;
    
    public AdditionsAndRetractions(List<Model>adds, List<Model>retractions){
        Model allAdds = ModelFactory.createDefaultModel();
        Model allRetractions = ModelFactory.createDefaultModel();
        
        for( Model model : adds ) {
            allAdds.add( model );
        }
        for( Model model : retractions ){
            allRetractions.add( model );
        }
        
        this.setAdditions(allAdds);
        this.setRetractions(allRetractions);
    }
    
    public AdditionsAndRetractions(Model add, Model retract){
        this.additions = add;
        this.retractions = retract;
    }
    
    public Model getAdditions() {
        return additions;
    }
    public void setAdditions(Model additions) {
        this.additions = additions;
    }
    public Model getRetractions() {
        return retractions;
    }
    public void setRetractions(Model retractions) {
        this.retractions = retractions;
    }
    
    @Override
    public String toString(){
        String str = "{";
        
        str += "\nadditions:[";
        if( getAdditions() != null ) {
           StringWriter writer = new StringWriter();
           getAdditions().write(writer, "N3-PP");
           str += "\n" + writer.toString() + "\n";
        }
        str += "],\n";        
        
        str += "\nretractions:[";
        if( getRetractions() != null ) {
           StringWriter writer = new StringWriter();
           getRetractions().write(writer, "N3-PP");
           str += "\n" + writer.toString() + "\n";
        }
        str += "],\n";        
        
        return str;
    }
    
}