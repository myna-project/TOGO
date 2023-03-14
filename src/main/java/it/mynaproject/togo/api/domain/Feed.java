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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.FeedJson;

@Entity
@Table(name="feed")
public class Feed extends BaseDomain {

	@Column
	private String description;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="feeds_clients", joinColumns = { @JoinColumn(name="feed_id",nullable=false,updatable=false) }, inverseJoinColumns = { @JoinColumn(name="client_id",nullable=false,updatable=false) })
	private List<Client> clients = new ArrayList<>();

	@OneToMany(fetch=FetchType.LAZY,mappedBy="feed")
	private List<Drain> drains = new ArrayList<>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients){
		this.clients = clients;
	}

	public void addClient(Client client) {

		if (!this.getClients().contains(client))
			this.clients.add(client);
	}

	public void removeClient(Client client) {
		this.clients.remove(client);
	}

	public List<Drain> getDrains() {
		return drains;
	}

	public void setDrains(List<Drain> drains) {
		this.drains = drains;
	}

	public void populateFeedFromInput(FeedJson input, List<Client> clients) {

		this.setDescription(input.getDescription());
		this.setClients(clients);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Feed [description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
}
