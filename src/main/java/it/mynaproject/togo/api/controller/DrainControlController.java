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
import it.mynaproject.togo.api.domain.DrainControl;
import it.mynaproject.togo.api.model.DrainControlJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.DrainControlService;

@RestController
public class DrainControlController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DrainControlService drainControlService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/admin/organization/drain_controls/{id}")
	public Object getDrainControl(@PathVariable("id") Integer id) {

		log.debug("Request for drain control with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.drainControlToDrainControlJson(this.drainControlService.getDrainControl(id, true, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/admin/organization/drain_controls")
	public Object getDrainControls() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<DrainControlJson> cjList = new ArrayList<>();
		for (DrainControl control : this.drainControlService.getDrainControls(true, user.getUsername()))
			cjList.add((DrainControlJson) JsonUtil.drainControlToDrainControlJson(control));

		return cjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/drain_controls")
	public Object postDrainControl(@Valid @RequestBody DrainControlJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainControlToDrainControlJson(this.drainControlService.createDrainControlFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/drain_controls/{id}")
	public Object updateDrainControl(@PathVariable("id") Integer id, @Valid @RequestBody DrainControlJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainControlToDrainControlJson(this.drainControlService.updateDrainControlFromInput(id, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/drain_controls/{id}")
	public Object deleteDrainControl(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.drainControlService.deleteDrainControlById(id, true, user.getUsername());

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
	@GetMapping(value = "/organization/drain_controls/{id}")
	public Object getDrainControlForUser(@PathVariable("id") Integer id) {

		log.debug("Request for drain control with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.drainControlToDrainControlJson(this.drainControlService.getDrainControl(id, false, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/organization/drain_controls")
	public Object getDrainControlsForUser() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<DrainControlJson> cjList = new ArrayList<>();
		for (DrainControl control : this.drainControlService.getDrainControls(false, user.getUsername()))
			cjList.add((DrainControlJson) JsonUtil.drainControlToDrainControlJson(control));

		return cjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/drain_controls")
	public Object postDrainControlForUser(@Valid @RequestBody DrainControlJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainControlToDrainControlJson(this.drainControlService.createDrainControlFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/drain_controls/{id}")
	public Object updateDrainControlForUser(@PathVariable("id") Integer id, @Valid @RequestBody DrainControlJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.drainControlToDrainControlJson(this.drainControlService.updateDrainControlFromInput(id, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/drain_controls/{id}")
	public Object deleteDrainControlForUser(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.drainControlService.deleteDrainControlById(id, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
