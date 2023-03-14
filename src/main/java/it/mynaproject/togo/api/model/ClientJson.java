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
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.mynaproject.togo.api.domain.ClientType;

@JsonInclude(Include.NON_EMPTY)
public class ClientJson {

	private Integer id;

	@NotBlank(message = "Please provide a name!")
	private String name;

	@JsonProperty("category_id")
	private Integer categoryId;

	@JsonProperty("parent_id")
	private Integer parentId;

	@JsonProperty("controller_id")
	private Integer controllerId;

	@NotNull(message = "Please provide an org id!")
	@JsonProperty("org_id")
	private Integer orgId;

	@NotNull(message = "Please provide a type!")
	private ClientType type;

	@JsonProperty("computer_client")
	private Boolean computerClient;

	@JsonProperty("energy_client")
	private Boolean energyClient;

	@JsonProperty("device_id")
	private String deviceId;

	@JsonProperty("plugin_id")
	private String pluginId;

	private String image;

	private Boolean active;

	@JsonProperty("child_ids")
	private List<Integer> childIds = new ArrayList<>();

	@JsonProperty("controlled_ids")
	private List<Integer> controlledIds = new ArrayList<>();

	@JsonProperty("feed_ids")
	private List<Integer> feedIds = new ArrayList<>();

	@JsonProperty("default_drain_ids")
	private List<Integer> defaultDrainIds = new ArrayList<>();

	@JsonProperty("formula_ids")
	private List<Integer> formulaIds = new ArrayList<>();

	private Boolean alert;

	private Boolean alarm;

	private Boolean warning;

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

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getControllerId() {
		return controllerId;
	}

	public void setControllerId(Integer controllerId) {
		this.controllerId = controllerId;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<Integer> getChildIds() {
		return childIds;
	}

	public void setChildIds(List<Integer> childIds) {
		this.childIds = childIds;
	}

	public List<Integer> getControlledIds() {
		return controlledIds;
	}

	public void setControlledIds(List<Integer> controlledIds) {
		this.controlledIds = controlledIds;
	}

	public List<Integer> getFeedIds() {
		return feedIds;
	}

	public void setFeedIds(List<Integer> feedIds) {
		this.feedIds = feedIds;
	}

	public List<Integer> getDefaultDrainIds() {
		return defaultDrainIds;
	}

	public void setDefaultDrainIds(List<Integer> defaultDrainIds) {
		this.defaultDrainIds = defaultDrainIds;
	}

	public List<Integer> getFormulaIds() {
		return formulaIds;
	}

	public void setFormulaIds(List<Integer> formulaIds) {
		this.formulaIds = formulaIds;
	}

	public Boolean getAlert() {
		return alert;
	}

	public void setAlert(Boolean alert) {
		this.alert = alert;
	}

	public Boolean getAlarm() {
		return alarm;
	}

	public void setAlarm(Boolean alarm) {
		this.alarm = alarm;
	}

	public Boolean getWarning() {
		return warning;
	}

	public void setWarning(Boolean warning) {
		this.warning = warning;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientJson [name=");
		builder.append(name);
		builder.append(", categoryId=");
		builder.append(categoryId);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append(", controllerId=");
		builder.append(controllerId);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", pluginId=");
		builder.append(pluginId);
		builder.append(", computerClient=");
		builder.append(computerClient);
		builder.append(", energyClient=");
		builder.append(energyClient);
		builder.append(", active=");
		builder.append(active);
		builder.append(", alert=");
		builder.append(alert);
		builder.append(", alarm=");
		builder.append(alarm);
		builder.append(", warning=");
		builder.append(warning);
		builder.append("]");
		return builder.toString();
	}
}
