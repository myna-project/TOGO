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

import it.mynaproject.togo.api.dao.IndexGroupDao;
import it.mynaproject.togo.api.domain.IndexGroup;

@Repository
public class IndexGroupDaoImpl extends BaseDaoImpl implements IndexGroupDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(IndexGroup group) {

		log.info("Creating new index group: {}", group.toString());

		em.persist(group);
		em.flush();
	}

	@Override
	public void update(IndexGroup group) {

		log.info("Updating index group: {}", group.toString());

		em.persist(em.merge(group));
		em.flush();
	}

	@Override
	public void delete(IndexGroup group) {

		log.info("Deleting index group: {}", group.toString());

		em.remove(em.merge(group));
		em.flush();
	}

	@Override
	public IndexGroup getIndexGroup(Integer id) {

		log.debug("Getting index group with id: {}", id);

		Query q = em.createQuery("FROM IndexGroup WHERE id = :id");
		q.setParameter("id", id);

		try {
			return (IndexGroup) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndexGroup> getIndexGroups() {

		log.debug("Getting all index groups");

		Query q = em.createQuery("FROM IndexGroup");

		return q.getResultList();
	}
}
