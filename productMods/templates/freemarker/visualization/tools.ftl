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

<#assign shortVisualizationURLRoot ="/vis">
<#assign refreshCacheURL = "${urls.base}${shortVisualizationURLRoot}/refresh-cache">

<h2>Visualization Tools</h2>

<a href="${refreshCacheURL}">Refresh Cached Models for Visualization</a> 
<section class="visualizationTools">
<h3>Why is it needed?</h3>
<p>Large-scale visualizations like the Temporal Graph or the Map of Science involve calculating total counts of publications or 
of grants for some entity. Since this also means checking through all of its sub-entities, the underlying queries can be both 
memory-intensive and time-consuming. For a faster user experience, we wish to save the results of these queries for later re-use.</p>

<h3>What's involved in the caching process?</h3>
<p>To this end we have devised a caching solution which will retain information about the hierarchy of organizations -- namely, 
which publications are attributed to which organizations -- by storing the RDF model.</p>

<p>We're currently caching these models in memory.  The cache is built (only once) on the first user request after a server restart.  
Because of this, the same model will be served until the next restart. This means that the data in these models may become stale 
depending upon when it was last created. This works well enough for now. In future releases we will improve this solution so that 
models are stored on disk and periodically updated.</p>

<p>The models are refreshed each time the server restarts.  Since this is not generally practical on production instances, 
administrators can instead use the "refresh cache" link above to do this without a restart.</p>
</section>


${stylesheets.add('<link rel="stylesheet" href="${urls.base}/css/visualization/visualization.css" />')}
