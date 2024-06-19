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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.DrainControlDao;
import it.mynaproject.togo.api.domain.DrainControl;

@Repository
public class DrainControlDaoImpl extends BaseDaoImpl implements DrainControlDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(DrainControl control) {

		log.info("Creating new drain control: {}", control.toString());

		em.persist(control);
		em.flush();
	}

	@Override
	public void update(DrainControl control) {

		log.info("Updating drain control: {}", control.toString());

		em.persist(em.merge(control));
		em.flush();
	}

	@Override
	public void delete(DrainControl control) {

		log.info("Deleting drain control: {}", control.toString());

		em.remove(em.merge(control));
		em.flush();
	}

	@Override
	public DrainControl getDrainControl(Integer id) {

		log.debug("Getting drain control with id: {}", id);

		Query q = em.createQuery("FROM DrainControl WHERE id = :id");
		q.setParameter("id", id);

		try {
			DrainControl control = (DrainControl) q.getSingleResult();
			this.initializeDrainControl(control);
			return control;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DrainControl> getDrainControls() {

		log.debug("Getting all drain controls");

		List<DrainControl> controls = em.createQuery("FROM DrainControl").getResultList();
		for (DrainControl c : controls)
			this.initializeDrainControl(c);

		return controls;
	}

	private void initializeDrainControl(DrainControl c) {
		Hibernate.initialize(c.getDetails());
	}
}
