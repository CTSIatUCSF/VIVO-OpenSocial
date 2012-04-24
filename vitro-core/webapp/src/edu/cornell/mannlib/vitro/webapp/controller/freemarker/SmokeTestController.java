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

package edu.cornell.mannlib.vitro.webapp.controller.freemarker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.TemplateResponseValues;
import edu.cornell.mannlib.vitro.webapp.utils.smoketest.SmokeTest;
import edu.cornell.mannlib.vitro.webapp.utils.smoketest.TestResult;

/**
 * The controller responsible for checking statuses of various
 * services across the web application. (Ex: Solr Server, etc.)
 * TODO: This is just an initial test implementation and will continue
 * to change.
 */
public class SmokeTestController extends FreemarkerHttpServlet {
	
    private static final long serialVersionUID = 1L;   
	
    private static final String TEMPLATE_NAME = "smokeTest.ftl";
	private static final String FILE_PATH = "/WEB-INF/classes/smokeTests";
	private static final String PACKAGE_CONTAINING_SMOKETEST_CLASSES = "edu.cornell.mannlib.vitro.webapp.utils.smoketest.";
	
	private List<String> listOfSmokeTestClasses = new ArrayList<String>();
	
    private static final Log log = LogFactory.getLog(SmokeTestController.class.getName());

	@Override
	protected ResponseValues processRequest(VitroRequest vreq){
		
		List<TestResult> results = new ArrayList<TestResult>();
		ServletContext context = vreq.getSession().getServletContext();
		readSmokeTestFilesFromPath(context);
		
		for(String className : listOfSmokeTestClasses){
			try {

				Class thisClass = Class.forName(PACKAGE_CONTAINING_SMOKETEST_CLASSES +""+ className);
				SmokeTest smokeTestsRunner = (SmokeTest)thisClass.newInstance();
				
				results.add(smokeTestsRunner.test(vreq));
				
			} catch (ClassNotFoundException e) {
				log.error("Class not found "+ e);
			} catch(IllegalAccessException e){
				log.error("Illegal access of the class " + e);
			} catch(InstantiationException e){
				log.error("Error instantiating class " + e);
			}
		}
			
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("results", results);
			
		return new TemplateResponseValues(TEMPLATE_NAME, body);
	}

	private void readSmokeTestFilesFromPath(ServletContext context) {

		log.debug("Reading smoketest files from "+ FILE_PATH );
		Set<String> paths = context.getResourcePaths(FILE_PATH);
		if(paths != null){
			for(String p : paths){
				readSmokeTestClassesFromFile(p, context);
			}
		}
	}

	private void readSmokeTestClassesFromFile(String p, ServletContext context) {
		//check that this is a file and not a directory.
		File f = new File(context.getRealPath(p));
		if(f.exists() && f.isFile()){
				InputStream fileStream = context.getResourceAsStream(p);
				listOfSmokeTestClasses.addAll(getContentsFromFileStream(fileStream));
		} else {
			if(!f.exists()){
				log.debug("File for path " + p + " does not exist");
			}else if(f.isDirectory()){
				log.debug("Path " + p + " corresponds to a directory and not file. File was not read.");
			}
		}
	}

	private List<String> getContentsFromFileStream(InputStream fileStream) {
		
		List<String> classesList = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
		String text = "";
		
		try {
			while((text = reader.readLine()) != null){
				//ignore comments in the file.
				if(text.startsWith("#") || StringUtils.isEmpty(text) || StringUtils.isBlank(text)){
					continue;
				}
				classesList.add(text);
			}
		} catch (IOException e) {
			log.error("Error reading file " + e);
		}
		return classesList;
	
	}
}
