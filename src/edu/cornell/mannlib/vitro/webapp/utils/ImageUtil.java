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

package edu.cornell.mannlib.vitro.webapp.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;

/**
 * Some static methods that help in dealing with image files.
 * 
 * So far, we only have methods that obtain the placeholder image for an
 * Individual that has no image of its own.
 * 
 */
public class ImageUtil {
	private static final String DEFAULT_IMAGE_PATH = "/images/placeholders/thumbnail.jpg";

	private static final Map<String, String> DEFAULT_IMAGE_PATHS_BY_TYPE = initImagePaths();

	private static Map<String, String> initImagePaths() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("http://xmlns.com/foaf/0.1/Person",
				"/images/placeholders/person.thumbnail.jpg");
		map.put(VitroVocabulary.USERACCOUNT,
				"/images/placeholders/person.thumbnail.jpg");
		return Collections.unmodifiableMap(map);
	}

	/**
	 * If we have a placeholder image for this exact type, return it. Otherwise,
	 * return the default.
	 */
	public static String getPlaceholderImagePathForType(String typeUri) {
		for (Entry<String, String> entry : DEFAULT_IMAGE_PATHS_BY_TYPE
				.entrySet()) {
			if (typeUri.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return DEFAULT_IMAGE_PATH;
	}

	/**
	 * If there is a placeholder image for any type that this Individual
	 * instantiates, return that image. Otherwise, return the default.
	 */
	public static String getPlaceholderImagePathForIndividual(
			VitroRequest vreq, String individualUri) {
		IndividualDao indDao = vreq.getWebappDaoFactory().getIndividualDao();
		for (Entry<String, String> entry : DEFAULT_IMAGE_PATHS_BY_TYPE
				.entrySet()) {
			if (indDao.isIndividualOfClass(entry.getKey(), individualUri)) {
				return entry.getValue();
			}
		}
		return DEFAULT_IMAGE_PATH;
	}

	/** Never need to instantiate this -- all methods are static. */
	private ImageUtil() {
		// Nothing to do
	}
}
