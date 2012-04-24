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

package edu.cornell.mannlib.vitro.webapp.search;

public class VitroSearchTermNames {
    /** Id of entity, vclass or tab */
    public static String URI         = "URI";
    /** search document id */
    public static String DOCID      = "DocId";
    /** java class of the object that the Doc represents. */
    public static String JCLASS     = "JCLASS";
    /** rdf:type */
    public static String RDFTYPE    = "type";
    /** rdf:type */
    public static String CLASSGROUP_URI    = "classgroup";
    /** Modtime from db */
    public static String MODTIME    = "modTime";

    /** time of index in msec since epoc */
    public static String INDEXEDTIME= "indexedTime";
    /** text for 'full text' search, this is stemmed */
    public static String ALLTEXT    = "ALLTEXT";
    /** text for 'full text' search, this is unstemmed for
     * use with wildcards and prefix queries */
    public static String ALLTEXTUNSTEMMED = "ALLTEXTUNSTEMMED";
    /** Does the individual have a thumbnail image? 1=yes 0=no */
    public static final String THUMBNAIL = "THUMBNAIL";        
    /** Should individual be included in full text search results? 1=yes 0=no */
    public static final String PROHIBITED_FROM_TEXT_RESULTS = "PROHIBITED_FROM_TEXT_RESULTS";
    /** class names in human readable form of an individual*/
    public static final String CLASSLOCALNAMELOWERCASE = "classLocalNameLowerCase";
    /** class names in human readable form of an individual*/
    public static final String CLASSLOCALNAME = "classLocalName";      

    // Fields derived from rdfs:label
    /** Raw rdfs:label: no lowercasing, no tokenizing, no stop words, no stemming **/
    public static String NAME_RAW = "nameRaw"; // 
    
    /** rdfs:label lowercased, no tokenizing, no stop words, no stemming **/
    public static String NAME_LOWERCASE = "nameLowercase"; // 

    /** Same as NAME_LOWERCASE, but single-valued so it's sortable. **/
    // RY Need to control how indexing selects which of multiple values to copy. 
    public static String NAME_LOWERCASE_SINGLE_VALUED = "nameLowercaseSingleValued";
    
    /** rdfs:label lowercased, tokenized, stop words, no stemming **/
    public static String NAME_UNSTEMMED = "nameUnstemmed"; 
    
    /** rdfs:label lowercased, tokenized, stop words, stemmed **/
    public static String NAME_STEMMED = "nameStemmed"; 
    
    /** rdfs:label lowercased, untokenized, edge-n-gram-filtered for autocomplete on people names **/
    public static String AC_NAME_UNTOKENIZED = "acNameUntokenized";

    /** rdfs:label lowercased, tokenized, stop words, stemmed, edge-n-gram-filtered for autocomplete 
     * on non-person labels such as book titles and grant names **/
    public static String AC_NAME_STEMMED = "acNameStemmed";
    
    /* There is currently no use case for an autocomplete search field that is tokenized but not stemmed. 
    public static String AC_NAME_UNSTEMMED = "acNameUnstemmed";  */
    
    /** field for beta values of all documents **/
    public static final String BETA = "BETA";
    public static final String PHI = "PHI";
    public static final String ADJACENT_NODES = "ADJACENT_NODES";
    
    /** adding phonetic field **/
    public static final String ALLTEXT_PHONETIC = "ALLTEXT_PHONETIC";
    public static final String NAME_PHONETIC = "NAME_PHONETIC";
    
    /** download url location for thumbnail */
	public static final String THUMBNAIL_URL = "THUMBNAIL_URL";
	
	/** source institution url */
	public static final String SITE_URL = "siteURL";
	
	/** source institution name */
	public static final String SITE_NAME = "siteName";
	
	/** preferred title */
	public static final String PREFERRED_TITLE = "PREFERRED_TITLE";
}
