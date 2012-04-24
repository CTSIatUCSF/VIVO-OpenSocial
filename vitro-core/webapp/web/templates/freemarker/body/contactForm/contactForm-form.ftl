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

<#-- Contact form -->

<section class="staticPageBackground feedbackForm" role="region">
    <h2>${title}</h2>
    
    <p>Thank you for your interest in ${siteName}. 
        Please submit this form with questions, comments, or feedback about the content of this site.
    </p>
        
    <form name="contact_form" id="contact_form" class="customForm" action="${formAction}" method="post" onsubmit="return ValidateForm('contact_form');" role="contact form">
        <input type="hidden" name="RequiredFields" value="webusername,webuseremail,s34gfd88p9x1" />
        <input type="hidden" name="RequiredFieldsNames" value="Name,Email address,Comments" />
        <input type="hidden" name="EmailFields" value="webuseremail" />
        <input type="hidden" name="EmailFieldsNames" value="emailaddress" />
        <input type="hidden" name="DeliveryType" value="contact" />
    
        <label for="webusername">Full name <span class="requiredHint"> *</span></label>
        <input type="text" name="webusername" />
        
        <label for="webuseremail">Email address <span class="requiredHint"> *</span></label>
        <input type="text" name="webuseremail" />

        <label>Comments, questions, or suggestions <span class="requiredHint"> *</span></label>
        <textarea name="s34gfd88p9x1" rows="10" cols="90"></textarea>
        
        <div class="buttons">
            <input id="submit" type="submit" value="Send Mail" />
        </div
        
        <p class="requiredHint">* required fields</p>
    </form>    
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/edit/forms/css/customForm.css" />')}
${scripts.add('<script type="text/javascript" src="${urls.base}/js/commentForm.js"></script>')}
