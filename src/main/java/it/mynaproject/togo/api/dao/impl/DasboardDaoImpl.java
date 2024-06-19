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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.DashboardDao;
import it.mynaproject.togo.api.domain.Dashboard;
import it.mynaproject.togo.api.domain.DashboardsUsers;
import it.mynaproject.togo.api.domain.User;

@Repository
public class DasboardDaoImpl extends BaseDaoImpl implements DashboardDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Dashboard dashboard) {

		log.info("Creating new dashboard: {}", dashboard.toString());

		em.persist(dashboard);
		em.flush();
	}

	@Override
	public void update(Dashboard dashboard) {

		log.info("Updating dashboard: {}", dashboard.toString());

		em.persist(em.merge(dashboard));
		em.flush();
	}

	@Override
	public void delete(Dashboard dashboard) {
		log.info("Deleting dashboard: {}", dashboard.toString());

		em.remove(em.merge(dashboard));
		em.flush();
	}

	@Override
	public Dashboard getDashboard(Integer id) {

		log.debug("Getting dashboard with id: {}", id);

		Query q = em.createQuery("FROM Dashboard WHERE id = :id");
		q.setParameter("id", id);

		try {
			Dashboard d = (Dashboard) q.getSingleResult();
			this.initializeDashboards(d);
			return d;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Dashboard> getDashboards() {

		log.debug("Getting dashboards");

		List<Dashboard> dashboards = em.createQuery("FROM Dashboard").getResultList();
		for (Dashboard d : dashboards)
			this.initializeDashboards(d);

		return dashboards;
	}

	@Override
	public List<User> getAssignedUsers(Integer dashboardId) {

		log.debug("Getting user assigned to dashboard with id: {}", dashboardId);

		Dashboard dashboard = this.getDashboard(dashboardId);
		if (dashboard == null)
			return null;

		Hibernate.initialize(dashboard.getDashboardsUsers());
		List<User> users = new ArrayList<>();
		for (DashboardsUsers du : dashboard.getDashboardsUsers())
			users.add(du.getUser());

		for (User u : users) {
			Hibernate.initialize(u.getRoles());
			Hibernate.initialize(u.getJobs());
		}

		return users;
	}

	private void initializeDashboards(Dashboard d) {
		Hibernate.initialize(d.getDashboardsUsers());
		Hibernate.initialize(d.getDashboardWidgets());
	}
}
