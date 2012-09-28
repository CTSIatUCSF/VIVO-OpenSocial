<#--
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
-->

<#-- Template for dump directives -->

 ${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/dump.css" />')}

<div class="dump">
    <h3>${title}</h3>
    
    <#-- dump has been changed to dumpValue to avoid confusion 
    with the dump directive which is stored in the DataModel as 'dump' -->    
    <@doDump dumpValue />
</div>

<#macro doDump dumpValue>
    <#if dumpValue?keys?has_content>
        <ul>
            <#list dumpValue?keys as key>
                <li class="variable">
                    <p><strong>Variable name:</strong> ${key}</p>  
                    
                    <#local type = dumpValue[key].type!>
                    <#if type == "Directive" || type == "Method"> 
                        <@doMethod dumpValue[key] />
                    <#else>                
                        <@doTypeAndValue dumpValue[key] />
                    </#if>
                </li>       
            </#list>
        </ul> 
    </#if>
</#macro>

<#macro doTypeAndValue map isMethod=false>
    <#local type = map.type!>
    <#if type?has_content>
        <p><strong>Type:</strong> ${type}</p>
        
        <#if map.dateType?has_content>
            <p><strong>Date type:</strong> ${map.dateType}</p>
        </#if>
    </#if>   

    <#local value = map.value!>   
    <#-- Not value?has_content: we want to print [empty] for empty strings.
         See doScalarValue macro. For methods, we don't show a list of values
         unless there is a value. --> 
    <#if value?? && (value?has_content || ! isMethod)> 
        <div class="values">
            <#if type?contains(".")><@doObjectValue value />
            <#elseif value?is_sequence><@doSequenceValue value type />
            <#elseif value?is_hash_ex><@doMapValue value />            
            <#else><@doScalarValue value />
            </#if>
       </div>
    </#if>                         
</#macro>

<#macro doObjectValue obj>
    <#if obj.properties?has_content>
        <p><strong>Properties:</strong></p>
        <ul class="properties">
            <#list obj.properties?keys as property>
                <@liItem>
                    ${property} => <@divValue><@doTypeAndValue obj.properties[property] /></@divValue>
                </@liItem>
            </#list>
        </ul>
    </#if>
    
    <#if obj.methods?has_content>
        <p><strong>Methods:</strong</p>
        <ul class="methods">
            <#list obj.methods?keys as method>
                <#local value = obj.methods[method]>
                <@liItem>
                    <#if ! value?has_content> <#-- no return value -->
                        ${method} 
                    <#elseif value?is_string> <#-- value is return type -->
                        ${method} => ${value}
                    <#else> <#-- no-arg method: value is result of method invocation -->
                        <#local isMethod = true>
                        ${method} => <@divValue><@doTypeAndValue value isMethod /></@divValue>
                    </#if>
                </@liItem>
            </#list>
        </ul>
    </#if>
</#macro>

<#macro doSequenceValue seq type>
    <strong>Values:</strong>
    <#if seq?has_content>
        <ul class="sequence">
            <#list seq as item>
                <@liItem>
                    <#if type == "Sequence">
                        Item ${item_index}: 
                        <@divValue><@doTypeAndValue item /></@divValue>
                    <#else><@doTypeAndValue item />
                    </#if>                    
                </@liItem>
            </#list>
        </ul>
     <#else>[none]
     </#if>
</#macro>

<#macro doMapValue map>
    <strong>Values:</strong>
    <#if map?has_content>
        <ul class="map">
            <#list map?keys as key>
                <@liItem>
                    ${key} => <@divValue><@doTypeAndValue map[key] /></@divValue>
                </@liItem>
            </#list>
        </ul>
    <#else>[none]
    </#if>
</#macro>

<#macro doScalarValue value>
    <strong>Value:</strong>
    
    <#if value?is_string>
        <#if value?has_content>${value}
        <#else>[empty]
        </#if>    
    <#elseif value?is_number>${value?c}
    <#elseif value?is_boolean>${value?string}
    <#elseif value?is_date>${value?string("EEEE, MMMM dd, yyyy hh:mm:ss a zzz")}
    </#if>    
</#macro>

<#macro doMethod method>
    <p><strong>Type:</strong> ${method.type} (${method.class})</p>
    <#local help = method.help!>
    <#if help?has_content>
        <p><strong>Help:</strong><p>
        <ul class="help">
            <#list help?keys as key>
                <li>
                    <#local value = help[key]>
                    <@divValue>                        
                        <#if value?is_string><p><strong>${key?cap_first}:</strong> ${value}</p>
                        <#else>
                            <p><strong>${key?cap_first}:</strong></p>
                            <ul>
                                <#if value?is_sequence>
                                    <#list value as item>
                                        <li>${item}</li>
                                    </#list>
                                <#elseif value?is_hash_ex>
                                    <#list value?keys as key>
                                        <li><strong>${key}:</strong> ${value[key]}</li>
                                    </#list>
                                </#if>
                            </ul>
                        </#if>
                    </@divValue>
                </li>
            </#list>        
        </ul>
    </#if>
</#macro>

<#macro divValue>
    <div class="value"><#nested></div>
</#macro>

<#macro liItem>
    <li class="item"><#nested></li>
</#macro>

