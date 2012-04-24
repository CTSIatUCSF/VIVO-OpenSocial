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

<#-- Template for Site Administration data input panel -->

<#import "lib-form.ftl" as form>

<#if dataInput?has_content>
    <section class="pageBodyGroup" role="region">
        <h3>Data Input</h3>

        <form id="addIndividualClass" action="${dataInput.formAction}" method="get">
            <select id="VClassURI" name="typeOfNew" class="form-item long-options" role="select">
                <@form.optionGroups groups=dataInput.groupedClassOptions />
            </select>
            <input type="hidden" name="editForm" value="edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators.NewIndividualFormGenerator" role="input" />
            <input type="submit" id="submit" value="Add individual of this class" role="button" />
        </form>
        
        <section id="addClassBubble" role="region">
            <p>Please create a <a title="Create a class gorup" href="${urls.base}/editForm?controller=Classgroup">class group</a> and associate classes with the group created.</p>
        </section>
    </section>
</#if>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/js/jquery-ui/css/smoothness/jquery-ui-1.8.9.custom.css" />')}

${scripts.add('<script type="text/javascript" src="${urls.base}/js/jquery-ui/js/jquery-ui-1.8.9.custom.min.js"></script>',
              '<script type="text/javascript" src="${urls.base}/js/siteAdmin/siteAdminUtils.js"></script>')}