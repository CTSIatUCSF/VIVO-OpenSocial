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

<#-- Template for the body of the GadgetDetails page -->

<style media="screen" type="text/css">
th { 
  vertical-align: top;
  width: 150px;
  text-align: right;
  padding-right: 10px;
}
</style>	

<div class="pageTitle" id="gadgets-title"><h2>${title}</h2></div>

<#-- VIVO OpenSocial Extension by UCSF -->
<#if openSocial??>
   	<form method="POST">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <th>
                    Gadget URLs</br>
                    One Per Line
                </th>
                <td>
                    <textarea name="gadgetURLS" rows="10" cols="160">${gadgetURLS}</textarea>
                </td>
            </tr>
            <tr>
                <th>
                    Debug mode
                </th>
                <td>
                    <input type="checkbox" name="debug" value="debug" checked="checked" />
                </td>
            </tr>
            <tr>
                <th>
                    Use Cache
                </th>
                <td>
                    <input type="checkbox" name="useCache" value="useCache" />
                </td>
            </tr>
            <tr>
                <th>
                </th>
                <td>
                    <input type="submit" value="Submit" />
                </td>
            </tr>
        </table>
   	</form>
</#if>	
