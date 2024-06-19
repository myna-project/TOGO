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
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.OrgJson;
import it.mynaproject.togo.api.service.OrgService;

@RestController
public class OrgController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrgService orgService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/orgs/{id}")
	public OrgJson getOrg(@PathVariable("id") Integer id) {

		log.debug("Request for org with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.orgToOrgJson(this.orgService.getOrg(id, true, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/orgs")
	public Object getOrgs() {

		log.debug("Request for orgs");

		List<Org> orgs= this.orgService.getOrgs();

		List<OrgJson> ojList = new ArrayList<>();
		for (Org o : orgs)
			ojList.add(JsonUtil.orgToOrgJson(o));

		return ojList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/orgs/hierarchically")
	public Object getOrgsHierarchically() {

		log.debug("Request for orgs hierarchically");

		List<OrgJson> ojList = new ArrayList<>();
		ojList.addAll(JsonUtil.orgToOrgJsonHierarchically(this.orgService.getOrgs()));

		return ojList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/orgs")
	public Object postOrg(@Valid @RequestBody OrgJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.orgToOrgJson(this.orgService.createOrgFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/orgs/{id}")
	public Object updateOrg(@PathVariable("id") int id, @Valid @RequestBody OrgJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.orgToOrgJson(this.orgService.updateOrgFromInput(id, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/orgs/{id}")
	public Object deleteOrg(@PathVariable("id") int id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.orgService.deleteOrgById(id, true, user.getUsername());

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
	@GetMapping(value = "/organization/orgs")
	public Object getOrgsForUser() {

		log.debug("Request for orgs");

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<OrgJson> ojList = new ArrayList<>();
		for (Org o : this.orgService.getOrgsForUser(user.getUsername()))
			ojList.add(JsonUtil.orgToOrgJson(o));

		return ojList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/orgs/hierarchically")
	public Object getOrgsHierarchicallyForUser() {

		log.debug("Request for orgs hierarchically");

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<OrgJson> ojList = new ArrayList<>();
		ojList.addAll(JsonUtil.orgToOrgJsonHierarchically(this.orgService.getOrgsForUser(user.getUsername())));

		return ojList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/orgs/{id}")
	public Object getOrgForUser(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.orgToOrgJson(this.orgService.getOrg(id, false, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/orgs")
	public Object postOrgForUser(@Valid @RequestBody OrgJson input,HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.orgToOrgJson(this.orgService.createOrgFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/orgs/{id}")
	public Object updateOrgForUser(@PathVariable("id") int id, @Valid @RequestBody OrgJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.orgToOrgJson(this.orgService.updateOrgFromInput(id, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/orgs/{id}")
	public Object deleteOrgForUser(@PathVariable("id") int id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.orgService.deleteOrgById(id, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
