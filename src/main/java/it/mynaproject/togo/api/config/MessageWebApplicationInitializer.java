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
package it.mynaproject.togo.api.config;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import it.mynaproject.togo.api.filter.LoggingServletFilter;

public class MessageWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { WebMvcConfiguration.class, HibernateConfig.class, SecurityConfig.class, DrainControlSchedulerConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setAsyncSupported(true);
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new LoggingServletFilter() };
	}
}
