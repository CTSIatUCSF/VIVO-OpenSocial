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

<#-- Template for general system error. -->

<p>
    There was an error in the system. 
    <#if sentEmail>
        This error has been reported to the site administrator. 
    </#if>
</p>  
    
<#if adminErrorData??> <#-- view for site administrators -->
   <#if adminErrorData.errorMessage?has_content>
        <p><strong>Error message:</strong> ${adminErrorData.errorMessage?html}</p>
    </#if>
    <#if adminErrorData.stackTrace?has_content>
        <p>
            <strong>Stack trace</strong> (full trace available in the vivo log): ${adminErrorData.stackTrace?html}
        </p>
                   
        <#if adminErrorData.cause?has_content>
            <p><strong>Caused by:</strong> ${adminErrorData.cause?html}</p>            
        </#if>
    </#if>  

<#elseif ! errorOnHomePage> <#-- view for other users -->
    <p>Return to the <a href="${urls.home}" title="home page">home page</a></p> 
</#if>

