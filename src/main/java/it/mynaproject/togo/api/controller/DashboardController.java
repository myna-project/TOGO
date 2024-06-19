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
import java.util.List;

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
import it.mynaproject.togo.api.domain.Dashboard;
import it.mynaproject.togo.api.model.DashboardJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.UserJson;
import it.mynaproject.togo.api.service.DashboardService;

@RestController
public class DashboardController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DashboardService dashboardService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/admin/organization/dashboards/{id}")
	public Object getDashboard(@PathVariable("id") Integer id) {

		log.debug("Request for dashboard with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.dashboardToDashboardJson((this.dashboardService.getDashboard(id, true, user.getUsername())));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/admin/organization/dashboards")
	public Object getDashboards() {

		log.debug("Request for dashboards");

		List<DashboardJson> djList = new ArrayList<>();
		for (Dashboard d : this.dashboardService.getDashboards())
			djList.add(JsonUtil.dashboardToDashboardJson(d));

		return djList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/dashboards/{id}/assigned-users")
	public Object getUserAssignedToDashboard(@PathVariable("id") Integer id) {

		log.debug("Request for users assigned to dashboard with id {}",id);

		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Dashboard d = this.dashboardService.getDashboard(id, true, user.getUsername());
		List<UserJson> ujList = new ArrayList<>();
		for (it.mynaproject.togo.api.domain.DashboardsUsers du : d.getDashboardsUsers())
			ujList.add(JsonUtil.userToUserJson(du.getUser()));

		return ujList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/dashboards")
	public Object postDahboard(@Valid @RequestBody DashboardJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardToDashboardJson(this.dashboardService.createDashboardFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/dashboards/{id}")
	public Object updateDashboard(@PathVariable("id") Integer id, @Valid @RequestBody DashboardJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardToDashboardJson(this.dashboardService.updateDashboardFromInput(id, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/dashboards/{id}")
	public Object deleteDashboard(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.dashboardService.deleteDashboardById(id, true, user.getUsername());

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
	@GetMapping(value = "/organization/dashboards/{id}")
	public Object getDashboardForUser(@PathVariable("id") Integer id) {

		log.debug("Request for dashboard with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.dashboardToDashboardJson((this.dashboardService.getDashboard(id, false, user.getUsername())));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/organization/dashboards")
	public Object getDashboardsForUser() {

		log.debug("Request for dashboards");

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<DashboardJson> djList = new ArrayList<>();
		for (Dashboard d : this.dashboardService.getDashboardsForUser(user.getUsername()))
			djList.add(JsonUtil.dashboardToDashboardJson(d));

		return djList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/dashboards")
	public Object postDashboardForUser(@Valid @RequestBody DashboardJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardToDashboardJson(this.dashboardService.createDashboardFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/dashboards/{id}")
	public Object updateDashboardForUser(@PathVariable("id") Integer id, @Valid @RequestBody DashboardJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.dashboardToDashboardJson(this.dashboardService.updateDashboardFromInput(id, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/dashboards/{id}")
	public Object deleteDashboardForUser(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.dashboardService.deleteDashboardById(id, false, user.getUsername());
		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);

	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/dashboards/{id}/assigned-users")
	public Object getUserAssignedToJobForUser(@PathVariable("id") Integer id) {

		log.debug("Request for users assigned to dashboard with id {}",id);

		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Dashboard d = this.dashboardService.getDashboard(id, false, user.getUsername());
		List<UserJson> ujList = new ArrayList<>();
		for (it.mynaproject.togo.api.domain.DashboardsUsers du : d.getDashboardsUsers())
			ujList.add(JsonUtil.userToUserJson(du.getUser()));

		return ujList;
	}
}
