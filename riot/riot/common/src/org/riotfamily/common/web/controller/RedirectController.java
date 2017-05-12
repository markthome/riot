/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that sends a redirect to a configurable URL.
 */
public class RedirectController implements Controller {

	private boolean permanent = false;
	
	private boolean http10Compatible = true;
	
	private boolean addContextPath = false;
	
	private String encodingScheme = "UTF-8";
		
	private String url;
	
	public RedirectController(String url) {
		this(url, false);
	}
	
	public RedirectController(String url, boolean permanent) {
		this(url, permanent, false);
	}
	
	public RedirectController(String url, boolean permanent, boolean addContextPath) {
		this.url = url;
		this.permanent = permanent;
		this.addContextPath = addContextPath;
	}

	protected RedirectController() {
	}

	/**
	 * Set this to true if the redirect should send the HTTP statuse code 301
	 * where only applicable. At current only GET and HEAD requests will send
	 * the 301, other methods will send the status depending on the http10compatible
	 * flag.
	 * @see #setHttp10Compatible
	 */
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	
	/**
	 * Set whether to stay compatible with HTTP 1.0 clients.
	 * <p>In the default implementation, this will enforce HTTP status code 302
	 * in any case, i.e. delegate to <code>HttpServletResponse.sendRedirect</code>.
	 * Turning this off will send HTTP status code 303, which is the correct
	 * code for HTTP 1.1 clients, but not understood by HTTP 1.0 clients.
	 * <p>Many HTTP 1.1 clients treat 302 just like 303, not making any
	 * difference. However, some clients depend on 303 when redirecting
	 * after a POST request; turn this flag off in such a scenario.
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect
	 */
	public void setHttp10Compatible(boolean http10Compatible) {
		this.http10Compatible = http10Compatible;
	}

	
	public void setAddContextPath(boolean contextRelative) {
		this.addContextPath = contextRelative;
	}

	/**
	 * Set the encoding to be used for parameter values.
	 */
	public void setEncodingScheme(String encodingScheme) {
		this.encodingScheme = encodingScheme;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		String destination = getDestination(request);
		if (destination == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		StringBuffer url = new StringBuffer();
		
		if (addContextPath && destination.startsWith("/")) {
			url.append(request.getContextPath());
		}
		url.append(destination);
		appendParameters(url, request);
		sendRedirect(request, response, url.toString());
		return null;
	}
	
	protected String getDestination(HttpServletRequest request) {
		return url;
	}
	
	@SuppressWarnings("unchecked")
	protected void appendParameters(StringBuffer targetUrl, 
			HttpServletRequest request)	throws UnsupportedEncodingException {

		boolean first = (targetUrl.indexOf("?") == -1);
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String name = (String) paramNames.nextElement();
			String[] values = request.getParameterValues(name);
			for (int i = 0; i < values.length; i++) {
				if (first) {
					targetUrl.append('?');
					first = false;
				}
				else {
					targetUrl.append('&');
				}
				targetUrl.append(name).append('=');
				if (values[i] != null) {
					targetUrl.append(URLEncoder.encode(values[i], encodingScheme));
				}
			}
		}
	}
	
	/**
	 * Send a redirect back to the HTTP client
	 * @param request current HTTP request (allows for reacting to request method)
	 * @param response current HTTP response (for sending response headers)
	 * @param targetUrl the target URL to redirect to
	 * @throws IOException if thrown by response methods
	 */
	protected void sendRedirect(HttpServletRequest request, 
			HttpServletResponse response, String targetUrl) throws IOException {
		
		if (permanent && isGetOrHeadRequest(request)) {
			// send status code 301 for GET or HEAD requests only
			response.setStatus(301);
			response.setHeader("Location", response.encodeRedirectURL(targetUrl));
		}
		else {
			if (http10Compatible) {
				// always send status code 302
				response.sendRedirect(response.encodeRedirectURL(targetUrl));
			}
			else {
				// correct HTTP status code is 303, in particular for POST requests
				response.setStatus(303);
				response.setHeader("Location", response.encodeRedirectURL(targetUrl));
			}
		}
	}
	
	private boolean isGetOrHeadRequest(HttpServletRequest request) {
		String method = request.getMethod();
		if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) {
			return true;
		}
		return false;
	}

}
