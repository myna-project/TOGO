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

import it.mynaproject.togo.api.dao.IndexDao;
import it.mynaproject.togo.api.domain.Index;
import it.mynaproject.togo.api.domain.IndexComponent;
import it.mynaproject.togo.api.exception.GenericException;

@Repository
public class IndexDaoImpl extends BaseDaoImpl implements IndexDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Index index) {

		log.info("Creating new index: {}", index.toString());

		em.persist(index);
		em.flush();
	}

	@Override
	public void update(Index index) {

		log.info("Updating index: {}", index.toString());

		em.persist(em.merge(index));
		em.flush();
	}

	@Override
	public void delete(Index index) {

		log.info("Deleting index: {}", index.toString());

		em.remove(em.merge(index));
		em.flush();
	}

	@Override
	public Index getIndex(Integer id) {

		log.debug("Getting index with id: {}", id);

		Query q = em.createQuery("FROM Index WHERE id = :id");
		q.setParameter("id", id);

		try {
			Index i = (Index) q.getSingleResult();
			this.initializeIndex(i);
			return i;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Index> getIndices() {

		log.debug("Getting indices");

		Query q = em.createQuery("FROM Index");

		List<Index> indices = q.getResultList();
		for (Index i : indices)
			this.initializeIndex(i);

		return indices;
	}

	private void initializeIndex(Index i) {
		Hibernate.initialize(i.getComponents());

		List<IndexComponent> ics = i.getComponents();
		Collections.sort(ics);
		if (ics.size() == 0)
			throw new GenericException(12001, "No components for index " + i.getId() + ".");
		
		for (IndexComponent ic : i.getComponents()) {
			Hibernate.initialize(ic.getFormula().getComponents());
			Collections.sort(ic.getFormula().getComponents());
		}
	}
}
