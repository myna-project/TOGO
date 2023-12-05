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
import java.util.List;

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
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Operation;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.model.Constants;
import it.mynaproject.togo.api.model.CsvMeasuresJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson.Value;
import it.mynaproject.togo.api.service.MeasureService;
import it.mynaproject.togo.api.util.Pair;

@RestController
public class MeasureController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MeasureService measureService;

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
	@PostMapping(value = "/admin/organization/measures")
	public Object postMeasure(@RequestBody CsvMeasuresJson csvMeasuresJson) {

		log.debug("Create measure for device_id: {}, size: {}", csvMeasuresJson.getDeviceId(), csvMeasuresJson.toString().getBytes().length);
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.createMeasuresFromJson(csvMeasuresJson, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/measuresmatrix")
	public Object postMeasureMatrix(@RequestBody List<CsvMeasuresJson> csvMeasuresJsonList) {

		if (csvMeasuresJsonList.isEmpty())
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.createMeasuresFromJsonList(csvMeasuresJsonList, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
	})
	@PutMapping(value = "/admin/organization/measures")
	public Object updateMeasure(@RequestBody CsvMeasuresJson csvMeasuresJson) {

		log.debug("Update measure for device_id: {}, size: {}", csvMeasuresJson.getDeviceId(), csvMeasuresJson.toString().getBytes().length);
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.updateMeasuresFromJson(csvMeasuresJson, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/measuresmatrix")
	public Object updateMeasureMatrix(@RequestBody List<CsvMeasuresJson> csvMeasuresJsonList) {

		if (csvMeasuresJsonList.isEmpty())
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.updateMeasuresFromJsonList(csvMeasuresJsonList, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/drains/measures")
	public List<PairDrainMeasuresJson> queryData(
			@RequestParam(value="ids[]") String[] drainIds,
			@RequestParam(value="operations[]") ArrayList<Operation> drainOperations,
			@RequestParam(value="measureAggregation[]") ArrayList<MeasureAggregation> measureAggregations,
			@RequestParam(value="positiveNegativeValues[]") ArrayList<String> positiveNegativeValues,
			@RequestParam(value="start") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end,
			@RequestParam(value="timeAggregation", defaultValue = "MINUTE") TimeAggregation timeAggregation
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return measureService.getMeasures(drainIds, drainOperations, measureAggregations, positiveNegativeValues, start, end, timeAggregation, true, user.getUsername());
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/measures")
	public Object deleteMeasures(
			@RequestParam(value="clientId") Integer clientId,
			@RequestParam(value="deviceId") String deviceId,
			@RequestParam(value="start") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.deleteMeasures(clientId, deviceId, start, end, true, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/drains/costs")
	public List<PairDrainMeasuresJson> getCosts(
			@RequestParam(value="drainCostId") Integer drainCostId,
			@RequestParam(value="ids[]") String[] drainIds,
			@RequestParam(value="operations[]") ArrayList<Operation> drainOperations,
			@RequestParam(value="measureAggregation") MeasureAggregation measureAggregation,
			@RequestParam(value="start") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end,
			@RequestParam(value="timeAggregation", defaultValue = "QHOUR") TimeAggregation timeAggregation
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return measureService.getCosts(drainCostId, drainIds, drainOperations, measureAggregation, start, end, timeAggregation, true, user.getUsername());
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
	@PostMapping(value = "/organization/measures")
	public Object postMeasureForUser(@RequestBody CsvMeasuresJson csvMeasuresJson) {

		log.debug("User post measure for device_id: {}, size: {}", csvMeasuresJson.getDeviceId(), csvMeasuresJson.toString().getBytes().length);
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.createMeasuresFromJson(csvMeasuresJson, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/organization/measuresmatrix")
	public Object postMeasureMatrixForUser(@RequestBody List<CsvMeasuresJson> csvMeasuresJsonList) {

		if (csvMeasuresJsonList.isEmpty())
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.createMeasuresFromJsonList(csvMeasuresJsonList, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/measures")
	public Object updateMeasureForUser(@RequestBody CsvMeasuresJson csvMeasuresJson) {

		log.debug("Create measure for device_id: {}, size: {}", csvMeasuresJson.getDeviceId(), csvMeasuresJson.toString().getBytes().length);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.updateMeasuresFromJson(csvMeasuresJson, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request"),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/organization/measuresmatrix")
	public Object updateMeasureMatrixForUser(@RequestBody List<CsvMeasuresJson> csvMeasuresJsonList) {

		if (csvMeasuresJsonList.isEmpty())
			return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.updateMeasuresFromJsonList(csvMeasuresJsonList, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/drains/measures")
	public List<PairDrainMeasuresJson> queryDataForUser(
			@RequestParam(value="ids[]") String[] drainIds,
			@RequestParam(value="operations[]") ArrayList<Operation> drainOperations,
			@RequestParam(value="measureAggregation[]") ArrayList<MeasureAggregation> measureAggregations,
			@RequestParam(value="positiveNegativeValues[]") ArrayList<String> positiveNegativeValues,
			@RequestParam(value="start") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end,
			@RequestParam(value="timeAggregation", defaultValue = "MINUTE") TimeAggregation timeAggregation
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return measureService.getMeasures(drainIds, drainOperations, measureAggregations, positiveNegativeValues, start, end, timeAggregation, false, user.getUsername());
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/drains/costs")
	public List<PairDrainMeasuresJson> getCostsForUser(
			@RequestParam(value="drainCostId") Integer drainCostId,
			@RequestParam(value="ids[]") String[] drainIds,
			@RequestParam(value="operations[]") ArrayList<Operation> drainOperations,
			@RequestParam(value="measureAggregation") MeasureAggregation measureAggregation,
			@RequestParam(value="start") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end,
			@RequestParam(value="timeAggregation", defaultValue = "QHOUR") TimeAggregation timeAggregation
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return measureService.getCosts(drainCostId, drainIds, drainOperations, measureAggregation, start, end, timeAggregation, false, user.getUsername());
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/organization/drains/{drainId}/allmeasures")
	public List<Value> queryMeasureForDrainForUser(
			@PathVariable(value="drainId") Integer drainId,
			@RequestParam(value="start", required=false) @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end", required=false) @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Pair<Drain, List<Measure>> dList = this.measureService.getAllMeasuresFromDrainId(drainId, start, end, false, user.getUsername());

		PairDrainMeasuresJson drainlist = JsonUtil.pairDrainMeasureToPairDrainMeasureJson(dList, TimeAggregation.MINUTE, (start != null) ? start : new Date(), (end != null) ? end : new Date());

		return drainlist.getMeasures();
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/organization/measures")
	public Object deleteMeasuresForUser(
			@RequestParam(value="clientId") Integer clientId,
			@RequestParam(value="deviceId") String deviceId,
			@RequestParam(value="start") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date start,
			@RequestParam(value="end") @DateTimeFormat(pattern = Constants.DATIME_FORMAT) Date end
			) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		this.measureService.deleteMeasures(clientId, deviceId, start, end, false, user.getUsername());

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
