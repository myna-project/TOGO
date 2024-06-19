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
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.model.FormulaJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.FormulaService;

@RestController
public class FormulaController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FormulaService formulaService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/admin/organization/formula/{formulaId}")
	public Object getFormula(@PathVariable("formulaId") Integer formulaId) {

		log.debug("Request for formula with id {}", formulaId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.formulaToFormulaJson(this.formulaService.getFormula(formulaId, true, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/formula")
	public Object getAllFormula() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<FormulaJson> fjList = new ArrayList<>();
		for (Formula f : this.formulaService.getAllFormulas(true, user.getUsername()))
			fjList.add(JsonUtil.formulaToFormulaJson(f));

		return fjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/formula")
	public Object postFormula(@Valid @RequestBody FormulaJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.formulaToFormulaJson(this.formulaService.createFormulaFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/formula/{formulaId}")
	public Object updateFormula(@PathVariable("formulaId") Integer formulaId, @Valid @RequestBody FormulaJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.formulaToFormulaJson(this.formulaService.updateFormulaFromInput(formulaId, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@DeleteMapping(value = "/admin/organization/formula/{formulaId}")
	public Object deleteFormula(@PathVariable("formulaId") Integer formulaId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.formulaService.deleteFormulaById(formulaId, true, user.getUsername());

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
	@GetMapping(value = "/organization/formula/{formulaId}")
	public Object getFormulaForUser(@PathVariable("formulaId") Integer formulaId) {

		log.debug("Request for formula with id {}", formulaId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.formulaToFormulaJson(this.formulaService.getFormula(formulaId, false, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/organization/formula")
	public Object getAllFormulaForUser() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<FormulaJson> fjList = new ArrayList<>();
		for (Formula f : this.formulaService.getAllFormulas(false, user.getUsername()))
			fjList.add(JsonUtil.formulaToFormulaJson(f));

		return fjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/formula")
	public Object postFormulaForUser(@Valid @RequestBody FormulaJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.formulaToFormulaJson(this.formulaService.createFormulaFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/formula/{formulaId}")
	public Object updateFormulaForUser(@PathVariable("formulaId") Integer formulaId, @Valid @RequestBody FormulaJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.formulaToFormulaJson(this.formulaService.updateFormulaFromInput(formulaId, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/formula/{formulaId}")
	public Object deleteFormulaForUser(@PathVariable("formulaId") Integer formulaId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.formulaService.deleteFormulaById(formulaId, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
