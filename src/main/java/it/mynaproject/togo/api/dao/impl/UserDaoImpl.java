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

import it.mynaproject.togo.api.dao.UserDao;
import it.mynaproject.togo.api.domain.User;

@Repository
public class UserDaoImpl extends BaseDaoImpl implements UserDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(User user) {

		log.info("Creating new user: {}", user.getUsername());

		em.persist(user);
		em.flush();
	}

	@Override
	public void update(User user) {

		log.info("Updating user: {}", user.getUsername());

		em.persist(em.merge(user));
		em.flush();
	}

	@Override
	public void delete(User user) {

		log.debug("Deleting user: {}", user.getUsername());

		em.remove(em.merge(user));
		em.flush();
	}

	@Override
	public User getUser(Integer id) {

		log.debug("Getting users with id: {}", id);

		Query q = em.createQuery("FROM User WHERE id = :id");
		q.setParameter("id", id);

		try {
			User u = (User) q.getSingleResult();
			this.initializeUser(u);
			return u;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUsers() {

		log.debug("Getting users");

		List<User> users = em.createQuery("FROM User").getResultList();
		for (User u : users)
			this.initializeUser(u);

		return users;
	}

	@Override
	public User getUserByUsername(String username) {

		log.debug("Getting user with username: {}", username);

		Query q = em.createQuery("FROM User WHERE username LIKE :username");
		q.setParameter("username", username);

		try {
			User u = (User) q.getSingleResult();
			this.initializeUser(u);
			return u;
		} catch (NoResultException nre) {
			return null;
		}
	}

	private void initializeUser(User u) {
		Hibernate.initialize(u.getRoles());
		Hibernate.initialize(u.getJobs());
		Hibernate.initialize(u.getDashboardsUsers());
	}
}
