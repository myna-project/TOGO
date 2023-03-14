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
import it.mynaproject.togo.api.domain.Vendor;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.VendorJson;
import it.mynaproject.togo.api.service.VendorService;

@RestController
public class VendorController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VendorService vendorService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/vendors/{vendorId}")
	public Object getVendor(@PathVariable("vendorId") Integer vendorId) {

		log.debug("Request for vendor with id {}", vendorId);

		return JsonUtil.vendorToVendorJson(this.vendorService.getVendor(vendorId, true));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@GetMapping(value = "/admin/organization/vendors")
	public Object getAllVendors() {

		ArrayList<VendorJson> vjList = new ArrayList<>();
		for (Vendor v : this.vendorService.getVendors(true))
			vjList.add((VendorJson) JsonUtil.vendorToVendorJson(v));

		return vjList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@PostMapping(value = "/admin/organization/vendors")
	public Object createVendor(@Valid @RequestBody VendorJson input, HttpServletRequest request) {

		return new ResponseEntity<>(JsonUtil.vendorToVendorJson(this.vendorService.createVendorFromInput(input, true)), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@PutMapping(value = "/admin/organization/vendors/{vendorId}")
	public Object updateVendor(@PathVariable("vendorId") Integer vendorId, @Valid @RequestBody VendorJson input) {

		log.info("QUI: " + input.toString());

		return new ResponseEntity<>(JsonUtil.vendorToVendorJson(this.vendorService.updateVendorFromInput(vendorId, input, true)), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request")
	})
	@DeleteMapping(value = "/admin/organization/vendors/{vendorId}")
	public Object deleteVendor(@PathVariable("vendorId") Integer vendorId) {

		this.vendorService.deleteVendorById(vendorId, true);

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
