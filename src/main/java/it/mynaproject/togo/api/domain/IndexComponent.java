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
@Table(name="index_component")
public class IndexComponent extends BaseDomain implements Comparable<IndexComponent> {

	@ManyToOne
	@JoinColumn(name="formula_id")
	private Formula formula;

	@Column
	@Enumerated(EnumType.STRING)
	private Operation operator;

	@Column(name="n_skip")
	private Integer nSkip;

	@Column(name="skip_period")
	@Enumerated(EnumType.STRING)
	private IndexRelativePeriod skipPeriod;

	@ManyToOne
	@JoinColumn(name="index_id")
	private Index index;

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public Operation getOperator() {
		return operator;
	}

	public void setOperator(Operation operator) {
		this.operator = operator;
	}

	public Integer getNSkip() {
		return nSkip;
	}

	public void setNSkip(Integer nSkip) {
		this.nSkip = nSkip;
	}

	public IndexRelativePeriod getSkipPeriod() {
		return skipPeriod;
	}

	public void setSkipPeriod(IndexRelativePeriod skipPeriod) {
		this.skipPeriod = skipPeriod;
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	@Override
	public int compareTo(IndexComponent o) {
		Integer i = this.getId();
		Integer oi = o.getId();
		return (Integer) i.compareTo(oi);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndexComponent [formula=");
		builder.append((formula != null) ? formula.getId() : null);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", nSkip=");
		builder.append(nSkip);
		builder.append(", skipPeriod=");
		builder.append(skipPeriod);
		builder.append(", index=");
		builder.append((index != null) ? index.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
