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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.ClientCategory;
import it.mynaproject.togo.api.domain.Dashboard;
import it.mynaproject.togo.api.domain.DashboardsUsers;
import it.mynaproject.togo.api.domain.DashboardUserId;
import it.mynaproject.togo.api.domain.DashboardWidget;
import it.mynaproject.togo.api.domain.DashboardWidgetDetail;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.DrainControl;
import it.mynaproject.togo.api.domain.DrainControlDetail;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.domain.Index;
import it.mynaproject.togo.api.domain.IndexComponent;
import it.mynaproject.togo.api.domain.IndexGroup;
import it.mynaproject.togo.api.domain.InvoiceItemkWh;
import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.domain.Role;
import it.mynaproject.togo.api.domain.TimeSlot;
import it.mynaproject.togo.api.domain.TimeSlotDetail;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.domain.Vendor;
import it.mynaproject.togo.api.domain.impl.MeasureBitfield;
import it.mynaproject.togo.api.domain.impl.MeasureDouble;
import it.mynaproject.togo.api.domain.impl.MeasureString;

@Configuration
@EnableTransactionManagement
@ComponentScans(value = { @ComponentScan("it.mynaproject.togo.api.dao"), @ComponentScan("it.mynaproject.togo.api.service") })
public class HibernateConfig {

	@Autowired
	private ApplicationContext context;

	@Bean
	public LocalSessionFactoryBean getSessionFactory() {
		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();

		factoryBean.setConfigLocation(context.getResource("classpath:hibernate.xml"));
		factoryBean.setAnnotatedClasses(
			Client.class,
			ClientCategory.class,
			Dashboard.class,
			DashboardWidget.class,
			DashboardWidgetDetail.class,
			DashboardsUsers.class,
			DashboardUserId.class,
			Drain.class,
			DrainControl.class,
			DrainControlDetail.class,
			Feed.class,
			Formula.class,
			FormulaComponent.class,
			Index.class,
			IndexComponent.class,
			IndexGroup.class,
			InvoiceItemkWh.class,
			Job.class,
			MeasureBitfield.class,
			MeasureDouble.class,
			MeasureString.class,
			Org.class,
			Role.class,
			TimeSlot.class,
			TimeSlotDetail.class,
			User.class,
			Vendor.class
		);
		return factoryBean;
	}

	@Bean
	public HibernateTransactionManager getTransactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(getSessionFactory().getObject());
		return transactionManager;
	}
}
