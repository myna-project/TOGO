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

import it.mynaproject.togo.api.dao.DashboardWidgetDao;
import it.mynaproject.togo.api.domain.DashboardWidget;
import it.mynaproject.togo.api.domain.DashboardWidgetDetail;

@Repository
public class DashboardWidgetDaoImpl extends BaseDaoImpl implements DashboardWidgetDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(DashboardWidget widget) {

		log.info("Creating new widget: {}", widget.toString());

		em.persist(widget);
		em.flush();
	}

	@Override
	public void update(DashboardWidget widget) {

		log.info("Updating widget: {}", widget.toString());

		em.persist(em.merge(widget));
		em.flush();
	}

	@Override
	public void delete(DashboardWidget widget) {

		log.info("Deleting widget: {}", widget.toString());

		em.remove(em.merge(widget));
		em.flush();
	}

	@Override
	public DashboardWidget getDashboardWidget(Integer id) {

		log.debug("Getting widget with id: {}", id);

		Query q = em.createQuery("FROM DashboardWidget WHERE id = :id");
		q.setParameter("id", id);

		try {
			DashboardWidget widget = (DashboardWidget) q.getSingleResult();
			this.initializeDashboardWidget(widget);
			return widget;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DashboardWidget> getDashboardWidgets() {

		log.debug("Getting all widgets");

		List<DashboardWidget> widgets = em.createQuery("FROM DashboardWidget").getResultList();
		for (DashboardWidget c : widgets)
			this.initializeDashboardWidget(c);

		return widgets;
	}

	private void initializeDashboardWidget(DashboardWidget w) {
		Hibernate.initialize(w.getDetails());

		// Bubble sort to obtain the correct order of details in widget
		for (int i = 0; i < (w.getDetails().size() - 1); i++) {
			for (Integer j = 0; j < (w.getDetails().size() - 1); j++) {
				if (w.getDetails().get(j).getId() > w.getDetails().get(j + 1).getId()) {
					DashboardWidgetDetail temp = w.getDetails().get(j);
					w.getDetails().set(j, w.getDetails().get(j + 1));
					w.getDetails().set(j + 1, temp);
				}
			}
		}
	}
}
