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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.jga.fn.UnaryFunctor;
import net.sf.jga.fn.adaptor.ChainUnary;
import net.sf.jga.fn.property.GetProperty;
import net.sf.jga.fn.string.Match;
import edu.cornell.mannlib.vitro.webapp.beans.Individual;
/**
 * Static methods to help create commonly used filters.
 */
public class VitroFilterUtils {
	/**
	 * Gets a filter that hides any property or resource that is restricted from
	 * public view.
	 */
	public static VitroFilters getPublicFilter(ServletContext ctx) {
		return new FilterByRoleLevelPermission(ctx);
	}

    /** Gets a VitroFilters that permits all objects */
    protected static VitroFiltersImpl getNoOpFilter(){
        return new VitroFiltersImpl();
    }

    /** Filter that only returns individuals with labels that start with the letter 'g' */
    public static VitroFilters getTestFilter(){
        UnaryFunctor<Individual,String> getName = new GetProperty<Individual,String>(Individual.class,"getName");
        UnaryFunctor<String,Boolean> startsWithG = new Match("^[gG].*");
        UnaryFunctor<Individual,Boolean> onlyNamesThatStartWithG =
                new ChainUnary<Individual,String,Boolean>(startsWithG,getName);
        return getNoOpFilter().setIndividualFilter(onlyNamesThatStartWithG);
    }  

    public static UnaryFunctor<Individual,String> FirstLetterOfIndividuals(){
        return new UnaryFunctor<Individual,String>(){
            @Override
			public String fn(Individual arg){
                return arg.getName().substring(0,1);
            }
            @Override
			public String toString(){ return "filter: FirstLetterOfIndividuals"; }
        };
    }

    public static class EntitySortTransform extends UnaryFunctor<List<Individual>,List<Individual>>{
        private final Comparator<? super Individual> comparator;

        public EntitySortTransform( String fieldName, boolean ascending){
                if( ascending )
                    comparator = nameComp;
                else
                    comparator = nameCompDesc;
        }

        @Override
		public List<Individual> fn(List<Individual> individuals) {
            Collections.sort(individuals,comparator);
            return individuals;
        }

        private static Comparator<? super Individual> nameComp =
                new Comparator<Individual>(){
            // return ((Individual)o1).getName().compareTo(((Individual)o2).getName());

                    @Override
					public int compare(Individual o1, Individual o2) {
                        String name1 = o1.getName();
                        String name2 = o2.getName();
                        if( name1 == null && name2 == null )
                            return 0;
                        if( name1 == null )
                            return 1;
                        if( name2 == null)
                            return -1;
                        Collator collator = Collator.getInstance();
                        return collator.compare(name1,name2);
                        //return name1.compareTo(name2);
                    }
                    @Override
					public String toString(){ return "nameComp"; }
                };

        private static Comparator<? super Individual> nameCompDesc =
                new Comparator<Individual>(){
                    @Override
					public int compare(Individual o1, Individual o2) {
                        String name1 = o1.getName();
                        String name2 = o2.getName();
                        if( name1 == null && name2 == null )
                            return 0;
                        if( name1 == null )
                            return -1;
                        if( name2 == null)
                            return 1;
                        Collator collator = Collator.getInstance();
                        return collator.compare(name1,name2);
                        //return name2.compareTo(name1);
                    }
                    @Override
					public String toString(){ return "nameCompDesc"; }
                };

    }   

}
