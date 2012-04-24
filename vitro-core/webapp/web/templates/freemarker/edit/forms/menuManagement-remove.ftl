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

<#--Template for removing menu items -->

<h3>Remove menu item</h3>

<section id="remove-menu-item" role="region">
    <form method="POST" action="${formUrls}" class="customForm" role="remove menu item">
        <input type="hidden" name="menuItem" id="menuItem" value="${menuItem}" role="input" />
        <input type="hidden" name="cmd" id="cmd" value="Remove" role="input" />
        <input type="hidden" name="switchToDisplayModel" id="switchToDisplayModel" value="true" role="input" />

        <p>Are you sure you want to remove <em>${menuName}</em> menu item?</p>

        <input type="submit" name="removeMenuItem" value="Remove menu item" class="submit" role="input" /> or <a class="cancel" href="${cancelUrl}" title="cancel">Cancel</a>
    </form>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/menupage/menuManagement.css" />')}
${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
