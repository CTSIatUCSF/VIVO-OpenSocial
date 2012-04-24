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

/*

 * $Id$
 * CONFIDENTIAL AND PROPRIETARY. ? 2007 Revolution Health Group LLC. All rights reserved.
 * This source code may not be disclosed to others, used or reproduced without the written permission of Revolution Health Group.
 *
 */
package edu.cornell.mannlib.semservices.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class ClassUtils {
   public static final String VERSION = "$Rev$";
   private static final Logger LOG = Logger.getLogger(ClassUtils.class);

   private ClassUtils() {}

   @SuppressWarnings("unchecked")
   public static Method[] getMethods(Class<?> clazz) {
      if (Proxy.isProxyClass(clazz) && clazz.getInterfaces().length > 0 ) {
         return clazz.getInterfaces()[0].getDeclaredMethods();
      }

      ArrayList<Method> methods = new ArrayList<Method>();
      Class<?> interfaceClass = clazz;
      // only change our classes
      if ((interfaceClass.getPackage().getName().indexOf("java") != 0) && (interfaceClass != null)) {
         if (!interfaceClass.isInterface()) {
            Class[] interfaces = interfaceClass.getInterfaces();
            for (Class interfaceTemp : interfaces) {
               if (interfaceTemp.isInterface()) {
                  methods.addAll(Arrays.asList(interfaceTemp.getMethods()));
               }
            }
         } else {
            methods.addAll(Arrays.asList(interfaceClass.getMethods()));
         }
      }

      if (methods.isEmpty()) {
          methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
      }

      return methods.toArray(new Method[methods.size()]);
   }

   public static Object findEnumConstant(Class<Object> enumType, Object findConst) {
      Object result = null;
      Object[] enumConstants = enumType.getEnumConstants();
      for (Object enumConst : enumConstants) {
         if (enumConst.toString().equals(findConst.toString())) {
            result = enumConst;
            break;
         }
      }

      if (result == null && findConst != null) { // last try for XmlEnum..
         result = getXmlEnumFromValue(enumType, findConst.toString());
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   public static boolean isXmlEnum(Class<? extends Object> class1) {
      boolean result = false;
      try {
         Class xmlEnumClass = Class.forName("javax.xml.bind.annotation.XmlEnum");
         result = (xmlEnumClass != null) && (class1.getAnnotation(xmlEnumClass) != null);
      } catch (ClassNotFoundException e) {
         LOG.info("Class Not Found: javax.xml.bind.annotation.XmlEnum, this is ok if you aren't serializing JaxB Objects");
      }

      return result;
   }


   public static Object getXmlEnumFromValue(Class<Object> enumType, String findConst) {
      Object result = null;
      try {
         if (isXmlEnum(enumType)) {
            // XmlEnum will have a "fromValue" method to convert the const to the enum const
            Method fromValue = enumType.getMethod("fromValue", String.class);
            result = fromValue.invoke(enumType, findConst);
         }
      } catch (Exception e) {
         // could happen..just give up and return null
      }
      return result;
   }

   public static boolean isArrayType(Class<?> type) {
      return List.class.isAssignableFrom(type);
   }

   public static boolean isMapType(Class<?> type) {
      return Map.class.isAssignableFrom(type);
   }

   public static Class<?> getArrayElementType(Method method, int paramIndex) {
      Type[] types = method.getGenericParameterTypes();
      if (types.length > paramIndex) {
         return getArrayElementType(types[paramIndex]);
      }
      return null;
   }

   public static Class<?> getArrayElementType(Type genericType) {
      Class<?> result = null;

      if (genericType instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType) genericType;
         //Type raw = pt.getRawType();
         //Type owner = pt.getOwnerType();
         Type[] typeArgs = pt.getActualTypeArguments();
         if (typeArgs[0] instanceof GenericArrayType) {
            Class<?> arrayElementType = (Class<?>) ((GenericArrayType) typeArgs[0])
                                   .getGenericComponentType();
            result = arrayElementType;
         } else if (typeArgs[0] instanceof Class<?>){
            result = (Class<?>) typeArgs[0];
         }
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   public static boolean hasEmptyConstructor(Class<?> type) {
      boolean result = false;
      Constructor[] constructors = type.getDeclaredConstructors();
      for (Constructor constructor : constructors) {
         if (constructor.getParameterTypes().length == 0) {
            result = true;
            break;
         }
      }

      return result;
   }
}
