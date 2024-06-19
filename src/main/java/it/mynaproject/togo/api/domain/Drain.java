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
import javax.persistence.OneToOne;

import it.mynaproject.togo.api.model.DrainJson;

@Entity
@Table(name="drain")
public class Drain extends BaseDomain {

	@ManyToOne
	@JoinColumn(name="feed_id") 
	private Feed feed;

	@Column(nullable=false)
	private String name;

	@Column(name="measure_id")
	private String measureId;

	@Column(name="unit_of_measure")
	private String unitOfMeasure;

	@Column(name="type")
	private String type;

	@Column(name="measure_type")
	private String measureType;

	@Column
	private Integer decimals;

	@Column(name="client_default_drain")
	private Boolean clientDefaultDrain;

	@Column(name="min_value")
	private Double minValue;

	@Column(name="max_value")
	private Double maxValue;

	@Column(name="positive_negative_value")
	private Boolean positiveNegativeValue;

	@ManyToOne
	@JoinColumn(name="base_drain_id")
	private Drain baseDrain;

	@Column(name="coefficient")
	private Float coefficient;

	@OneToOne
	@JoinColumn(name="diff_drain_id")
	private Drain diffDrain;

	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.REFRESH},mappedBy="drain")
	private List<DrainControlDetail> controls = new ArrayList<>();

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
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

	public void setMeasureType(String measure_type) {
		this.measureType = measure_type;
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

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Boolean getPositiveNegativeValue() {
		return positiveNegativeValue;
	}

	public void setPositiveNegativeValue(Boolean positiveNegativeValue) {
		this.positiveNegativeValue = positiveNegativeValue;
	}

	public Drain getBaseDrain() {
		return baseDrain;
	}

	public void setBaseDrain(Drain baseDrain) {
		this.baseDrain = baseDrain;
	}

	public Float getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Float coefficient) {
		this.coefficient = coefficient;
	}

	public Drain getDiffDrain() {
		return diffDrain;
	}

	public void setDiffDrain(Drain diffDrain) {
		this.diffDrain = diffDrain;
	}

	public List<DrainControlDetail> getControls() {
		return controls;
	}

	public void setControls(List<DrainControlDetail> controls) {
		this.controls = controls;
	}

	public void populateDrainFromInput(DrainJson input, Drain baseDrain, Drain diffDrain, Feed feed, Boolean editable) {

		if (editable) {
			this.setMeasureId(input.getMeasureId());
			this.setName(input.getName());
			this.setUnitOfMeasure(input.getUnitOfMeasure());
			this.setMeasureType(input.getMeasureType());
		}
		this.setFeed(feed);
		this.setDecimals(input.getDecimals());
		this.setType(input.getType());
		this.setClientDefaultDrain((input.getClientDefaultDrain() != null) ? input.getClientDefaultDrain() : false);
		this.setMaxValue(input.getMaxValue());
		this.setMinValue(input.getMinValue());
		this.setPositiveNegativeValue(input.getPositiveNegativeValue());
		this.setBaseDrain(baseDrain);
		this.setCoefficient(input.getCoefficient());
		this.setDiffDrain(diffDrain);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Drain [feed=");
		builder.append((feed != null) ? feed.getId() : null);
		builder.append(", name=");
		builder.append(name);
		builder.append(", measureId=");
		builder.append(measureId);
		builder.append(", unitOfMeasure=");
		builder.append(unitOfMeasure);
		builder.append(", type=");
		builder.append(type);
		builder.append(", measureType=");
		builder.append(measureType);
		builder.append(", decimals=");
		builder.append(decimals);
		builder.append(", clientDefaultDrain=");
		builder.append(clientDefaultDrain);
		builder.append(", minValue=");
		builder.append(minValue);
		builder.append(", maxValue=");
		builder.append(maxValue);
		builder.append(", positiveNegativeValue=");
		builder.append(positiveNegativeValue);
		builder.append(", baseDrain=");
		builder.append((baseDrain != null) ? baseDrain.getId() : null);
		builder.append(", coefficient=");
		builder.append(coefficient);
		builder.append(", diffDrain=");
		builder.append((diffDrain != null) ? diffDrain.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
