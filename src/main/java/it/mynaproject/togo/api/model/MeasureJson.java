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

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class MeasureJson {

	@NotNull(message = "Please provide a drain id!")
	@JsonProperty("drain_id")
	private Integer drainId;

	@NotNull(message = "Please provide a timestamp!")
	private Date at;

	@NotNull(message = "Please provide a value!")
	private String value;

	public Integer getDrainId() {
		return drainId;
	}

	public void setDrainId(Integer drainId) {
		this.drainId = drainId;
	}

	public Date getAt() {
		return at;
	}

	public void setAt(Date at) {
		this.at = at;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Measure [");
		builder.append(", drainId=");
		builder.append(drainId);
		builder.append(", at=");
		builder.append(at);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
}
