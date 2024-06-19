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
import it.mynaproject.togo.api.domain.Role;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.RoleJson;
import it.mynaproject.togo.api.service.RoleService;

@RestController
public class RoleController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoleService roleService;

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/roles")
	public Object getRoles() {

		log.debug("Request for roles");

		List<RoleJson> rjList = new ArrayList<>();
		for (Role role : roleService.getRoles())
			rjList.add(JsonUtil.roleToRoleJson(role));

		return rjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/roles/{id}")
	public Object getRole(@PathVariable("id") Integer id) {

		log.debug("Request for role with id {}", id);

		return JsonUtil.roleToRoleJson(this.roleService.getRole(id));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/roles")
	public Object postRole(@Valid @RequestBody RoleJson input, HttpServletRequest request) {

		return new ResponseEntity<>(JsonUtil.roleToRoleJson(this.roleService.createRoleFromJson(input)), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/roles/{id}")
	public Object updateRole(@PathVariable("id") Integer id, @Valid @RequestBody RoleJson input) {

		return new ResponseEntity<>(JsonUtil.roleToRoleJson(this.roleService.updateRoleFromJson(id, input)), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/roles/{id}")
	public Object deleteRole(@PathVariable("id") Integer id) {

		this.roleService.deleteRoleById(id);

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
