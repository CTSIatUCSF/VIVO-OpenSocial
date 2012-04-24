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
/**
 * Manage async download contents and make sure don't have the same 
 * download request in the same time. Provide ability to abort download.
 */

var DownloadManager = Class.extend({
	init: function() {
		this.downloadList = {};
	},
	download: function(url, success) {
		if (!this.hasKey(url)) {
			this.downloadList[url] = { success: success, // TODO Try removing this property
					jqxhr: this.startDownload(url, success, this.downloadList)};
		}
	},
	downloadAndWait: function(url, success) {
		if (!this.hasKey(url)) {
			$.ajax({
				url: url,
				async: false,
				dataType: 'json',
				success: function(countData) { success(countData); }
			});
		}
	},
	startDownload: function(url, success, downloadList) {
		 return $.getJSON(url, // TODO Not always "latest" //TODO Test on server with big file that consume 3 seconds download time. Then Keep on and off the checkbox while downloading, to verify if the duplicate happens
				function(countData) {
			        if (success) {
						success(countData);
		 			}
			        delete(downloadList[url]);
				}
			);
	},
	hasKey: function(url) {
		return (this.downloadList[url]);
	},
	abort: function(url) {
		var options = this.downloadList[url];
		if (options) {
			options.jqxhr.abort();
			delete(this.downloadList[url]);
		}
	},
	isDone: function(url) {
		return !this.hasKey(url);
	}
});