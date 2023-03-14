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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.mynaproject.togo.api.domain.DrainControlType;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DrainControlJson {

	private Integer id;

	@NotBlank(message = "Please provide a name!")
	private String name;

	@NotNull(message = "Please provide an org id!")
	@JsonProperty("org_id")
	private Integer orgId;

	@NotNull(message = "Please provide a type!")
	private DrainControlType type;

	@NotBlank(message = "Please provide a cron second!")
	@JsonProperty("cron_second")
	private String cronSecond;

	@NotBlank(message = "Please provide a cron minute!")
	@JsonProperty("cron_minute")
	private String cronMinute;

	@NotBlank(message = "Please provide a cron hour!")
	@JsonProperty("cron_hour")
	private String cronHour;

	@NotBlank(message = "Please provide a cron day month!")
	@JsonProperty("cron_day_month")
	private String cronDayMonth;

	@NotBlank(message = "Please provide a cron day week!")
	@JsonProperty("cron_day_week")
	private String cronDayWeek;

	@NotBlank(message = "Please provide a cron month!")
	@JsonProperty("cron_month")
	private String cronMonth;

	@NotBlank(message = "Please provide a mail receiver!")
	@JsonProperty("mail_receivers")
	private String mailReceivers;

	private Integer errors;

	@JsonProperty("last_mail_sent_time")
	private Date lastMailSentTime;

	private List<DrainControlDetailJson> details = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public DrainControlType getType() {
		return type;
	}

	public void setType(DrainControlType type) {
		this.type = type;
	}

	public String getCronSecond() {
		return cronSecond;
	}

	public void setCronSecond(String cronSecond) {
		this.cronSecond = cronSecond;
	}

	public String getCronMinute() {
		return cronMinute;
	}

	public void setCronMinute(String cronMinute) {
		this.cronMinute = cronMinute;
	}

	public String getCronHour() {
		return cronHour;
	}

	public void setCronHour(String cronHour) {
		this.cronHour = cronHour;
	}

	public String getCronDayMonth() {
		return cronDayMonth;
	}

	public void setCronDayMonth(String cronDayMonth) {
		this.cronDayMonth = cronDayMonth;
	}

	public String getCronDayWeek() {
		return cronDayWeek;
	}

	public void setCronDayWeek(String cronDayWeek) {
		this.cronDayWeek = cronDayWeek;
	}

	public String getCronMonth() {
		return cronMonth;
	}

	public void setCronMonth(String cronMonth) {
		this.cronMonth = cronMonth;
	}

	public String getMailReceivers() {
		return mailReceivers;
	}

	public void setMailReceivers(String mailReceivers) {
		this.mailReceivers = mailReceivers;
	}

	public Integer getErrors() {
		return errors;
	}

	public void setErrors(Integer errors) {
		this.errors = errors;
	}

	public Date getLastMailSentTime() {
		return lastMailSentTime;
	}

	public void setLastMailSentTime(Date lastMailSentTime) {
		this.lastMailSentTime = lastMailSentTime;
	}

	public List<DrainControlDetailJson> getDetails() {
		return details;
	}

	public void setDetails(List<DrainControlDetailJson> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasureControlJson [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", orgId=");
		builder.append(orgId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", cronSecond=");
		builder.append(cronSecond);
		builder.append(", cronMinute=");
		builder.append(cronMinute);
		builder.append(", cronHour=");
		builder.append(cronHour);
		builder.append(", cronDayMonth=");
		builder.append(cronDayMonth);
		builder.append(", cronDayWeek=");
		builder.append(cronDayWeek);
		builder.append(", cronMonth=");
		builder.append(cronMonth);
		builder.append(", mailReceivers=");
		builder.append(mailReceivers);
		builder.append(", errors=");
		builder.append(errors);
		builder.append(", lastMailSentTime=");
		builder.append(lastMailSentTime);
		builder.append("]");
		return builder.toString();
	}
}
