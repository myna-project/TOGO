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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.mynaproject.togo.api.domain.DashboardWidgetType;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Period;
import it.mynaproject.togo.api.domain.TimeAggregation;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardWidgetJson {

	private Integer id;

	@NotNull(message = "Please provide number of columns!")
	@JsonProperty("n_cols")
	private Integer nCols;

	@NotNull(message = "Please provide a number of rows!")
	@JsonProperty("n_rows")
	private Integer nRows;

	@NotNull(message = "Please provide x position!")
	@JsonProperty("x_pos")
	private Integer xPos;

	@NotNull(message = "Please provide y position!")
	@JsonProperty("y_pos")
	private Integer yPos;

	@NotNull(message = "Please provide widget type!")
	@JsonProperty("widget_type")
	private DashboardWidgetType widgetType;

	@JsonProperty("costs_drain_id")
	private Integer costsDrainId;

	@JsonProperty("costs_aggregation")
	private MeasureAggregation costsAggregation;

	@JsonProperty("interval_seconds")
	private Integer intervalSeconds;

	private String title;

	@JsonProperty("background_color")
	private String backgroundColor;

	@NotNull(message = "Please provide number of periods!")
	@JsonProperty("number_periods")
	private Integer numberPeriods;

	@NotNull(message = "Please provide a period!")
	private Period period;

	@JsonProperty("start_time")
	private Date startTime;

	private Boolean legend;

	@JsonProperty("legend_position")
	private String legendPosition;

	@JsonProperty("legend_layout")
	private String legendLayout;

	private Boolean navigator;

	@JsonProperty("time_aggregation")
	private TimeAggregation timeAggregation;

	@JsonProperty("min_value")
	private Double minValue;

	@JsonProperty("max_value")
	private Double maxValue;

	@JsonProperty("warning_value")
	private Double warningValue;

	@JsonProperty("alarm_value")
	private Double alarmValue;

	private String color1;

	private String color2;

	private String color3;

	private List<DashboardWidgetDetailJson> details = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getnCols() {
		return nCols;
	}

	public void setnCols(Integer nCols) {
		this.nCols = nCols;
	}

	public Integer getnRows() {
		return nRows;
	}

	public void setnRows(Integer nRows) {
		this.nRows = nRows;
	}

	public Integer getxPos() {
		return xPos;
	}

	public void setxPos(Integer xPos) {
		this.xPos = xPos;
	}

	public Integer getyPos() {
		return yPos;
	}

	public void setyPos(Integer yPos) {
		this.yPos = yPos;
	}

	public DashboardWidgetType getWidgetType() {
		return widgetType;
	}

	public void setWidgetType(DashboardWidgetType widgetType) {
		this.widgetType = widgetType;
	}

	public Integer getCostsDrainId() {
		return costsDrainId;
	}

	public void setCostsDrainId(Integer costsDrainId) {
		this.costsDrainId = costsDrainId;
	}

	public MeasureAggregation getCostsAggregation() {
		return costsAggregation;
	}

	public void setCostsAggregation(MeasureAggregation costsAggregation) {
		this.costsAggregation = costsAggregation;
	}

	public Integer getIntervalSeconds() {
		return intervalSeconds;
	}

	public void setIntervalSeconds(Integer intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Integer getNumberPeriods() {
		return numberPeriods;
	}

	public void setNumberPeriods(Integer numberPeriods) {
		this.numberPeriods = numberPeriods;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Boolean getLegend() {
		return legend;
	}

	public void setLegend(Boolean legend) {
		this.legend = legend;
	}

	public String getLegendPosition() {
		return legendPosition;
	}

	public void setLegendPosition(String legendPosition) {
		this.legendPosition = legendPosition;
	}

	public String getLegendLayout() {
		return legendLayout;
	}

	public void setLegendLayout(String legendLayout) {
		this.legendLayout = legendLayout;
	}

	public Boolean getNavigator() {
		return navigator;
	}

	public void setNavigator(Boolean navigator) {
		this.navigator = navigator;
	}

	public TimeAggregation getTimeAggregation() {
		return timeAggregation;
	}

	public void setTimeAggregation(TimeAggregation timeAggregation) {
		this.timeAggregation = timeAggregation;
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

	public String getColor1() {
		return color1;
	}

	public void setColor1(String color1) {
		this.color1 = color1;
	}

	public String getColor2() {
		return color2;
	}

	public void setColor2(String color2) {
		this.color2 = color2;
	}

	public String getColor3() {
		return color3;
	}

	public void setColor3(String color3) {
		this.color3 = color3;
	}

	public List<DashboardWidgetDetailJson> getDetails() {
		return details;
	}

	public void setDetails(List<DashboardWidgetDetailJson> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DashboardWidgetJson [id=");
		builder.append(id);
		builder.append(", nCols=");
		builder.append(nCols);
		builder.append(", nRows=");
		builder.append(nRows);
		builder.append(", xPos=");
		builder.append(xPos);
		builder.append(", yPos=");
		builder.append(yPos);
		builder.append(", widgetType=");
		builder.append(widgetType);
		builder.append(", costsDrainId=");
		builder.append(costsDrainId);
		builder.append(", costsAggregation=");
		builder.append(costsAggregation);
		builder.append(", intervalSeconds=");
		builder.append(intervalSeconds);
		builder.append(", title=");
		builder.append(title);
		builder.append(", backgroundColor=");
		builder.append(backgroundColor);
		builder.append(", numberPeriods=");
		builder.append(numberPeriods);
		builder.append(", period=");
		builder.append(period);
		builder.append(", legend=");
		builder.append(legend);
		builder.append(", legendPosition=");
		builder.append(legendPosition);
		builder.append(", legendLayout=");
		builder.append(legendLayout);
		builder.append(", navigator=");
		builder.append(navigator);
		builder.append(", timeAggregation=");
		builder.append(timeAggregation);
		builder.append(", minValue=");
		builder.append(minValue);
		builder.append(", maxValue=");
		builder.append(maxValue);
		builder.append(", alarmValue=");
		builder.append(alarmValue);
		builder.append(", warningValue=");
		builder.append(warningValue);
		builder.append(", color1=");
		builder.append(color1);
		builder.append(", color2=");
		builder.append(color2);
		builder.append(", color3=");
		builder.append(color3);
		builder.append("]");
		return builder.toString();
	}
}
