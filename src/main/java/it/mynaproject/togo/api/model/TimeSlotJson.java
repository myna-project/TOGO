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

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class TimeSlotJson {

	private Integer id;

	@NotBlank(message = "Please provide a name!")
	private String name;

	private List<TimeSlotDetailJson> details;

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

	public List<TimeSlotDetailJson> getDetails() {
		return details;
	}

	public void setDetails(List<TimeSlotDetailJson> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeSlotJson [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", details=");
		builder.append(details);
		builder.append("]");
		return builder.toString();
	}
}
