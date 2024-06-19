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

import it.mynaproject.togo.api.domain.DashboardWidgetDetail;

public interface DashboardWidgetDetailService {

	public void persist(DashboardWidgetDetail detail);
	public void update(DashboardWidgetDetail detail);
	public void delete(DashboardWidgetDetail detail);
}
