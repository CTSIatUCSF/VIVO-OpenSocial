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

package edu.cornell.mannlib.vitro.webapp.controller.individual;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.web.ContentType;

/**
 * All sorts of requests are fielded by the IndividualController. This is the
 * essence of such a the request.
 */
public class IndividualRequestInfo {
	public enum Type {
		RDF_REDIRECT, // Redirect a requst for RDF to the preferred URL.
		BYTESTREAM_REDIRECT, // Redirect a request for file contents.
		NO_INDIVIDUAL, // The requested individual doesn't exist.
		LINKED_DATA, // Requesting RDF for this individual.
		DEFAULT // Requesting HTML response for this individual.
	}

	public static IndividualRequestInfo buildRdfRedirectInfo(String redirectUrl) {
		return new IndividualRequestInfo(Type.RDF_REDIRECT, redirectUrl, null,
				null);
	}

	public static IndividualRequestInfo buildBytestreamRedirectInfo(
			String redirectUrl) {
		return new IndividualRequestInfo(Type.BYTESTREAM_REDIRECT, redirectUrl,
				null, null);
	}

	public static IndividualRequestInfo buildNoIndividualInfo() {
		return new IndividualRequestInfo(Type.NO_INDIVIDUAL, null, null, null);
	}

	public static IndividualRequestInfo buildLinkedDataInfo(
			Individual individual, ContentType rdfFormat) {
		return new IndividualRequestInfo(Type.LINKED_DATA, null, individual,
				rdfFormat);
	}

	public static IndividualRequestInfo buildDefaultInfo(Individual individual) {
		return new IndividualRequestInfo(Type.DEFAULT, null, individual, null);
	}

	private final Type type;
	private final String redirectUrl;
	private final Individual individual;
	private final ContentType rdfFormat;

	private IndividualRequestInfo(Type type, String redirectUrl,
			Individual individual, ContentType rdfFormat) {
		if (type == null) {
			throw new NullPointerException("type may not be null.");
		}

		if (((type == Type.RDF_REDIRECT) || (type == Type.BYTESTREAM_REDIRECT))
				&& (redirectUrl == null)) {
			throw new NullPointerException(
					"redirectUrl may not be null if type is " + type + ".");
		}

		if (((type == Type.LINKED_DATA) || (type == Type.DEFAULT))
				&& (individual == null)) {
			throw new NullPointerException(
					"individual may not be null if type is " + type + ".");
		}

		if ((type == Type.LINKED_DATA) && (rdfFormat == null)) {
			throw new NullPointerException(
					"rdfFormat may not be null if type is " + type + ".");
		}

		this.type = type;
		this.redirectUrl = redirectUrl;
		this.individual = individual;
		this.rdfFormat = rdfFormat;
	}

	public Type getType() {
		return this.type;
	}

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

	public Individual getIndividual() {
		return this.individual;
	}

	public ContentType getRdfFormat() {
		return this.rdfFormat;
	}
}
