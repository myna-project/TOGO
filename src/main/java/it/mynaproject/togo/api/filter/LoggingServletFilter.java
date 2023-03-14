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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class LoggingServletFilter implements Filter {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString() != "anonymousUser") {
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			httpRequest.getSession().setAttribute("username", (user != null) ? user.getUsername() : "");
		}

		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);

		try {
			chain.doFilter(requestWrapper, response);
		} finally {
			if ("POST".equals(httpRequest.getMethod()) || "PUT".equals(httpRequest.getMethod())) {
				String requestBody = new String(requestWrapper.getContentAsByteArray());
				log.debug("Request body: {}", requestBody);
			}
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}
}
