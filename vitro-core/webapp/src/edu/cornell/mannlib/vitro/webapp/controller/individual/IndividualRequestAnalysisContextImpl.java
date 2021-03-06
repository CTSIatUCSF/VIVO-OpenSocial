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

import java.util.List;

import javax.servlet.ServletContext;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.SelfEditingConfiguration;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.IndividualDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.filestorage.model.FileInfo;
import edu.cornell.mannlib.vitro.webapp.utils.NamespaceMapper;
import edu.cornell.mannlib.vitro.webapp.utils.NamespaceMapperFactory;

/**
 * Implement all of the fiddly-bits that we need for analyzing the request for
 * an individual, but that we do not want to do in unit tests.
 */
public class IndividualRequestAnalysisContextImpl implements
		IndividualRequestAnalysisContext {
	private final VitroRequest vreq;
	private final ServletContext ctx;
	private final WebappDaoFactory wadf;
	private final IndividualDao iDao;

	public IndividualRequestAnalysisContextImpl(VitroRequest vreq) {
		this.vreq = vreq;
		this.ctx = vreq.getSession().getServletContext();
		this.wadf = vreq.getWebappDaoFactory();
		this.iDao = wadf.getIndividualDao();
	}

	@Override
	public String getDefaultNamespace() {
		return wadf.getDefaultNamespace();
	}

	@Override
	public String getNamespaceForPrefix(String prefix) {
		if (prefix == null) {
			return "";
		}

		NamespaceMapper namespaceMapper = NamespaceMapperFactory
				.getNamespaceMapper(ctx);
		String ns = namespaceMapper.getNamespaceForPrefix(prefix);

		return (ns == null) ? "" : ns;
	}

	@Override
	public Individual getIndividualByURI(String individualUri) {
		if (individualUri == null) {
			return null;
		}
		return iDao.getIndividualByURI(individualUri);
	}

	@Override
	public Individual getIndividualByNetId(String netId) {
		if (netId == null) {
			return null;
		}

		SelfEditingConfiguration sec = SelfEditingConfiguration.getBean(vreq);
		List<Individual> assocInds = sec.getAssociatedIndividuals(iDao, netId);
		if (!assocInds.isEmpty()) {
			return assocInds.get(0);
		} else {
			return null;
		}
	}

	@Override
	public String getAliasUrlForBytestreamIndividual(Individual individual) {
		if (individual == null) {
			return null;
		}

		FileInfo fileInfo = FileInfo.instanceFromBytestreamUri(wadf,
				individual.getURI());
		if (fileInfo == null) {
			return null;
		}

		return fileInfo.getBytestreamAliasUrl();
	}

}
