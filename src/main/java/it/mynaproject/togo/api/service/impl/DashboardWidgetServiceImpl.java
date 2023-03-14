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
package it.mynaproject.togo.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.domain.Dashboard;
import it.mynaproject.togo.api.domain.DashboardsUsers;
import it.mynaproject.togo.api.dao.DashboardWidgetDao;
import it.mynaproject.togo.api.dao.DashboardWidgetDetailDao;
import it.mynaproject.togo.api.domain.DashboardWidget;
import it.mynaproject.togo.api.domain.DashboardWidgetDetail;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.DashboardWidgetDetailJson;
import it.mynaproject.togo.api.model.DashboardWidgetJson;
import it.mynaproject.togo.api.service.DashboardService;
import it.mynaproject.togo.api.service.DashboardWidgetService;
import it.mynaproject.togo.api.service.DrainControlService;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.FormulaService;
import it.mynaproject.togo.api.service.IndexService;
import it.mynaproject.togo.api.service.UserService;

@Service
public class DashboardWidgetServiceImpl implements DashboardWidgetService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DashboardWidgetDao dashboardWidgetDao;

	@Autowired
	private DashboardWidgetDetailDao dashboardWidgetDetailDao;

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private DrainService drainService;

	@Autowired
	private FormulaService formulaService;

	@Autowired
	private IndexService indexService;

	@Autowired
	private DrainControlService drainControlService;

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public DashboardWidget getDashboardWidget(Integer id, Integer dashboardId, String username) {

		User user = this.userService.getUserByUsername(username);
		DashboardWidget widget = this.dashboardWidgetDao.getDashboardWidget(id);
		Dashboard dashboard = this.dashboardService.getDashboard(dashboardId,true, username);
		List<User> userList = new ArrayList<>();
		for (DashboardsUsers du : dashboard.getDashboardsUsers()) {
			userList.add(du.getUser());
		}

		if ((widget == null) || (!userList.contains(user)))
			throw new NotFoundException(404, "Widget " + id + " not found");

		return widget;
	}

	@Override
	@Transactional
	public List<DashboardWidget> getDashboardWidgets(Integer dashboardId, String username) {

		Dashboard dashboard = this.dashboardService.getDashboard(dashboardId, true, username);

		List<DashboardWidget> widgetList = new ArrayList<DashboardWidget>();
		for (DashboardWidget widget : this.dashboardWidgetDao.getDashboardWidgets())
			if (widget.getDashboard().getId() == dashboard.getId())
				widgetList.add(widget);

		return widgetList;
	}

	@Override
	@Transactional
	public void persist(DashboardWidget widget) {
		this.dashboardWidgetDao.persist(widget);
	}

	@Override
	@Transactional
	public DashboardWidget createDashboardWidgetFromInput(Integer dashboardId, DashboardWidgetJson input, Boolean isAdmin, String username) {

		log.info("Creating new widget");

		User u = this.userService.getUserByUsername(username);

		Dashboard dashboard = this.dashboardService.getDashboard(dashboardId, true, username);

		Boolean owner = false;
		for (DashboardsUsers du : dashboard.getDashboardsUsers()) {
			if (du.getUser().getId() == u.getId()) {
				owner = true;
				break;
			}
		}
		if (!owner)
			throw new NotFoundException(404, "User " + u.getId() + " not found in dashboard's owners");

		DashboardWidget widget = new DashboardWidget();
		widget.populateDashboardWidgetFromInput(input, ((input.getCostsDrainId() != null) ? this.drainService.getDrain(input.getCostsDrainId(), isAdmin, username) : null), dashboard);

		this.persist(widget);

		widget.setDetails(this.saveWidgetDetails(widget, input, isAdmin, username));

		return widget;
	}

	@Override
	@Transactional
	public void update(DashboardWidget widget) {
		this.dashboardWidgetDao.update(widget);
	}

	@Override
	@Transactional
	public DashboardWidget updateDashboardWidgetFromInput(Integer id, Integer dashboardId, DashboardWidgetJson input, Boolean isAdmin, String username) {

		log.info("Updating widget with id {}", id);

		User u = this.userService.getUserByUsername(username);

		DashboardWidget widget = this.getDashboardWidget(id, dashboardId, username);

		Dashboard dashboard = this.dashboardService.getDashboard(dashboardId,true, username);

		Boolean owner = false;
		for (DashboardsUsers du : dashboard.getDashboardsUsers()) {
			if (du.getUser().getId() == u.getId()) {
				owner = true;
				break;
			}
		}
		if (!owner)
			throw new NotFoundException(404, "User " + u.getId() + " not found in dashboard's owners");

		for (DashboardWidgetDetail d : widget.getDetails())
			this.dashboardWidgetDetailDao.delete(d);

		widget.populateDashboardWidgetFromInput(input, ((input.getCostsDrainId() != null) ? this.drainService.getDrain(input.getCostsDrainId(), isAdmin, username) : null), dashboard);

		widget.setDetails(this.saveWidgetDetails(widget, input, isAdmin, username));

		this.update(widget);

		return widget;
	}

	@Override
	@Transactional
	public void deleteDashboardWidgetById(Integer id, Integer dashboardId, Boolean isAdmin, String username) {

		log.info("Deleting widget with id {}", id);

		this.dashboardWidgetDao.delete(this.getDashboardWidget(id,dashboardId, username));
	}

	private List<DashboardWidgetDetail> saveWidgetDetails(DashboardWidget widget, DashboardWidgetJson input, Boolean isAdmin, String username) {

		List<DashboardWidgetDetail> details = new ArrayList<DashboardWidgetDetail>();
		for (DashboardWidgetDetailJson dj : input.getDetails()) {
			if ((dj.getIndexId() != null) || (dj.getDrainId() != null) || (dj.getFormulaId() != null) || (dj.getDrainControlId() != null)) {
				DashboardWidgetDetail detail = new DashboardWidgetDetail();
				detail.populateDashboardWidgetDetailFromInput(dj, (dj.getDrainId() != null) ? this.drainService.getDrain(dj.getDrainId(), isAdmin, username) : null, (dj.getFormulaId() != null) ? this.formulaService.getFormula(dj.getFormulaId(), isAdmin, username) : null, (dj.getIndexId() != null) ? this.indexService.getIndex(dj.getIndexId(), false, null, null, isAdmin, username) : null, (dj.getDrainControlId() != null) ? this.drainControlService.getDrainControl(dj.getDrainControlId(), isAdmin, username) : null);
				detail.setDashboardWidget(widget);
				this.dashboardWidgetDetailDao.persist(detail);
				details.add(detail);
			}
		}

		return details;
	}
}
