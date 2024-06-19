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
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.util.DateUtil;

public class PairDrainMeasuresJson {

	@JsonProperty("drain_id")
	private Integer drainId = null;

	@JsonProperty("drain_name")
	private String drainName = null;

	private String unit = null;

	private String type = null;

	private Integer decimals = null;

	@JsonProperty("measure_type")
	private String measureType = null;

	private boolean formula;

	@JsonProperty("query_param")
	private QueryParam queryParam;

	private List<Value> measures = new ArrayList<Value>();

	public PairDrainMeasuresJson(TimeAggregation timeAggregation, Date start, Date end) {

		this.queryParam = new QueryParam();
		queryParam.setEnd(end);
		queryParam.setStart(start);
		queryParam.setTimeAggregation(timeAggregation);
	}

	public Integer getDrainId() {
		return drainId;
	}

	public void setDrainId(Integer drainId) {
		this.drainId = drainId;
	}

	public String getDrainName() {
		return drainName;
	}

	public void setDrainName(String drainName) {
		this.drainName = drainName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public QueryParam getQueryParam() {
		return queryParam;
	}

	public void setQueryParam(QueryParam queryParam) {
		this.queryParam = queryParam;
	}

	public List<Value> getMeasures() {
		return measures;
	}

	public boolean isFormula() {
		return formula;
	}

	public void setFormula(boolean formula) {
		this.formula = formula;
	}

	@SuppressWarnings("unchecked")
	public void addValue(Object object, Date time) {
		measures.add(new Value(object, time));
	}

	public void setMeasures(List<Value> measures) {
		this.measures = measures;
	}

	public class Value <T> {

		private T value = null;

		private Date time = null;

		public Value(T object, Date time) {
			super();
			this.value = object;
			this.time = time;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public String getTime() {
			return DateUtil.formatDateString(time, Constants.DATIME_FORMAT);
		}
	}

	public class QueryParam {

		private TimeAggregation timeAggregation;

		private Date start = null;

		private Date end = null;

		public TimeAggregation getTimeAggregation() {
			return timeAggregation;
		}

		public void setTimeAggregation(TimeAggregation timeAggregation) {
			this.timeAggregation = timeAggregation;
		}

		public String getStart() {
			return DateUtil.formatDateString(start, Constants.DATIME_FORMAT);
		}

		public void setStart(Date start) {
			this.start = start;
		}

		public String getEnd() {
			return DateUtil.formatDateString(end, Constants.DATIME_FORMAT);
		}

		public void setEnd(Date end) {
			this.end = end;
		}
	}
}
