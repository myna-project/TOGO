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

@JsonInclude(Include.NON_EMPTY)
public class DrainJson {

	private Integer id;

	@NotNull(message = "Please provide a feed id!")
	@JsonProperty("feed_id")
	private Integer feedId;

	@NotBlank(message = "Please provide a name!")
	private String name;

	@NotBlank(message = "Please provide a measure id!")
	@JsonProperty("measure_id")
	private String measureId;

	@JsonProperty("measure_unit")
	private String unitOfMeasure;

	private String type;

	@NotBlank(message = "Please provide a type!")
	@JsonProperty("measure_type")
	private String measureType;

	private Integer decimals;

	@JsonProperty("client_default_drain")
	private Boolean clientDefaultDrain;

	@JsonProperty("positive_negative_value")
	private Boolean positiveNegativeValue;

	@JsonProperty("base_drain_id")
	private Integer baseDrainId;

	private Float coefficient;

	@JsonProperty("diff_drain_id")
	private Integer diffDrainId;

	private List<DrainControlDetailJson> controls = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFeedId() {
		return feedId;
	}

	public void setFeedId(Integer feedId) {
		this.feedId = feedId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMeasureId() {
		return measureId;
	}

	public void setMeasureId(String measureId) {
		this.measureId = measureId;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public Boolean getClientDefaultDrain() {
		return clientDefaultDrain;
	}

	public void setClientDefaultDrain(Boolean clientDefaultDrain) {
		this.clientDefaultDrain = clientDefaultDrain;
	}

	public Boolean getPositiveNegativeValue() {
		return positiveNegativeValue;
	}

	public void setPositiveNegativeValue(Boolean positiveNegativeValue) {
		this.positiveNegativeValue = positiveNegativeValue;
	}

	public Integer getBaseDrainId() {
		return baseDrainId;
	}

	public void setBaseDrainId(Integer baseDrainId) {
		this.baseDrainId = baseDrainId;
	}

	public Float getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Float coefficient) {
		this.coefficient = coefficient;
	}

	public Integer getDiffDrainId() {
		return diffDrainId;
	}

	public void setDiffDrainId(Integer diffDrainId) {
		this.diffDrainId = diffDrainId;
	}

	public List<DrainControlDetailJson> getControls() {
		return controls;
	}

	public void setControls(List<DrainControlDetailJson> controls) {
		this.controls = controls;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DrainJson [id=");
		builder.append(id);
		builder.append(", feedId=");
		builder.append(feedId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", measureId=");
		builder.append(measureId);
		builder.append(", unit of measure=");
		builder.append(unitOfMeasure);
		builder.append(", type=");
		builder.append(type);
		builder.append(", decimals=");
		builder.append(decimals);
		builder.append(", clientDefaultDrain=");
		builder.append(clientDefaultDrain);
		builder.append(", positiveNegativeValue=");
		builder.append(positiveNegativeValue);
		builder.append(", baseDrainId=");
		builder.append(baseDrainId);
		builder.append(", coefficient=");
		builder.append(coefficient);
		builder.append(", diffDrainId=");
		builder.append(diffDrainId);
		builder.append("]");
		return builder.toString();
	}
}
