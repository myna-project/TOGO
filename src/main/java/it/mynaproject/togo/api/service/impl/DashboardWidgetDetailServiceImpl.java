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
package it.mynaproject.togo.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.DashboardWidgetDetailDao;
import it.mynaproject.togo.api.domain.DashboardWidgetDetail;
import it.mynaproject.togo.api.service.DashboardWidgetDetailService;

@Service
public class DashboardWidgetDetailServiceImpl implements DashboardWidgetDetailService {

	@Autowired
	private DashboardWidgetDetailDao dashboardWidgetDetailDao;

	@Override
	@Transactional
	public void persist(DashboardWidgetDetail detail) {
		this.dashboardWidgetDetailDao.persist(detail);
	}

	@Override
	@Transactional
	public void update(DashboardWidgetDetail detail) {
		this.dashboardWidgetDetailDao.update(detail);
	}

	@Override
	@Transactional
	public void delete(DashboardWidgetDetail detail) {
		this.dashboardWidgetDetailDao.delete(detail);
	}
}
