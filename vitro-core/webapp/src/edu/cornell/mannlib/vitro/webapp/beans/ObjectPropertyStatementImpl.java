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

package edu.cornell.mannlib.vitro.webapp.beans;

import java.util.Comparator;
import java.util.Date;

/**
 * a class representing a particular instance of an object property
 *
 */
public class ObjectPropertyStatementImpl implements ObjectPropertyStatement
{
    private String subjectURI = null;
    private Individual subject = null;
    private String objectURI = null;
    private Individual object = null;

    private String propertyURI = null;
    private ObjectProperty property = null;
    private boolean subjectOriented = true; //is the range the item of interest?  
    
   public ObjectPropertyStatementImpl() { }

   public ObjectPropertyStatementImpl(String subjectUri, String propertyUri, String objectUri) {
        subjectURI = subjectUri;
        propertyURI = propertyUri;
        objectURI = objectUri;        
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#toString()
     */
    public String toString(){
           String prop = (getProperty()!=null)?getProperty().getDomainPublic():"by propURI"+getPropertyURI();
           String ran = (getObject()!= null)?getObject().getName():"objectURI:"+getObjectURI();
           String dom = (getSubject()!= null)?getSubject().getName():"subjectURI:"+getSubjectURI();
           return "Object Property Statements: "+dom+" "+prop+" to "+ran+" ";
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#getSubjectURI()
     */
    public String getSubjectURI() {
        return subjectURI;
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#setSubjectURI(java.lang.String)
     */
    public void setSubjectURI(String subjectURI) {
        this.subjectURI = subjectURI;
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#getObjectURI()
     */
    public String getObjectURI() {
        return objectURI;
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#setObjectURI(java.lang.String)
     */
    public void setObjectURI(String objectURI) {
        this.objectURI = objectURI;
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#getSubject()
     */
    public Individual getSubject() {
        return subject;
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#setSubject(edu.cornell.mannlib.vitro.webapp.beans.Individual)
     */
    public void setSubject(Individual subject) {
        this.subject = subject;
        if (subject == null || subject.isAnonymous()) {
            setSubjectURI(null);    
        } else {
            setSubjectURI(subject.getURI());
        }
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#getProperty()
     */
    public ObjectProperty getProperty() {
        return property;
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#setProperty(edu.cornell.mannlib.vitro.webapp.beans.ObjectProperty)
     */
    public void setProperty(ObjectProperty property) {
        if(property != null )
            setPropertyURI(property.getURI());
        else
            setPropertyURI(null);
        this.property = property;
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#getObject()
     */
    public Individual getObject() {
        return object;
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#setObject(edu.cornell.mannlib.vitro.webapp.beans.Individual)
     */
    public void setObject(Individual object) {
        this.object = object;
        if (object == null || object.isAnonymous()) {
            setObjectURI(null);    
        } else {
            setObjectURI(object.getURI());
        }
    }


    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#getPropertyURI()
     */
    public String getPropertyURI() {
        return this.propertyURI;
    }
    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#setPropertyURI(java.lang.String)
     */
    public void setPropertyURI(String URI){
        this.propertyURI = URI;
    }

    /**
     * Sorts entity object for display presentation.
     * @author bdc34
     */
    public static class DisplayComparator implements Comparator{    	    	
        public int compare(Object o1, Object o2) {
            Individual ent1 = ((ObjectPropertyStatement) o1).getSubject();
            Individual ent2 = ((ObjectPropertyStatement) o2).getSubject();            
            return ent1.getName().compareTo(ent2.getName());
        }
    }

    /* (non-Javadoc)
     * @see edu.cornell.mannlib.vitro.webapp.beans.ObjectPropertyStatement#toPropertyInstance()
     */
    public PropertyInstance toPropertyInstance(){
        PropertyInstance pi = new PropertyInstance();

        pi.setPropertyURI(propertyURI);
        pi.setSubjectEntURI(subjectURI);
        pi.setObjectEntURI(objectURI);
        return pi;
    }

}
