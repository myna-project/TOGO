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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.TimeSlotDetailJson;
import it.mynaproject.togo.api.model.TimeSlotJson;

@Entity
@Table(name="time_slot")
public class TimeSlot extends BaseDomain {

	@Column
	private String name;

	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.ALL},orphanRemoval=true,mappedBy="timeSlot")
	private List<TimeSlotDetail> details = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TimeSlotDetail> getDetails() {
		return details;
	}

	public void setDetails(List<TimeSlotDetail> details) {

		for (TimeSlotDetail newDetail : details){
			newDetail.setTimeSlot(this);
		}

		this.details = details;
	}

	public void addDetail(TimeSlotDetail detail) {

		if (!this.getDetails().contains(detail))
			this.details.add(detail);
	}

	public void removeDetail(TimeSlotDetail detail) {
		this.details.remove(detail);
	}

	public void populateTimeSlotFromInput(TimeSlotJson input) throws ParseException {

		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

		List<TimeSlotDetail> details = new ArrayList<TimeSlotDetail>();
		for(TimeSlotDetailJson d : input.getDetails()) {
			TimeSlotDetail newDetail = new TimeSlotDetail();
			newDetail.setDayOfWeek(d.getDayOfWeek());
			newDetail.setStartTime(new java.sql.Time(formatter.parse(d.getStartTime()).getTime()));
			newDetail.setEndTime(new java.sql.Time(formatter.parse(d.getEndTime()).getTime()));
			details.add(newDetail);
		}

		this.setName(input.getName());
		this.setDetails(details);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeSlot [name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
}
