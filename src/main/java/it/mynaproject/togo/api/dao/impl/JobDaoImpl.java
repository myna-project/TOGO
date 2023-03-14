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

import it.mynaproject.togo.api.dao.JobDao;
import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.User;

@Repository
public class JobDaoImpl extends BaseDaoImpl implements JobDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Job job) {

		log.info("Creating new job: {}", job.toString());

		em.persist(job);
		em.flush();
	}

	@Override
	public void update(Job job) {

		log.info("Updating job: {}", job.toString());

		em.persist(em.merge(job));
		em.flush();
	}

	@Override
	public void delete(Job job) {

		log.info("Deleting job: {}", job.toString());

		em.remove(em.merge(job));
		em.flush();
	}

	@Override
	public Job getJob(Integer id) {

		log.debug("Getting job with id: {}", id);

		Query q = em.createQuery("FROM Job WHERE id = :id");
		q.setParameter("id", id);

		try {
			return (Job) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Job> getJobs() {
		return em.createQuery("FROM Job").getResultList();
	}

	@Override
	public List<User> getAssignedUsers(Integer jobId) {

		log.debug("Getting user assigned to job with id: {}", jobId);

		Job job = this.getJob(jobId);

		if (job == null)
			return null;

		Hibernate.initialize(job.getUsers());
		List<User> users = job.getUsers();
		for (User u : users) {
			Hibernate.initialize(u.getRoles());
			Hibernate.initialize(u.getJobs());
		}

		return users;
	}
}
