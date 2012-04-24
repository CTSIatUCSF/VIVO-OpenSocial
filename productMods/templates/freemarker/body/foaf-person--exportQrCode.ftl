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

<#-- Page providing options for disseminating QR codes -->
<#assign qrCodeWidth = "150">

<#include "individual-qrCodeGenerator.ftl">

<h2>Export QR Code <em>(<a href="${individual.qrData().aboutQrCodesUrl}" title="More info on QR codes">What is this?</a>)</em></h2>

<#assign thumbUrl = individual.thumbUrl! "${urls.images}/placeholders/person.thumbnail.jpg" >
<img class="individual-photo qrCode" src="${thumbUrl}" width="160" />

<h3 class="qrCode"><a href="${individual.profileUrl}" title="View this person's profile">${individual.nameStatement.value}</a></h3>

<section class="vcard">
	<h4>VCard</h4>
	<@qrCodeVCard qrCodeWidth />
	<textarea name="qrCodeVCard" readonly>
		&lt;img src="${getQrCodeUrlForVCard(qrCodeWidth)!}" /&gt;<#t>
	</textarea><#t>
</section>

<section>
	<h4>Hyperlink</h4>
	<@qrCodeLink qrCodeWidth />
	<textarea name="qrCodeLink" readonly>
		&lt;img src="${getQrCodeUrlForLink(qrCodeWidth)!}" /&gt;<#t>
	</textarea><#t>
</section>

${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/individual/individual-qr.css" />')}