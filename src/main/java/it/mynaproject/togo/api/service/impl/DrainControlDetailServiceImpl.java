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

import it.mynaproject.togo.api.dao.DrainControlDetailDao;
import it.mynaproject.togo.api.domain.DrainControlDetail;
import it.mynaproject.togo.api.service.DrainControlDetailService;

@Service
public class DrainControlDetailServiceImpl implements DrainControlDetailService {

	@Autowired
	private DrainControlDetailDao drainControlDetailDao;

	@Override
	@Transactional
	public void persist(DrainControlDetail detail) {
		this.drainControlDetailDao.persist(detail);
	}

	@Override
	@Transactional
	public void update(DrainControlDetail detail) {
		this.drainControlDetailDao.update(detail);
	}

	@Override
	@Transactional
	public void delete(DrainControlDetail detail) {
		this.drainControlDetailDao.delete(detail);
	}
}
