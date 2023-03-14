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

import java.util.Calendar;
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
import javax.persistence.Transient;

import it.mynaproject.togo.api.model.DrainControlDetailJson;

@Entity
@Table(name="drain_control_detail")
public class DrainControlDetail extends BaseDomain {

	@ManyToOne
	@JoinColumn(name="drain_control_id")
	private DrainControl drainControl;

	@ManyToOne
	@JoinColumn(name="drain_id")
	private Drain drain;

	@ManyToOne
	@JoinColumn(name="formula_id")
	private Formula formula;

	@Column(name="last_minutes")
	private Integer lastMinutes;

	@Column
	@Enumerated(EnumType.STRING)
	private MeasureAggregation aggregation;

	@Column(name="low_threshold")
	private Float lowThreshold;

	@Column(name="high_threshold")
	private Float highThreshold;

	@Column
	private Float delta;

	@Column(nullable=false)
	private Boolean active;

	@Column(name="waiting_measures")
	private Integer waitingMeasures;

	@Column(nullable=false)
	private Boolean error;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_error_time")
	private Date lastErrorTime;

	@Transient
	private String description;

	@Transient
	private Calendar startTime;

	@Transient
	private Calendar endTime;

	@Transient
	private Integer receivedMeasures;

	@Transient
	private Measure lastMeasure;

	public DrainControl getDrainControl() {
		return drainControl;
	}

	public void setDrainControl(DrainControl drainControl) {
		this.drainControl = drainControl;
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

	public Integer getLastMinutes() {
		return lastMinutes;
	}

	public void setLastMinutes(Integer lastMinutes) {
		this.lastMinutes = lastMinutes;
	}

	public MeasureAggregation getAggregation() {
		return aggregation;
	}

	public void setAggregation(MeasureAggregation aggregation) {
		this.aggregation = aggregation;
	}

	public Float getLowThreshold() {
		return lowThreshold;
	}

	public void setLowThreshold(Float lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	public Float getHighThreshold() {
		return highThreshold;
	}

	public void setHighThreshold(Float highThreshold) {
		this.highThreshold = highThreshold;
	}

	public Float getDelta() {
		return delta;
	}

	public void setDelta(Float delta) {
		this.delta = delta;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Integer getWaitingMeasures() {
		return waitingMeasures;
	}

	public void setWaitingMeasures(Integer waitingMeasures) {
		this.waitingMeasures = waitingMeasures;
	}

	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

	public Date getLastErrorTime() {
		return lastErrorTime;
	}

	public void setLastErrorTime(Date lastErrorTime) {
		this.lastErrorTime = lastErrorTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public Integer getReceivedMeasures() {
		return receivedMeasures;
	}

	public void setReceivedMeasures(Integer receivedMeasures) {
		this.receivedMeasures = receivedMeasures;
	}

	public Measure getLastMeasure() {
		return lastMeasure;
	}

	public void setLastMeasure(Measure lastMeasure) {
		this.lastMeasure = lastMeasure;
	}

	public void populateDrainControlDetailFromInput(DrainControlDetailJson input, Drain drain, Formula formula) {

		this.setDrain(drain);
		this.setFormula(formula);
		this.setLastMinutes(input.getLastMinutes());
		this.setAggregation(input.getAggregation());
		this.setLowThreshold(input.getLowThreshold());
		this.setHighThreshold(input.getHighThreshold());
		this.setDelta(input.getDelta());
		this.setActive(input.getActive());
		if (input.getWaitingMeasures() != null)
			this.setWaitingMeasures(input.getWaitingMeasures());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DrainControlDetail [drainControl=");
		builder.append((drainControl != null) ? drainControl.getId() : "");
		builder.append(", drain=");
		builder.append((drain != null) ? drain.getId() : "");
		builder.append(", formula=");
		builder.append((formula != null) ? formula.getId() : "");
		builder.append(", lastMinutes=");
		builder.append(lastMinutes);
		builder.append(", aggregation=");
		builder.append(aggregation);
		builder.append(", lowThreshold=");
		builder.append(lowThreshold);
		builder.append(", highThreshold=");
		builder.append(highThreshold);
		builder.append(", delta=");
		builder.append(delta);
		builder.append(", active=");
		builder.append(active);
		builder.append(", waitingMeasures=");
		builder.append(waitingMeasures);
		builder.append(", error=");
		builder.append(error);
		builder.append(", lastErrorTime=");
		builder.append(lastErrorTime);
		builder.append("]");
		return builder.toString();
	}
}
