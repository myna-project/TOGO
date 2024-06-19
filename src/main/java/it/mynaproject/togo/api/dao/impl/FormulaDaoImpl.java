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

import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.FormulaDao;
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.exception.GenericException;

@Repository
public class FormulaDaoImpl extends BaseDaoImpl implements FormulaDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Formula formula) {

		log.info("Creating new formula: {}", formula.toString());

		em.persist(formula);
		em.flush();
	}

	@Override
	public void update(Formula formula) {

		log.info("Updating formula: {}", formula.toString());

		em.persist(em.merge(formula));
		em.flush();
	}

	@Override
	public void delete(Formula formula) {

		log.info("Deleting formula: {}", formula.toString());

		em.remove(em.merge(formula));
		em.flush();
	}

	@Override
	public Formula getFormula(Integer id) {

		log.debug("Getting formula with id: {}", id);

		Query q = em.createQuery("FROM Formula WHERE id=:id");
			q.setParameter("id", id);

		try {
			Formula f = (Formula) q.getSingleResult();
			this.initializeFormula(f);
			return f;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Formula> getAllFormulas() {

		log.debug("Getting all formulas");

		Query q = em.createQuery("FROM Formula");

		List<Formula> formulas = q.getResultList();
		for (Formula f : formulas)
			this.initializeFormula(f);

		return formulas;
	}

	private void initializeFormula(Formula f) {
		Hibernate.initialize(f.getComponents());

		List<FormulaComponent> fcs = f.getComponents();
		Collections.sort(fcs);
		if (fcs.size() == 0)
			throw new GenericException(12001, "No components for formula " + f.getId() + ".");
	}
}
