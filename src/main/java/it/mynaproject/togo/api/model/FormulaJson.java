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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Operation;

@JsonInclude(Include.NON_EMPTY)
public class FormulaJson {

	private Integer id;

	@JsonProperty("org_id")
	private Integer orgId;

	@JsonProperty("client_id")
	private Integer clientId;

	private String name;

	private List<Integer> components;

	private List<MeasureAggregation> aggregations;

	private List<Operation> operators;

	private List<String> legends;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getComponents() {
		return components;
	}

	public void setComponents(List<Integer> components) {
		this.components = components;
	}

	public List<MeasureAggregation> getAggregations() {
		return aggregations;
	}

	public void setAggregations(List<MeasureAggregation> aggregations) {
		this.aggregations = aggregations;
	}

	public List<Operation> getOperators() {
		return operators;
	}

	public void setOperators(List<Operation> operators) {
		this.operators = operators;
	}

	public List<String> getLegends() {
		return legends;
	}

	public void setLegends(List<String> legends) {
		this.legends = legends;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FormulaJson [id=");
		builder.append(id);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", components=");
		builder.append(components);
		builder.append(", aggregations=");
		builder.append(aggregations);
		builder.append(", operators=");
		builder.append(operators);
		builder.append(", legends=");
		builder.append(legends);
		builder.append("]");
		return builder.toString();
	}
}
