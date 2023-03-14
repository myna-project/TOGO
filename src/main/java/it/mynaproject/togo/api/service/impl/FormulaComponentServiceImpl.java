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

import it.mynaproject.togo.api.dao.FormulaComponentDao;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.service.FormulaComponentService;

@Service
public class FormulaComponentServiceImpl implements FormulaComponentService {

	@Autowired
	private FormulaComponentDao formulaComponentDao;

	@Override
	@Transactional
	public void persist(FormulaComponent formulaComponent) {
		this.formulaComponentDao.persist(formulaComponent);
	}

	@Override
	@Transactional
	public void update(FormulaComponent formulaComponent) {
		this.formulaComponentDao.update(formulaComponent);
	}

	@Override
	@Transactional
	public void delete(FormulaComponent formulaComponent) {
		this.formulaComponentDao.delete(formulaComponent);
	}

	@Override
	@Transactional
	public FormulaComponent getFormulaComponentById(Integer id) {
		return this.formulaComponentDao.getFormulaComponentById(id);
	}
}
