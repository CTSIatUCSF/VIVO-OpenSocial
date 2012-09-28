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

package edu.cornell.mannlib.vitro.webapp.servlet.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.cornell.mannlib.vitro.webapp.dao.DisplayVocabulary;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.ontology.update.KnowledgeBaseUpdater;
import edu.cornell.mannlib.vitro.webapp.ontology.update.UpdateSettings;

/**
 * Invokes process to test whether the knowledge base needs any updating
 * to align with ontology changes.
 * @author bjl23
 *
 */
public class UpdateKnowledgeBase implements ServletContextListener {
	
    public static final String KBM_REQURIED_AT_STARTUP = "KNOWLEDGE_BASE_MIGRATION_REQUIRED_AT_STARTUP";
	private final static Log log = LogFactory.getLog(UpdateKnowledgeBase.class);
	
	private static final String DATA_DIR = "/WEB-INF/ontologies/update/";
	private static final String LOG_DIR = "logs/";
	private static final String CHANGED_DATA_DIR = "changedData/";
	private static final String ASK_QUERY_FILE = DATA_DIR + "askUpdated.sparql";
	private static final String SUCCESS_ASSERTIONS_FILE = DATA_DIR + "success.n3";
	private static final String SUCCESS_RDF_FORMAT = "N3";
	private static final String DIFF_FILE = DATA_DIR + "diff.tab.txt";
	private static final String REMOVED_DATA_FILE = DATA_DIR + CHANGED_DATA_DIR + 	"removedData.n3";
	private static final String ADDED_DATA_FILE = DATA_DIR + CHANGED_DATA_DIR + "addedData.n3";
	private static final String SPARQL_CONSTRUCT_ADDITIONS_DIR = DATA_DIR + "sparqlConstructs/additions/";
	private static final String SPARQL_CONSTRUCT_DELETIONS_DIR = DATA_DIR + "sparqlConstructs/deletions/";
	private static final String OLD_TBOX_MODEL_DIR = DATA_DIR + "oldVersion/";
	private static final String NEW_TBOX_MODEL_DIR = "/WEB-INF/filegraph/tbox/";
	private static final String OLD_TBOX_ANNOTATIONS_DIR = DATA_DIR + "oldAnnotations/";
	private static final String NEW_TBOX_ANNOTATIONS_DIR = "/WEB-INF/ontologies/user/tbox/";
	//For display model migration
	private static final String OLD_DISPLAYMODEL_TBOX_PATH = DATA_DIR + "oldDisplayModel/displayTBOX.n3";
	private static final String NEW_DISPLAYMODEL_TBOX_PATH = "/WEB-INF/ontologies/app/menuload/displayTBOX.n3";
	private static final String OLD_DISPLAYMODEL_DISPLAYMETADATA_PATH = DATA_DIR + "oldDisplayModel/displayDisplay.n3";
	private static final String NEW_DISPLAYMODEL_DISPLAYMETADATA_PATH = "/WEB-INF/ontologies/app/menuload/displayDisplay.n3";
	private static final String NEW_DISPLAYMODEL_PATH = "/WEB-INF/ontologies/app/menu.n3";
	private static final String LOADED_STARTUPT_DISPLAYMODEL_DIR = "/WEB-INF/ontologies/app/loadedAtStartup/";
	private static final String OLD_DISPLAYMODEL_VIVOLISTVIEW_PATH = DATA_DIR + "oldDisplayModel/vivoListViewConfig.rdf";

	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext ctx = sce.getServletContext();
			
			// If the DATA_DIR directory doesn't exist no migration check will be done.
			// This is a normal situation for Vitro.
			File updateDirectory = new File(ctx.getRealPath(DATA_DIR));
			if (!updateDirectory.exists()) {
				log.debug("Directory " + ctx.getRealPath(DATA_DIR) + " does not exist, no migration check will be attempted.");
				return;
			}

			String logFileName =  DATA_DIR + LOG_DIR + timestampedFileName("knowledgeBaseUpdate", "log");
			String errorLogFileName = DATA_DIR + LOG_DIR + 	timestampedFileName("knowledgeBaseUpdate.error", "log");
						
			UpdateSettings settings = new UpdateSettings();
			settings.setAskUpdatedQueryFile(getAskUpdatedQueryPath(ctx));
			settings.setDataDir(ctx.getRealPath(DATA_DIR));
			settings.setSparqlConstructAdditionsDir(ctx.getRealPath(SPARQL_CONSTRUCT_ADDITIONS_DIR));
			settings.setSparqlConstructDeletionsDir(ctx.getRealPath(SPARQL_CONSTRUCT_DELETIONS_DIR));
			settings.setDiffFile(ctx.getRealPath(DIFF_FILE));
			settings.setSuccessAssertionsFile(ctx.getRealPath(SUCCESS_ASSERTIONS_FILE));
			settings.setSuccessRDFFormat(SUCCESS_RDF_FORMAT);
			settings.setLogFile(ctx.getRealPath(logFileName));
			settings.setErrorLogFile(ctx.getRealPath(errorLogFileName));
			settings.setAddedDataFile(ctx.getRealPath(ADDED_DATA_FILE));
			settings.setRemovedDataFile(ctx.getRealPath(REMOVED_DATA_FILE));
			WebappDaoFactory wadf = (WebappDaoFactory) ctx.getAttribute("webappDaoFactory");
			settings.setDefaultNamespace(wadf.getDefaultNamespace());
			settings.setAssertionOntModelSelector(ModelContext.getBaseOntModelSelector(ctx));
			settings.setInferenceOntModelSelector(ModelContext.getInferenceOntModelSelector(ctx));
			settings.setUnionOntModelSelector(ModelContext.getUnionOntModelSelector(ctx));
			boolean tryMigrateDisplay = true;
			try {
				settings.setDisplayModel(ModelContext.getDisplayModel(ctx));
				OntModel oldTBoxModel = loadModelFromDirectory(ctx.getRealPath(OLD_TBOX_MODEL_DIR));
				settings.setOldTBoxModel(oldTBoxModel);
				OntModel newTBoxModel = loadModelFromDirectory(ctx.getRealPath(NEW_TBOX_MODEL_DIR));
				settings.setNewTBoxModel(newTBoxModel);
				OntModel oldTBoxAnnotationsModel = loadModelFromDirectory(ctx.getRealPath(OLD_TBOX_ANNOTATIONS_DIR));
				settings.setOldTBoxAnnotationsModel(oldTBoxAnnotationsModel);
				OntModel newTBoxAnnotationsModel = loadModelFromDirectory(ctx.getRealPath(NEW_TBOX_ANNOTATIONS_DIR));
				settings.setNewTBoxAnnotationsModel(newTBoxAnnotationsModel);
				//Display model tbox and display metadata 
				//old display model tbox model
				OntModel oldDisplayModelTboxModel = loadModelFromFile(ctx.getRealPath(OLD_DISPLAYMODEL_TBOX_PATH));
				settings.setOldDisplayModelTboxModel(oldDisplayModelTboxModel);
				//new display model tbox model
				OntModel newDisplayModelTboxModel = loadModelFromFile(ctx.getRealPath(NEW_DISPLAYMODEL_TBOX_PATH));
				settings.setNewDisplayModelTboxModel(newDisplayModelTboxModel);
				//old display model display model metadata
				OntModel oldDisplayModelDisplayMetadataModel = loadModelFromFile(ctx.getRealPath(OLD_DISPLAYMODEL_DISPLAYMETADATA_PATH));
				settings.setOldDisplayModelDisplayMetadataModel(oldDisplayModelDisplayMetadataModel);
				//new display model display model metadata
				OntModel newDisplayModelDisplayMetadataModel = loadModelFromFile(ctx.getRealPath(NEW_DISPLAYMODEL_DISPLAYMETADATA_PATH));
				settings.setNewDisplayModelDisplayMetadataModel(newDisplayModelDisplayMetadataModel);
				//Get new display model
				OntModel newDisplayModelFromFile = loadModelFromFile(ctx.getRealPath(NEW_DISPLAYMODEL_PATH));
				settings.setNewDisplayModelFromFile(newDisplayModelFromFile);
				OntModel loadedAtStartupFiles = loadModelFromDirectory(ctx.getRealPath(LOADED_STARTUPT_DISPLAYMODEL_DIR));
				settings.setLoadedAtStartupDisplayModel(loadedAtStartupFiles);
				OntModel oldDisplayModelVivoListView = loadModelFromFile(ctx.getRealPath(OLD_DISPLAYMODEL_VIVOLISTVIEW_PATH));
				settings.setVivoListViewConfigDisplayModel(oldDisplayModelVivoListView);
			} catch (Exception e) {
				log.info("unable to read display model migration files, display model not migrated. " + e.getMessage());
				tryMigrateDisplay = false;
			}
				
			try {		
			   KnowledgeBaseUpdater ontologyUpdater = new KnowledgeBaseUpdater(settings);
			  
			   try {
				  if (ontologyUpdater.updateRequired(ctx)) {
					  ctx.setAttribute(KBM_REQURIED_AT_STARTUP, Boolean.TRUE);
					  ontologyUpdater.update(ctx);
					  if (tryMigrateDisplay) {
						  try {
						       migrateDisplayModel(settings);
						       log.info("Migrated display model");
						  } catch (Exception e) {
							   log.warn("unable to successfully update display model: " + e.getMessage());
						  }
					  }
				  }
			   } catch (Exception ioe) {
					String errMsg = "Exception updating knowledge base " +
						"for ontology changes: ";
					// Tomcat doesn't always seem to print exceptions thrown from
					// context listeners
					System.out.println(errMsg);
					ioe.printStackTrace();
					throw new RuntimeException(errMsg, ioe);
			   }	
			} catch (Throwable t){
				  log.warn("warning", t);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}	
	
	//Multiple changes from 1.4 to 1.5 will occur
	//update migration model
	public void migrateDisplayModel(UpdateSettings settings) throws Exception {
		log.debug("Beginning migration of display model");
		OntModel displayModel = settings.getDisplayModel();
		Model addStatements = ModelFactory.createDefaultModel();
		Model removeStatements = ModelFactory.createDefaultModel();
		//remove old tbox and display metadata statements and add statements from new versions
		replaceTboxAndDisplayMetadata(displayModel, addStatements, removeStatements,settings);
		//Update statements for data getter class types that have changed in 1.5 
		updateDataGetterClassNames(displayModel, addStatements, removeStatements);
		//add cannot delete flags to pages that shouldn't allow deletion on page list
		addCannotDeleteFlagDisplayModel(displayModel, addStatements, removeStatements);
		//removes requiresTemplate statement for people page
		updatePeoplePageDisplayModel(displayModel, addStatements, removeStatements);
		//add page list
		addPageListDisplayModel(displayModel, addStatements, removeStatements,settings);
		//update data getter labels
		updateDataGetterLabels(displayModel, addStatements, removeStatements,settings);
		
		displayModel.enterCriticalSection(Lock.WRITE);
		try {
			if(log.isDebugEnabled()) {
				StringWriter sw = new StringWriter();
				addStatements.write(sw,"N3");
				log.debug("Statements to be added are: ");
				log.debug(sw.toString());
				sw.close();
				sw = new StringWriter();
				removeStatements.write(sw, "N3");
				log.debug("Statements to be removed are: ");
				log.debug(sw.toString());
				sw.close();
			}
			displayModel.remove(removeStatements);
			displayModel.add(addStatements);
		} finally {
			displayModel.leaveCriticalSection();
		}
	}
	
	//replace 
	private void replaceTboxAndDisplayMetadata(OntModel displayModel, Model addStatements, Model removeStatements, UpdateSettings settings) {
		
		OntModel oldDisplayModelTboxModel = settings.getOldDisplayModelTboxModel();
		OntModel oldDisplayModelDisplayMetadataModel = settings.getOldDisplayModelDisplayMetadataModel();
		OntModel newDisplayModelTboxModel = settings.getNewDisplayModelTboxModel();
		OntModel newDisplayModelDisplayMetadataModel = settings.getNewDisplayModelDisplayMetadataModel();	
		OntModel loadedAtStartup = settings.getLoadedAtStartupDisplayModel();
		OntModel oldVivoListView = settings.getVivoListViewConfigDisplayModel();
		//Remove old display model tbox and display metadata statements from display model
		removeStatements.add(oldDisplayModelTboxModel);
		removeStatements.add(oldDisplayModelDisplayMetadataModel);
		//the old startup folder only contained by oldVivoListView
		removeStatements.add(oldVivoListView);
		StringWriter sw = new StringWriter();

		try {
			log.debug("Adding old display tbox model, display metadata model, and oldVivoListView to remove statements.  Remove statements now include:");
			removeStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close(); 
		}
		catch(Exception ex) {
			log.error("Exception occurred", ex);
		}
		//Add statements from new tbox and display metadata 
		addStatements.add(newDisplayModelTboxModel);
		addStatements.add(newDisplayModelDisplayMetadataModel);
		//this should include the list view in addition to other files
		addStatements.add(loadedAtStartup);
		try {
			sw = new StringWriter();
			log.debug("Adding new display tbox model, display metadata model, and loaded at startup to add statements.  Add statements now include:");
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close(); 	
		} catch(Exception ex) {
			log.error("Exception occurred in adding new display model tbox/metadata info to add statements ", ex);
		}
		log.debug("Adding new display tbox model, display metadata model, and all models loaded at startup");
	}
	
	//update statements for data getter classes
	private void updateDataGetterClassNames(OntModel displayModel, Model addStatements, Model removeStatements) {
		Resource classGroupOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.ClassGroupPageData");
		Resource browseOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.BrowseDataGetter");
		Resource individualsForClassesOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.IndividualsForClassesDataGetter");
		Resource internalClassesOldType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.pageDataGetter.InternalClassesDataGetter");
		Resource classGroupNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData");
		Resource browseNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.BrowseDataGetter");
		Resource individualsForClassesNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.IndividualsForClassesDataGetter");
		Resource internalClassesNewType = ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter");
		
		//Find statements where type is ClassGroupData
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, classGroupOldType, classGroupNewType);
		//Find statements where type is BrowseDataGetter
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, browseOldType, browseNewType);
		//Find statements where type is individuals for classes
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, individualsForClassesOldType, individualsForClassesNewType);
		//Find statements where type is internal class
		updateAddRemoveDataGetterStatements(displayModel, removeStatements, addStatements, internalClassesOldType, internalClassesNewType);
	}
	
	private void updateAddRemoveDataGetterStatements(OntModel displayModel, 
			Model removeStatements, Model addStatements,
			Resource oldType, Resource newType) {
		log.debug("Old type: " + oldType.getURI() + " - newType: " + newType.getURI());
		removeStatements.add(displayModel.listStatements(null, RDF.type, oldType));
		StmtIterator oldStatements = displayModel.listStatements(null, RDF.type, oldType);
		while(oldStatements.hasNext()) {
			Statement stmt = oldStatements.nextStatement();
			addStatements.add(stmt.getSubject(), RDF.type, newType);
		}
		//Print out remove and add statements here
		StringWriter sw = new StringWriter();
		try {
			log.debug("Adding statements with old data getter types to remove statements, remove statements is now");
			removeStatements.write(sw,"N3");
			log.debug(sw.toString());
			sw.close();
			sw = new StringWriter();
			log.debug("Adding statements with new data getter types to add statements, add statements is now");
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		} catch(Exception ex) {
			log.error("Error occurred in writing out remove and statements for data getter types", ex);
		}
	}
	
	//add cannotDeleteFlag to display model
	private void addCannotDeleteFlagDisplayModel(OntModel displayModel, Model addStatements, Model removeStatements) {
		Resource homePage = displayModel.getResource(DisplayVocabulary.HOME_PAGE_URI);
		addStatements.add(homePage, 
				ResourceFactory.createProperty(DisplayVocabulary.DISPLAY_NS + "cannotDeletePage"),
				ResourceFactory.createPlainLiteral("true"));
	}
		
	//remove requires template
	private void updatePeoplePageDisplayModel(OntModel displayModel, Model addStatements, Model removeStatements) {
		Resource peoplePage = displayModel.getResource(DisplayVocabulary.DISPLAY_NS + "People");
		if(peoplePage != null) {
			removeStatements.add(peoplePage, DisplayVocabulary.REQUIRES_BODY_TEMPLATE,
					ResourceFactory.createPlainLiteral("menupage--classgroup-people.ftl"));
		}
		log.debug("Will remove body template from people page so added that to remove statements ");
	}
	
	//add page list sparql query
	private void addPageListDisplayModel(OntModel displayModel, Model addStatements, Model removeStatements, UpdateSettings settings) {
		OntModel newDisplayModel = settings.getNewDisplayModelFromFile();
		//Get all statements about pageListPage and pageListData
		Resource pageList = newDisplayModel.getResource(DisplayVocabulary.DISPLAY_NS + "pageListPage");
		Resource pageListData = newDisplayModel.getResource(DisplayVocabulary.DISPLAY_NS + "pageListData");

		addStatements.add(newDisplayModel.listStatements(pageList, null, (RDFNode) null));
		addStatements.add(newDisplayModel.listStatements(pageListData, null, (RDFNode) null));
		StringWriter sw = new StringWriter();
		try {
			if(pageList != null) {
				log.debug("Page list uri is " + pageList.getURI());
			} else {
				log.debug("Page list uri is null for some reason");
			}
			log.debug("New Display model from file is ");
			newDisplayModel.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
			sw = new StringWriter();
			log.debug("Added statements now include ");
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		}catch(Exception ex) {
			log.error("Exception occurred in writing out new display model", ex);
		}
		
		log.debug("Checking: AFTER adding pageList resource, what do we have for pageList page");
		Resource testResource = ResourceFactory.createResource(DisplayVocabulary.DISPLAY_NS + "pageListPage");
		StmtIterator testIt = addStatements.listStatements(testResource, null, (RDFNode) null);
		if(!testIt.hasNext()) {
			log.debug("Add statements does not have the page list page resource " + testResource.getURI());
		}
		
		while(testIt.hasNext()) {
			log.debug("Statement for page list resource: " + testIt.nextStatement().toString());
		}
	}
	
	//update any new labels
	private void updateDataGetterLabels(OntModel displayModel, Model addStatements, Model removeStatements, UpdateSettings settings) {
		log.debug("Checking: BEFORE adding any statements, what do we have for pageList page");
		Resource testResource = ResourceFactory.createResource(DisplayVocabulary.DISPLAY_NS + "pageListPage");
		StmtIterator testIt = addStatements.listStatements(testResource, null, (RDFNode) null);
		if(!testIt.hasNext()) {
			log.debug("Add statements does not have the page list page resource " + testResource.getURI());
		}
		
		while(testIt.hasNext()) {
			log.debug("Statement for page list resource: " + testIt.nextStatement().toString());
		}
		
		log.debug("Triple checking -- before this method, the add statements model contains");
		StringWriter sw = new StringWriter();
		try {
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		}catch(Exception ex) 
		{
			log.error("Error occurred in adding resource labels ", ex);
		}
		
		
		OntModel newDisplayModel = settings.getNewDisplayModelFromFile();
		List<Resource> resourcesForLabels = new ArrayList<Resource>();
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.ClassGroupPageData"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.BrowseDataGetter"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.IndividualsForClassesDataGetter"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.InternalClassesDataGetter"));
		resourcesForLabels.add(ResourceFactory.createResource("java:edu.cornell.mannlib.vitro.webapp.utils.dataGetter.SparqlQueryDataGetter"));
		for(Resource r: resourcesForLabels) {
			log.debug("Adding the following for " + r.getURI());
			log.debug(newDisplayModel.listStatements(r, RDFS.label, (RDFNode)null).toList().toString());
			addStatements.add(newDisplayModel.listStatements(r, RDFS.label, (RDFNode)null));
			log.debug("After adding statements, we now have the following in addStatements:::");
			sw = new StringWriter();
			try {
				addStatements.write(sw, "N3");
				log.debug(sw.toString());
				sw.close();
			}catch(Exception ex) 
			{
				log.error("Error occurred in adding resource labels ", ex);
			}
			
		}
		//Add statements now includes
		log.debug("AFTER all resources added, Add statements now includes ");
		sw = new StringWriter();
		try {
			addStatements.write(sw, "N3");
			log.debug(sw.toString());
			sw.close();
		}catch(Exception ex) 
		{
			log.error("Error occurred in adding resource labels ", ex);
		}
		
	}
	
  					
	private OntModel loadModelFromDirectory(String directoryPath) {
		log.debug("Loading model from directory " + directoryPath);
		OntModel om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		File directory = new File(directoryPath);
		if (!directory.isDirectory()) {
			throw new ModelDirectoryNotFoundException(directoryPath + " must be a directory " +
					"containing RDF files.");
		}
		File[] rdfFiles = directory.listFiles();
		for (int i = 0; i < rdfFiles.length; i++) {
			readFile(rdfFiles[i], om, directoryPath);
		}
		return om;
	}
	
	//load file from file path
	private OntModel loadModelFromFile(String filePath) {
		log.debug("Load model from file " + filePath);
		OntModel om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		File file = new File(filePath);
		if (!file.isFile()) {
			throw new ModelFileNotFoundException(filePath + " must be a file " +
					"containing RDF files.");
		}
		readFile(file, om, filePath);
		return om;
	}
	
	
	private void readFile(File f, OntModel om, String path) {
		try {
			FileInputStream fis = new FileInputStream(f);
			try {	
				if (f.getName().endsWith(".n3")) {
					om.read(fis, null, "N3");
				} else {
					om.read(fis, null, "RDF/XML");
				}
			} catch (Exception e) {
				log.error("Unable to load RDF from " + f.getName(), e); 
			}
		} catch (FileNotFoundException fnfe) {
			log.error(f.getName() + " not found. Unable to load" +
					" RDF from this location: " + path);
		}	
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do	
	}
	
	public static String getAskUpdatedQueryPath(ServletContext ctx) {
		return ctx.getRealPath(ASK_QUERY_FILE);
	
    }
	
	private static String timestampedFileName(String prefix, String suffix) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-sss");
		return prefix + "." + sdf.format(new Date()) + "." + suffix;
	}
	
	private class ModelDirectoryNotFoundException extends RuntimeException {
		public ModelDirectoryNotFoundException(String msg) {
			super(msg);
		}
	}
	
	private class ModelFileNotFoundException extends RuntimeException {
		public ModelFileNotFoundException(String msg) {
			super(msg);
		}
	}
}
