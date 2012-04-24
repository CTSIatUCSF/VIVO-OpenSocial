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
/**
 * 
 */
package edu.cornell.mannlib.vitro.webapp.search.controller;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder.ParamMap;
import edu.cornell.mannlib.vitro.webapp.search.controller.PagedSearchController.PagingLink;

public class PagedSearchControllerTest {

    @Test
    public void testGetPagingLinks() {
        ParamMap pm = new ParamMap();         
        int hitsPerPage = 25;
        int totalHits = 500;
        int currentStartIndex = 0;
        List<PagingLink> pageLinks = PagedSearchController.getPagingLinks(currentStartIndex, hitsPerPage, totalHits, "baseURL", pm);
        Assert.assertNotNull(pageLinks);
        Assert.assertEquals(500 / 25, pageLinks.size());
        
        //test for no page links on a very short result
        hitsPerPage = 25;
        totalHits = 10;
        currentStartIndex = 0;
        pageLinks = PagedSearchController.getPagingLinks(currentStartIndex, hitsPerPage, totalHits, "baseURL", pm);
        Assert.assertNotNull(pageLinks);
        Assert.assertEquals(0, pageLinks.size());
    }
    
    @Test
    public void testGetPagingLinksForLargeResults() {
        ParamMap pm = new ParamMap();         
        int hitsPerPage = 25;
        int totalHits = 349909;
        int currentStartIndex = 0;
        List<PagingLink> pageLinks =  PagedSearchController.getPagingLinks(currentStartIndex, hitsPerPage, totalHits, "baseURL", pm);
        Assert.assertNotNull(pageLinks);
        Assert.assertEquals( PagedSearchController.DEFAULT_MAX_HIT_COUNT / hitsPerPage, pageLinks.size());
        
        //test for large sets of results with high start index
        hitsPerPage = 25;
        totalHits = PagedSearchController.DEFAULT_MAX_HIT_COUNT + 20329;
        currentStartIndex = PagedSearchController.DEFAULT_MAX_HIT_COUNT + 5432;
        pageLinks = PagedSearchController.getPagingLinks(currentStartIndex, hitsPerPage, totalHits, "baseURL", pm);
        Assert.assertNotNull(pageLinks);
        Assert.assertEquals( 
                (currentStartIndex / hitsPerPage) + //all the pages that are before the current page 
                (PagedSearchController.DEFAULT_MAX_HIT_COUNT / hitsPerPage) + //some pages after the current apge
                1, //for the more... page
                pageLinks.size());
    }

}
