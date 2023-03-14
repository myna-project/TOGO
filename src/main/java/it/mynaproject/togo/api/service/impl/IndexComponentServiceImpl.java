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

import it.mynaproject.togo.api.dao.IndexComponentDao;
import it.mynaproject.togo.api.domain.IndexComponent;
import it.mynaproject.togo.api.service.IndexComponentService;

@Service
public class IndexComponentServiceImpl implements IndexComponentService {

	@Autowired
	private IndexComponentDao indexComponentDao;

	@Override
	@Transactional
	public void persist(IndexComponent indexComponent) {
		this.indexComponentDao.persist(indexComponent);
	}

	@Override
	@Transactional
	public void update(IndexComponent indexComponent) {
		this.indexComponentDao.update(indexComponent);
	}

	@Override
	@Transactional
	public void delete(IndexComponent indexComponent) {
		this.indexComponentDao.delete(indexComponent);
	}

	@Override
	@Transactional
	public IndexComponent getIndexComponentById(Integer id) {
		return this.indexComponentDao.getIndexComponentById(id);
	}
}
