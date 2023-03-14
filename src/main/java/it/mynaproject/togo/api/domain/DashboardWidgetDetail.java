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

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.DashboardWidgetDetailJson;

@Entity
@Table(name="dashboard_widget_detail")
public class DashboardWidgetDetail extends BaseDomain {

	@ManyToOne
	@JoinColumn(name="dashboard_widget_id")
	private DashboardWidget dashboardWidget;

	@ManyToOne
	@JoinColumn(name="index_id")
	private Index index;

	@ManyToOne
	@JoinColumn(name="drain_id")
	private Drain drain;

	@ManyToOne
	@JoinColumn(name="formula_id")
	private Formula formula;

	@ManyToOne
	@JoinColumn(name="drain_control_id")
	private DrainControl drainControl;

	@Enumerated(EnumType.STRING)
	private MeasureAggregation aggregation;

	@Enumerated(EnumType.STRING)
	private Operation operator;

	public DashboardWidget getDashboardWidget() {
		return dashboardWidget;
	}

	public void setDashboardWidget(DashboardWidget dashboardWidget) {
		this.dashboardWidget = dashboardWidget;
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public Drain getDrain() {
		return drain;
	}

	public void setDrain(Drain drain) {
		this.drain = drain;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public DrainControl getDrainControl() {
		return drainControl;
	}

	public void setDrainControl(DrainControl drainControl) {
		this.drainControl = drainControl;
	}

	public MeasureAggregation getAggregation() {
		return aggregation;
	}

	public void setAggregation(MeasureAggregation aggregation) {
		this.aggregation = aggregation;
	}

	public Operation getOperator() {
		return operator;
	}

	public void setOperator(Operation operator) {
		this.operator = operator;
	}

	public void populateDashboardWidgetDetailFromInput(DashboardWidgetDetailJson input, Drain drain, Formula formula, Index index, DrainControl control) {

		this.setIndex(index);
		this.setDrain(drain);
		this.setFormula(formula);
		this.setDrainControl(control);
		this.setAggregation(input.getAggregation());
		this.setOperator(input.getOperator());
	}

	public void duplicateDashboardWidgetDetail(DashboardWidgetDetail input) {

		this.setIndex(input.index);
		this.setDrain(input.drain);
		this.setFormula(input.formula);
		this.setDrainControl(input.drainControl);
		this.setAggregation(input.getAggregation());
		this.setOperator(input.getOperator());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DashboardWidgetDetail [dashboardWidget=");
		builder.append(dashboardWidget);
		builder.append(", index=");
		builder.append((index != null) ? index.getId() : "");
		builder.append(", drain=");
		builder.append((drain != null) ? drain.getId() : "");
		builder.append(", formula=");
		builder.append((formula != null) ? formula.getId() : "");
		builder.append(", drainControl=");
		builder.append((drainControl != null) ? drainControl.getId() : "");
		builder.append(", aggregation=");
		builder.append(aggregation);
		builder.append(", operator=");
		builder.append(operator);
		builder.append("]");
		return builder.toString();
	}
}
