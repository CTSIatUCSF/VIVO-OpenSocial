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

package edu.cornell.mannlib.vitro.webapp.utils.smoketest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.solr.client.solrj.SolrServer;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * A sample class implementing SmokeTestsRunner interface that 
 * prints out to a webpage the status of SolrServer i.e whether it 
 * is up and running or not.
 * TODO: This is just an initial test implementation and will continue
 * to change.
 */
public class SolrContextChecker implements SmokeTest {

	@Override
	public TestResult test(VitroRequest vreq) {
	
		HttpSession session = vreq.getSession();
		ServletContext context = (ServletContext)session.getServletContext();
		
		//get the index details about SolrServer from the context
		SolrServer server = (SolrServer) context.getAttribute("vitro.local.solr.server");
		
		TestResult testResult;
		
		if(server != null){
			 testResult = new TestResult("Solr Server is up and running!", true);
		}else{
			testResult = null;
		}
	
		return testResult;
	}
	
	@Override
	public String getName(){
		return SolrContextChecker.class.getName();
	}

}
