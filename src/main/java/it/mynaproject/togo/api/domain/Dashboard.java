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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.DashboardJson;

@Entity
@Table(name="dashboard")
public class Dashboard extends BaseDomain {

	@Column(unique = true)
	private String name;

	@ManyToOne
	@JoinColumn(name="org_id", nullable=false)
	private Org org;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true, mappedBy = "dashboard")
	private List<DashboardsUsers> dashboardsUsers = new ArrayList<>();

	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.MERGE,CascadeType.REFRESH, CascadeType.REMOVE}, mappedBy="dashboard")
	private List<DashboardWidget> dashboardWidgets = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}

	public List<DashboardsUsers> getDashboardsUsers() {
		return dashboardsUsers;
	}

	public void setDashboardsUsers(List<DashboardsUsers> dashboardsUsers) {
		this.dashboardsUsers = dashboardsUsers;
	}

	public List<DashboardWidget> getDashboardWidgets() {
		return dashboardWidgets;
	}

	public void setDashboardWidgets(List<DashboardWidget> dashboardWidgets) {
		this.dashboardWidgets = dashboardWidgets;
	}

	public void addDashboardUser(DashboardsUsers dashboardsUsers) {
		this.dashboardsUsers.add(dashboardsUsers);
	}

	public void removeDashboardUser(DashboardsUsers dashboardsUsers) {
		this.dashboardsUsers.remove(dashboardsUsers);
	}

	public void updateDashboardUser(DashboardsUsers duNew) {

		int index = 0;
		int i = 0;
		for (DashboardsUsers du : this.dashboardsUsers) {
			if (du.getDashboard().getId() == duNew.getDashboard().getId() && du.getUser().getId() == duNew.getUser().getId())
				index = i;
			i++;
		}
		this.dashboardsUsers.set(index, duNew);
	}

	public void populateDashboardFromInput(DashboardJson input, Org org) {

		this.setName(input.getName());
		this.setOrg(org);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("Dashboard [");
		builder.append("name='").append(name).append('\'');
		builder.append(", org=").append(org);
		builder.append(']');
		return builder.toString();
	}
}
