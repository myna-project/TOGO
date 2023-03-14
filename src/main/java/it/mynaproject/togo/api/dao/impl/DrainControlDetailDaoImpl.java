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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.DrainControlDetailDao;
import it.mynaproject.togo.api.domain.DrainControlDetail;

@Repository
public class DrainControlDetailDaoImpl extends BaseDaoImpl implements DrainControlDetailDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(DrainControlDetail detail) {

		log.info("Creating new drain control detail: {}", detail.toString());

		em.persist(detail);
		em.flush();
	}

	@Override
	public void update(DrainControlDetail detail) {

		log.info("Updating drain control detail: {}", detail.toString());

		em.persist(em.merge(detail));
		em.flush();
	}

	@Override
	public void delete(DrainControlDetail detail) {

		log.info("Deleting drain control detail: {}", detail.toString());

		em.remove(em.merge(detail));
		em.flush();
	}

	@Override
	public DrainControlDetail getDrainControlDetail(Integer drainControlId, Integer drainId) {

		log.debug("Getting drain control detail with drain control id {} and drain id {}", drainControlId, drainId);

		Query q = em.createQuery("FROM DrainControlDetail WHERE drain_control_id = :drainControlId AND drain_id = :drainId");
		q.setParameter("drainControlId", drainControlId);
		q.setParameter("drainId", drainId);

		try {
			return (DrainControlDetail) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DrainControlDetail> getDrainControlDetails() {

		log.debug("Getting all drain control details");

		return em.createQuery("FROM DrainControlDetail").getResultList();
	}
}
