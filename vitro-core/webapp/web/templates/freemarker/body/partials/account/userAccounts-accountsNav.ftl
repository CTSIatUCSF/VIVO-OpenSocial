<#--
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
-->

<#-----------------------------------------------------------------------------
    Macro for generating number of accounts, pagination, accounts per page, 
    and delete function.
------------------------------------------------------------------------------>


<#assign counts=[25, 50, 100] /> <#-- accounts per page-->

<#macro accountsNav accountsCount=counts>

  <section class="accounts">
      <input type="submit" name="delete-account" class="delete-account delete" value="Delete" onClick="changeAction(this.form, '${formUrls.delete}')" />
      <!-- 
          When this is clicked, the checkboxes are noticed and all other fields are ignored. 
          submit the form (submit action is formUrls.delete)
      -->

      <nav class="display-tools">
          <span>| ${total} accounts | </span>  

          <select name="accountsPerPage" class="accounts-per-page">
              <#list accountsCount as count>
              <option value="${count}" <#if accountsPerPage=count>selected</#if> >${count}</option>
              </#list>
             <option value="${total}" <#if accountsPerPage=total>selected</#if> >All</option>
              <!--     
                  When accountsPerPage changes, 
                  set pageIndex to 1 
                  submit the form (submit action is formUrls.list) 
              -->     
          </select>

          accounts per page <input type="submit" name="accounts-per-page" value="Update" /> | 

          <#if page.previous?has_content>
              <a href="${formUrls.list}?accountsPerPage=${accountsPerPage}&pageIndex=${page.previous}" title="previous">Previous</a> <!-- only present if current page is not 1.-->
          </#if>
              ${page.current} of ${page.last} 
          <#if page.next?has_content>
              <a href="${formUrls.list}?accountsPerPage=${accountsPerPage}&pageIndex=${page.next}" title="next">Next</a><!-- only present if current page is not last page.-->
          </#if>
      </nav>
  </section>

</#macro>