package it.mynaproject.togo.api.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.mynaproject.togo.api.domain.DrainControlType;
import it.mynaproject.togo.api.domain.MeasureAggregation;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DrainControlDetailJson {

	private DrainControlType type;

	@JsonProperty("drain_id")
	private Integer drainId;

	@JsonProperty("formula_id")
	private Integer formulaId;

	@JsonProperty("last_minutes")
	private Integer lastMinutes;

	private MeasureAggregation aggregation;

	@JsonProperty("low_threshold")
	private Float lowThreshold;

	@JsonProperty("high_threshold")
	private Float highThreshold;

	private Float delta;

	@NotNull(message = "Please indicate if is active or not!")
	private Boolean active;

	@JsonProperty("waiting_measures")
	private Integer waitingMeasures;

	private Boolean error;

	@JsonProperty("last_error_time")
	private Date lastErrorTime;

	public DrainControlType getType() {
		return type;
	}

	public void setType(DrainControlType type) {
		this.type = type;
	}

	public Integer getDrainId() {
		return drainId;
	}

	public void setDrainId(Integer drainId) {
		this.drainId = drainId;
	}

	public Integer getFormulaId() {
		return formulaId;
	}

	public void setFormulaId(Integer formulaId) {
		this.formulaId = formulaId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DrainControlDetailJson [type=");
		builder.append(type);
		builder.append(", drainId=");
		builder.append(drainId);
		builder.append(", formulaId=");
		builder.append(formulaId);
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
