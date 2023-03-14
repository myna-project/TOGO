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

import it.mynaproject.togo.api.model.OrgJson;

@Entity
@Table(name="org")
public class Org extends BaseDomain {

	@Column(nullable=false)
	private String name;

	@ManyToOne
	@JoinColumn(name="parent_id")
	private Org parent;

	@OneToMany
	@JoinColumn(name="org_id")
	private List<Client> clients;

	@OneToMany
	@JoinColumn(name="org_id")
	private List<Job> jobs;

	@OneToMany
	@JoinColumn(name="org_id")
	private List<Dashboard> dashboards;

	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE, CascadeType.REFRESH},mappedBy="parent")
	private List<Org> childList = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Org getParent() {
		return parent;
	}

	public void setParent(Org parent) {
		this.parent = parent;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public List<Dashboard> getDashboards() { return dashboards; }

	public void setDashboards(List<Dashboard> dashboards) { this.dashboards = dashboards; }

	public List<Org> getChildList() {
		return this.childList;
	}

	public void setChildList(List<Org> orgs) {

		for (Org newChild : orgs)
			newChild.setParent(this);

		this.childList = orgs;
	}

	public void populateOrgFromInput (OrgJson input, Org parent) {

		this.setName(input.getName());
		this.setParent(parent);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Org [name=");
		builder.append(name);
		builder.append(", parent=");
		builder.append((parent != null) ? parent.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
