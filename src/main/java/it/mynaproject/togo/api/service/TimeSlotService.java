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
package it.mynaproject.togo.api.service;

import java.util.List;

import it.mynaproject.togo.api.domain.TimeSlot;
import it.mynaproject.togo.api.model.TimeSlotJson;

public interface TimeSlotService {

	public TimeSlot getTimeSlot(Integer id, Boolean isAdmin);
	public List<TimeSlot> getAllTimeSlots();
	public void persist(TimeSlot timeSlot);
	public TimeSlot createTimeSlotFromInput(TimeSlotJson input, Boolean isAdmin);
	public void update(TimeSlot timeSlot);
	public TimeSlot updateTimeSlotFromInput(Integer id, TimeSlotJson input, Boolean isAdmin);
	public void deleteTimeSlotById(Integer id, Boolean isAdmin);
}
