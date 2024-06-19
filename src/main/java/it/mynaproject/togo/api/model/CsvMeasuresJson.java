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

import java.util.Date;
import java.util.ArrayList;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsvMeasuresJson {

	@NotNull(message = "Please provide measures!")
	@JsonProperty("measures")
	ArrayList<CsvMeasureJson> measures;

	@NotNull(message = "Please provide a client id!")
	@JsonProperty("client_id")
	Integer clientId;

	@NotNull(message = "Please provide a device id!")
	@JsonProperty("device_id")
	String deviceId;

	@NotNull(message = "Please provide a timestamp!")
	@JsonProperty("at")
	Date at;

	public Date getAt() {
		return at;
	}

	public void setAt(Date at) {
		this.at = at;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public ArrayList<CsvMeasureJson> getMeasures() {
		return measures;
	}

	public void setMeasures(ArrayList<CsvMeasureJson> measures){
		this.measures = measures;
	}

	public void addMeasure(CsvMeasureJson measure) {
		this.measures.add(measure);
	}
}
