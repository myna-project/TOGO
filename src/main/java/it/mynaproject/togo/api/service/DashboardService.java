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

import it.mynaproject.togo.api.domain.Dashboard;
import it.mynaproject.togo.api.model.DashboardJson;

public interface DashboardService {

	public Dashboard getDashboard(Integer id, Boolean isAdmin, String username);
	public List<Dashboard> getDashboards();
	public List<Dashboard> getDashboardsForUser(String username);
	public void persist(Dashboard dashboard);
	public Dashboard createDashboardFromInput(DashboardJson input, Boolean isAdmin, String username);
	public void update(Dashboard dashboard);
	public Dashboard updateDashboardFromInput(Integer id, DashboardJson input, Boolean isAdmin, String username);
	public void deleteDashboardById(Integer dahboardId,Boolean isAdmin, String username);
}
