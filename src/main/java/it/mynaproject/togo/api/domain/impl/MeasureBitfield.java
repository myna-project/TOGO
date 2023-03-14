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
package it.mynaproject.togo.api.domain.impl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Measure;

@Entity
@Table(name="measure_bitfield")
public class MeasureBitfield implements Measure<String>, Serializable {

	private static final long serialVersionUID = -547175182280255203L;

	@Id
	@ManyToOne
	@JoinColumn(name="drain_id", nullable=false)
	private Drain drain;

	@Column(nullable=false)
	private String value;

	@Id
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date time;

	public Drain getDrain() {
		return drain;
	}

	public void setDrain(Drain drain) {
		this.drain = drain;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Measure [value=");
		builder.append(value);
		builder.append(", drainId=");
		builder.append((drain == null) ? "" : drain.getId());
		builder.append(", time=");
		builder.append(time);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((drain == null) ? 0 : drain.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeasureBitfield other = (MeasureBitfield) obj;
		if (drain == null) {
			if (other.drain != null)
				return false;
		} else if (!drain.equals(other.drain))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
}
