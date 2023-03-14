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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.domain.ClientCategory;
import it.mynaproject.togo.api.model.ClientCategoryJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.ClientCategoryService;

@RestController
public class ClientCategoryController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClientCategoryService clientCategoryService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/client_categories/{id}")
	public Object getClientCategory(@PathVariable("id") Integer id) {

		return JsonUtil.clientCategoryToClientCategoryJson(this.clientCategoryService.getClientCategory(id));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/client_categories")
	public Object getClientCategories() {

		log.debug("Request for all client categories");

		List<ClientCategoryJson> cjList = new ArrayList<>();
		for (ClientCategory c : this.clientCategoryService.getClientCategories())
			cjList.add(JsonUtil.clientCategoryToClientCategoryJson(c));

		return cjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/client_categories")
	public Object postClientCategory(@Valid @RequestBody ClientCategoryJson input, HttpServletRequest request) {

		return new ResponseEntity<>(JsonUtil.clientCategoryToClientCategoryJson(this.clientCategoryService.createClientCategoryFromJson(input)), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/client_categories/{id}")
	public Object updateClientCategory(@PathVariable("id") Integer id, @Valid @RequestBody ClientCategoryJson input) {

		return new ResponseEntity<>(JsonUtil.clientCategoryToClientCategoryJson(this.clientCategoryService.updateClientCategoryFromJson(id, input)), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/client_categories/{id}")
	public Object deleteClientCategory(@PathVariable("id") Integer id) {

		this.clientCategoryService.deleteClientCategoryById(id);

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
	@GetMapping(value = "/organization/client_categories/{id}")
	public Object getClientCategoryForUser(@PathVariable("id") Integer id) {

		return JsonUtil.clientCategoryToClientCategoryJson(this.clientCategoryService.getClientCategory(id));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/organization/client_categories")
	public Object getClientCategoriesForUser() {

		log.debug("Request for all client categories");

		List<ClientCategoryJson> cjList = new ArrayList<>();
		for (ClientCategory c : this.clientCategoryService.getClientCategories())
			cjList.add(JsonUtil.clientCategoryToClientCategoryJson(c));

		return cjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/client_categories")
	public Object postClientCategoryForUser(@Valid @RequestBody ClientCategoryJson input, HttpServletRequest request) {

		return new ResponseEntity<>(JsonUtil.clientCategoryToClientCategoryJson(this.clientCategoryService.createClientCategoryFromJson(input)), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/client_categories/{id}")
	public Object updateClientCategoryForUser(@PathVariable("id") Integer id, @Valid @RequestBody ClientCategoryJson input) {

		return new ResponseEntity<>(JsonUtil.clientCategoryToClientCategoryJson(this.clientCategoryService.updateClientCategoryFromJson(id, input)), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@DeleteMapping(value = "/organization/client_categories/{id}")
	public Object deleteClientCategoryForUser(@PathVariable("id") Integer id) {

		this.clientCategoryService.deleteClientCategoryById(id);

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
