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

import it.mynaproject.togo.api.dao.RoleDao;
import it.mynaproject.togo.api.domain.Role;

@Repository
public class RoleDaoImpl extends BaseDaoImpl implements RoleDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Role role) {

		log.info("Creating new role: {}", role.toString());

		em.persist(role);
		em.flush();
	}

	@Override
	public void update(Role role) {

		log.info("Updating role: {}", role.toString());

		em.persist(role);
		em.flush();
	}

	@Override
	public void delete(Role role) {

		log.info("Deleting role: {}", role.toString());

		em.remove(em.merge(role));
		em.flush();
	}

	@Override
	public Role getRole(Integer id) {

		log.debug("Getting role with id: {}", id);

		Query q = em.createQuery("FROM Role WHERE id=:id");
		q.setParameter("id", id);

		try {
			Role r = (Role) q.getSingleResult();
			this.initializeRole(r);
			return r;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Role> getRoles() {

		List<Role> roles = em.createQuery("FROM Role").getResultList();
		for (Role r : roles)
			this.initializeRole(r);

		return roles;
	}

	private void initializeRole(Role r) {
		Hibernate.initialize(r.getUsers());
	}
}
