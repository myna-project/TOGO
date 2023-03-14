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
package it.mynaproject.togo.api.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "dashboards_users")
public class DashboardsUsers implements Serializable {

	@EmbeddedId
	private DashboardUserId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("dashboardId")
	private Dashboard dashboard;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	private User user;

	@Column(name = "default_dashboard")
	private boolean defaultDashboard;

	@SuppressWarnings("unused")
	private DashboardsUsers() {}

	public DashboardsUsers(Dashboard dashboard, User user) {
		this.dashboard = dashboard;
		this.user = user;
		this.id = new DashboardUserId(dashboard.getId(), user.getId());
	}

	public DashboardUserId getId() {
		return id;
	}

	public void setId(DashboardUserId id) {
		this.id = id;
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean getDefaultDashboard() {
		return defaultDashboard;
	}

	public void setDefaultDashboard(boolean defaultDashboard) {
		this.defaultDashboard = defaultDashboard;
	}
}
