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

import it.mynaproject.togo.api.dao.IndexComponentDao;
import it.mynaproject.togo.api.domain.IndexComponent;

@Repository
public class IndexComponentDaoImpl extends BaseDaoImpl implements IndexComponentDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(IndexComponent indexComponent) {

		log.info("Creating new index component: {}", indexComponent.toString());

		em.persist(indexComponent);
		em.flush();
	}

	@Override
	public void update(IndexComponent indexComponent) {

		log.info("Updating index component: {}", indexComponent.toString());

		em.persist(em.merge(indexComponent));
		em.flush();
	}

	@Override
	public void delete(IndexComponent indexComponent) {

		log.info("Deleting index component: {}", indexComponent.toString());

		em.remove(em.merge(indexComponent));
		em.flush();
	}

	@Override
	public IndexComponent getIndexComponentById(Integer id) {

		log.debug("Getting indexComponent with id: {}", id);

		Query q = em.createQuery("FROM IndexComponent WHERE id=:id");
		q.setParameter("id", id);

		try {
			return (IndexComponent) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}
