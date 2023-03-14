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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.model.ConfigMeasureJson;
import it.mynaproject.togo.api.service.ConfigMeasureService;

@RestController
public class ConfigController {

	@Autowired
	private ConfigMeasureService configMeasureService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/measures/config")
	public Object postConfig(@Valid @RequestBody List<ConfigMeasureJson> input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (input.isEmpty())
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		this.configMeasureService.updateConfigFromInput(input, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	/*
	 *  -------------
	 *  USER SECTION
	 *  -------------
	 *  These routes can be accessible for users without ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/measures/config")
	public Object postConfigForUser(@Valid @RequestBody List<ConfigMeasureJson> input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (input.isEmpty())
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		this.configMeasureService.updateConfigFromInput(input, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.CREATED);
	}
}
