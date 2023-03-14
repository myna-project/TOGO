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
package it.mynaproject.togo.api.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.domain.DashboardWidget;
import it.mynaproject.togo.api.model.DashboardWidgetJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.DashboardWidgetService;

@RestController
public class DashboardWidgetController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DashboardWidgetService dashboardWidgetService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/admin/organization/dashboard/{dashboard_id}/dashboard_widgets/{id}")
	public Object getDashboardWidget(@PathVariable("dashboard_id") Integer dashboardId, @PathVariable("id") Integer id) {

		log.debug("Request for dashboard widget with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.dashboardWidgetToDashboardWidgetJson(this.dashboardWidgetService.getDashboardWidget(id, dashboardId, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/admin/organization/dashboard/{dashboard_id}/dashboard_widgets")
	public Object getDashboardWidgets(@PathVariable("dashboard_id") Integer dashboardId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<DashboardWidgetJson> wjList = new ArrayList<>();
		for (DashboardWidget widget : this.dashboardWidgetService.getDashboardWidgets(dashboardId, user.getUsername()))
			wjList.add((DashboardWidgetJson) JsonUtil.dashboardWidgetToDashboardWidgetJson(widget));

		return wjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/dashboard/{dashboard_id}/dashboard_widgets")
	public Object postDashboardWidget(@PathVariable("dashboard_id") Integer dashboardId, @Valid @RequestBody DashboardWidgetJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardWidgetToDashboardWidgetJson(this.dashboardWidgetService.createDashboardWidgetFromInput(dashboardId, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/dashboard/{dashboard_id}/dashboard_widgets/{id}")
	public Object updateDashboardWidget(@PathVariable("dashboard_id") Integer dashboardId, @PathVariable("id") Integer id, @Valid @RequestBody DashboardWidgetJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardWidgetToDashboardWidgetJson(this.dashboardWidgetService.updateDashboardWidgetFromInput(id, dashboardId, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/dashboard/{dashboard_id}/dashboard_widgets/{id}")
	public Object deleteDashboardWidget(@PathVariable("dashboard_id") Integer dashboardId, @PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.dashboardWidgetService.deleteDashboardWidgetById(id, dashboardId,true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	/*
	 *  -------------
	 *  USER SECTION
	 *  -------------
	 *  These routes can be accessible for users without ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/organization/dashboard/{dashboard_id}/dashboard_widgets/{id}")
	public Object getDashboardWidgetForUser(@PathVariable("dashboard_id") Integer dashboardId, @PathVariable("id") Integer id) {

		log.debug("Request for widget with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.dashboardWidgetToDashboardWidgetJson(this.dashboardWidgetService.getDashboardWidget(id, dashboardId, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/organization/dashboard/{dashboard_id}/dashboard_widgets")
	public Object getDashboardWidgetsForUser(@PathVariable("dashboard_id") Integer dashboardId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<DashboardWidgetJson> wjList = new ArrayList<>();
		for (DashboardWidget widget : this.dashboardWidgetService.getDashboardWidgets(dashboardId, user.getUsername()))
			wjList.add((DashboardWidgetJson) JsonUtil.dashboardWidgetToDashboardWidgetJson(widget));

		return wjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/dashboard/{dashboard_id}/dashboard_widgets")
	public Object postDashboardWidgetForUser(@PathVariable("dashboard_id") Integer dashboardId, @Valid @RequestBody DashboardWidgetJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardWidgetToDashboardWidgetJson(this.dashboardWidgetService.createDashboardWidgetFromInput(dashboardId, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/dashboard/{dashboard_id}/dashboard_widgets/{id}")
	public Object updateDashboardWidgetForUser(@PathVariable("dashboard_id") Integer dashboardId, @PathVariable("id") Integer id, @Valid @RequestBody DashboardWidgetJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardWidgetToDashboardWidgetJson(this.dashboardWidgetService.updateDashboardWidgetFromInput(id, dashboardId, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/dashboard/{dashboard_id}/dashboard_widgets/{id}")
	public Object deleteDashboardWidgetForUser(@PathVariable("dashboard_id") Integer dashboardId, @PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.dashboardWidgetService.deleteDashboardWidgetById(id, dashboardId, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
