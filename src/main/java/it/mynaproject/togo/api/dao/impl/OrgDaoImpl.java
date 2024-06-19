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

import it.mynaproject.togo.api.dao.OrgDao;
import it.mynaproject.togo.api.domain.Org;

@Repository
public class OrgDaoImpl extends BaseDaoImpl implements OrgDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Org org) {

		log.info("Creating new org: {}", org.toString());

		em.persist(org);
		em.flush();
	}

	@Override
	public void update(Org org) {

		log.info("Updating org: {}", org.toString());

		em.persist(em.merge(org));
		em.flush();
	}

	@Override
	public void delete(Org org) {

		log.info("Deleting org: {}", org.toString());

		em.remove(em.merge(org));
		em.flush();
	}

	@Override
	public Org getOrg(Integer id) {

		log.debug("Getting org with id: {}", id);

		Query q = em.createQuery("FROM Org WHERE id = :id");
		q.setParameter("id", id);

		try {
			Org o = (Org) q.getSingleResult();
			this.initializeOrg(o);
			return o;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Org> getOrgs() {

		log.debug("List all orgs");

		List<Org> orgs = em.createQuery("FROM Org").getResultList();
		for (Org o : orgs)
			this.initializeOrg(o);

		return orgs;
	}

	private void initializeOrg(Org o) {
		Hibernate.initialize(o.getClients());
		Hibernate.initialize(o.getJobs());
	}
}
