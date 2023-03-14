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
import javax.persistence.Transient;

import it.mynaproject.togo.api.model.IndexJson;

@Entity
@Table(name="index")
public class Index extends BaseDomain {

	@Column
	private String name;

	@ManyToOne
	@JoinColumn(name="org_id")
	private Org org;

	@ManyToOne
	@JoinColumn(name="index_group_id")
	private IndexGroup group;

	@Column(name="min_value")
	private Double minValue;

	@Column(name="max_value")
	private Double maxValue;

	@Column(name="warning_value")
	private Double warningValue;

	@Column(name="alarm_value")
	private Double alarmValue;

	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,orphanRemoval=true,mappedBy="index")
	private List<IndexComponent> components = new ArrayList<>();

	@Transient
	private List<Measure> result;

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

	public IndexGroup getGroup() {
		return group;
	}

	public void setGroup(IndexGroup group) {
		this.group = group;
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

	public Double getWarningValue() {
		return warningValue;
	}

	public void setWarningValue(Double warningValue) {
		this.warningValue = warningValue;
	}

	public Double getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(Double alarmValue) {
		this.alarmValue = alarmValue;
	}

	public List<IndexComponent> getComponents() {
		return components;
	}

	public void setComponents(List<IndexComponent> components) {

		for (IndexComponent newComponent : components)
			newComponent.setIndex(this);

		this.components = components;
	}

	public List<Measure> getResult() {
		return result;
	}

	public void setResult(List<Measure> result) {
		this.result = result;
	}

	public void populateIndexFromInput(IndexJson input, Org org, IndexGroup group) {

		this.setName(input.getName());
		this.setOrg(org);
		this.setGroup(group);
		this.setMinValue(input.getMinValue());
		this.setMaxValue(input.getMaxValue());
		this.setWarningValue(input.getWarningValue());
		this.setAlarmValue(input.getAlarmValue());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Index [name=");
		builder.append(name);
		builder.append(", org=");
		builder.append((org != null) ? org.getId() : null);
		builder.append(", group=");
		builder.append((group != null) ? group.getId() : null);
		builder.append(", minValue=");
		builder.append(minValue);
		builder.append(", maxValue=");
		builder.append(maxValue);
		builder.append(", warningValue=");
		builder.append(warningValue);
		builder.append(", alarmValue=");
		builder.append(alarmValue);
		builder.append("]");
		return builder.toString();
	}
}
