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

package edu.cornell.mannlib.vitro.webapp.controller.json;

import static edu.cornell.mannlib.vitro.webapp.controller.json.JsonServlet.REPLY_SIZE;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.cornell.mannlib.vitro.webapp.beans.Individual;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * 
 */
public class GetEntitiesByVClassContinuation extends JsonArrayProducer {
	private static final Log log = LogFactory
		.getLog(GetEntitiesByVClassContinuation.class);

	protected GetEntitiesByVClassContinuation(VitroRequest vreq) {
		super(vreq);
	}

	@Override
	protected JSONArray process() throws ServletException {
        log.debug("in getEntitiesByVClassContinuation()");
        String resKey = vreq.getParameter("resultKey");
        if( resKey == null )
            throw new ServletException("Could not get resultKey");
        HttpSession session = vreq.getSession();
        if( session == null )
            throw new ServletException("there is no session to get the pervious results from");
        @SuppressWarnings("unchecked")
        List<Individual> entsInVClass = (List<Individual>) session.getAttribute(resKey);
        if( entsInVClass == null )
            throw new ServletException("Could not find List<Individual> for resultKey " + resKey);

        List<Individual> entsToReturn = new ArrayList<Individual>(REPLY_SIZE);
        boolean more = false;
        int count = 0;
        /* we have a large number of items to send back so we need to stash the list in the session scope */
        if( entsInVClass.size() > REPLY_SIZE){
            more = true;
            ListIterator<Individual> entsFromVclass = entsInVClass.listIterator();
            while ( entsFromVclass.hasNext() && count <= REPLY_SIZE ){
                entsToReturn.add( entsFromVclass.next());
                entsFromVclass.remove();
                count++;
            }
            if( log.isDebugEnabled() ) log.debug("getEntitiesByVClassContinuation(): Creating reply with continue token," +
            		" sending in this reply: " + count +", remaing to send: " + entsInVClass.size() );  
        } else {
            //send out reply with no continuation
            entsToReturn = entsInVClass;
            count = entsToReturn.size();
            session.removeAttribute(resKey);
            if( log.isDebugEnabled()) log.debug("getEntitiesByVClassContinuation(): sending " + count + " Ind without continue token");
        }

        //put all the entities on the JSON array
        JSONArray ja =  individualsToJson( entsToReturn );

        //put the responseGroup number on the end of the JSON array
        if( more ){
            try{
                JSONObject obj = new JSONObject();
                obj.put("resultGroup", "true");
                obj.put("size", count);

                StringBuffer nextUrlStr = vreq.getRequestURL();
                nextUrlStr.append("?")
                        .append("getEntitiesByVClass").append( "=1&" )
                        .append("resultKey=").append( resKey );
                obj.put("nextUrl", nextUrlStr.toString());

                ja.put(obj);
            }catch(JSONException je ){
                throw new ServletException(je.getMessage());
            }
        }        
        log.debug("done with getEntitiesByVClassContinuation()");
        return ja;
    }


}
