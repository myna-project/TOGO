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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.ClientJson;

@Entity
@Table(name="client")
public class Client extends BaseDomain {

	@Column(nullable=false)
	private String name;

	@ManyToOne
	@JoinColumn(name="client_category_id")
	private ClientCategory category;

	@ManyToOne
	@JoinColumn(name="parent_id")
	private Client parent;

	@ManyToOne
	@JoinColumn(name="controller_id")
	private Client controller;

	@ManyToOne
	@JoinColumn(name="org_id",nullable=false)
	private Org org;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private ClientType type = ClientType.GENERIC;

	@Column(name="computer_client")
	private Boolean computerClient;

	@Column(name="energy_client")
	private Boolean energyClient;

	@Column(name="device_id")
	private String deviceId;

	@Column(name="plugin_id")
	private String pluginId;

	@Column
	private byte[] image;

	@Column
	private Boolean active;

	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.REFRESH},mappedBy="parent")
	private List<Client> childList = new ArrayList<>();

	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.REFRESH},mappedBy="controller")
	private List<Client> controlledList = new ArrayList<>();

	@ManyToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.REFRESH},mappedBy="clients")
	private List<Feed> feedList = new ArrayList<>();

	@OneToMany(fetch=FetchType.LAZY,mappedBy="client")
	private List<Formula> formulas = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClientCategory getCategory() {
		return category;
	}

	public void setCategory(ClientCategory category) {
		this.category = category;
	}

	public Client getParent() {
		return parent;
	}

	public void setParent(Client parent) {
		this.parent = parent;
	}

	public Client getController() {
		return controller;
	}

	public void setController(Client controller) {
		this.controller = controller;
	}

	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
	}

	public ClientType getType() {
		return type;
	}

	public void setType(ClientType type) {
		this.type = type;
	}

	public Boolean getComputerClient() {
		return computerClient;
	}

	public void setComputerClient(Boolean computerClient) {
		this.computerClient = computerClient;
	}

	public Boolean getEnergyClient() {
		return energyClient;
	}

	public void setEnergyClient(Boolean energyClient) {
		this.energyClient = energyClient;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<Feed> getFeeds() {
		return this.feedList;
	}

	public void setFeeds(List<Feed> feeds) {

		for (Feed newFeed : feeds)
			newFeed.addClient(this);

		this.feedList = feeds;
	}

	public List<Client> getChildList() {
		return this.childList;
	}

	public void setChildList(List<Client> clients) {

		for (Client newChild : clients)
			newChild.setParent(this);

		this.childList = clients;
	}

	public List<Client> getControlledList() {
		return this.controlledList;
	}

	public void setControlledList(List<Client> clients) {

		for (Client newControlled : clients)
			newControlled.setController(this);

		this.controlledList = clients;
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
	}

	public void populateClientFromInput(ClientJson input, ClientCategory category, Org org, Client controller, Client parent, List<Client> children, List<Client> controlledList, List<Feed> feeds, Boolean editable) {

		if (editable) {
			this.setName(input.getName());
			this.setOrg(org);
			this.setDeviceId(input.getDeviceId());
			this.setType(input.getType());
			this.setControlledList(controlledList);
			this.setFeeds(feeds);
			this.setPluginId(input.getPluginId());
			this.setController(controller);
			this.setActive(input.getActive());
		}
		this.setCategory(category);
		this.setComputerClient(input.getComputerClient());
		this.setEnergyClient(input.getEnergyClient());
		this.setChildList(children);
		if (input.getImage() != null)
			this.setImage(Base64.getDecoder().decode(input.getImage()));
		this.setParent(parent);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Client [name=");
		builder.append(name);
		builder.append(", category=");
		builder.append((category != null) ? category.getId() : null);
		builder.append(", parent=");
		builder.append((parent != null) ? parent.getId() : null);
		builder.append(", controller=");
		builder.append((controller != null) ? controller.getId() : null);
		builder.append(", org=");
		builder.append((org != null) ? org.getId() : null);
		builder.append(", type=");
		builder.append(type);
		builder.append(", computerClient=");
		builder.append(computerClient);
		builder.append(", energyClient=");
		builder.append(energyClient);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", pluginId=");
		builder.append(pluginId);
		builder.append(", active=");
		builder.append(active);
		builder.append("]");
		return builder.toString();
	}
}
