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
package it.mynaproject.togo.api.model;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardJson {

	private Integer id;

	@NotBlank(message = "Please provide a name")
	private String name;

	@NotNull(message = "Please provide an org id!")
	@JsonProperty("org_id")
	private Integer orgId;

	@NotNull(message = "Please provide user ids!")
	@Size(min=1, message = "Please provide at least a user id!")
	@JsonProperty("user_ids")
	private List<Integer> userIds = null;

	@JsonProperty("default")
	private Boolean defaultDashboard;

	@JsonProperty("duplicate_dashboard_id")
	private Integer duplicateDashboardId;

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public List<Integer> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}

	public Boolean getDefaultDashboard() {
		return defaultDashboard;
	}

	public void setDefaultDashboard(Boolean defaultDashboard) {
		this.defaultDashboard = defaultDashboard;
	}

	public Integer getDuplicateDashboardId() {
		return duplicateDashboardId;
	}

	public void setDuplicateDashboardId(Integer duplicateDashboardId) {
		this.duplicateDashboardId = duplicateDashboardId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("DashboardJson[");
		builder.append("id=").append(id);
		builder.append(", name='").append(name).append('\'');
		builder.append(", orgId=").append(orgId);
		builder.append(", userIds=");
		builder.append(userIds);
		builder.append(']');
		return builder.toString();
	}
}
