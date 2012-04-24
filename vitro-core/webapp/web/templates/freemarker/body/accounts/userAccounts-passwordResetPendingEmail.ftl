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

<#-- Confirmation email for user account password reset -->

<#assign subject = "${siteName} reset password request" />

<#assign html>
<html>
    <head>
        <title>${subject}</title>
    </head>
    <body>
        <p>
            Dear ${userAccount.firstName} ${userAccount.lastName}:
        </p>
        
        <p>
            We have received a request to reset the password for your ${siteName} account (${userAccount.emailAddress}). 
        </p>
        
        <p>
            Please follow the instructions below to proceed with your password reset.
        </p>
        
        <p>
            If you did not request this new account you can safely ignore this email. 
            This request will expire if not acted upon within 30 days.
        </p>
        
        <p>
            Click on the link below or paste it into your browser's address bar to reset your password 
            using our secure server.
        </p>
        
        <p>${passwordLink}</p>
        
        <p>Thank you!</p>
    </body>
</html>
</#assign>

<#assign text>
Dear ${userAccount.firstName} ${userAccount.lastName}:
        
We have received a request to reset the password for your ${siteName} account 
(${userAccount.emailAddress}). 

Please follow the instructions below to proceed with your password reset.

If you did not request this new account you can safely ignore this email. 
This request will expire if not acted upon within 30 days.

Paste the link below into your browser's address bar to reset your password 
using our secure server.

${passwordLink}
        
Thank you!
</#assign>

<@email subject=subject html=html text=text />