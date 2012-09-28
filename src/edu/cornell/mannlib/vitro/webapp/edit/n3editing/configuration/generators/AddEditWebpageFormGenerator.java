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
package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.jena.QueryUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationUtils;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesWithParent;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;
/**

Custom form for adding or editing a webpage associated with an individual. The primary page,
ManageWebpagesForIndividual, should forward to this page if: (a) we are adding a new page, or 
(b) an edit link in the Manage Webpages view has been clicked. But right now (a) is not implemented. 

Object properties: 
core:webpage (range: core:URLLink)
core:webpageOf (domain: core:URLLink) (inverse of core:webpage)

Class: 
core:URLLink - the link to be added to the individual

Data properties of core:URLLink:
core:linkURI
core:linkAnchorText
core:rank

*/
public class AddEditWebpageFormGenerator extends BaseEditConfigurationGenerator implements EditConfigurationGenerator {
    public static Log log = LogFactory.getLog( AddEditWebpageFormGenerator.class );
    
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq, HttpSession session) throws Exception {
        EditConfigurationVTwo config = new EditConfigurationVTwo();
        
        config.setTemplate("addEditWebpageForm.ftl");
        
        initBasics(config, vreq);
        initPropertyParameters(vreq, session, config);
        initObjectPropForm(config, vreq);       
                
        config.setVarNameForSubject("subject");
        config.setVarNameForObject("link");

        config.addNewResource("link", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        config.setN3Required(list( N3_FOR_WEBPAGE, N3_FOR_URLTYPE ));
        config.setN3Optional(list( N3_FOR_ANCHOR, N3_FOR_RANK));
        
        config.addUrisInScope("webpageProperty",     list( core + "webpage"));
        config.addUrisInScope("inverseProperty",     list( core + "webpageOf"));
        config.addUrisInScope("linkClass",           list( core + "URLLink"));
        config.addUrisInScope("linkURI",       list( core + "linkURI" ));
        config.addUrisInScope("linkAnchorPredicate", list( core + "linkAnchorText" ));
        config.addUrisInScope("rankPredicate",       list( core + "rank"));
        
        config.setUrisOnForm("urlType");
        config.setLiteralsOnForm(list("url","anchor","rank"));

        config.addSparqlForExistingLiteral("url",    URL_QUERY);
        config.addSparqlForExistingLiteral("anchor", ANCHOR_QUERY);
        config.addSparqlForExistingLiteral("rank",   MAX_RANK_QUERY);
        config.addSparqlForExistingUris("urlType", URLTYPE_QUERY);
            
        config.addField(new FieldVTwo().
                setName("url").
                setValidators(list("nonempty", "datatype:"+XSD.anyURI.toString(), "httpUrl")).
                setRangeDatatypeUri(XSD.anyURI.toString()));
        
        config.addField( new FieldVTwo().
                setName("urlType").
                setValidators( list("nonempty") ).
                setOptions( 
                    new ChildVClassesWithParent(core + "URLLink")));

        config.addField(new FieldVTwo().
                setName("anchor"));
        
        config.addField(new FieldVTwo().
                setName("rank").
                setRangeDatatypeUri(XSD.integer.toString()));
        
        config.addFormSpecificData("newRank", 
                getMaxRank( EditConfigurationUtils.getObjectUri(vreq), 
                            EditConfigurationUtils.getSubjectUri(vreq), vreq )
                        + 1 );
                
        config.addValidator(new AntiXssValidation());
        
        //might be null
        config.addFormSpecificData("subjectName", getName( config, vreq));
        prepare(vreq, config);
        return config;
    }

    /** may be null */
    private Object getName(EditConfigurationVTwo config, VitroRequest vreq) {
        Individual ind = vreq.getWebappDaoFactory().getIndividualDao().getIndividualByURI(config.getSubjectUri());
        if( ind == null )
            return null;
        else
            return ind.getName();
    }

    /* ********* N3 Assertions *********** */
    static String N3_FOR_WEBPAGE = 
        "?subject ?webpageProperty ?link . \n"+
        "?link    ?inverseProperty ?subject . \n"+
        "?link    a                ?linkClass  . \n" +      
        "?link    ?linkURI         ?url .";    
    
    static String N3_FOR_URLTYPE =
        "?link a ?urlType .";

    static String N3_FOR_ANCHOR =
        "?link ?linkAnchorPredicate ?anchor .";
    
    static String N3_FOR_RANK = 
        "?link ?rankPredicate ?rank .";

    /* *********** SPARQL queries for existing values ************** */
    
    static String URL_QUERY = 
        "SELECT ?urlExisting WHERE { ?link ?linkURI ?urlExisting }";
    
    static String URLTYPE_QUERY = 
        "PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?linkClassExisting WHERE { ?link vitro:mostSpecificType ?linkClassExisting }";
    
    static String ANCHOR_QUERY = 
        "SELECT ?anchorExisting WHERE { ?link ?linkAnchorPredicate ?anchorExisting }";

    static String RANK_QUERY =
        "SELECT ?rankExisting WHERE { ?link ?rankPredicate ?rankExisting }";
    
    static String core = "http://vivoweb.org/ontology/core#";
    
    /* Note on ordering by rank in sparql: if there is a non-integer value on a link, that will be returned,
     * since it's ranked highest. Preventing that would require getting all the ranks and sorting in Java,
     * throwing out non-int values. 
     */
    private static String MAX_RANK_QUERY = ""
        + "PREFIX core: <http://vivoweb.org/ontology/core#> \n"
        + "SELECT DISTINCT ?rank WHERE { \n"
        + "    ?subject core:webpage ?link . \n"
        + "    ?link core:rank ?rank .\n"
        + "} ORDER BY DESC(?rank) LIMIT 1";
        
    private int getMaxRank(String objectUri, String subjectUri, VitroRequest vreq) {

        int maxRank = 0; // default value 
        if (objectUri == null) { // adding new webpage   
            String queryStr = QueryUtils.subUriForQueryVar(MAX_RANK_QUERY, "subject", subjectUri);
            log.debug("Query string is: " + queryStr);
            try {
                ResultSet results = QueryUtils.getQueryResults(queryStr, vreq);
                if (results != null && results.hasNext()) { // there is at most one result
                    QuerySolution soln = results.next(); 
                    RDFNode node = soln.get("rank");
                    if (node != null && node.isLiteral()) {
                        // node.asLiteral().getInt() won't return an xsd:string that 
                        // can be parsed as an int.
                        int rank = Integer.parseInt(node.asLiteral().getLexicalForm());
                        if (rank > maxRank) {  
                            log.debug("setting maxRank to " + rank);
                            maxRank = rank;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                log.error("Invalid rank returned from query: not an integer value.");
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        return maxRank;
    }

}
