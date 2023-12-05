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
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.UserJson;

@Entity
@Table(name="application_user")
public class User extends BaseDomain {

	@Column(nullable=false,unique=true)
	private String username;

	private String name;

	private String surname;

	@Column(nullable=false)
	private String password;

	@Column(nullable=false)
	private Integer enabled;

	@Column(nullable=false,unique=true)
	private String email;

	@Column
	private byte[] avatar;

	@Column
	private String style;

	@Column
	private String lang;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	private List<DashboardsUsers> dashboardsUsers = new ArrayList<>();

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="users_roles",joinColumns={ @JoinColumn(name="user_id", nullable=false, updatable=false) },inverseJoinColumns={ @JoinColumn(name="role_id", nullable=false, updatable=false) })
	private List<Role> roleList = new ArrayList<>();

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="users_jobs",joinColumns={ @JoinColumn(name="user_id", nullable=false, updatable=false) },inverseJoinColumns={ @JoinColumn(name="job_id", nullable=false, updatable=false) })
	private List<Job> jobList = new ArrayList<>();

	@Column(name="default_start")
	private Date defaultStart;

	@Column(name="default_end")
	private Date defaultEnd;

	@Column(name="drain_tree_depth",nullable=false)
	private String drainTreeDepth;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
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

	public List<Role> getRoles() {
		return this.roleList;
	}

	public void setRoles(List<Role> roles) {
		this.roleList = roles;
	}

	public List<Job> getJobs() {
		return jobList;
	}

	public void setJobs(List<Job> jobList) {
		this.jobList = jobList;
	}

	public void addJob(Job job) {
		this.jobList.add(job);
	}

	public void removeJob(Job job){
		this.jobList.remove(job);
	}

	public List<DashboardsUsers> getDashboardsUsers() {
		return dashboardsUsers;
	}

	public void setDashboardsUsers(List<DashboardsUsers> dashboardsUsers) {
		this.dashboardsUsers = dashboardsUsers;
	}

	public void updateDashboardUser(DashboardsUsers duNew, Integer index) {
		this.dashboardsUsers.set(index,duNew);
	}

	public void removeDashboardUser(DashboardsUsers dashboardsUsers) {
		this.dashboardsUsers.remove(dashboardsUsers);
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

	public void populateUserFromInput(UserJson input, List<Role> roles) {

		this.setUsername(input.getUsername());
		this.setName(input.getName());
		this.setSurname(input.getSurname());
		if (input.getPassword() != null)
			this.setPassword(input.getPassword());
		if (input.getAvatar() != null)
			this.setAvatar(Base64.getDecoder().decode(input.getAvatar()));
		this.setStyle(input.getStyle());
		this.setEmail(input.getEmail());
		this.setLang(input.getLang());
		this.setEnabled((input.getEnabled() != null) ? (input.getEnabled () ? 1 : 0) : 0);
		this.setRoles(roles);
		this.setDefaultStart(input.getDefaultStart());
		this.setDefaultEnd(input.getDefaultEnd());
		this.setDrainTreeDepth(input.getDrainTreeDepth());
	}
}
