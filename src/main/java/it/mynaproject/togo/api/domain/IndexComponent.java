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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="index_component")
public class IndexComponent extends BaseDomain implements Comparable<IndexComponent> {

	@ManyToOne
	@JoinColumn(name="formula_id")
	private Formula formula;

	@Column
	@Enumerated(EnumType.STRING)
	private Operation operator;

	@Column(name="relative_time")
	private Integer relativeTime;

	@Column(name="relative_period")
	@Enumerated(EnumType.STRING)
	private IndexRelativePeriod relativePeriod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_time",nullable=false)
	private Date startTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_time",nullable=false)
	private Date endTime;

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

	public Integer getRelativeTime() {
		return relativeTime;
	}

	public void setRelativeTime(Integer relativeTime) {
		this.relativeTime = relativeTime;
	}

	public IndexRelativePeriod getRelativePeriod() {
		return relativePeriod;
	}

	public void setRelativePeriod(IndexRelativePeriod relativePeriod) {
		this.relativePeriod = relativePeriod;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
		builder.append(", relativeTime=");
		builder.append(relativeTime);
		builder.append(", relativePeriod=");
		builder.append(relativePeriod);
		builder.append(", startTime=");
		builder.append(startTime);
		builder.append(", endTime=");
		builder.append(endTime);
		builder.append(", index=");
		builder.append((index != null) ? index.getId() : null);
		builder.append("]");
		return builder.toString();
	}
}
