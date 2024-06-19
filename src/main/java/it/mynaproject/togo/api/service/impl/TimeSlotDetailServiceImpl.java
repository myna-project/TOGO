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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.TimeSlotDetailDao;
import it.mynaproject.togo.api.domain.TimeSlotDetail;
import it.mynaproject.togo.api.service.TimeSlotDetailService;

@Service
public class TimeSlotDetailServiceImpl implements TimeSlotDetailService {

	@Autowired
	private TimeSlotDetailDao timeSlotDetailDao;

	@Override
	@Transactional
	public void persist(TimeSlotDetail timeSlotDetail) {
		this.timeSlotDetailDao.persist(timeSlotDetail);
	}

	@Override
	@Transactional
	public void update(TimeSlotDetail timeSlotDetail) {
		this.timeSlotDetailDao.update(timeSlotDetail);
	}

	@Override
	@Transactional
	public void delete(TimeSlotDetail timeSlotDetail) {
		this.timeSlotDetailDao.delete(timeSlotDetail);
	}

	@Override
	@Transactional
	public TimeSlotDetail getTimeSlotDetail(Integer id) {
		return this.timeSlotDetailDao.getTimeSlotDetail(id);
	}
}
