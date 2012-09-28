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

package edu.cornell.mannlib.vitro.webapp.rdfservice.filter;

import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class FilteredResultSet implements ResultSet {

    protected Iterator<QuerySolution> solutIt;
    protected ResultSet originalResultSet;
    protected int rowNum = -1;
    
    public FilteredResultSet (List<QuerySolution> solutions, ResultSet originalResultSet) {
        this.solutIt = solutions.iterator();
        this.originalResultSet = originalResultSet;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Attempt to remove an element");
    }

    @Override
    public Model getResourceModel() {
        return originalResultSet.getResourceModel();
    }

    @Override
    public List<String> getResultVars() {
        return originalResultSet.getResultVars();
    }

    @Override
    public int getRowNumber() {
        return rowNum;
    }

    @Override
    public boolean hasNext() {
        return solutIt.hasNext();
    }

    @Override
    public QuerySolution next() {
        return nextSolution();
    }

    @Override
    public Binding nextBinding() {
        throw new UnsupportedOperationException("Can we ignore this?");
    }

    @Override
    public QuerySolution nextSolution() {
        rowNum++;
        return solutIt.next();
    }

}
