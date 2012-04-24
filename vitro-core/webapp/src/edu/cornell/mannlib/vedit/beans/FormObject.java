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

package edu.cornell.mannlib.vedit.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormObject implements Serializable {

    private HashMap<String, String> values = new HashMap<String, String>();
    private HashMap<String, List<Option>> optionLists = new HashMap<String, List<Option>>();
    private HashMap<String, List<Checkbox>> checkboxLists = new HashMap<String, List<Checkbox>>();
    private Map<String, String> errorMap = new HashMap<String, String>();
    private List<DynamicField> dynamicFields = new ArrayList<DynamicField>();

    public HashMap<String, String> getValues(){
        return values;
    }

    public void setValues(HashMap<String, String> values){
        this.values = values;
    }

    public String valueByName(String name){
        return values.get(name);
    }

    public HashMap<String, List<Option>> getOptionLists() {
        return optionLists;
    }

    public void setOptionLists(HashMap<String, List<Option>> optionLists) {
        this.optionLists = optionLists;
    }

    public List<Option> optionListByName(String key){
        return optionLists.get(key);
    }

    public HashMap<String, List<Checkbox>> getCheckboxLists(){
        return checkboxLists;
    }

    public Map<String, String> getErrorMap(){
        return errorMap;
    }

    public void setErrorMap(Map<String, String> errorMap){
        this.errorMap = errorMap;
    }

    public List<DynamicField> getDynamicFields() {
        return dynamicFields;
    }

    public void setDynamicFields(List<DynamicField> dynamicFields){
        this.dynamicFields = dynamicFields;
    }

}
