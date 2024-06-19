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

import it.mynaproject.togo.api.model.DrainControlJson;

@Entity
@Table(name="drain_control")
public class DrainControl extends BaseDomain {

	@Column(nullable=false)
	private String name;

	@ManyToOne
	@JoinColumn(name="org_id",nullable=false)
	private Org org;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private DrainControlType type;

	@Column(name="cron_second",nullable=false)
	private String cronSecond;

	@Column(name="cron_minute",nullable=false)
	private String cronMinute;

	@Column(name="cron_hour",nullable=false)
	private String cronHour;

	@Column(name="cron_day_month",nullable=false)
	private String cronDayMonth;

	@Column(name="cron_day_week",nullable=false)
	private String cronDayWeek;

	@Column(name="cron_month",nullable=false)
	private String cronMonth;

	@Column(name="mail_receivers",nullable=false)
	private String mailReceivers;

	@Column
	private Integer errors;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_mail_sent_time")
	private Date lastMailSentTime;

	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="drainControl")
	private List<DrainControlDetail> details = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
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

	public List<DrainControlDetail> getDetails() {
		return details;
	}

	public void setDetails(List<DrainControlDetail> details) {
		this.details = details;
	}

	public void populateDrainControlFromInput(DrainControlJson input, Org org) {

		this.setName(input.getName());
		this.setOrg(org);
		this.setType(input.getType());
		this.setCronSecond(input.getCronSecond());
		this.setCronMinute(input.getCronMinute());
		this.setCronHour(input.getCronHour());
		this.setCronDayMonth(input.getCronDayMonth());
		this.setCronDayWeek(input.getCronDayWeek());
		this.setCronMonth(input.getCronMonth());
		this.setMailReceivers(input.getMailReceivers());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasureControl [name=");
		builder.append(name);
		builder.append(", org=");
		builder.append((org != null) ? org.getId() : null);
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
