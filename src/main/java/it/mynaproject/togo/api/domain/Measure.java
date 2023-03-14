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

import java.util.Date;

public interface Measure<T> {

	public Drain getDrain();
	public void setDrain(Drain drain);
	public T getValue();
	public void setValue(T value);
	public Date getTime();
	public void setTime(Date time);
	public String toString();
	public int hashCode();
	public boolean equals(Object obj);
}
