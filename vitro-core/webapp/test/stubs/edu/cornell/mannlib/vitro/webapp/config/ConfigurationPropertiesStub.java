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

package stubs.edu.cornell.mannlib.vitro.webapp.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import edu.cornell.mannlib.vitro.webapp.config.ConfigurationProperties;

/**
 * A version of ConfigurationProperties that we can use for unit tests. Unlike
 * the basic implementation, this starts as an empty map, and allows the user to
 * add properties as desired.
 * 
 * Call setBean() to store these properties in the ServletContext.
 */
public class ConfigurationPropertiesStub extends ConfigurationProperties {
	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------

	private final Map<String, String> propertyMap = new HashMap<String, String>();

	public void setProperty(String key, String value) {
		propertyMap.put(key, value);
	}

	public void setBean(ServletContext ctx) {
		setBean(ctx, this);
	}

	@Override
	public String toString() {
		return "ConfigurationPropertiesStub[map=" + propertyMap + "]";
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

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

	// ----------------------------------------------------------------------
	// Un-implemented methods
	// ----------------------------------------------------------------------

}
