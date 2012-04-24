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

package edu.cornell.mannlib.vitro.webapp.filestorage.uploadrequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * <p>
 * Wraps an HTTP request and parses it for file uploads, without losing the
 * request parameters.
 * </p>
 * <p>
 * The request will have an attribute named by {@link #FILE_ITEM_MAP}. Either
 * this attribute or the call to {@link #getFiles()} will produce a map that may
 * be empty but is never null. The keys to the map are the field names for the
 * file fields. Since a form may have multiple fields with the same name, each
 * field name maps to a list of items. If a user does not provide a file to be
 * uploaded in a given field, the length of the file will be 0.
 * </p>
 * <p>
 * If the uploaded file(s) would be larger than the <code>maxFileSize</code>,
 * {@link #parseRequest(HttpServletRequest, int)} does not throw an Exception.
 * Instead, it records the exception in a request attribute named by
 * {@link #FILE_UPLOAD_EXCEPTION}. This attribute can be accessed directly, or
 * indirectly via the methods {@link #hasFileUploadException()} and
 * {@link #getFileUploadException()}. If there is an exception, the file item
 * map (see above) will still be non-null, but it will be empty.
 * </p>
 * <p>
 * Most methods are declared here simply delegate to the wrapped request.
 * Methods that have to do with parameters, files, or parsing exceptions, are
 * handled differently for simple requests and multipart request, and are
 * implemented in the sub-classes.
 * </p>
 */
@SuppressWarnings("deprecation")
public abstract class FileUploadServletRequest extends HttpServletRequestWrapper  {
	public static final String FILE_ITEM_MAP = "file_item_map";
	public static final String FILE_UPLOAD_EXCEPTION = "file_upload_exception";

	// ----------------------------------------------------------------------
	// The factory method
	// ----------------------------------------------------------------------

	/**
	 * Wrap this {@link HttpServletRequest} in an appropriate wrapper class.
	 */
	public static FileUploadServletRequest parseRequest(
			HttpServletRequest request, int maxFileSize) throws IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			return new MultipartHttpServletRequest(request, maxFileSize);
		} else {
			return new SimpleHttpServletRequestWrapper(request);
		}
	}

	// ----------------------------------------------------------------------
	// The constructor and the delegate.
	// ----------------------------------------------------------------------

	private final HttpServletRequest delegate;

	public FileUploadServletRequest(HttpServletRequest delegate) {
	    super(delegate);
		this.delegate = delegate;
	}

	protected HttpServletRequest getDelegate() {
		return this.delegate;
	}

	// ----------------------------------------------------------------------
	// New functionality to be implemented by the subclasses.
	// ----------------------------------------------------------------------

	/** Was this a multipart HTTP request? */
	public abstract boolean isMultipart();

	/**
	 * Get the map of file items, by name.
	 */
	public abstract Map<String, List<FileItem>> getFiles();

	/**
	 * Find a non-empty file item with this name.
	 * 
	 * @return the first such file item, or <code>null</code> if no matching,
	 *         non-empty items were found.
	 */
	public abstract FileItem getFileItem(String string);

	/**
	 * Was there an exception when uploading the file items?
	 */
	public abstract boolean hasFileUploadException();

	/**
	 * Get the exception that occurred when uploading the file items. If no such
	 * exception, return <code>null</code>.
	 */
	public abstract FileUploadException getFileUploadException();

}
