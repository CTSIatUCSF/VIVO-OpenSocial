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

package edu.cornell.mannlib.vitro.webapp.web.templatemodels.searchresult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.dao.ObjectPropertyStatementDao;
import edu.cornell.mannlib.vitro.webapp.web.ViewFinder;
import edu.cornell.mannlib.vitro.webapp.web.ViewFinder.ClassView;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.BaseTemplateModel;

public abstract class BaseIndividualSearchResult extends BaseTemplateModel {

    private static final Log log = LogFactory.getLog(BaseIndividualSearchResult.class);
    
    protected final VitroRequest vreq;
    protected final Individual individual;
       
    public BaseIndividualSearchResult(Individual individual, VitroRequest vreq) {
        this.vreq = vreq;
        this.individual = individual;
    }

    protected String getView(ClassView view) {
        ViewFinder vf = new ViewFinder(view);
        return vf.findClassView(individual, vreq);
    }
    
    public static List<IndividualSearchResult> getIndividualTemplateModels(List<Individual> individuals, VitroRequest vreq) {
        List<IndividualSearchResult> models = new ArrayList<IndividualSearchResult>(individuals.size());
        for (Individual individual : individuals) {
          models.add(new IndividualSearchResult(individual, vreq));
        }  
        return models;
    }
    
    /* Template properties */

	public String getUri() {
		return individual.getURI();
	}
    
    public String getProfileUrl() {
        return UrlBuilder.getIndividualProfileUrl(individual, vreq);
    }    
    
    public String getName() {           
        return individual.getName();
    }
    
    public Collection<String> getMostSpecificTypes() {
        ObjectPropertyStatementDao opsDao = vreq.getWebappDaoFactory().getObjectPropertyStatementDao();
        Map<String, String> types = opsDao.getMostSpecificTypesInClassgroupsForIndividual(individual.getURI()); 
        return types.values();  
    }
    
    public String getSnippet() {        
        return individual.getSearchSnippet();
    }
    
}
