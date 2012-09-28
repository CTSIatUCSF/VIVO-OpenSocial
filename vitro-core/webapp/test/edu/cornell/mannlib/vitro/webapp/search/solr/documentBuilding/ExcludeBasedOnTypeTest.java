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

package edu.cornell.mannlib.vitro.webapp.search.solr.documentBuilding;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.cornell.mannlib.vitro.webapp.beans.IndividualImpl;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;

public class ExcludeBasedOnTypeTest {

	@Test
	public void testCheckForExclusion() {
		
		ExcludeBasedOnType ebot = new ExcludeBasedOnType();
		ebot.addTypeToExclude("http://xmlns.com/foaf/0.1/Person");
		
		IndividualImpl ind = new IndividualImpl();
		ind.setURI("http://example.com/n2343");
		VClass personClass = new VClass("http://xmlns.com/foaf/0.1/Person");		
		ind.setVClass(personClass);
		
		String excludeResult = ebot.checkForExclusion(ind);
		assertNotNull( excludeResult );		
	}

	@Test
	public void testCheckForExclusion2() {
		
		ExcludeBasedOnType ebot = new ExcludeBasedOnType();
		ebot.addTypeToExclude("http://example.com/KillerRobot");
		
		IndividualImpl ind = new IndividualImpl();
		ind.setURI("http://example.com/n2343");
		VClass personClass = new VClass("http://xmlns.com/foaf/0.1/Agent");		
		ind.setVClass(personClass);
		
		List<VClass> vClassList = new ArrayList<VClass>();
		vClassList.add( new VClass("http://example.com/Robot"));
		vClassList.add( new VClass("http://example.com/KillerRobot"));
		vClassList.add( new VClass("http://example.com/Droid"));
		ind.setVClasses(vClassList, true);
		
		String excludeResult = ebot.checkForExclusion(ind);
		assertNotNull( excludeResult );		
	}
	
	@Test
	public void testCheckForNonExclusion() {
		
		ExcludeBasedOnType ebot = new ExcludeBasedOnType();
		ebot.addTypeToExclude("http://xmlns.com/foaf/0.1/Person");
		
		IndividualImpl ind = new IndividualImpl();
		ind.setURI("http://example.com/n2343");
		VClass personClass = new VClass("http://xmlns.com/foaf/0.1/Robot");		
		ind.setVClass(personClass);
		
		String excludeResult = ebot.checkForExclusion(ind);
		assertNull( excludeResult );		
	}
	
	@Test
	public void testCheckForNonExclusion2() {		
		ExcludeBasedOnType ebot = new ExcludeBasedOnType();
		ebot.addTypeToExclude("http://xmlns.com/foaf/0.1/Person");
		
		IndividualImpl ind = new IndividualImpl();
		ind.setURI("http://example.com/n2343");
		VClass personClass = new VClass("http://xmlns.com/foaf/0.1/Agent");		
		ind.setVClass(personClass);
		
		List<VClass> vClassList = new ArrayList<VClass>();
		vClassList.add( new VClass("http://example.com/Robot"));
		vClassList.add( new VClass("http://example.com/KillerRobot"));
		vClassList.add( new VClass("http://example.com/Droid"));
		ind.setVClasses(vClassList, true);
		
		String excludeResult = ebot.checkForExclusion(ind);
		assertNull( excludeResult );		
	}
}
