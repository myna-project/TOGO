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
package it.mynaproject.togo.api.dao;

import java.util.List;

import it.mynaproject.togo.api.domain.DashboardWidgetDetail;

public interface DashboardWidgetDetailDao {

	public void persist(DashboardWidgetDetail detail);
	public void update(DashboardWidgetDetail detail);
	public void delete(DashboardWidgetDetail detail);
	public DashboardWidgetDetail getDashboardWidgetDetail(Integer dashboardWidgetid);
	public List<DashboardWidgetDetail> getDashboardWidgetDetails();
}
