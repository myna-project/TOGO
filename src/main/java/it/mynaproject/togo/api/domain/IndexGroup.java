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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.IndexGroupJson;

@Entity
@Table(name="index_group")
public class IndexGroup extends BaseDomain {

	@Column
	private String name;

	@ManyToOne
	@JoinColumn(name="org_id")
	private Org org;

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

	public void populateIndexGroupFromInput(IndexGroupJson input, Org org) {

		this.setName(input.getName());
		this.setOrg(org);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndexGroup [name=");
		builder.append(name);
		builder.append(", org=");
		builder.append((org != null) ? org.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
