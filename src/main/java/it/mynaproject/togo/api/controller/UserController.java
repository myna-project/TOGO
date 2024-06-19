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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.UserJson;
import it.mynaproject.togo.api.service.JobService;
import it.mynaproject.togo.api.service.UserService;

@RestController
public class UserController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserService userService;

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
	@GetMapping(value = "/admin/organization/users")
	public Object getUsers(@RequestParam(required = false) String username) {

		log.info("Request for users with username: {}", username);

		List<UserJson> ujList = new ArrayList<>();
		for (User user : userService.getUsers())
			if ((username == null) ||((username != null) && (user.getUsername().equals(username))))
				ujList.add(JsonUtil.userToUserJson(user));

		return ujList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/users/{id}")
	public Object getUser(@PathVariable("id") Integer id) {

		log.debug("Getting user with id {}", id);

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.userToUserJson(this.userService.getUser(id, true, authUser.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/users")
	public Object postUser(@Valid @RequestBody UserJson input,HttpServletRequest request) {

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.userToUserJson(this.userService.createUserFromInput(input, true, authUser.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/users/{id}")
	public Object updateUser(@PathVariable("id") Integer id, @Valid @RequestBody UserJson input) {

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.userToUserJson(this.userService.updateUserFromInput(id, input, true, authUser.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/users/{id}")
	public Object deleteUser(@PathVariable("id") Integer id) {

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.userService.deleteUserById(id, true, authUser.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/users/{id}/jobs/{jobId}")
	public Object addJobToUser(@Valid @PathVariable("id") Integer id,@PathVariable("jobId") Integer jobId) {

		log.info("Adding job {} to user {}", jobId, id);

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.userService.addJobToUser(this.userService.getUser(id, true, authUser.getUsername()), this.jobService.getJob(jobId, true, authUser.getUsername()));

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/users/{id}/jobs/{jobId}")
	public Object removeJobFromUser(@PathVariable("id") Integer id, @PathVariable("jobId") Integer jobId) {

		log.info("Removing job {} from user {}", id, jobId);

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.userService.removeJobFromUser(this.userService.getUser(id, true, authUser.getUsername()), this.jobService.getJob(jobId, true, authUser.getUsername()));

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
	@GetMapping(value = "/organization/users")
	public Object getUsersForUser(@RequestParam(required = false) String username) {

		log.info("Request for users with username: {}", username);

		List<UserJson> ujList = new ArrayList<>();
		for (User user : userService.getUsers())
			if ((username == null) ||((username != null) && (user.getUsername().equals(username))))
				ujList.add(JsonUtil.userToUserJson(user));

		return ujList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/users/visible-users")
	public Object getVisibleUsers() {

		log.debug("Getting visible users");

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<UserJson> ujList = new ArrayList<>();
		for (User u : this.userService.getVisibleUsers(authUser.getUsername()))
			ujList.add(JsonUtil.userToUserJson(u));

		return ujList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/users/{id}")
	public Object updateProfileForUser(@PathVariable("id") Integer id, @Valid @RequestBody UserJson input) {

		org.springframework.security.core.userdetails.User authUser = (org.springframework.security.core.userdetails.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.userToUserJson(this.userService.updateUserFromInput(id, input, false, authUser.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}
}
