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

package edu.cornell.mannlib.vitro.webapp.dao.filtering.filters;

import net.sf.jga.fn.UnaryFunctor;
import net.sf.jga.fn.adaptor.AdaptorFunctors;
import edu.cornell.mannlib.vitro.webapp.beans.DataProperty;
import edu.cornell.mannlib.vitro.webapp.beans.DataPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty;
import edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement;
import edu.cornell.mannlib.vitro.webapp.beans.PropertyGroup;
import edu.cornell.mannlib.vitro.webapp.beans.VClass;
import edu.cornell.mannlib.vitro.webapp.beans.VClassGroup;
import edu.cornell.mannlib.vitro.webapp.web.templatemodels.User;

/**
 * A object to hold all the filters commonly used by the vitro webapp.
 *
 * @author bdc34
 *
 */
public class VitroFiltersImpl extends VitroFiltersBase {

    /* *************** filters used by Webapp and Core ******************* */
    /** filter for vitro.bean.Individual objects */
    UnaryFunctor<Individual,Boolean>              individualFilter;

   /** filter for DataProperty objects */
    UnaryFunctor<DataProperty,Boolean>            dataPropertyFilter;
    /** filter for ObjectProperty objects */
    UnaryFunctor<ObjectProperty,Boolean>          objectPropertyFilter;

    /** filter for DataPropertyStatement objects */
    UnaryFunctor<DataPropertyStatement,Boolean>   dataPropertyStatementFilter;
    /** filter for ObjectPropertyStatement objects */
    UnaryFunctor<ObjectPropertyStatement,Boolean> objectPropertyStatementFilter;

    /** filter for VClass objects */
    UnaryFunctor<VClass,Boolean>                  classFilter;

    /* *************** filters only used by Webapp ******************* */
    /** filter for VClassGroup objects */
    UnaryFunctor<VClassGroup,Boolean>          vClassGroupFilter;

    /** fitler for PropertyGroup objects */
    UnaryFunctor<PropertyGroup, Boolean>      propertyGroupFilter;

    public final static UnaryFunctor FILTER_OUT_NOTHING =
        AdaptorFunctors.constantUnary(Boolean.TRUE);

    /**
     * Builds a filter that does no filtering.
     */
    @SuppressWarnings("unchecked")
    public VitroFiltersImpl(){
        individualFilter = FILTER_OUT_NOTHING;
        dataPropertyFilter = FILTER_OUT_NOTHING;
        objectPropertyFilter = FILTER_OUT_NOTHING;
        dataPropertyStatementFilter  = FILTER_OUT_NOTHING;
        objectPropertyStatementFilter= FILTER_OUT_NOTHING;
        classFilter= FILTER_OUT_NOTHING;
        vClassGroupFilter = FILTER_OUT_NOTHING;
        propertyGroupFilter = FILTER_OUT_NOTHING;
    }

    public VitroFiltersImpl(
            UnaryFunctor<Individual, Boolean> individualFilter,
            UnaryFunctor<DataProperty, Boolean> dataPropertyFilter,
            UnaryFunctor<ObjectProperty, Boolean> objectPropertyFilter,
            UnaryFunctor<DataPropertyStatement, Boolean> dataPropertyStatementFilter,
            UnaryFunctor<ObjectPropertyStatement, Boolean> objectPropertyStatementFilter,
            UnaryFunctor<VClass, Boolean> classFilter,
            UnaryFunctor<VClassGroup, Boolean> classGroupFilter,
            UnaryFunctor<PropertyGroup,Boolean>propertyGroupFilter) {
        super();
        this.individualFilter = individualFilter;
        this.dataPropertyFilter = dataPropertyFilter;
        this.objectPropertyFilter = objectPropertyFilter;
        this.dataPropertyStatementFilter = dataPropertyStatementFilter;
        this.objectPropertyStatementFilter = objectPropertyStatementFilter;
        this.classFilter = classFilter;
        vClassGroupFilter = classGroupFilter;
        this.propertyGroupFilter = propertyGroupFilter;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.dao.filtering.VitroFilters#getIndividualFilter()
     */
    public UnaryFunctor<Individual, Boolean> getIndividualFilter() {
        return individualFilter;
    }

    public VitroFilters setIndividualFilter(
            UnaryFunctor<Individual, Boolean> individualFilter) {
        this.individualFilter = individualFilter;
        return this;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.dao.filtering.VitroFilters#getDataPropertyFilter()
     */
    public UnaryFunctor<DataProperty, Boolean> getDataPropertyFilter() {
        return dataPropertyFilter;
    }

    public VitroFilters setDataPropertyFilter(
            UnaryFunctor<DataProperty, Boolean> dataPropertyFilter) {
        this.dataPropertyFilter = dataPropertyFilter;
        return this;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.dao.filtering.VitroFilters#getObjectPropertyFilter()
     */
    public UnaryFunctor<ObjectProperty, Boolean> getObjectPropertyFilter() {
        return objectPropertyFilter;
    }

    public VitroFilters setObjectPropertyFilter(
            UnaryFunctor<ObjectProperty, Boolean> objectPropertyFilter) {
        this.objectPropertyFilter = objectPropertyFilter;
        return this;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.dao.filtering.VitroFilters#getDataPropertyStatementFilter()
     */
    public UnaryFunctor<DataPropertyStatement, Boolean> getDataPropertyStatementFilter() {
        return dataPropertyStatementFilter;
    }

    public VitroFilters setDataPropertyStatementFilter(
            UnaryFunctor<DataPropertyStatement, Boolean> dataPropertyStatementFilter) {
        this.dataPropertyStatementFilter = dataPropertyStatementFilter;
        return this;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.dao.filtering.VitroFilters#getObjectPropertyStatementFilter()
     */
    public UnaryFunctor<ObjectPropertyStatement, Boolean> getObjectPropertyStatementFilter() {
        return objectPropertyStatementFilter;
    }

    public VitroFilters setObjectPropertyStatementFilter(
            UnaryFunctor<ObjectPropertyStatement, Boolean> objectPropertyStatementFilter) {
        this.objectPropertyStatementFilter = objectPropertyStatementFilter;
        return this;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.dao.filtering.VitroFilters#getClassFilter()
     */
    public UnaryFunctor<VClass, Boolean> getClassFilter() {
        return classFilter;
    }

    public VitroFilters setClassFilter(UnaryFunctor<VClass, Boolean> classFilter) {
        this.classFilter = classFilter;
        return this;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.dao.filtering.VitroFilters#getVClassGroupFilter()
     */
    public UnaryFunctor<VClassGroup, Boolean> getVClassGroupFilter() {
        return vClassGroupFilter;
    }

    public VitroFilters setVClassGroupFilter(
            UnaryFunctor<VClassGroup, Boolean> classGroupFilter) {
        vClassGroupFilter = classGroupFilter;
        return this;
    }

    public UnaryFunctor<PropertyGroup, Boolean> getPropertyGroupFilter() {
        return propertyGroupFilter;
    }

    public void setPropertyGroupFilter(
            UnaryFunctor<PropertyGroup, Boolean> propertyGroupFilter) {
        this.propertyGroupFilter = propertyGroupFilter;
    }


}
