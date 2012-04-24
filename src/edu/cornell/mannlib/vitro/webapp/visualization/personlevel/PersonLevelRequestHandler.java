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

package edu.cornell.mannlib.vitro.webapp.visualization.personlevel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.auth.requestedAction.Actions;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.visualization.VisualizationFrameworkConstants;
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.coauthorship.CoAuthorshipVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.collaborationutils.CollaborationData;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIGrantCountConstructQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIGrantCountQueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.coprincipalinvestigator.CoPIVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.persongrantcount.PersonGrantCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.personpubcount.PersonPublicationCountVisCodeGenerator;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.Activity;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SparklineData;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.SubEntity;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.QueryRunner;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.SelectOnModelUtilities;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.UtilityFunctions;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.VisualizationRequestHandler;

/**
 * This request handler is used to serve content rendered on the person level vis page
 * like,
 * 		1. Front end of the vis including the co-author & publication sparkline.
 * 		2. Downloadable file having the co-author network in graphml format.
 * 		3. Downloadable file having the list of co-authors that the individual has
 * worked with & count of such co-authorships.
 * 
 * @author cdtank
 */
public class PersonLevelRequestHandler implements VisualizationRequestHandler {

    private static final String EGO_PUB_SPARKLINE_VIS_CONTAINER_ID = "ego_pub_sparkline";
    private static final String UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID = 
    									"unique_coauthors_sparkline";
    private static final String EGO_GRANT_SPARKLINE_VIS_CONTAINER_ID = "ego_grant_sparkline";
    private static final String UNIQUE_COPIS_SPARKLINE_VIS_CONTAINER_ID = 
    									"unique_copis_sparkline";

	@Override
	public Object generateAjaxVisualization(VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Person Level does not provide Ajax Response.");
	}

	@Override
	public Map<String, String> generateDataVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		throw new UnsupportedOperationException("Person Level does not provide Data Response.");
	}
    
	
	@Override
	public ResponseValues generateStandardVisualization(
			VitroRequest vitroRequest, Log log, Dataset dataset)
			throws MalformedQueryParametersException {
		
        String egoURI = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY);

        String visMode = vitroRequest.getParameter(
        							VisualizationFrameworkConstants.VIS_MODE_KEY);
        
        return generateStandardVisualizationForPersonLevelVis(vitroRequest,
				log, dataset, egoURI, visMode);
        
	}

	@Override
	public ResponseValues generateVisualizationForShortURLRequests(
			Map<String, String> parameters, VitroRequest vitroRequest, Log log,
			Dataset dataset) throws MalformedQueryParametersException {
        
        return generateStandardVisualizationForPersonLevelVis(
        				vitroRequest,
        				log, 
        				dataset, 
        				parameters.get(VisualizationFrameworkConstants.INDIVIDUAL_URI_KEY), 
        				parameters.get(VisualizationFrameworkConstants.VIS_MODE_KEY));
	}

	private ResponseValues generateStandardVisualizationForPersonLevelVis(
			VitroRequest vitroRequest, Log log, Dataset dataset, String egoURI,
			String visMode) throws MalformedQueryParametersException {
		
		if (VisualizationFrameworkConstants.COPI_VIS_MODE.equalsIgnoreCase(visMode)) { 
        	
			
			
        	ModelConstructor constructQueryRunner = 
        			new CoPIGrantCountConstructQueryRunner(egoURI, dataset, log);
    		Model constructedModel = constructQueryRunner.getConstructedModel();
    		
    		QueryRunner<CollaborationData> coPIQueryManager = 
    				new CoPIGrantCountQueryRunner(egoURI, constructedModel, log);
           
            CollaborationData coPIData = coPIQueryManager.getQueryResult();
            
	    	/*
	    	 * grants over time sparkline
	    	 */
    		SubEntity person = new SubEntity(egoURI,
											 UtilityFunctions
											 	.getIndividualLabelFromDAO(vitroRequest, egoURI));

    		Map<String, Activity> grantsToURI = SelectOnModelUtilities.getGrantsForPerson(dataset, person, false);
    		
        	/*
        	 * Create a map from the year to number of grants. Use the Grant's
        	 * parsedGrantYear to populate the data.
        	 * */
        	Map<String, Integer> yearToGrantCount = 
    			UtilityFunctions.getYearToActivityCount(grantsToURI.values());
        	
	    	
	    	PersonGrantCountVisCodeGenerator personGrantCountVisCodeGenerator = 
	    		new PersonGrantCountVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_GRANT_SPARKLINE_VIS_CONTAINER_ID,
	    			yearToGrantCount,
	    			log);
	    	
	    	SparklineData grantSparklineVO = personGrantCountVisCodeGenerator
			.getValueObjectContainer();
	    	
	    	
	    	/*
	    	 * Co-PI's over time sparkline
	    	 */
	    	CoPIVisCodeGenerator uniqueCopisVisCodeGenerator = 
	    		new CoPIVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COPIS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getActivityYearToCollaborators(coPIData),
	    			log);
	    	
	    	SparklineData uniqueCopisSparklineVO = uniqueCopisVisCodeGenerator
			.getValueObjectContainer();
	    	
	    	
	    	return prepareCoPIStandaloneResponse(
					egoURI, 
					grantSparklineVO,
					uniqueCopisSparklineVO,
					coPIData,
	    			vitroRequest);
	    	
        } else {
        	
        	QueryRunner<CollaborationData> coAuthorshipQueryManager = 
        			new CoAuthorshipQueryRunner(egoURI, dataset, log);
        
        	CollaborationData coAuthorshipData = coAuthorshipQueryManager.getQueryResult();
        	
        	/*
			 * When the front-end for the person level vis has to be displayed we render couple of 
			 * sparklines. This will prepare all the data for the sparklines & other requested 
			 * files.
			 * */
    		SubEntity person = new SubEntity(egoURI,
											 UtilityFunctions
											 	.getIndividualLabelFromDAO(vitroRequest, egoURI));

    		Map<String, Activity> publicationsToURI = SelectOnModelUtilities.getPublicationsForPerson(dataset, person, false);
			
	    	/*
	    	 * Create a map from the year to number of publications. Use the BiboDocument's
	    	 * parsedPublicationYear to populate the data.
	    	 * */
	    	Map<String, Integer> yearToPublicationCount = 
	    			UtilityFunctions.getYearToActivityCount(publicationsToURI.values());
	    														
	    	/*
	    	 * Computations required to generate HTML for the sparklines & related context.
	    	 * */
	    	PersonPublicationCountVisCodeGenerator personPubCountVisCodeGenerator = 
	    		new PersonPublicationCountVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			EGO_PUB_SPARKLINE_VIS_CONTAINER_ID,
	    			yearToPublicationCount,
	    			log);	  
	    	
	    	SparklineData publicationSparklineVO = personPubCountVisCodeGenerator
	    														.getValueObjectContainer();
	    	
            CoAuthorshipVisCodeGenerator uniqueCoauthorsVisCodeGenerator = 
	    		new CoAuthorshipVisCodeGenerator(
	    			egoURI,
	    			VisualizationFrameworkConstants.FULL_SPARKLINE_VIS_MODE,
	    			UNIQUE_COAUTHORS_SPARKLINE_VIS_CONTAINER_ID,
	    			UtilityFunctions.getActivityYearToCollaborators(coAuthorshipData),
	    			log);
	    	
	    	SparklineData uniqueCoauthorsSparklineVO = uniqueCoauthorsVisCodeGenerator
	    															.getValueObjectContainer();
	    	
	    	return prepareCoAuthorStandaloneResponse(
					egoURI, 
	    			publicationSparklineVO,
	    			uniqueCoauthorsSparklineVO,
	    			coAuthorshipData,
	    			vitroRequest);

        }
	}
	
	private TemplateResponseValues prepareCoAuthorStandaloneResponse(
					String egoURI, 
					SparklineData egoPubSparklineVO, 
					SparklineData uniqueCoauthorsSparklineVO, 
					CollaborationData coAuthorshipVO, 
					VitroRequest vitroRequest) {
		
		Map<String, Object> body = new HashMap<String, Object>();
		
        String	standaloneTemplate = "coAuthorPersonLevel.ftl";
        
        body.put("egoURIParam", egoURI);
        
        body.put("egoLocalName", UtilityFunctions.getIndividualLocalName(egoURI, vitroRequest));
        
        String title = "";
        
        if (coAuthorshipVO.getCollaborators() != null 
        			&& coAuthorshipVO.getCollaborators().size() > 0) {
        	body.put("numOfAuthors", coAuthorshipVO.getCollaborators().size());
        	title = coAuthorshipVO.getEgoCollaborator().getCollaboratorName() + " - ";
		}
		
		if (coAuthorshipVO.getCollaborations() != null 
					&& coAuthorshipVO.getCollaborations().size() > 0) {
			body.put("numOfCoAuthorShips", coAuthorshipVO.getCollaborations().size());
		}
		
		body.put("egoPubSparklineVO", egoPubSparklineVO);
		body.put("uniqueCoauthorsSparklineVO", uniqueCoauthorsSparklineVO);

		body.put("title",  title + "Person Level Visualization");

		return new TemplateResponseValues(standaloneTemplate, body);
		
	}
	
	private TemplateResponseValues prepareCoPIStandaloneResponse(
					String egoURI, 
					SparklineData egoGrantSparklineVO, 
					SparklineData uniqueCopisSparklineVO, 
					CollaborationData coPIVO, 
					VitroRequest vitroRequest) {
		
		Map<String, Object> body = new HashMap<String, Object>();
        
        body.put("egoURIParam", egoURI);
        
        body.put("egoLocalName", UtilityFunctions.getIndividualLocalName(egoURI, vitroRequest));
        
        String title = "";
        
        if (coPIVO.getCollaborators() != null && coPIVO.getCollaborators().size() > 0) {
        	body.put("numOfInvestigators", coPIVO.getCollaborators().size());
        	title = coPIVO.getEgoCollaborator().getCollaboratorName() + " - ";
		}
		
		if (coPIVO.getCollaborations() != null && coPIVO.getCollaborations().size() > 0) {
			body.put("numOfCoInvestigations", coPIVO.getCollaborations().size());
		}
		
        String	standaloneTemplate = "coPIPersonLevel.ftl";
		
		body.put("egoGrantSparklineVO", egoGrantSparklineVO);
		body.put("uniqueCoInvestigatorsSparklineVO", uniqueCopisSparklineVO);        	

		body.put("title",  title + "Person Level Visualization");

		return new TemplateResponseValues(standaloneTemplate, body);
		
	}

	@Override
	public Actions getRequiredPrivileges() {
		// TODO Auto-generated method stub
		return null;
	}
}
