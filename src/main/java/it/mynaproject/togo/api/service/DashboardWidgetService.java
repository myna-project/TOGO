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
package it.mynaproject.togo.api.service;

import java.util.List;

import it.mynaproject.togo.api.domain.DashboardWidget;
import it.mynaproject.togo.api.model.DashboardWidgetJson;

public interface DashboardWidgetService {

	public DashboardWidget getDashboardWidget(Integer id, Integer dashboardId, String username);
	public List<DashboardWidget> getDashboardWidgets(Integer dashboardId, String username);
	public void persist(DashboardWidget widget);
	public DashboardWidget createDashboardWidgetFromInput(Integer dashboardId, DashboardWidgetJson input, Boolean isAdmin, String username);
	public void update(DashboardWidget widget);
	public DashboardWidget updateDashboardWidgetFromInput(Integer id, Integer dashboardId, DashboardWidgetJson input, Boolean isAdmin, String username);
	public void deleteDashboardWidgetById(Integer id, Integer dashboardId, Boolean isAdmin, String username);
}
