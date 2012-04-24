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

$(document).ready(function(){
    // This function creates and styles the "qTip" tooltip that displays the resource uri and the rdf link when the user clicks the uri/rdf icon.
    $('#uriIcon').each(function()
    {
        $(this).qtip(
        {
            content: {
                prerender: true, // We need this for the .click() event listener on 'a.close'
                text: '<h5>share the URI for this profile</h5> <input id="uriLink" type="text" value="' + $('#uriIcon').attr('title') + '" /><h5><a class ="rdf-url" href="' + individualRdfUrl + '">view profile in RDF format</a></h5><a class="close" href="#">close</a>'
            },
            position: {
                corner: {
                    target: 'bottomLeft',
                    tooltip: 'topLeft'
                }
            },
            show: {
                when: {event: 'click'}
            },
            hide: {
                fixed: true, // Make it fixed so it can be hovered over and interacted with
                when: {
                    target: $('a.close'),
                    event: 'click'
                }
            },
            style: {
                padding: '1em',
                width: 400,
                backgroundColor: '#f1f2ee'
            }
        });
    });

    // Prevent close link for URI qTip from requesting bogus '#' href
    $('a.close').click(function() {
        $('#uriIcon').qtip("hide");
        return false;
    });
});