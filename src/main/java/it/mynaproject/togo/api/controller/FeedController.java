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
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.model.DrainJson;
import it.mynaproject.togo.api.model.FeedJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.FeedService;

@RestController
public class FeedController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FeedService feedService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/feeds/{feedId}")
	public Object getFeed( @PathVariable("feedId") Integer feedId) {

		log.debug("Request for feed with id {}", feedId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.feedToFeedJson(this.feedService.getFeed(feedId, true, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/feeds")
	public Object getFeeds() {

		log.debug("Request feeds");

		ArrayList<FeedJson> fjList = new ArrayList<>();
		for (Feed f : this.feedService.getFeedsForUser(null))
			fjList.add(JsonUtil.feedToFeedJson(f));

		return fjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/feeds")
	public Object postFeed(@Valid @RequestBody FeedJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.feedToFeedJson(this.feedService.createFeedFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/feeds/{feedId}")
	public Object updateFeed(@PathVariable("feedId") Integer feedId, @Valid @RequestBody FeedJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.feedToFeedJson(this.feedService.updateFeedFromInput(feedId, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/feeds/{feedId}")
	public Object deleteFeed(@PathVariable("feedId") Integer feedId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.feedService.deleteFeedById(feedId, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/feeds/{feedId}/drains")
	public Object getDrainsForFeed(@PathVariable("feedId") Integer feedId) {

		log.debug("Request drains for feed with id {}", feedId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Feed f = this.feedService.getFeed(feedId, true, user.getUsername());

		ArrayList<DrainJson> djList = new ArrayList<>();
		for (Drain d : f.getDrains())
			djList.add(JsonUtil.drainToDrainJson(d));

		return djList;
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
	@GetMapping(value = "/organization/feeds/{feedId}")
	public FeedJson getFeedForUser(@PathVariable("feedId") Integer feedId) {

		log.debug("Request for feed with id {}", feedId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return JsonUtil.feedToFeedJson(this.feedService.getFeed(feedId, false, user.getUsername()));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/feeds")
	public Object getFeedsForUser() {

		log.debug("Request feeds for user");

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<FeedJson> fjList = new ArrayList<>();
		for (Feed f : this.feedService.getFeedsForUser(user.getUsername()))
			fjList.add(JsonUtil.feedToFeedJson(f));

		return fjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/feeds")
	public Object postFeedForUser(@Valid @RequestBody FeedJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.feedToFeedJson(this.feedService.createFeedFromInput(input, false, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/feeds/{feedId}")
	public Object updateFeedForUser(@PathVariable("feedId") Integer feedId, @Valid @RequestBody FeedJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.feedToFeedJson(this.feedService.updateFeedFromInput(feedId, input, false, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/feeds/{feedId}")
	public Object deleteFeedForUser(@PathVariable("feedId") Integer feedId) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.feedService.deleteFeedById(feedId, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/feeds/{feedId}/drains")
	public Object getDrainsForFeedForUser(@PathVariable(value="feedId") Integer feedId) {

		log.debug("Request drains for feed with id {}", feedId);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Feed f = this.feedService.getFeed(feedId, false, user.getUsername());

		ArrayList<DrainJson> fjList = new ArrayList<>();
		for (Drain d : f.getDrains())
			fjList.add(JsonUtil.drainToDrainJson(d));

		return fjList;
	}
}
