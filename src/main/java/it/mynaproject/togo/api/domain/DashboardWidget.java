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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.mynaproject.togo.api.model.DashboardWidgetJson;

@Entity
@Table(name="dashboard_widget")
public class DashboardWidget extends BaseDomain {

	@ManyToOne
	@JoinColumn(name="dashboard_id",nullable=false)
	private Dashboard dashboard;

	@Column(name="n_cols",nullable=false)
	private Integer nCols;

	@Column(name="n_rows",nullable=false)
	private Integer nRows;

	@Column(name="x_pos",nullable=false)
	private Integer xPos;

	@Column(name="y_pos",nullable=false)
	private Integer yPos;

	@Column(name="widget_type",nullable=false)
	@Enumerated(EnumType.STRING)
	private DashboardWidgetType widgetType;

	@ManyToOne
	@JoinColumn(name="costs_drain_id")
	private Drain costsDrain;

	@Column(name="costs_aggregation")
	@Enumerated(EnumType.STRING)
	private MeasureAggregation costsAggregation;

	@Column(name="interval_seconds")
	private Integer intervalSeconds;

	@Column
	private String title;

	@Column(name="background_color")
	private String backgroundColor;

	@Column(name="number_periods",nullable=false)
	private Integer numberPeriods;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Period period;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_time")
	private Date startTime;

	private Boolean legend;

	@Column(name="legend_position")
	private String legendPosition;

	@Column(name="legend_layout")
	private String legendLayout;

	private Boolean navigator;

	@Column(name="time_aggregation",nullable=false)
	@Enumerated(EnumType.STRING)
	private TimeAggregation timeAggregation;

	@Column(name="min_value")
	private Double minValue;

	@Column(name="max_value")
	private Double maxValue;

	@Column(name="warning_value")
	private Double warningValue;

	@Column(name="alarm_value")
	private Double alarmValue;

	private String color1;

	private String color2;

	private String color3;

	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.REMOVE}, mappedBy="dashboardWidget")
	private List<DashboardWidgetDetail> details = new ArrayList<>();

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
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

	public Drain getCostsDrain() {
		return costsDrain;
	}

	public void setCostsDrain(Drain costsDrain) {
		this.costsDrain = costsDrain;
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

	public List<DashboardWidgetDetail> getDetails() {
		return details;
	}

	public void setDetails(List<DashboardWidgetDetail> details) {
		this.details = details;
	}

	public void populateDashboardWidgetFromInput(DashboardWidgetJson input, Drain costDrain, Dashboard dashboard) {

		this.setDashboard(dashboard);
		this.setnCols(input.getnCols());
		this.setnRows(input.getnRows());
		this.setxPos(input.getxPos());
		this.setyPos(input.getyPos());
		this.setWidgetType(input.getWidgetType());
		if (costDrain != null)
			this.setCostsDrain(costDrain);
		this.setCostsAggregation(input.getCostsAggregation());
		this.setIntervalSeconds(input.getIntervalSeconds());
		this.setTitle(input.getTitle());
		this.setBackgroundColor(input.getBackgroundColor());
		this.setNumberPeriods(input.getNumberPeriods());
		this.setPeriod((input.getPeriod() != null) ? input.getPeriod() : Period.hours);
		this.setStartTime(input.getStartTime());
		this.setLegend(input.getLegend());
		this.setLegendPosition(input.getLegendPosition());
		this.setLegendLayout(input.getLegendLayout());
		this.setNavigator(input.getNavigator());
		this.setTimeAggregation((input.getTimeAggregation() != null) ? input.getTimeAggregation() : TimeAggregation.ALL);
		this.setMinValue(input.getMinValue());
		this.setMaxValue(input.getMaxValue());
		this.setWarningValue(input.getWarningValue());
		this.setAlarmValue(input.getAlarmValue());
		this.setColor1(input.getColor1());
		this.setColor2(input.getColor2());
		this.setColor3(input.getColor3());
	}

	public void duplicateDashboardWidget(DashboardWidget input, Drain costDrain) {

		this.setnCols(input.getnCols());
		this.setnRows(input.getnRows());
		this.setxPos(input.getxPos());
		this.setyPos(input.getyPos());
		this.setWidgetType(input.getWidgetType());
		if (costDrain != null)
			this.setCostsDrain(costDrain);
		this.setCostsAggregation(input.getCostsAggregation());
		this.setIntervalSeconds(input.getIntervalSeconds());
		this.setTitle(input.getTitle());
		this.setBackgroundColor(input.getBackgroundColor());
		this.setNumberPeriods(input.getNumberPeriods());
		this.setPeriod((input.getPeriod() != null) ? input.getPeriod() : Period.hours);
		this.setStartTime(input.getStartTime());
		this.setLegend(input.getLegend());
		this.setLegendPosition(input.getLegendPosition());
		this.setLegendLayout(input.getLegendLayout());
		this.setNavigator(input.getNavigator());
		this.setTimeAggregation((input.getTimeAggregation() != null) ? input.getTimeAggregation() : TimeAggregation.ALL);
		this.setMinValue(input.getMinValue());
		this.setMaxValue(input.getMaxValue());
		this.setWarningValue(input.getWarningValue());
		this.setAlarmValue(input.getAlarmValue());
		this.setColor1(input.getColor1());
		this.setColor2(input.getColor2());
		this.setColor3(input.getColor3());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DashboardWidget [nCols=");
		builder.append(nCols);
		builder.append(", nRows=");
		builder.append(nRows);
		builder.append(", xPos=");
		builder.append(xPos);
		builder.append(", yPos=");
		builder.append(yPos);
		builder.append(", widgetType=");
		builder.append(widgetType);
		builder.append(", costsDrain=");
		builder.append((costsDrain != null) ? costsDrain.getId() : "");
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
		builder.append(", startTime=");
		builder.append(startTime);
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
