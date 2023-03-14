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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.domain.TimeSlot;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.TimeSlotJson;
import it.mynaproject.togo.api.service.TimeSlotService;

@RestController
public class TimeSlotController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TimeSlotService timeSlotService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/time_slot/{timeSlotId}")
	public Object getTimeSlot(@PathVariable("timeSlotId") Integer timeSlotId) {

		log.debug("Request for time slot with id {}", timeSlotId);

		return JsonUtil.timeSlotToTimeSlotJson(this.timeSlotService.getTimeSlot(timeSlotId, true));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/time_slot")
	public Object getAllTimeSlots() {

		ArrayList<TimeSlotJson> tsjList = new ArrayList<>();
		for (TimeSlot ts : this.timeSlotService.getAllTimeSlots())
			tsjList.add((TimeSlotJson) JsonUtil.timeSlotToTimeSlotJson(ts));

		return tsjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/time_slot")
	public Object createTimeSlot(@Valid @RequestBody TimeSlotJson input, HttpServletRequest request) {

		return new ResponseEntity<>(JsonUtil.timeSlotToTimeSlotJson(this.timeSlotService.createTimeSlotFromInput(input, true)), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/time_slot/{timeSlotId}")
	public Object updateTimeSlot(@PathVariable("timeSlotId") Integer timeSlotId, @Valid @RequestBody TimeSlotJson input) {

		return new ResponseEntity<>(JsonUtil.timeSlotToTimeSlotJson(this.timeSlotService.updateTimeSlotFromInput(timeSlotId, input, true)), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/time_slot/{timeSlotId}")
	public Object deleteTimeSlot(@PathVariable("timeSlotId") Integer timeSlotId) {

		this.timeSlotService.deleteTimeSlotById(timeSlotId, true);

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
