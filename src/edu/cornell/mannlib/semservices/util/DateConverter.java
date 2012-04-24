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

 * $Id: DateConverter.java 50408 2007-03-28 19:14:46Z jdamick $
 *
 * Copyright 2006- Revolution Health Group.  All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Revolution Health Group.  (Confidential Information).
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license
 * agreement you entered into with Revolution Health Group.
 *
 */

package edu.cornell.mannlib.semservices.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.log4j.Logger;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public final class DateConverter implements Converter {
    private static final Logger LOG = Logger.getLogger(DateConverter.class);

    //  Constructors

    /**
     * Create a {@link Converter} that will throw a {@link ConversionException}
     * if a conversion error occurs.
     */
    public DateConverter() {
        this.defaultValue = null;
        converter = new XMLGregorianCalendarConverter(defaultValue);
    }

    /**
     * Create a {@link Converter} that will return the specified default value
     * if a conversion error occurs.
     *
     * @param defaultValue
     *            The default value to be returned
     */
    public DateConverter(Object defaultValue) {
        this.defaultValue = defaultValue;
        converter = new XMLGregorianCalendarConverter(defaultValue);
    }

    //  Instance Variables

    /**
     * The default value specified to our Constructor, if any.
     */
    private Object defaultValue = null;
    private XMLGregorianCalendarConverter converter = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Convert the specified input object into an output object of the specified
     * type.
     *
     * @param type
     *   XMLGregorianCalendar type to which this value should be
     *   converted
     * @param value
     *   The input value to be converted
     *
     * @exception ConversionException
     *    if conversion cannot be performed successfully
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
   public Object convert(Class type, Object value) {

        String dateValue = value.toString();

        if (value instanceof Date) {
            return (value);
        } else {
            try {
                JSONObject jsonObj = JSONObject.fromObject(value.toString());
                dateValue = jsonObj
                        .optString("Date" /* Date.class.toString() */);
            } catch (JSONException e) { /* empty, could fail.. */
                LOG.debug("no date object found in the json");
            }
        }
        XMLGregorianCalendar calendar = (XMLGregorianCalendar) converter
                .convert(type, dateValue);

        Object result = null;
        try {
            result = calendar.toGregorianCalendar().getTime();
        } catch (Exception exception) { /*
                                         * empty, had some error parsing the
                                         * time
                                         */
            LOG.debug("Error converting the time");
            if (result == null) {
                try {
                    result = new Date(Date.parse(dateValue));
                } catch (IllegalArgumentException argException) {
                    // last chance
                    result = java.sql.Date.valueOf(dateValue);
                }
            }
        }

        if (result != null && (result instanceof Date)
                && type.equals(java.sql.Date.class)) {
            result = new java.sql.Date(((Date) result).getTime());
        }

        return result;
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    calendar);
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }

    public static String toFormattedString(Date date) {
        String s = new String();
        s = new SimpleDateFormat("MMM d, h:mm a").format(date.getTime());
        return s;
    }
}
