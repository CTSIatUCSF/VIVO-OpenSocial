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

package edu.cornell.mannlib.vitro.webapp.controller.jena;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.Lock;

import edu.cornell.mannlib.vedit.beans.LoginStatusBean;
import edu.cornell.mannlib.vitro.webapp.auth.permissions.SimplePermission;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.JenaModelUtils;
import edu.cornell.mannlib.vitro.webapp.dao.jena.ModelContext;
import edu.cornell.mannlib.vitro.webapp.dao.jena.OntModelSelector;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.BulkUpdateEvent;
import edu.cornell.mannlib.vitro.webapp.dao.jena.event.EditEvent;
import edu.cornell.mannlib.vitro.webapp.filestorage.uploadrequest.FileUploadServletRequest;
import edu.cornell.mannlib.vitro.webapp.rdfservice.ChangeSet;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.jena.model.RDFServiceModel;
import edu.cornell.mannlib.vitro.webapp.servlet.setup.JenaDataSourceSetupBase;

public class RDFUploadController extends JenaIngestController {
    
    private static int maxFileSizeInBytes = 1024 * 1024 * 2000; //2000mb
    private static FileItem fileStream = null; 
    private static final String INGEST_MENU_JSP = "/jenaIngest/ingestMenu.jsp";
    private static final String LOAD_RDF_DATA_JSP="/jenaIngest/loadRDFData.jsp";
    private static final String LIST_MODELS_JSP = "/jenaIngest/listModels.jsp";
    
    public void doPost(HttpServletRequest rawRequest,
            HttpServletResponse response) throws ServletException, IOException {
		if (!isAuthorizedToDisplayPage(rawRequest, response,
				SimplePermission.USE_ADVANCED_DATA_TOOLS_PAGES.ACTIONS)) {
            return;
        }

        FileUploadServletRequest req = FileUploadServletRequest.parseRequest(
                rawRequest, maxFileSizeInBytes);
        if (req.hasFileUploadException()) {
            forwardToFileUploadError(
                    req.getFileUploadException().getLocalizedMessage(), 
                            req, response);
            return;
        }

        Map<String, List<FileItem>> fileStreams = req.getFiles();
        
        VitroRequest request = new VitroRequest(req);        
        LoginStatusBean loginBean = LoginStatusBean.getBean(request);
        
        String modelName = req.getParameter("modelName");
        if(modelName!=null){
            loadRDF(req,request,response);
            return;
        }            
                       
        boolean remove = "remove".equals(request.getParameter("mode"));
        String verb = remove?"Removed":"Added";
        
        String languageStr = request.getParameter("language");
        
        boolean makeClassgroups = ("true".equals(request.getParameter(
                "makeClassgroups")));
        
        // add directly to the ABox model without reading first into 
        // a temporary in-memory model
        boolean directRead = ("directAddABox".equals(request.getParameter(
                "mode")));
          
        String uploadDesc ="";        
                
        OntModel uploadModel = (directRead) 
            ? getABoxModel(request.getSession(), getServletContext())
            : ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        
        /* ********************* GET RDF by URL ********************** */
        String RDFUrlStr =  request.getParameter("rdfUrl");
        if (RDFUrlStr != null && RDFUrlStr.length() > 0) {
            try {
                uploadModel.enterCriticalSection(Lock.WRITE);
                try {
                    uploadModel.read(RDFUrlStr, languageStr); 
                    // languageStr may be null and default would be RDF/XML
                } finally {
                    uploadModel.leaveCriticalSection();
                }
                uploadDesc = verb + " RDF from " + RDFUrlStr;                
            } catch (JenaException ex){
                    forwardToFileUploadError("Could not parse file to " + 
                           languageStr + ": " + ex.getMessage(), req, response);
                    return;
            }catch (Exception e) {                               
                forwardToFileUploadError("Could not load from URL: " + 
                        e.getMessage(), req, response);                  
                return;
            }            
        } else {
            /* **************** upload RDF from POST ********************* */
            if( fileStreams.get("rdfStream") != null 
                    && fileStreams.get("rdfStream").size() > 0 ) {
                FileItem rdfStream = fileStreams.get("rdfStream").get(0);
                try {
                    if (directRead) {
                        addUsingRDFService(rdfStream.getInputStream(), languageStr,
                                request.getRDFService());
                    } else {
                        uploadModel.enterCriticalSection(Lock.WRITE);
                        try {
                            uploadModel.read(
                                    rdfStream.getInputStream(), null, languageStr);
                        } finally {
                            uploadModel.leaveCriticalSection();
                        }
                    }
                    uploadDesc = verb + " RDF from file " + rdfStream.getName();                
                } catch (IOException e) {
                    forwardToFileUploadError("Could not read file: " + 
                            e.getLocalizedMessage(), req, response);
                    return;
                }catch (JenaException ex){
                    forwardToFileUploadError("Could not parse file to " + 
                            languageStr + ": " + ex.getMessage(), 
                                    req, response);
                    return;
                }catch (Exception e) {                               
                    forwardToFileUploadError("Could not load from file: " + 
                            e.getMessage(), req, response);                  
                    return;
                }finally{            
                    rdfStream.delete();
                }
            }
        }
        
        /* ********** Do the model changes *********** */
        if( !directRead && uploadModel != null ){
            
            uploadModel.loadImports();
            
            long tboxstmtCount = 0L;
            long aboxstmtCount = 0L;

            JenaModelUtils xutil = new JenaModelUtils();
            
            OntModel tboxModel = getTBoxModel(
                    request.getSession(), getServletContext());
            OntModel aboxModel = getABoxModel(
                    request.getSession(), getServletContext());
            OntModel tboxChangeModel = null;
            Model aboxChangeModel = null;
            OntModelSelector ontModelSelector = ModelContext.getOntModelSelector(
                    getServletContext());
            
            if (tboxModel != null) {
                boolean AGGRESSIVE = true;
                tboxChangeModel = xutil.extractTBox(uploadModel, AGGRESSIVE);
                // aggressively seek all statements that are part of the TBox  
                tboxstmtCount = operateOnModel(request.getFullWebappDaoFactory(),
                        tboxModel, tboxChangeModel, ontModelSelector,
                                remove, makeClassgroups, loginBean.getUserURI());
            }
            if (aboxModel != null) {
                aboxChangeModel = uploadModel.remove(tboxChangeModel);
                aboxstmtCount = operateOnModel(request.getFullWebappDaoFactory(),
                        aboxModel, aboxChangeModel, ontModelSelector, 
                                remove, makeClassgroups, loginBean.getUserURI());
            }
            request.setAttribute("uploadDesc", uploadDesc + ". " + verb + " " + 
                    (tboxstmtCount + aboxstmtCount) + "  statements.");
        } else {
            request.setAttribute("uploadDesc", "RDF upload successful.");
        }
        
        RequestDispatcher rd = request.getRequestDispatcher(
                Controllers.BASIC_JSP);
        request.setAttribute(
                "bodyJsp", "/templates/edit/specific/upload_rdf_result.jsp");
        request.setAttribute("title","Ingest RDF Data");

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            log.error("Could not forward to view: " + e.getLocalizedMessage());            
        }
    }

    private void addUsingRDFService(InputStream in, String languageStr, 
            RDFService rdfService) {
        ChangeSet changeSet = rdfService.manufactureChangeSet();
        RDFService.ModelSerializationFormat format = 
                ("RDF/XML".equals(languageStr) 
                        || "RDF/XML-ABBREV".equals(languageStr))
                                ? RDFService.ModelSerializationFormat.RDFXML
                                : RDFService.ModelSerializationFormat.N3;
        changeSet.addAddition(in, format, 
                JenaDataSourceSetupBase.JENA_DB_MODEL);
        try {
            rdfService.changeSetUpdate(changeSet);
        } catch (RDFServiceException rdfse) {
            log.error(rdfse);
            throw new RuntimeException(rdfse);
        }
    }
    
    public void loadRDF(FileUploadServletRequest req,
                        VitroRequest request,
                        HttpServletResponse response) 
                                throws ServletException, IOException {
        Map<String, List<FileItem>> fileStreams = req.getFiles();
        String filePath = fileStreams.get("filePath").get(0).getName();
        fileStream = fileStreams.get("filePath").get(0);
        String modelName = req.getParameter("modelName");
        String docLoc = req.getParameter("docLoc");
        String languageStr = request.getParameter("language");
        ModelMaker maker = getVitroJenaModelMaker(request);
        
        if (docLoc!=null && modelName != null) {
            RDFService rdfService = RDFServiceUtils.getRDFServiceFactory(
                    getServletContext()).getRDFService();
            try {
                doLoadRDFData(modelName, docLoc, filePath, languageStr, rdfService);
            } finally {
                rdfService.close();
            }
            String modelType = getModelType(request, maker);
            showModelList(request, maker, modelType);
        } else {
            request.setAttribute("title","Load RDF Data");
            request.setAttribute("bodyJsp",LOAD_RDF_DATA_JSP);
        }
        
        RequestDispatcher rd = request.getRequestDispatcher(
                Controllers.BASIC_JSP);      

        try {
            rd.forward(request, response);
        } catch (Exception e) {
            String errMsg = " could not forward to view.";
            log.error(errMsg, e);
            throw new ServletException(errMsg, e);
        }
        
    }
    
    private long operateOnModel(WebappDaoFactory webappDaoFactory, 
                                OntModel mainModel, 
                                Model changesModel, 
                                OntModelSelector ontModelSelector,
                                boolean remove, 
                                boolean makeClassgroups,  
                                String userURI) {
            
        EditEvent startEvent = null, endEvent = null;
        
        if (remove) {
            startEvent = new BulkUpdateEvent(userURI, true);
            endEvent = new BulkUpdateEvent(userURI, false);
        } else {
            startEvent = new EditEvent(userURI, true);
            endEvent = new EditEvent(userURI, false);
        }
         
        Model[] classgroupModel = null;
        
        if (makeClassgroups) {
            classgroupModel = JenaModelUtils.makeClassGroupsFromRootClasses(
                        webappDaoFactory, changesModel);
            OntModel appMetadataModel = ontModelSelector
                    .getApplicationMetadataModel(); 
            appMetadataModel.enterCriticalSection(Lock.WRITE);
            try {
                appMetadataModel.add(classgroupModel[0]);
            } finally {
                appMetadataModel.leaveCriticalSection();
            }
        }
            
        mainModel.enterCriticalSection(Lock.WRITE);
        try {
            
            mainModel.getBaseModel().notifyEvent(startEvent);
            try {                                                 
                if (remove) {
                    RDFService rdfService = new RDFServiceModel(mainModel);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    changesModel.write(out, "N-TRIPLE");
                    ChangeSet cs = rdfService.manufactureChangeSet();
                    ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                    cs.addRemoval(in, RDFService.ModelSerializationFormat.NTRIPLE, null);
                    try {
                        rdfService.changeSetUpdate(cs);
                    } catch (RDFServiceException e) {
                        throw new RuntimeException(e);
                    }
                    //mainModel.remove(changesModel);
                } else {
                    mainModel.add(changesModel);
                    if (classgroupModel != null) {
                        mainModel.add(classgroupModel[1]);
                    }
                } 
            } finally {
                mainModel.getBaseModel().notifyEvent(endEvent);
            }
        } finally {
            mainModel.leaveCriticalSection();
        }
        return changesModel.size();        
    }
    
    private void doLoadRDFData(String modelName, 
                               String docLoc, 
                               String filePath, 
                               String language, 
                               RDFService rdfService) {
        try {
            if ( (docLoc != null) && (docLoc.length()>0) ) {
                URL docLocURL = new URL(docLoc);
                InputStream in = docLocURL.openStream();
                readIntoModel(in, language, rdfService, modelName);
            } else if ( (filePath != null) && (filePath.length()>0) ) {
                File file = new File(filePath);
                File[] files;
                if (file.isDirectory()) {
                    files = file.listFiles();
                } else {
                    files = new File[1];
                    files[0] = file;
                }
                for (int i=0; i<files.length; i++) {
                    File currentFile = files[i];
                    log.debug("Reading file " + currentFile.getName());
                    try {
                        readIntoModel(fileStream.getInputStream(), language, 
                                rdfService, modelName);
                        fileStream.delete();
                    } catch (IOException ioe) {
                        String errMsg = "Error loading RDF from " + 
                                currentFile.getName();
                        log.error(errMsg, ioe);
                        throw new RuntimeException(errMsg, ioe);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void readIntoModel(InputStream in, String language, 
            RDFService rdfService, String modelName) {
        ChangeSet cs = rdfService.manufactureChangeSet();
        cs.addAddition(in, RDFServiceUtils.getSerializationFormatFromJenaString(
                        language), modelName);
        try {
            rdfService.changeSetUpdate(cs);
        } catch (RDFServiceException e) {
            throw new RuntimeException(e);
        }
    }
    
     private void forwardToFileUploadError( String errrorMsg , 
                                            HttpServletRequest req, 
                                            HttpServletResponse response) 
                                                    throws ServletException{
         VitroRequest vreq = new VitroRequest(req);
         req.setAttribute("title","RDF Upload Error ");
         req.setAttribute("bodyJsp","/jsp/fileUploadError.jsp");
         req.setAttribute("errors", errrorMsg);
         
         RequestDispatcher rd = req.getRequestDispatcher(
                 Controllers.BASIC_JSP);      
         req.setAttribute("css", 
                 "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + 
                 vreq.getAppBean().getThemeDir() + "css/edit.css\"/>");
         try {
             rd.forward(req, response);
         } catch (IOException e1) {
             log.error(e1);
             throw new ServletException(e1);
         }            
         return;
     }
     
     private OntModel getABoxModel(HttpSession session, ServletContext ctx) {   
         if (session != null 
                 && session.getAttribute("baseOntModelSelector")
                         instanceof OntModelSelector) {
             return ((OntModelSelector) 
                     session.getAttribute("baseOntModelSelector"))
                     .getABoxModel();   
         } else {
             return ((OntModelSelector) 
                     ctx.getAttribute("baseOntModelSelector")).getABoxModel();
         }
     }    

     private OntModel getTBoxModel(HttpSession session, ServletContext ctx) {   
         if (session != null 
                 && session.getAttribute("baseOntModelSelector")
                         instanceof OntModelSelector) {
             return ((OntModelSelector) 
                     session.getAttribute("baseOntModelSelector"))
                     .getTBoxModel();   
         } else {
             return ((OntModelSelector) 
                     ctx.getAttribute("baseOntModelSelector")).getTBoxModel();
         }
     }    
     
    private static final Log log = LogFactory.getLog(
            RDFUploadController.class.getName());
}
