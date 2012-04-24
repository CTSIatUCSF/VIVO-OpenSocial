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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateUtils {
   /** Logger for this class and subclasses */
   protected final Log logger = LogFactory.getLog(getClass());

   public static final Long MILLISECONDSINDAY = 86400L;
   public static final Long MILLISECONDSINYEAR = MILLISECONDSINDAY * 365;

   public static String getToday(String fmt) {
      String today;
      Calendar now = Calendar.getInstance();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM");
      today = formatter.format(now.getTime());
      return today;
   }

   public static String getCurrentYear() {
      String y;
      Calendar now = Calendar.getInstance();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
      y = formatter.format(now.getTime());
      return y;
   }


   /**
    * Get Current Jave Date
    *
    * returns a date in following format: Tue Apr 01 08:21:57 EST 2003
    */
   public static java.util.Date getCurrentJavaDate() {
      java.util.Date currentDate = new java.util.Date();
      return currentDate;
   }

   /**
    * get Current SQL date
    *
    * returns a date in following format: 2003-04-01
    */
   public static java.sql.Date getCurrentSQLDate() {
      java.util.Date currentDate = new java.util.Date();
      java.sql.Date sqlDate = new java.sql.Date(currentDate.getTime());
      return sqlDate;
   }

   /**
    * get current SQL Timestamp
    *
    * returns a date in following format: 2003-04-01 08:21:57.556
    */
   public static java.sql.Timestamp getCurrentSQLTimestamp() {
      java.sql.Timestamp sqlTime = new java.sql.Timestamp(System.currentTimeMillis());
      return sqlTime;
   }

   /**
    * returns a formatted date/time as a string
    *
    * returns a date in the following format 01-04-03 08:21:57
    */
   public static String getFormattedDate() {
      String s = null;
      Calendar today = Calendar.getInstance();
      s = new SimpleDateFormat("dd-MM-yy hh:mm:ss").format(today.getTime());
      return s;
   }

   /**
    * returns a formatted date/time as a string
    * @param a format string
    * @return the current date in the specified format
    */
   public static String getFormattedDate(String f) {
      String s = null;
      Calendar today = Calendar.getInstance();
      s = new SimpleDateFormat(f).format(today.getTime());
      return s;
   }

   /**
    * given a String date in a described format (i.e. YYYY-mm-dd) convert
    * it to a String date in a new format (i.e. MM-dd-YYYY)
    *
    * @param s input date string
    * @param formatin a format string
    * @param formatout a format string
    * @return formated date string
    */
   public static String convertStringDate(String s, String formatIn, String formatOut) {

      try {
        if (s.equals("now")) {
          return (new SimpleDateFormat(formatOut).format(new java.util.Date()));
        } else {
          return (new SimpleDateFormat(formatOut)).format(
                 (new SimpleDateFormat(formatIn)).parse(
                   s, new ParsePosition(0)));
        }
      } catch( Exception e ) {
        return "";
      }

   }

   /**
    * returns a Date given a formatted string
    */
   public static java.util.Date setDate(String s) {
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
      ParsePosition pos = new ParsePosition(0);
      java.util.Date newDate = formatter.parse(s, pos);
      return newDate;
   }

   /**
    * returns a Date given a formatted string
    */
   public static java.util.Date setDate(String s, String fmt) {
      SimpleDateFormat formatter = new SimpleDateFormat(fmt);
      ParsePosition pos = new ParsePosition(0);
      java.util.Date newDate = formatter.parse(s, pos);
      return newDate;
   }

   /**
    * returns a SQL Date given a formatted string
    */
   public static java.sql.Date getSQLDateFromString(String s) {
      return java.sql.Date.valueOf(s);
   }

   /**
    * returns a SQL Time given a formatted string
    */
   public static java.sql.Time getSQLTimeFromString(String s) {
      return java.sql.Time.valueOf(s);
   }

   /*
    * return  a formatted version of a java.sqlDate
    */
   public static String getFormattedDate(java.util.Date date, String fmt) {
      String formattedDate = new String();
      formattedDate = new SimpleDateFormat(fmt).format(new java.util.Date(date.getTime()));
      return formattedDate;
   }

   /*
    * return  a formatted version of a java.sqlDate
    */
   public static String getFormattedSQLDate(java.sql.Date sqldate, String fmt) {
      String formattedDate = new String();
      formattedDate = new SimpleDateFormat(fmt).format(new java.util.Date(sqldate.getTime()));
      return formattedDate;
   }


   public static java.sql.Date getNextSQLDate(java.sql.Date currentDate) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(currentDate);
      cal.add(Calendar.DATE, 1);
      java.util.Date tomorrow = cal.getTime();
      return new java.sql.Date(tomorrow.getTime());
   }

   /**
   * @param start
   * @param end
   * @return elapsed time
   */
   public static String getElapsedTime(String start, String end) {
      long startlong = Long.parseLong(start, 10);
      long endlong = Long.parseLong(end, 10);
      long elapsed = endlong - startlong;
      return Long.toString(elapsed, 10);
   }

   /**
    * returns a SQL Timestamp given a formatted string
    */
   public static java.sql.Timestamp convertSQLTimestamp(String s, String formatIn, String formatOut) {
      String s2 = convertStringDate(s, formatIn, formatOut);
      //System.out.println("s2: "+s2);
      return java.sql.Timestamp.valueOf(s2+".0");
   }

}
