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
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.BaseEditElementVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditElementVTwo;
public class FieldVTwo {

//    public enum OptionsType {
//        LITERALS, 
//        HARDCODED_LITERALS,
//        STRINGS_VIA_DATATYPE_PROPERTY, 
//        INDIVIDUALS_VIA_OBJECT_PROPERTY, 
//        INDIVIDUALS_VIA_VCLASS, 
//        CHILD_VCLASSES, 
//        CHILD_VCLASSES_WITH_PARENT,
//        VCLASSGROUP,
//        FILE, 
//        UNDEFINED, 
//        DATETIME, 
//        DATE,
//        TIME
//    };

    public static String RDF_XML_LITERAL_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral";
                
       
    private String name;
    
    /**
     * List of basic validators.  See BaiscValidation.
     */
    private List <String> validators = new ArrayList<String>();  
    
    /** Object to get options from. */
    private FieldOptions fieldOptions;
    
    /**
     * Used for holding the expected/required datatype of the predicate when the predicate is a datatype propertyl.
     * this can be a explicit URI or a qname.
     * example:
     *  "this is the literal"^^<http://someuri.com/v1.2#type23>
     *  or
     *  "this is the literal"^^someprefix:type23
     */
    private String rangeDatatypeUri;
    
    /**
     * Used for holding the language of the literal when the predicate is a datatype property.
     * This is the lang of the literal.  lang strings must be: [a-z]+(-[a-z0-9]+)*
     */
    private String rangeLang;

    /**
     * Property for special edit element.
     */
    private EditElementVTwo editElement=null;;
        
    /* *********************** Constructors ************************** */
    
    public FieldVTwo() {}
        
    //private static String[] parameterNames = {"editElement","newResource","validators","optionsType","predicateUri","objectClassUri","rangeDatatypeUri","rangeLang","literalOptions","assertions"};
    //static{  Arrays.sort(parameterNames); }

    /* ****************** Getters and Setters ******************************* */

    public FieldVTwo setEditElement(EditElementVTwo editElement){
        this.editElement = editElement;
        
        if( editElement instanceof BaseEditElementVTwo)
            ((BaseEditElementVTwo) editElement).setField(this);
        
        return this;
    }
       

    public String getName(){
        return name;
    }                    

    public List <String> getValidators() {
        return validators;
    }
    public FieldVTwo setValidators(List <String> v) {
        validators = v;
        return this;
    }

    public FieldVTwo setOptions( FieldOptions fopts){
        this.fieldOptions = fopts;
        return this;
    }
    
    public FieldOptions getFieldOptions(){
        return this.fieldOptions;
    }
    
    public String getRangeDatatypeUri() {
        return rangeDatatypeUri;
    }
    public FieldVTwo setRangeDatatypeUri(String r) {
        if( rangeLang != null && rangeLang.trim().length() > 0 )
            throw new IllegalArgumentException("A Field object may not have both rangeDatatypeUri and rangeLanguage set");
        
        rangeDatatypeUri = r;
        return this;
    }
     
    public String getRangeLang() {
        return rangeLang;
    }

    public FieldVTwo setRangeLang(String rangeLang) {
        if( rangeDatatypeUri != null && rangeDatatypeUri.trim().length() > 0)
            throw new IllegalArgumentException("A Field object may not have both rangeDatatypeUri and rangeLanguage set");
        
        this.rangeLang = rangeLang;
        return this;
    }

    public EditElementVTwo getEditElement(){
        return editElement;
    }
        
    public FieldVTwo setName(String name){
        this.name = name;    
        return this;
    }
    
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
    //Check that two fields are the same
    //Not sure how to compare EditElement and FieldOptions 
    public boolean isEqualTo(FieldVTwo inputField) {
    	//Name required
    	boolean nameEqual = this.name.equals(inputField.getName());
    	//Validators initialized so can check
    	boolean validatorsEqual = this.validators.equals(inputField.getValidators());
    	//other fields optional and may be null
    	boolean rangeDatatypeEqual = ((this.rangeDatatypeUri == null && inputField.getRangeDatatypeUri() == null) ||
    								(this.rangeDatatypeUri != null && inputField.getRangeDatatypeUri() != null 
    								&& this.rangeDatatypeUri.equals(inputField.getRangeDatatypeUri())));
    	boolean rangeLangEqual = ((this.rangeLang == null && inputField.getRangeLang() == null) ||
				(this.rangeLang != null && inputField.getRangeLang() != null 
				&& this.rangeLang.equals(inputField.getRangeLang())));
    	
    	return (nameEqual &&
    			validatorsEqual &&
    			rangeDatatypeEqual &&
    			rangeLangEqual);
    	
    }
}
