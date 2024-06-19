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

import it.mynaproject.togo.api.dao.UserDao;
import it.mynaproject.togo.api.domain.Dashboard;
import it.mynaproject.togo.api.domain.DashboardsUsers;
import it.mynaproject.togo.api.domain.DashboardWidget;
import it.mynaproject.togo.api.domain.DashboardWidgetDetail;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.service.DashboardService;
import it.mynaproject.togo.api.service.DashboardWidgetDetailService;
import it.mynaproject.togo.api.service.DashboardWidgetService;
import it.mynaproject.togo.api.service.OrgService;
import it.mynaproject.togo.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.DashboardDao;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.DashboardJson;
import it.mynaproject.togo.api.util.Pair;

@Service
public class DashboardServiceImpl implements DashboardService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DashboardDao dashboardDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserService userService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private DashboardWidgetService dashboardWidgetService;

	@Autowired
	private DashboardWidgetDetailService dashboardWidgetDetailService;

	@Override
	@Transactional
	public Dashboard getDashboard(Integer id, Boolean isAdmin, String username) {

		Dashboard d = this.dashboardDao.getDashboard(id);
		if ((d == null) || (!isAdmin && !this.getDashboardsForUser(username).contains(d)))
			throw new NotFoundException(404, "Dashboard " + id + " not found");

		return d;
	}

	@Override
	@Transactional
	public List<Dashboard> getDashboards() {
		return this.dashboardDao.getDashboards();
	}

	@Override
	@Transactional
	public List<Dashboard> getDashboardsForUser(String username) {

		List<Dashboard> dashboards = new ArrayList<>();
		for (Dashboard d : this.dashboardDao.getDashboards())
			if ((d != null) && ((username != null) && this.orgService.orgIsVisibleByUser(d.getOrg(), username)))
				dashboards.add(d);

		return dashboards;
	}

	@Override
	@Transactional
	public void persist(Dashboard dashboard) {
		this.dashboardDao.persist(dashboard);
	}

	@Override
	@Transactional
	public Dashboard createDashboardFromInput(DashboardJson input, Boolean isAdmin, String username) {

		log.info("Creating new dashboard: {}", input.toString());

		Org org = orgService.getOrg(input.getOrgId(), isAdmin, username);

		Dashboard dashboard = new Dashboard();
		dashboard.populateDashboardFromInput(input, org);

		List<DashboardsUsers> dashboardsUsersList = new ArrayList<>();
		for (Integer userId : input.getUserIds()) {
			User u = this.userService.getUser(userId, isAdmin, username);
			DashboardsUsers du = new DashboardsUsers(dashboard, u);
			du.setDefaultDashboard(this.checkDashboardForUser(input, u,u.getUsername().equals(username), null));
			dashboardsUsersList.add(du);
		}
		dashboard.setDashboardsUsers(dashboardsUsersList);

		if (input.getDuplicateDashboardId() != null) {
			dashboard.setDashboardWidgets(new ArrayList<DashboardWidget>());
			this.persist(dashboard);
			List<DashboardWidget> dashboardWidgetsList = this.dashboardWidgetService.getDashboardWidgets(input.getDuplicateDashboardId(), username);
			List<DashboardWidget> newDashboardWidgetList = new ArrayList<DashboardWidget>();
			for (DashboardWidget dashboardWidget : dashboardWidgetsList) {
				DashboardWidget newDashboardWidget = new DashboardWidget();
				newDashboardWidget.setDashboard(dashboard);
				newDashboardWidget.duplicateDashboardWidget(dashboardWidget,dashboardWidget.getCostsDrain());
				this.dashboardWidgetService.persist(newDashboardWidget);
				List<DashboardWidgetDetail> details = new ArrayList<DashboardWidgetDetail>();
				for (DashboardWidgetDetail dashboardWidgetDetail : dashboardWidget.getDetails()) {
					DashboardWidgetDetail newDetail = new DashboardWidgetDetail();
					newDetail.duplicateDashboardWidgetDetail(dashboardWidgetDetail);
					newDetail.setDashboardWidget(newDashboardWidget);
					this.dashboardWidgetDetailService.persist(newDetail);
					details.add(newDetail);
				}
				newDashboardWidget.setDetails(details);
				newDashboardWidgetList.add(newDashboardWidget);
			}
			dashboard.setDashboardWidgets(newDashboardWidgetList);
		} else {
			this.persist(dashboard);
		}

		return dashboard;
	}

	@Override
	@Transactional
	public void update(Dashboard dashboard) {
		this.dashboardDao.update(dashboard);
	}

	@Override
	@Transactional
	public Dashboard updateDashboardFromInput(Integer id, DashboardJson input, Boolean isAdmin, String username) {

		log.info("Update dashboard with id {}", id);

		Dashboard dashboard = this.getDashboard(id, isAdmin, username);

		List<DashboardsUsers> oldDashboardsUsersList = new ArrayList<>();
		for (DashboardsUsers du : dashboard.getDashboardsUsers())
			oldDashboardsUsersList.add(du);

		List<Pair<Integer, Integer>> oldDashboardsUsersListIds = new ArrayList<>();
		for (DashboardsUsers du : oldDashboardsUsersList)
			oldDashboardsUsersListIds.add(new Pair<>(du.getUser().getId(), du.getDashboard().getId()));

		Org org = orgService.getOrg(input.getOrgId(), isAdmin, username);
		dashboard.populateDashboardFromInput(input, org);
		List<DashboardsUsers> dashboardsUsersList = new ArrayList<>();
		List<Pair<Integer, Integer>> newDashboardsUsersListIds = new ArrayList<>();
		for (Integer userId : input.getUserIds()) {
			User u = this.userService.getUser(userId, true, username);
			DashboardsUsers du = new DashboardsUsers(dashboard, u);
			du.setDefaultDashboard(this.checkDashboardForUser(input, u,u.getUsername().equals(username), id));
			newDashboardsUsersListIds.add(new Pair<>(du.getUser().getId(), du.getDashboard().getId()));
			dashboardsUsersList.add(du);
		}

		List<User> removeUserList = new ArrayList<>();
		List<Boolean> isDefaultDashboardOfRemoveUser = new ArrayList<>();
		for (DashboardsUsers duOld : oldDashboardsUsersList) {
			if (!newDashboardsUsersListIds.contains(new Pair<>(duOld.getUser().getId(), duOld.getDashboard().getId()))) {
				dashboard.removeDashboardUser(duOld);
				isDefaultDashboardOfRemoveUser.add(duOld.getDefaultDashboard());
				removeUserList.add(duOld.getUser());
			}
		}
		for (DashboardsUsers duNew : dashboardsUsersList) {
			if (!oldDashboardsUsersListIds.contains(new Pair<>(duNew.getUser().getId(), duNew.getDashboard().getId())))
				dashboard.addDashboardUser(duNew);
			else
				dashboard.updateDashboardUser(duNew);
		}

		this.update(dashboard);

		if (removeUserList.size() > 0) {
			int i = 0;
			for (User u : removeUserList) {
				if (u.getDashboardsUsers().size() == 1 || (isDefaultDashboardOfRemoveUser.get(i) && u.getDashboardsUsers().size() > 0)) {
					Dashboard lastDashboard = u.getDashboardsUsers().get(0).getDashboard();
					DashboardsUsers lastDashboardUser = u.getDashboardsUsers().get(0);
					lastDashboardUser.setDefaultDashboard(true);
					lastDashboard.updateDashboardUser(lastDashboardUser);
					this.update(lastDashboard);
				}
				i++;
			}
		}

		return dashboard;
	}

	@Override
	@Transactional
	public void deleteDashboardById(Integer dashboardId, Boolean isAdmin, String username) {
		User user = new User();
		Dashboard d = this.getDashboard(dashboardId, isAdmin, username);
		List<DashboardsUsers> dashboardsUsersList = new ArrayList<DashboardsUsers>(d.getDashboardsUsers());
		if (dashboardsUsersList.size() > 1) {
			for (DashboardsUsers du : dashboardsUsersList) {
				if (du.getUser().getUsername().equals(username) && !du.getDefaultDashboard()) {
					user = du.getUser();
					user.removeDashboardUser(du);
					d.removeDashboardUser(du);
				}
				if (du.getUser().getUsername().equals(username) && du.getDefaultDashboard())
					throw new ConflictException(16101, "Cannot delete dashboard " + d.getName() + " because it is the default Dashboard");
			}
			this.userDao.update(user);
			this.dashboardDao.update(d);
		} else {
			DashboardsUsers du = dashboardsUsersList.get(0);
			user = du.getUser();
			user.removeDashboardUser(du);
			d.removeDashboardUser(du);
			this.userDao.update(user);
			this.dashboardDao.delete(d);
		}
	}

	public Boolean checkDashboardForUser(DashboardJson input, User user, Boolean currentUser, Integer dashboardId) {

		Boolean defaultDashboard = true;
		Boolean currentUserDefaultDashboard = false;
		int index = 0;
		for (DashboardsUsers du : user.getDashboardsUsers()) {
			if (dashboardId != null) {
				if (du.getDashboard().getName().trim().toLowerCase().equals(input.getName().trim().toLowerCase()))
					if ((dashboardId == null) || (du.getDashboard().getId() != dashboardId))
						throw new ConflictException(16001, "Dashboard name " + input.getName() + " not available");

				if (currentUser) {
					if (du.getDashboard().getId() == dashboardId) {
						defaultDashboard = input.getDefaultDashboard();
						du.setDefaultDashboard(input.getDefaultDashboard());
					} else {
						if (du.getDefaultDashboard())
							currentUserDefaultDashboard = true;
						if (input.getDefaultDashboard())
							du.setDefaultDashboard(false);
					}
					user.updateDashboardUser(du,index);
				} else {
					if (du.getDefaultDashboard() && du.getDashboard().getId() != dashboardId)
						defaultDashboard = false;
				}
			} else {
				if (currentUser) {
					if (input.getDefaultDashboard()) {
						du.setDefaultDashboard(false);
						user.updateDashboardUser(du, index);
					} else {
						currentUserDefaultDashboard = true;
						defaultDashboard = false;
					}
				} else {
					defaultDashboard = false;
				}
			}
			index++;
		}

		return currentUser ? (defaultDashboard ? defaultDashboard : (currentUserDefaultDashboard ? defaultDashboard : true)) : defaultDashboard;
	}
}
