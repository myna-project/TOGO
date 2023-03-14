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
package it.mynaproject.togo.api.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.TimeSlotDao;
import it.mynaproject.togo.api.domain.TimeSlot;
import it.mynaproject.togo.api.exception.GenericException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.TimeSlotDetailJson;
import it.mynaproject.togo.api.model.TimeSlotJson;
import it.mynaproject.togo.api.service.TimeSlotService;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TimeSlotDao timeSlotDao;

	@Override
	@Transactional
	public TimeSlot getTimeSlot(Integer id, Boolean isAdmin) {

		TimeSlot ts = this.timeSlotDao.getTimeSlot(id);
		if (ts == null)
			throw new NotFoundException(404, "Time slot " + id + " not found");

		return ts;
	}

	@Override
	@Transactional
	public List<TimeSlot> getAllTimeSlots() {
		return this.timeSlotDao.getAllTimeSlots();
	}

	@Override
	@Transactional
	public void persist(TimeSlot timeSlot) {
		this.timeSlotDao.persist(timeSlot);
	}

	@Override
	@Transactional
	public TimeSlot createTimeSlotFromInput(TimeSlotJson input, Boolean isAdmin) {

		log.info("Creating new time slot");

		// check time correct format
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		for(TimeSlotDetailJson d : input.getDetails()) {
			try {
				new java.sql.Time(formatter.parse(d.getStartTime()).getTime());
			} catch (ParseException e) {
				throw new GenericException(500, "Cannot transform time " + d.getStartTime());
			}
			try {
				new java.sql.Time(formatter.parse(d.getEndTime()).getTime());
			} catch (ParseException e) {
				throw new GenericException(500, "Cannot transform time " + d.getEndTime());
			}
		}

		TimeSlot ts = new TimeSlot();
		try {
			ts.populateTimeSlotFromInput(input);
		} catch (ParseException e) {
			throw new GenericException(500, "Cannot populate time slot from input");
		}

		this.persist(ts);

		return ts;
	}

	@Override
	@Transactional
	public void update(TimeSlot timeSlot) {
		this.timeSlotDao.update(timeSlot);
	}

	@Override
	@Transactional
	public TimeSlot updateTimeSlotFromInput(Integer id, TimeSlotJson input, Boolean isAdmin) {

		log.info("Updating time slot with id {}", id);

		TimeSlot ts = this.getTimeSlot(id, isAdmin);

		// check time correct format
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		for(TimeSlotDetailJson d : input.getDetails()) {
			try {
				new java.sql.Time(formatter.parse(d.getStartTime()).getTime());
			} catch (ParseException e) {
				throw new GenericException(500, "Cannot transform time " + d.getStartTime());
			}
			try {
				new java.sql.Time(formatter.parse(d.getEndTime()).getTime());
			} catch (ParseException e) {
				throw new GenericException(500, "Cannot transform time " + d.getEndTime());
			}
		}

		try {
			ts.populateTimeSlotFromInput(input);
		} catch (ParseException e) {
			throw new GenericException(500, "Cannot populate time slot from input");
		}

		this.update(ts);

		return ts;
	}

	@Override
	@Transactional
	public void deleteTimeSlotById(Integer id, Boolean isAdmin) {

		log.info("Deleting time slot with id {}", id);

		this.timeSlotDao.delete(this.getTimeSlot(id, isAdmin));
	}
}
