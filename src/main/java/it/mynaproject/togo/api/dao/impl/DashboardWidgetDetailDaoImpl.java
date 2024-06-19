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

import it.mynaproject.togo.api.dao.DashboardWidgetDetailDao;
import it.mynaproject.togo.api.domain.DashboardWidgetDetail;

@Repository
public class DashboardWidgetDetailDaoImpl extends BaseDaoImpl implements DashboardWidgetDetailDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(DashboardWidgetDetail detail) {

		log.info("Creating new dashboard widget detail: {}", detail.toString());

		em.persist(detail);
		em.flush();
	}

	@Override
	public void update(DashboardWidgetDetail detail) {

		log.info("Updating dashboard widget detail: {}", detail.toString());

		em.persist(em.merge(detail));
		em.flush();
	}

	@Override
	public void delete(DashboardWidgetDetail detail) {

		log.info("Deleting dashboard widget detail: {}", detail.toString());

		em.remove(em.merge(detail));
		em.flush();
	}

	@Override
	public DashboardWidgetDetail getDashboardWidgetDetail(Integer dashboardWidgetId) {

		log.debug("Getting dashboard widget detail with id {}", dashboardWidgetId);

		Query q = em.createQuery("FROM DashboardWidgetDetail WHERE dashboard_widget_id = :dashboardWidgetId");
		q.setParameter("dashboardWidgetId", dashboardWidgetId);

		try {
			return (DashboardWidgetDetail) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DashboardWidgetDetail> getDashboardWidgetDetails() {

		log.debug("Getting all dashboard widget details");

		return em.createQuery("FROM DashboardWidgetDetail").getResultList();
	}
}
