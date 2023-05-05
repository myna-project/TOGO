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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.mynaproject.togo.api.domain.Operation;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexJson {

	private Integer id;

	@NotBlank(message = "Please provide a name!")
	private String name;

	@NotNull(message = "Please provide an org id!")
	@JsonProperty("org_id")
	private Integer orgId;

	private IndexGroupJson group;

	private Float coefficient;

	@JsonProperty("measure_unit")
	private String unitOfMeasure;

	private Integer decimals;

	@JsonProperty("min_value")
	private Double minValue;

	@JsonProperty("max_value")
	private Double maxValue;

	@JsonProperty("warning_value")
	private Double warningValue;

	@JsonProperty("alarm_value")
	private Double alarmValue;

	@NotNull(message = "Please provide formula elements!")
	@JsonProperty("formula_elements")
	private ArrayList<FormulaElementJson> formulaElements;

	@NotNull(message = "Please provide operators!")
	private List<Operation> operators;

	private List<MeasureJson> result;

	public Integer getId() {
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

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public IndexGroupJson getGroup() {
		return group;
	}

	public void setGroup(IndexGroupJson group) {
		this.group = group;
	}

	public Float getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Float coefficient) {
		this.coefficient = coefficient;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
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

	public ArrayList<FormulaElementJson> getFormulaElements() {
		return formulaElements;
	}

	public void setFormulaElements(ArrayList<FormulaElementJson> formulaElements) {
		this.formulaElements = formulaElements;
	}

	public List<Operation> getOperators() {
		return operators;
	}

	public void setOperators(List<Operation> operators) {
		this.operators = operators;
	}

	public List<MeasureJson> getResult() {
		return result;
	}

	public void setResult(List<MeasureJson> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndexJson [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", group=");
		builder.append(group);
		builder.append(", coefficient=");
		builder.append(coefficient);
		builder.append(", unit of measure=");
		builder.append(unitOfMeasure);
		builder.append(", decimals=");
		builder.append(decimals);
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
