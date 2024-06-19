package it.mynaproject.togo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Operation;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardWidgetDetailJson {

	private Integer id;

	@JsonProperty("index_id")
	private Integer indexId;

	@JsonProperty("drain_id")
	private Integer drainId;

	@JsonProperty("formula_id")
	private Integer formulaId;

	@JsonProperty("drain_control_id")
	private Integer drainControlId;

	private MeasureAggregation aggregation;

	private Operation operator;

	@JsonProperty("positive_negative_value")
	private String positiveNegativeValue;

	@JsonProperty("exclude_outliers")
	private boolean excludeOutliers;

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIndexId() {
		return indexId;
	}

	public void setIndexId(Integer indexId) {
		this.indexId = indexId;
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

	public Integer getDrainControlId() {
		return drainControlId;
	}

	public void setDrainControlId(Integer drainControlId) {
		this.drainControlId = drainControlId;
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

	public String getPositiveNegativeValue() {
		return positiveNegativeValue;
	}

	public void setPositiveNegativeValue(String positiveNegativeValue) {
		this.positiveNegativeValue = positiveNegativeValue;
	}

	public Boolean getExcludeOutliers() {
		return excludeOutliers;
	}

	public void setExcludeOutliers(boolean excludeOutliers) {
		this.excludeOutliers = excludeOutliers;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DashboardWidgetDetailJson [indexId=");
		builder.append(indexId);
		builder.append(", drainId=");
		builder.append(drainId);
		builder.append(", formulaId=");
		builder.append(formulaId);
		builder.append(", drainControlId=");
		builder.append(drainControlId);
		builder.append(", aggregation=");
		builder.append(aggregation);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", positiveNegativeValue=");
		builder.append(positiveNegativeValue);
		builder.append(", excludeOutliers=");
		builder.append(excludeOutliers);
		builder.append("]");
		return builder.toString();
	}
}
