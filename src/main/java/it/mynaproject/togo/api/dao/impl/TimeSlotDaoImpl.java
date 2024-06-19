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
package it.mynaproject.togo.api.dao.impl;

import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.TimeSlotDao;
import it.mynaproject.togo.api.domain.TimeSlot;

@Repository
public class TimeSlotDaoImpl extends BaseDaoImpl implements TimeSlotDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(TimeSlot timeSlot) {

		log.info("Creating new time slot: {}", timeSlot.toString());

		em.persist(timeSlot);
		em.flush();
	}

	@Override
	public void update(TimeSlot timeSlot) {

		log.info("Updating time slot: {}", timeSlot.toString());

		em.persist(em.merge(timeSlot));
		em.flush();
	}

	@Override
	public void delete(TimeSlot timeSlot) {

		log.info("Deleting time slot: {}", timeSlot.toString());

		em.remove(em.merge(timeSlot));
		em.flush();
	}

	@Override
	public TimeSlot getTimeSlot(Integer id) {

		log.debug("Getting time slot with id: {}", id);

		Query q = em.createQuery("FROM TimeSlot WHERE id = :id");
		q.setParameter("id", id);

		try {
			TimeSlot ts = (TimeSlot) q.getSingleResult();
			this.initializeTimeSlot(ts);
			return ts;
		}catch(NoResultException nre){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TimeSlot> getAllTimeSlots() {

		log.debug("Getting list of time slots");

		Query q = em.createQuery("FROM TimeSlot");

		List<TimeSlot> tsList = q.getResultList();
		for (TimeSlot ts : tsList)
			this.initializeTimeSlot(ts);

		return tsList;
	}

	@Override
	public Integer getTimeSlotFromTime(Calendar cal) {

		Integer result = 0;

		Query query = em.createQuery("SELECT t.name FROM TimeSlot t JOIN t.details WHERE day_of_week = :day_of_week AND start_time <= :start_time AND end_time >= :end_time");
		query.setParameter("day_of_week", cal.get(Calendar.DAY_OF_WEEK) - 1);
		query.setParameter("start_time", cal.getTime(), TemporalType.TIME);
		query.setParameter("end_time", cal.getTime(), TemporalType.TIME);

		String tsName = (String) query.getSingleResult();
		if (tsName.equals("F1"))
			result = 1;
		else if (tsName.equals("F2"))
			result = 2;
		else if (tsName.equals("F3"))
			result = 3;

		return result;
	}

	private void initializeTimeSlot(TimeSlot ts) {
		Hibernate.initialize(ts.getDetails());
	}
}
