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

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.DrainDao;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.GenericException;

@Repository
public class DrainDaoImpl extends BaseDaoImpl implements DrainDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Drain drain) {

		log.info("Creating new drain: {}", drain.toString());

		em.persist(drain);
		em.flush();
	}

	@Override
	public void update(Drain drain) {

		log.info("Updating drain: {}", drain.toString());

		em.persist(em.merge(drain));
		em.flush();
	}

	@Override
	public void delete(Drain drain) {

		log.info("Deleting drain: {}", drain.toString());

		try {
			em.remove(em.merge(drain));
			em.flush();
		} catch(Exception e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				throw new ConflictException(7101, "Cannot delete drain " + drain.getName() + " because there are saved measures", e);
			} else {
				throw new GenericException(7103, "Drain " + drain.getName() + " not deleted due to an error", e);
			}
		}
	}

	@Override
	public Drain getDrain(Integer id) {

		log.debug("Getting drain with id: {}", id);

		Query q = em.createQuery("FROM Drain WHERE id = :id");
		q.setParameter("id", id);

		try {
			Drain d = (Drain) q.getSingleResult();
			this.initializeDrain(d);
			return d;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Drain> getDrains() {

		log.debug("Getting list of drains");

		List<Drain> drains = em.createQuery("FROM Drain").getResultList();
		for (Drain d : drains)
			this.initializeDrain(d);

		return drains;
	}

	private void initializeDrain(Drain d) {
		Hibernate.initialize(d.getControls());
	}
}
