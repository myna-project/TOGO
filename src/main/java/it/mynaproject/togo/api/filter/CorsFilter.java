/*******************************************************************************
 * Copyright (c) Myna-Project SRL <info@myna-project.org>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 * - Myna-Project SRL <info@myna-project.org> - initial API and implementation
 ******************************************************************************/
package it.mynaproject.togo.api.filter;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.mynaproject.togo.api.util.PropertiesFileReader;

public class CorsFilter implements Filter {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		Properties prop = PropertiesFileReader.loadPropertiesFile();

		String origin = null;
		if (httpResponse.getHeader("Access-Control-Allow-Origin") == null) {
			String remote = httpRequest.getHeader("Origin");
			if (prop.getProperty("originAllowed").equals("*")) {
				origin = remote;
			} else {
				String[] allowed = prop.getProperty("originAllowed").split(",");
				for (String allow : allowed) {
					if (allow.equals(remote)) {
						origin = allow;
						break;
					}
				}
			}
		}

		if (httpRequest.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(httpRequest.getMethod())) {
			log.debug("CORS pre-flight request");

			// CORS "pre-flight" request
			if (httpResponse.getHeader("Access-Control-Allow-Origin") == null) {
				httpResponse.addHeader("Access-Control-Allow-Origin", origin);
				httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
				httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept, x-csrf-token, x-login-ajax-call");
				httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
				httpResponse.addHeader("Access-Control-Expose-Headers", "x-csrf-token");
				httpResponse.addHeader("Access-Control-Max-Age", "1800");
			}

			httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		} else {
			if (httpResponse.getHeader("Access-Control-Allow-Origin") == null) {
				httpResponse.addHeader("Access-Control-Allow-Origin", origin);
				httpResponse.addHeader("Access-Control-Expose-Headers", "x-csrf-token, isAdmin");
				httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
			}
		}

		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
