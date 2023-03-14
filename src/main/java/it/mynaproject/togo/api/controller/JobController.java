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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.model.JobJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.UserJson;
import it.mynaproject.togo.api.service.JobService;

@RestController
public class JobController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JobService jobService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/jobs/{id}")
	public Object getJob(@PathVariable("id") Integer id) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.jobToJobJson(this.jobService.getJob(id, true, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/jobs")
	public Object getJobs() {

		log.debug("Request for jobs");

		List<JobJson> jjList = new ArrayList<>();
		for (Job j : this.jobService.getJobs())
			jjList.add(JsonUtil.jobToJobJson(j));

		return jjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/jobs/{id}/assigned-users")
	public Object getUserAssignedToJob(@PathVariable("id") Integer id) {

		log.debug("Request for users assigned to job with id {}",id);

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Job j = this.jobService.getJob(id, true, user.getUsername());

		List<UserJson> jjList = new ArrayList<>();
		for (User u : j.getUsers())
			jjList.add(JsonUtil.userToUserJson(u));

		return jjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/jobs")
	public Object postJob(@Valid @RequestBody JobJson input, HttpServletRequest request) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.jobToJobJson(this.jobService.createJobFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/jobs/{id}")
	public Object updateJob(@PathVariable("id") Integer id, @Valid @RequestBody JobJson input) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.jobToJobJson(this.jobService.updateJobFromInput(id, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/jobs/{id}")
	public Object deleteJob(@PathVariable("id") Integer id) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.jobService.deleteJobById(id, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	/*
	 *  -------------
	 *  USER SECTION
	 *  -------------
	 *  These routes can be accessible for users without ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/jobs/{id}")
	public JobJson getJobForUser(@PathVariable("id") Integer id) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.jobToJobJson(this.jobService.getJob(id, false, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/jobs")
	public Object getJobsForUser() {

		log.debug("Request for jobs");

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<JobJson> jjList = new ArrayList<>();
		for (Job j : this.jobService.getJobsForUser(user.getUsername()))
			jjList.add(JsonUtil.jobToJobJson(j));

		return jjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/jobs/{id}/assigned-users")
	public Object getUserAssignedToJobForUser(@PathVariable("id") Integer id) {

		log.debug("Request for users assigned to job with id {}",id);

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Job j = this.jobService.getJob(id, false, user.getUsername());

		List<UserJson> jjList = new ArrayList<>();
		for (User u : j.getUsers())
			jjList.add(JsonUtil.userToUserJson(u));

		return jjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/jobs")
	public Object postJobForUser(@Valid @RequestBody JobJson input,HttpServletRequest request) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.jobToJobJson(this.jobService.createJobFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/jobs/{id}")
	public Object updateJobForUser(@PathVariable("id") Integer id, @Valid @RequestBody JobJson input) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.jobToJobJson(this.jobService.updateJobFromInput(id, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/jobs/{id}")
	public Object deleteJobForUser(@PathVariable("id") Integer id) {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.jobService.deleteJobById(id, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
