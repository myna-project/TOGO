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

import it.mynaproject.togo.api.model.FormulaJson;

@Entity
@Table(name="formula")
public class Formula extends BaseDomain {

	@Column
	private String name;

	@ManyToOne
	@JoinColumn(name="org_id")
	private Org org;

	@ManyToOne
	@JoinColumn(name="client_id") 
	private Client client;

	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,orphanRemoval=true,mappedBy="formula")
	private List<FormulaComponent> components = new ArrayList<>();

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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public List<FormulaComponent> getComponents() {
		return components;
	}

	public void setComponents(List<FormulaComponent> components) {

		for (FormulaComponent newComponent : components)
			newComponent.setFormula(this);

		this.components = components;
	}

	public void populateFormulaFromInput(FormulaJson input, Org org, Client client) {

		this.setName(input.getName());
		this.setOrg(org);
		this.setClient(client);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Formula [name=");
		builder.append(name);
		builder.append(", org=");
		builder.append((org != null) ? org.getId() : null);
		builder.append(", client=");
		builder.append((client != null) ? client.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
