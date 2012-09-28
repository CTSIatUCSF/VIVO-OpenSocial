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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.MultiValueEditSubmission;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;

public class AntiXssValidationTest {
    
    @Test
    public void  testLiteral( ){
        //test all fields constructor
        AntiXssValidation validator =new AntiXssValidation();

        EditConfigurationVTwo eConf = new EditConfigurationVTwo();
        eConf.setEditKey("fakeEditKey");
        eConf.addField( new FieldVTwo().setName("X") );
        eConf.setLiteralsOnForm( Arrays.asList("X") );
        
        Map<String, String[]> params = new HashMap<String,String[]>();
        String[] vals= { "some sort of string" };
        params.put("X", vals);         
        
        MultiValueEditSubmission mvEditSub = 
            new MultiValueEditSubmission(params,eConf);                    
        
        Map<String, String> res = validator.validate(eConf, mvEditSub);
        Assert.assertEquals(null, res);
    }
    
    @Test
    public void  testAllURI( ){
        //test all fields constructor
        AntiXssValidation validator =new AntiXssValidation();

        EditConfigurationVTwo eConf = new EditConfigurationVTwo();
        eConf.setEditKey("fakeEditKey");
        eConf.setUrisOnform( Arrays.asList("X","Y","Z"));
        
        Map<String, String[]> params = new HashMap<String,String[]>();
        String[] strings0 = {"no problem 0"};
        params.put("X", strings0 ); 
        String[] strings1 = {"no problem 1"};
        params.put("Y", strings1 );
        String[] strings2 = {"no problem 2"};
        params.put("Z", strings2 );
        
        MultiValueEditSubmission mvEditSub = 
            new MultiValueEditSubmission(params,eConf);                    
        
        Map<String, String> res = validator.validate(eConf, mvEditSub);
        Assert.assertNull( res );
    }
    
    protected Map<String, String> testURI( String ... strings){

        AntiXssValidation validator = 
            new AntiXssValidation(Arrays.asList("X"));

        EditConfigurationVTwo eConf = new EditConfigurationVTwo();
        eConf.setEditKey("fakeEditKey");
        eConf.setUrisOnform( Arrays.asList("X"));
        
        Map<String, String[]> params = new HashMap<String,String[]>();              
        params.put("X", strings ); 
        
        MultiValueEditSubmission mvEditSub = 
            new MultiValueEditSubmission(params,eConf);                    
        
        return validator.validate(eConf, mvEditSub);        
    }
    
    @Test
    public void testURIValidation(){                   
        Map<String, String> result = testURI("http://this.should.be.fine.com/xyz#lskd?junk=a&bkeck=%23");                
        Assert.assertNull(result);
    }
    
    @Test
    public void testURIValidationWithScriptTagLevel1(){                                   
        Map<String, String> result = null;
        result = testURI("http:<SCRIPT SRC=http://ha.ckers.org/xss.js></SCRIPT>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<IMG SRC=JaVaScRiPt:alert('XSS')>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<IMG SRC=javascript:alert('XSS')>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<IMG SRC=javascript:alert(&quot;XSS&quot;)>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<IMG SRC=\"jav\tascript:alert('XSS');\">//bad.news.com");                
        Assert.assertNotNull(result);                
    }
    
    @Test
    public void testURIValidationWithScriptTagLevel2(){                                   
        Map<String, String> result = null;
        result = testURI("http:<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>//bad.news.com");                
        Assert.assertNotNull(result);
                
        result = testURI("http:<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<<SCRIPT>alert(\"XSS\");//<</SCRIPT>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>//bad.news.com");                
        Assert.assertNotNull(result);
        
        result = testURI("http:<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>//bad.news.com");                
        Assert.assertNotNull(result);                        
    }
    
    
}
