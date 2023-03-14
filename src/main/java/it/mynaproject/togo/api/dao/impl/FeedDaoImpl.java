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

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.FeedDao;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Feed;

@Repository
public class FeedDaoImpl extends BaseDaoImpl implements FeedDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Feed feed) {

		log.info("Creating new feed: {}", feed.toString());

		em.persist(feed);
		em.flush();
	}

	@Override
	public void update(Feed feed) {

		log.info("Updating feed: {}", feed.toString());

		em.persist(em.merge(feed));
		em.flush();
	}

	@Override
	public void delete(Feed feed) {

		log.info("Deleting feed: {}", feed.toString());

		em.remove(em.merge(feed));
		em.flush();
	}

	@Override
	public Feed getFeed(Integer id) {

		log.debug("Getting feed with id: {}", id);

		Query q = em.createQuery("FROM Feed WHERE id=:id");
		q.setParameter("id", id);

		try {
			Feed f = (Feed) q.getSingleResult();
			this.initializeFeed(f);
			return f;
		} catch (NoResultException nre) {
			return null;
		}
	}

	private void initializeFeed(Feed f) {
		Hibernate.initialize(f.getClients());
		Hibernate.initialize(f.getDrains());
		for (Drain d : f.getDrains())
			Hibernate.initialize(d.getControls());
	}
}
