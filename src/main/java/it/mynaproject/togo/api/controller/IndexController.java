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
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.domain.Index;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.model.Constants;
import it.mynaproject.togo.api.model.IndexJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.IndexService;

@RestController
public class IndexController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IndexService indexService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@GetMapping(value = "/admin/organization/index/{id}")
	public Object getIndex(@PathVariable("id") Integer id) {

		log.debug("Request for index with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.indexToIndexJson(this.indexService.getIndex(id, null, null, null, true, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/admin/organization/index")
	public Object getIndices() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<IndexJson> ijList = new ArrayList<>();
		for (Index index : this.indexService.getIndices(true, user.getUsername()))
			ijList.add((IndexJson) JsonUtil.indexToIndexJson(index));

		return ijList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/index")
	public Object postIndex(@Valid @RequestBody IndexJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.indexToIndexJson(this.indexService.createIndexFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/index/{id}")
	public Object updateIndex(@PathVariable("id") Integer id, @Valid @RequestBody IndexJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.indexToIndexJson(this.indexService.updateIndexFromInput(id, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/index/{id}")
	public Object deleteIndex(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.indexService.deleteIndexById(id, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/index/{id}/calculate")
	public Object calculateIndex(
			@PathVariable("id") Integer id,
			@RequestParam(value="start") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end,
			@RequestParam(value="timeAggregation") TimeAggregation timeAggregation
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.indexToIndexJson(indexService.getIndex(id, start, end, timeAggregation, true, user.getUsername()));
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
	@GetMapping(value = "/organization/index/{id}")
	public Object getIndexForUser(@PathVariable("id") Integer id) {

		log.debug("Request for index with id {}", id);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.indexToIndexJson(this.indexService.getIndex(id, null, null, null, false, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@GetMapping(value = "/organization/index")
	public Object getIndicesForUser() {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<IndexJson> ijList = new ArrayList<>();
		for (Index index : this.indexService.getIndices(false, user.getUsername()))
			ijList.add((IndexJson) JsonUtil.indexToIndexJson(index));

		return ijList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/index")
	public Object postIndexForUser(@Valid @RequestBody IndexJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.indexToIndexJson(this.indexService.createIndexFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/index/{id}")
	public Object updateIndexForUser(@PathVariable("id") Integer id, @Valid @RequestBody IndexJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.indexToIndexJson(this.indexService.updateIndexFromInput(id, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/index/{id}")
	public Object deleteIndexForUser(@PathVariable("id") Integer id) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.indexService.deleteIndexById(id, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/index/{id}/calculate")
	public Object calculateIndexForUser(
			@PathVariable("id") Integer id,
			@RequestParam(value="start", required=false) @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end", required=false) @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end,
			@RequestParam(value="timeAggregation") TimeAggregation timeAggregation
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.indexToIndexJson(indexService.getIndex(id, start, end, timeAggregation, false, user.getUsername()));
	}
}
