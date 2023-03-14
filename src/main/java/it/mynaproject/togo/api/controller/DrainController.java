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
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.model.DrainJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.DrainService;

@RestController
public class DrainController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DrainService drainService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/admin/organization/drains/{drainId}")
	public DrainJson getDrain(@PathVariable("drainId") Integer drainId) {

		log.debug("Request drain with id {}", drainId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.drainToDrainJson(this.drainService.getDrain(drainId, true, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/drains")
	public Object getDrains() {

		log.debug("Request drains");

		ArrayList<DrainJson> djList = new ArrayList<>();
		for (Drain d : this.drainService.getDrainsForUser(null))
			djList.add(JsonUtil.drainToDrainJson(d));

		return djList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/drains")
	public Object postDrain(@Valid @RequestBody DrainJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainToDrainJson(this.drainService.createDrainFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/drains/{drainId}")
	public Object updateDrain(@PathVariable("drainId") Integer drainId, @Valid @RequestBody DrainJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainToDrainJson(this.drainService.updateDrainFromInput(drainId, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@DeleteMapping(value = "/admin/organization/drains/{drainId}")
	public Object deleteDrain(@PathVariable("drainId") Integer drainId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.drainService.deleteDrainById(drainId, true, user.getUsername());

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
	@GetMapping(value = "/organization/drains/{drainId}")
	public DrainJson getDrainForUser(@PathVariable("drainId") Integer drainId) {

		log.debug("Request drain with id {}", drainId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.drainToDrainJson(this.drainService.getDrain(drainId, false, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/drains")
	public Object getDrainsForUser() {

		log.debug("Request drains for user");

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<DrainJson> djList = new ArrayList<>();
		for (Drain d : this.drainService.getDrainsForUser(user.getUsername()))
			djList.add(JsonUtil.drainToDrainJson(d));

		return djList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/drains")
	public Object postDrainForUser(@Valid @RequestBody DrainJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainToDrainJson(this.drainService.createDrainFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/drains/{drainId}")
	public Object updateDrainForUser(@PathVariable("drainId") Integer drainId, @Valid @RequestBody DrainJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainToDrainJson(this.drainService.updateDrainFromInput(drainId, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/drains/{drainId}")
	public Object deleteDrainForUser(@PathVariable("drainId") Integer drainId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.drainService.deleteDrainById(drainId, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
