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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class UserJson {

	private Integer id;

	@NotBlank(message = "Please provide an username!")
	private String username;

	private String name;

	private String surname;

	@JsonProperty("old_password")
	private String oldPassword;

	private String password;

	@NotNull(message = "Please provide if is enabled or not!")
	private Boolean enabled;

	@Email(message = "Please provide a valid email!")
	@NotNull(message = "Please provide an email!")
	private String email;

	private String avatar;

	private String style;

	private String lang;

	@JsonProperty("role_ids")
	private List<Integer> roleIds = new ArrayList<>();

	@JsonProperty("job_ids")
	private List<Integer> jobIds = new ArrayList<>();

	@JsonProperty("default_dashboard_id")
	private Integer defaultDashboardId;

	@JsonProperty("dashboard_ids")
	private List<Integer> dashboardIds = new ArrayList<>();

	@JsonProperty("default_start")
	private Date defaultStart;

	@JsonProperty("default_end")
	private Date defaultEnd;

	@JsonProperty("drain_tree_depth")
	private String drainTreeDepth;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean b) {
		this.enabled = b;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public List<Integer> getJobIds() {
		return jobIds;
	}

	public void setJobIds(List<Integer> list) {
		this.jobIds = list;
	}

	public List<Integer> getDashboardIds() {
		return dashboardIds;
	}

	public void setDashboardIds(List<Integer> list) {
		this.dashboardIds = list;
	}

	public Integer getDefaultDashboardId() {
		return defaultDashboardId;
	}

	public void setDefaultDashboardId(Integer defaultDashboardId) {
		this.defaultDashboardId = defaultDashboardId;
	}

	public Date getDefaultStart() {
		return defaultStart;
	}

	public void setDefaultStart(Date defaultStart) {
		this.defaultStart = defaultStart;
	}

	public Date getDefaultEnd() {
		return defaultEnd;
	}

	public void setDefaultEnd(Date defaultEnd) {
		this.defaultEnd = defaultEnd;
	}

	public String getDrainTreeDepth() {
		return drainTreeDepth;
	}

	public void setDrainTreeDepth(String drainTreeDepth) {
		this.drainTreeDepth = drainTreeDepth;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserJson [username=");
		builder.append(username);
		builder.append(", name=");
		builder.append(name);
		builder.append(", surname=");
		builder.append(surname);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", style=");
		builder.append(style);
		builder.append(", email=");
		builder.append(email);
		builder.append(", lang=");
		builder.append(lang);
		builder.append(", roles=");
		builder.append(roleIds);
		builder.append(", jobs=");
		builder.append(jobIds);
		builder.append(", dashboards=");
		builder.append(dashboardIds);
		builder.append("]");
		return builder.toString();
	}
}
