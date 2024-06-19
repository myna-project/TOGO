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

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ConfigMeasureJson {

	@NotBlank(message = "Please provide a client id!")
	@JsonProperty("client_id")
	private String clientId;

	@NotBlank(message = "Please provide a drain name!")
	@JsonProperty("measure_descr")
	private String drainName;

	@NotBlank(message = "Please provide a device id!")
	@JsonProperty("device_id")
	private String deviceId;

	@NotBlank(message = "Please provide a measure id!")
	@JsonProperty("measure_id")
	private String measureId;

	@NotBlank(message = "Please provide a client descr!")
	@JsonProperty("device_descr")
	private String clientName;

	@JsonProperty("measure_type")
	private String measureType;

	@JsonProperty("plugin_id")
	private String pluginId;

	@JsonProperty("measure_unit")
	private String unitOfMeasure;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getDrainName() {
		return drainName;
	}

	public void setDrainName(String drainName) {
		this.drainName = drainName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMeasureId() {
		return measureId;
	}

	public void setMeasureId(String measureId) {
		this.measureId = measureId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigMeasureJson [clientId=");
		builder.append(clientId);
		builder.append(", drainName=");
		builder.append(drainName);
		builder.append(", deviceId=");
		builder.append(deviceId);
		builder.append(", measureId=");
		builder.append(measureId);
		builder.append(", clientName=");
		builder.append(clientName);
		builder.append(", measureType=");
		builder.append(measureType);
		builder.append(", pluginId=");
		builder.append(pluginId);
		builder.append(", unitOfMeasure=");
		builder.append(unitOfMeasure);
		builder.append("]");
		return builder.toString();
	}
}
