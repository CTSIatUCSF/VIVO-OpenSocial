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

package edu.cornell.mannlib.vitro.webapp.web.beanswrappers;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapper.MethodAppearanceDecision;

/** A BeansWrapper that is more restrictive than EXPOSE_SAFE, by
 * exposing getters but not setters. A setter is defined for this
 * purpose as a method that returns void, or whose name
 * starts with "set". It also hides built-in methods of Java
 * utility classes like Map.put(), etc.
 * 
 * @author rjy7
 *
 */
public class ReadOnlyBeansWrapper extends BeansWrapper {

    private static final Log log = LogFactory.getLog(ReadOnlyBeansWrapper.class);
    
    public ReadOnlyBeansWrapper() {
        // Start by exposing all safe methods.
        setExposureLevel(EXPOSE_SAFE);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected void finetuneMethodAppearance(Class cls, Method method, MethodAppearanceDecision decision) {
        
        // How to define a setter? This is a weak approximation: a method whose name
        // starts with "set" or returns void.
        if ( method.getName().startsWith("set") ) {
            decision.setExposeMethodAs(null);
            
        } else if ( method.getReturnType().getName().equals("void") ) {
            decision.setExposeMethodAs(null);
            
        } else {
            
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(java.lang.Object.class)) {
                decision.setExposeMethodAs(null);
                
            } else {
                Package pkg = declaringClass.getPackage();
                if (pkg.getName().equals("java.util")) {
                    decision.setExposeMethodAs(null);
                }
            }
        }
    }
    
// For exposing a method as a property (when it's not named getX or isX). Note that this is not
// just a syntactic change in the template from X() to X, but also makes the value get precomputed.
//    private void exposeAsProperty(Method method, MethodAppearanceDecision decision)  {
//        try {
//            PropertyDescriptor pd = new PropertyDescriptor(method.getName(), method, null);
//            decision.setExposeAsProperty(pd);
//            decision.setMethodShadowsProperty(false);
//        } catch (IntrospectionException e) {
//            log.error(e, e);
//        }
//    }
    
}
