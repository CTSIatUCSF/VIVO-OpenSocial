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

package edu.cornell.mannlib.vitro.webapp.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The basic implementation of ConfigurationProperties. It loads the
 * configuration properties from a properties file and stores them in a map.
 * 
 * Leading and trailing white space are trimmed from the property values.
 * 
 * Once the properties have been parsed and stored, they are immutable.
 */
public class ConfigurationPropertiesImpl extends ConfigurationProperties {
	private static final Log log = LogFactory
			.getLog(ConfigurationPropertiesImpl.class);

	private final Map<String, String> propertyMap;

	public ConfigurationPropertiesImpl(InputStream stream) {
		Properties props = loadFromPropertiesFile(stream);
		Map<String, String> map = copyPropertiesToMap(props);
		trimWhiteSpaceFromValues(map);
		this.propertyMap = Collections.unmodifiableMap(map);

		log.debug("Configuration properties are: " + map);
	}

	private Properties loadFromPropertiesFile(InputStream stream) {
		Properties props = new Properties();
		try {
			props.load(stream);
		} catch (IOException e) {
			throw new IllegalStateException(
					"Failed to parse the configuration properties file.", e);
		}
		return props;
	}

	private Map<String, String> copyPropertiesToMap(Properties props) {
		Map<String, String> map = new HashMap<String, String>();
		for (Enumeration<?> keys = props.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			String value = props.getProperty(key);
			map.put(key, value);
		}
		return map;
	}

	private void trimWhiteSpaceFromValues(Map<String, String> map) {
		for (String key : map.keySet()) {
			map.put(key, map.get(key).trim());
		}
	}

	@Override
	public String getProperty(String key) {
		return propertyMap.get(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		if (propertyMap.containsKey(key)) {
			return propertyMap.get(key);
		} else {
			return defaultValue;
		}
	}

	@Override
	public Map<String, String> getPropertyMap() {
		return new HashMap<String, String>(propertyMap);
	}

	@Override
	public String toString() {
		return "ConfigurationPropertiesImpl[propertyMap=" + propertyMap + "]";
	}

}
