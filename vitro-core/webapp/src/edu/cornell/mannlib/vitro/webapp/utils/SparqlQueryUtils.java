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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.Syntax;

/**
 * Some utility methods that help when dealing with SPARQL queries.
 */
public class SparqlQueryUtils {
	/**
	 * If the user enters any of these characters in a search term, escape it
	 * with a backslash.
	 */
	private static final char[] REGEX_SPECIAL_CHARACTERS = "[\\^$.|?*+()]"
			.toCharArray();
	
	/**
	 * A list of SPARQL syntaxes to try when parsing queries
	 */
    public static final List<Syntax> SUPPORTED_SYNTAXES = Arrays.asList( 
            Syntax.syntaxARQ , Syntax.syntaxSPARQL_11);

	/**
	 * Escape any regex special characters in the string.
	 * 
	 * Note that the SPARQL parser requires two backslashes, in order to pass a
	 * single backslash to the REGEX function.
	 * 
	 * Also escape a single quote ('), but only with a single backslash, since 
	 * this one is for the SPARQL parser itself (single quote is not a special
	 * character to REGEX).
	 */
	public static String escapeForRegex(String raw) {
		StringBuilder clean = new StringBuilder();
		outer: for (char c : raw.toCharArray()) {
			for (char special : REGEX_SPECIAL_CHARACTERS) {
				if (c == special) {
					clean.append('\\').append('\\').append(c);
					continue outer;
				} 
			}
			if (c == '\'') {
				clean.append('\\').append(c);
				continue outer;
			}
			clean.append(c);
		}
		return clean.toString();
	}
	
	/**
	 * A convenience method to attempt parsing a query string with various syntaxes
	 * @param queryString
	 * @return Query
	 */
	public static Query create(String queryString) {
	    boolean parseSuccess = false;
        Iterator<Syntax> syntaxIt = SUPPORTED_SYNTAXES.iterator();
        Query query = null;
        while (!parseSuccess && syntaxIt.hasNext()) {
            Syntax syntax = syntaxIt.next();
            try {
                query = QueryFactory.create(queryString, syntax);
                parseSuccess = true;
            } catch (QueryParseException e) {
                if (!syntaxIt.hasNext()) {
                    throw e;
                }
            }
        }
        return query;
	}

}
