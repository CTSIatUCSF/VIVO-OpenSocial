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

package stubs.javax.servlet.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;

/**
 * A simple stub for HttpServletRequest
 */
public class HttpServletRequestStub implements HttpServletRequest {
	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------

	private String pathInfo;
	private String requestUri;
	private String requestUrl;
	private String contextPath;
	private String servletPath;

	private String httpMethodType = "GET";
	private String remoteAddr = "127.0.0.1";

	private HttpSession session;
	private final Map<String, List<String>> parameters;
	private final Map<String, Object> attributes;
	private final Map<String, List<String>> headers;

	public HttpServletRequestStub() {
		parameters = new HashMap<String, List<String>>();
		attributes = new HashMap<String, Object>();
		headers = new HashMap<String, List<String>>();
	}

	public HttpServletRequestStub(Map<String, List<String>> parameters,
			Map<String, Object> attributes) {
		this();
		this.parameters.putAll(parameters);
		this.attributes.putAll(attributes);
	}

	/**
	 * Supply the request URL as a single URL. We will parse it on the
	 * assumption that the contextPath and the pathInfo are empty. 
	 * Don't include a query string. Instead, set parameters.
	 */
	public void setRequestUrl(URL url) {
		this.contextPath = "";
		this.pathInfo = null;

		this.requestUrl = url.toString();

		String path = url.getPath();
		if (path.isEmpty()) {
			this.servletPath = "/";
		} else {
			this.servletPath = path;
		}

		this.requestUri = this.servletPath;
	}

	/**
	 * Supply the pieces of the request URL, so we can respond correctly when
	 * asked for a piece.
	 * Don't include a query string. Instead, set parameters.
	 */
	public void setRequestUrlByParts(String shemeHostPort, String contextPath,
			String servletPath, String pathInfo) {
		if (contextPath == null) {
			throw new NullPointerException("contextPath may not be null.");
		}
		this.contextPath = contextPath;

		this.pathInfo = pathInfo;

		if (servletPath == null) {
			throw new NullPointerException("servletPath may not be null.");
		}
		if (!servletPath.startsWith("/")) {
			throw new IllegalArgumentException(
					"servletPath must start with a /");
		}
		this.servletPath = servletPath;

		this.requestUri = contextPath + servletPath + ((pathInfo == null) ? "" : pathInfo);
		
		if (shemeHostPort == null) {
			throw new NullPointerException("shemeHostPort may not be null.");
		}
		if (!shemeHostPort.contains("://")) {
			throw new IllegalArgumentException(
					"schemeHostPort must be sheme://host[:port]");
		}
		this.requestUrl = shemeHostPort + this.requestUri;
	}

	/** Set to "GET" or "POST", etc. */
	public void setMethod(String method) {
		this.httpMethodType = method;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public void setHeader(String name, String value) {
		name = name.toLowerCase();
		if (!headers.containsKey(name)) {
			headers.put(name, new ArrayList<String>());
		}
		headers.get(name).add(value);
	}

	public void addParameter(String name, String value) {
		if (!parameters.containsKey(name)) {
			parameters.put(name, new ArrayList<String>());
		}
		parameters.get(name).add(value);
	}

	/** Clear all values for a given parameter name. */
	public void removeParameter(String name) {
		parameters.remove(name);
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (create && (session == null)) {
			session = new HttpSessionStub();
		}
		return session;
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(requestUrl);
	}

	@Override
	public String getRequestURI() {
		return requestUri;
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public String getServletPath() {
		return servletPath;
	}
	
	@Override
	public String getPathInfo() {
		return pathInfo;
	}
	
	@Override
	public String getQueryString() {
		if (parameters.isEmpty()) {
			return null;
		}
		
		String qs = "";
		for (String key:parameters.keySet()) {
			for (String value: parameters.get(key)) {
				qs += "&" + key + "=" + URLEncoder.encode(value);
			}
		}
		return "?" + qs.substring(1);
	}

	@Override
	public String getMethod() {
		return httpMethodType;
	}

	@Override
	public String getRemoteAddr() {
		return remoteAddr;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map getParameterMap() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		for (String key : parameters.keySet()) {
			map.put(key, parameters.get(key).toArray(new String[0]));
		}
		return map;
	}

	@Override
	public String getParameter(String name) {
		if (!parameters.containsKey(name)) {
			return null;
		}
		return parameters.get(name).get(0);
	}

	@Override
	public String[] getParameterValues(String name) {
		if (!parameters.containsKey(name)) {
			return null;
		}
		List<String> list = parameters.get(name);
		return list.toArray(new String[list.size()]);
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (value == null) {
			removeAttribute(name);
		}
		attributes.put(name, value);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getHeaderNames() {
		return Collections.enumeration(headers.keySet());
	}

	@Override
	public String getHeader(String name) {
		name = name.toLowerCase();
		if (headers.containsKey(name)) {
			return headers.get(name).get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration getHeaders(String name) {
		name = name.toLowerCase();
		if (headers.containsKey(name)) {
			return Collections.enumeration(headers.get(name));
		} else {
			return Collections.enumeration(Collections.emptyList());
		}
	}

	// ----------------------------------------------------------------------
	// Un-implemented methods
	// ----------------------------------------------------------------------

	public String getAuthType() {
		throw new RuntimeException(
				"HttpServletRequestStub.getAuthType() not implemented.");
	}

	public Cookie[] getCookies() {
		throw new RuntimeException(
				"HttpServletRequestStub.getCookies() not implemented.");
	}

	public long getDateHeader(String arg0) {
		throw new RuntimeException(
				"HttpServletRequestStub.getDateHeader() not implemented.");
	}

	public int getIntHeader(String arg0) {
		throw new RuntimeException(
				"HttpServletRequestStub.getIntHeader() not implemented.");
	}

	public String getPathTranslated() {
		throw new RuntimeException(
				"HttpServletRequestStub.getPathTranslated() not implemented.");
	}

	public String getRemoteUser() {
		throw new RuntimeException(
				"HttpServletRequestStub.getRemoteUser() not implemented.");
	}

	public String getRequestedSessionId() {
		throw new RuntimeException(
				"HttpServletRequestStub.getRequestedSessionId() not implemented.");
	}

	public Principal getUserPrincipal() {
		throw new RuntimeException(
				"HttpServletRequestStub.getUserPrincipal() not implemented.");
	}

	public boolean isRequestedSessionIdFromCookie() {
		throw new RuntimeException(
				"HttpServletRequestStub.isRequestedSessionIdFromCookie() not implemented.");
	}

	public boolean isRequestedSessionIdFromURL() {
		throw new RuntimeException(
				"HttpServletRequestStub.isRequestedSessionIdFromURL() not implemented.");
	}

	public boolean isRequestedSessionIdFromUrl() {
		throw new RuntimeException(
				"HttpServletRequestStub.isRequestedSessionIdFromUrl() not implemented.");
	}

	public boolean isRequestedSessionIdValid() {
		throw new RuntimeException(
				"HttpServletRequestStub.isRequestedSessionIdValid() not implemented.");
	}

	public boolean isUserInRole(String arg0) {
		throw new RuntimeException(
				"HttpServletRequestStub.isUserInRole() not implemented.");
	}

	public String getCharacterEncoding() {
		throw new RuntimeException(
				"HttpServletRequestStub.getCharacterEncoding() not implemented.");
	}

	public int getContentLength() {
		throw new RuntimeException(
				"HttpServletRequestStub.getContentLength() not implemented.");
	}

	public String getContentType() {
		throw new RuntimeException(
				"HttpServletRequestStub.getContentType() not implemented.");
	}

	public ServletInputStream getInputStream() throws IOException {
		throw new RuntimeException(
				"HttpServletRequestStub.getInputStream() not implemented.");
	}

	public String getLocalAddr() {
		throw new RuntimeException(
				"HttpServletRequestStub.getLocalAddr() not implemented.");
	}

	public String getLocalName() {
		throw new RuntimeException(
				"HttpServletRequestStub.getLocalName() not implemented.");
	}

	public int getLocalPort() {
		throw new RuntimeException(
				"HttpServletRequestStub.getLocalPort() not implemented.");
	}

	public Locale getLocale() {
		throw new RuntimeException(
				"HttpServletRequestStub.getLocale() not implemented.");
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getLocales() {
		throw new RuntimeException(
				"HttpServletRequestStub.getLocales() not implemented.");
	}

	public String getProtocol() {
		throw new RuntimeException(
				"HttpServletRequestStub.getProtocol() not implemented.");
	}

	public BufferedReader getReader() throws IOException {
		throw new RuntimeException(
				"HttpServletRequestStub.getReader() not implemented.");
	}

	public String getRealPath(String arg0) {
		throw new RuntimeException(
				"HttpServletRequestStub.getRealPath() not implemented.");
	}

	public String getRemoteHost() {
		throw new RuntimeException(
				"HttpServletRequestStub.getRemoteHost() not implemented.");
	}

	public int getRemotePort() {
		throw new RuntimeException(
				"HttpServletRequestStub.getRemotePort() not implemented.");
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		throw new RuntimeException(
				"HttpServletRequestStub.getRequestDispatcher() not implemented.");
	}

	public String getScheme() {
		throw new RuntimeException(
				"HttpServletRequestStub.getScheme() not implemented.");
	}

	public String getServerName() {
		throw new RuntimeException(
				"HttpServletRequestStub.getServerName() not implemented.");
	}

	public int getServerPort() {
		throw new RuntimeException(
				"HttpServletRequestStub.getServerPort() not implemented.");
	}

	public boolean isSecure() {
		throw new RuntimeException(
				"HttpServletRequestStub.isSecure() not implemented.");
	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		throw new RuntimeException(
				"HttpServletRequestStub.setCharacterEncoding() not implemented.");
	}

}
