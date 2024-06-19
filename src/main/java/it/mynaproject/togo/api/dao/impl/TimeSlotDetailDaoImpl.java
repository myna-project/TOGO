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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.TimeSlotDetailDao;
import it.mynaproject.togo.api.domain.TimeSlotDetail;

@Repository
public class TimeSlotDetailDaoImpl extends BaseDaoImpl implements TimeSlotDetailDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(TimeSlotDetail timeSlotDetail) {

		log.info("Creating new time slot detail: {}", timeSlotDetail.toString());

		em.persist(timeSlotDetail);
		em.flush();
	}

	@Override
	public void update(TimeSlotDetail timeSlotDetail) {

		log.info("Updating time slot detail: {}", timeSlotDetail.toString());

		em.persist(em.merge(timeSlotDetail));
		em.flush();
	}

	@Override
	public void delete(TimeSlotDetail timeSlotDetail) {

		log.info("Deleting time slot detail: {}", timeSlotDetail.toString());

		em.remove(em.merge(timeSlotDetail));
		em.flush();
	}

	@Override
	public TimeSlotDetail getTimeSlotDetail(Integer id) {

		log.debug("Getting time slot detail with id: {}", id);

		Query q = em.createQuery("FROM TimeSlotDetail WHERE id = :id");
		q.setParameter("id", id);

		try {
			return (TimeSlotDetail) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
