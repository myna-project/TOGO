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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.JobJson;

@Entity
@Table(name="job")
public class Job extends BaseDomain {

	@Column(nullable=false)
	private String name;

	@Column(nullable=false)
	private String description;

	@ManyToOne
	@JoinColumn(name="org_id",nullable=false)
	private Org org;

	@ManyToMany(fetch=FetchType.LAZY,cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},mappedBy="jobList")
	private List<User> userList = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}

	public List<User> getUsers() {
		return userList;
	}

	public void setUsers(List<User> userList) {
		this.userList = userList;
	}

	public void populateJobFromInput(JobJson input, Org org) {

		this.setName(input.getName());
		this.setDescription(input.getDescription());
		this.setOrg(org);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Job [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", org=");
		builder.append((org != null) ? org.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
