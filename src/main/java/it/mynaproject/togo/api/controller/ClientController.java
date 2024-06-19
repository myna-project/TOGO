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
import java.util.Base64;
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
import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.ClientType;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.model.ClientJson;
import it.mynaproject.togo.api.model.FeedJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.ClientService;

@RestController
public class ClientController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClientService clientService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/clients/{id}")
	public Object getClient(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.clientToClientJson(this.clientService.getClient(id, true, user.getUsername()), true);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/clients/{id}/image")
	public Object getClientImage(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Client c = this.clientService.getClient(id, true, user.getUsername());

		return (c.getImage() != null) ? "{ \"image\": \"" + Base64.getEncoder().encodeToString(c.getImage()) + "\" }" : null;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/clients")
	public Object getClients() {

		log.debug("Request for all clients");

		List<ClientJson> cjList = new ArrayList<>();
		for (Client c : this.clientService.getClients())
			cjList.add(JsonUtil.clientToClientJson(c, false));

		return cjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/clients")
	public Object postClient(@Valid @RequestBody ClientJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.clientToClientJson(this.clientService.createClientFromJson(input, true, user.getUsername()), true), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/clients/{id}")
	public Object updateClient(@PathVariable("id") Integer id, @Valid @RequestBody ClientJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.clientToClientJson(this.clientService.updateClientFromJson(id, input, true, user.getUsername()), true), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/clients/{id}")
	public Object deleteClient(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.clientService.deleteClientById(id, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/clients/{clientId}/feeds")
	public Object getFeedsForClient(@PathVariable(value="clientId") Integer clientId) {

		log.debug("Request feeds for client with id {}" , clientId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Client c = this.clientService.getClient(clientId, true, user.getUsername());

		ArrayList<FeedJson> beanList = new ArrayList<>();
		for (Feed f : c.getFeeds())
			beanList.add(JsonUtil.feedToFeedJson(f));

		return beanList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/client_types")
	public Object getTypes() {

		log.debug("Getting types");

		return ClientType.values();
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
	@GetMapping(value = "/organization/clients/{id}")
	public Object getClientForUser(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.clientToClientJson(this.clientService.getClient(id, false, user.getUsername()), true);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/clients/{id}/image")
	public Object getClientImageForUser(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Client c = this.clientService.getClient(id, false, user.getUsername());

		return (c.getImage() != null) ? "{ \"image\": \"" + Base64.getEncoder().encodeToString(c.getImage()) + "\" }" : null;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/organization/clients")
	public Object getClientsForUser() {

		log.debug("Request for clients");

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<ClientJson> cjList = new ArrayList<>();
		for (Client j : this.clientService.getClientsForUser(user.getUsername()))
			cjList.add(JsonUtil.clientToClientJson(j, false));

		return cjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/clients")
	public Object postClientForUser(@Valid @RequestBody ClientJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.clientToClientJson(this.clientService.createClientFromJson(input, false, user.getUsername()), true), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/clients/{id}")
	public Object updateClientForUser(@PathVariable("id") Integer id, @Valid @RequestBody ClientJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.clientToClientJson(this.clientService.updateClientFromJson(id, input, false, user.getUsername()), true), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@DeleteMapping(value = "/organization/clients/{id}")
	public Object deleteClientForUser(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.clientService.deleteClientById(id, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/clients/{clientId}/feeds")
	public Object getFeedsForClientForUser(@PathVariable(value="clientId") Integer clientId) {

		log.debug("Request feeds for client with id {}" , clientId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Client c = this.clientService.getClient(clientId, false, user.getUsername());

		ArrayList<FeedJson> beanList = new ArrayList<>();
		for (Feed f : c.getFeeds())
			beanList.add(JsonUtil.feedToFeedJson(f));

		return beanList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/client_types")
	public Object getTypesForUser() {

		log.debug("Getting types");

		return ClientType.values();
	}
}
