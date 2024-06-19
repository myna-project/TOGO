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

import it.mynaproject.togo.api.dao.ClientCategoryDao;
import it.mynaproject.togo.api.domain.ClientCategory;

@Repository
public class ClientCategoryDaoImpl extends BaseDaoImpl implements ClientCategoryDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(ClientCategory category) {

		log.info("Creating new client category: {}", category.toString());

		em.persist(category);
		em.flush();
	}

	@Override
	public void update(ClientCategory category) {

		log.info("Updating client category: {}", category.toString());

		em.persist(em.merge(category));
		em.flush();
	}

	@Override
	public void delete(ClientCategory category) {

		log.info("Deleting client category: {}", category.toString());

		em.remove(em.merge(category));
		em.flush();
	}

	@Override
	public ClientCategory getClientCategory(Integer id) {

		log.debug("Getting client category with id: {}", id);

		Query q = em.createQuery("FROM ClientCategory WHERE id = :id");
		q.setParameter("id", id);

		try {
			return (ClientCategory) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClientCategory> getClientCategories() {

		log.debug("Getting all client categories");

		return em.createQuery("FROM ClientCategory").getResultList();
	}
}
