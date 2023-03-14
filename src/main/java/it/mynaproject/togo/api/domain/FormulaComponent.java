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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="formula_component")
public class FormulaComponent extends BaseDomain implements Comparable<FormulaComponent> {

	@ManyToOne
	@JoinColumn(name="drain_id") 
	private Drain drain;

	@Column
	@Enumerated(EnumType.STRING)
	private Operation operator;

	@Column
	@Enumerated(EnumType.STRING)
	private MeasureAggregation aggregation;

	@Column
	private String legend;

	@ManyToOne
	@JoinColumn(name="formula_id")
	private Formula formula;

	public Drain getDrain() {
		return drain;
	}

	public void setDrain(Drain drain) {
		this.drain = drain;
	}

	public Operation getOperator() {
		return operator;
	}

	public void setOperator(Operation operator) {
		this.operator = operator;
	}

	public MeasureAggregation getAggregation() {
		return aggregation;
	}

	public void setAggregation(MeasureAggregation aggregation) {
		this.aggregation = aggregation;
	}

	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	@Override
	public int compareTo(FormulaComponent o) {
		Integer i = this.getId();
		Integer oi = o.getId();
		return (Integer) i.compareTo(oi);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FormulaComponent [drain=");
		builder.append((drain != null) ? drain.getId() : null);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", aggregation=");
		builder.append(aggregation);
		builder.append(", formula=");
		builder.append((formula != null) ? formula.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
