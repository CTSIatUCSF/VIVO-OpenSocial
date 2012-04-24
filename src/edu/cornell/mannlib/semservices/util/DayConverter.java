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

package edu.cornell.mannlib.semservices.util;



import java.util.Date;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.semservices.bo.Day;

public class DayConverter implements Converter {
   @SuppressWarnings("unused")
   private static final Log logger = LogFactory.getLog(DayConverter.class);
    /**
     * The default value specified to our Constructor, if any.
     */
    @SuppressWarnings("unused")
   private Object defaultValue = null;

    /**
     * Should we return the default value on conversion errors?
     */
    @SuppressWarnings("unused")
   private boolean useDefault = true;


   /**
    * Constructor
    */
   public DayConverter() {
      this.defaultValue = null;
      this.useDefault = false;
   }

   /**
    * Constructor with object
    * @param defaultValue
    */
   public DayConverter(Object defaultValue) {
      this.defaultValue = defaultValue;
      this.useDefault = true;
   }

    /* (non-Javadoc)
    * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
    */
   @SuppressWarnings("unchecked")
   public Object convert(Class type, Object value) {
        String s = value.toString();
        return s;
    }

    /**
     * Format the output to something reasonable
    * @param day
    * @return
    */
   public static String toFormattedString(Object day) {
       Day dayObject = (Day) day;
       String s = dayObject.getDayName()+
           ", "+dayObject.getMonthName()+
           " "+dayObject.getDayOfMonth()+
           ", "+dayObject.getYear();
       return s;
   }

   public static String toUnixTime(Object day) {
      // get date in milliseconds and divide by zero to return unixtime
      Day dayObject = (Day) day;
      Date date = dayObject.getDate();
      Long seconds = date.getTime() / 1000;
      //logger.info("unixtime: " + seconds.toString());
      return seconds.toString();
   }


}
