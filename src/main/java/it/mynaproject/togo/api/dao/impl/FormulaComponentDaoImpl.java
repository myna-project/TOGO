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

import it.mynaproject.togo.api.dao.FormulaComponentDao;
import it.mynaproject.togo.api.domain.FormulaComponent;

@Repository
public class FormulaComponentDaoImpl extends BaseDaoImpl implements FormulaComponentDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(FormulaComponent formulaComponent) {

		log.info("Creating new formula component: {}", formulaComponent.toString());

		em.persist(formulaComponent);
		em.flush();
	}

	@Override
	public void update(FormulaComponent formulaComponent) {

		log.info("Updating formula component: {}", formulaComponent.toString());

		em.persist(em.merge(formulaComponent));
		em.flush();
	}

	@Override
	public void delete(FormulaComponent formulaComponent) {

		log.info("Deleting formula component: {}", formulaComponent.toString());

		em.remove(em.merge(formulaComponent));
		em.flush();
	}

	@Override
	public FormulaComponent getFormulaComponentById(Integer id) {

		log.debug("Getting formulaComponent with id: {}", id);

		Query q = em.createQuery("FROM FormulaComponent WHERE id=:id");
		q.setParameter("id", id);

		try {
			return (FormulaComponent) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
