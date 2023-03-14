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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.mynaproject.togo.api.domain.InvoiceItemkWh;
import it.mynaproject.togo.api.model.InvoiceItemkWhJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.service.InvoiceItemkWhService;

@RestController
public class InvoiceItemkWhController {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private InvoiceItemkWhService invoiceItemkWhService;

	/*
	 *  -------------
	 *  ADMIN SECTION
	 *  -------------
	 *  These routes must be accessible only for ROLE_ADMIN
	 */
	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = InvoiceItemkWhJson.class)
	})
	@GetMapping(value = "/admin/organization/invoice_item_kwh/{invoiceItemId}")
	public Object getInvoiceItemkWh(@PathVariable("invoiceItemId") Integer invoiceItemId) {

		log.debug("Request for invoice item kwh with id {}", invoiceItemId);

		return JsonUtil.invoiceItemkWhToinvoiceItemkWhJson(this.invoiceItemkWhService.getInvoiceItemkWh(invoiceItemId, true));
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = InvoiceItemkWhJson.class)
	})
	@GetMapping(value = "/admin/organization/invoice_item_kwh")
	public Object getInvoiceItemskWh(
			@RequestParam(value="drain_id", required = false) Integer drainId,
			@RequestParam(value="vendor_id", required = false) Integer vendorId,
			@RequestParam(value="year", required = false) Integer year,
			@RequestParam(value="month", required = false) Integer month
		) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ArrayList<InvoiceItemkWhJson> ijList = new ArrayList<>();
		for (InvoiceItemkWh i : this.invoiceItemkWhService.getInvoiceItemskWh(drainId, vendorId, year, month, true, user.getUsername()))
			ijList.add((InvoiceItemkWhJson) JsonUtil.invoiceItemkWhToinvoiceItemkWhJson(i));

		return ijList;
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = InvoiceItemkWhJson.class),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PostMapping(value = "/admin/organization/invoice_item_kwh")
	public Object createInvoiceItemkWh(@Valid @RequestBody InvoiceItemkWhJson input, HttpServletRequest request) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.invoiceItemkWhToinvoiceItemkWhJson(this.invoiceItemkWhService.createInvoiceItemkWhFromInput(input, true, user.getUsername())), new HttpHeaders(), HttpStatus.CREATED);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = InvoiceItemkWhJson.class),
		@ApiResponse(code = 409, message = "Conflict")
	})
	@PutMapping(value = "/admin/organization/invoice_item_kwh/{invoiceItemId}")
	public Object updateInvoiceItemkWh(@PathVariable("invoiceItemId") Integer invoiceItemId, @Valid @RequestBody InvoiceItemkWhJson input) {

		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return new ResponseEntity<>(JsonUtil.invoiceItemkWhToinvoiceItemkWhJson(this.invoiceItemkWhService.updateInvoiceItemkWhFromInput(invoiceItemId, input, true, user.getUsername())), new HttpHeaders(), HttpStatus.OK);
	}

	@ApiResponses({
		@ApiResponse(code = 400, message = "Bad Request", response = InvoiceItemkWhJson.class)
	})
	@DeleteMapping(value = "/admin/organization/invoice_item_kwh/{invoiceItemId}")
	public Object deleteInvoiceItemkWh(@PathVariable("invoiceItemId") Integer invoiceItemId) {

		this.invoiceItemkWhService.deleteInvoiceItemkWhById(invoiceItemId, true);

		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
