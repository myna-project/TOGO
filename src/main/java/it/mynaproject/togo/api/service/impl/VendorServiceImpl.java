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
package it.mynaproject.togo.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.VendorDao;
import it.mynaproject.togo.api.domain.Vendor;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.VendorJson;
import it.mynaproject.togo.api.service.InvoiceItemkWhService;
import it.mynaproject.togo.api.service.VendorService;

@Service
public class VendorServiceImpl implements VendorService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private VendorDao vendorDao;

	@Autowired
	private InvoiceItemkWhService invoiceItemKwhService;

	@Override
	@Transactional
	public Vendor getVendor(Integer id, Boolean isAdmin) {

		Vendor v = this.vendorDao.getVendor(id);
		if (v == null)
			throw new NotFoundException(404, "Vendor " + id + " not found");

		return v;
	}

	@Override
	@Transactional
	public List<Vendor> getVendors(Boolean isAdmin) {
		return this.vendorDao.getVendors();
	}

	@Override
	@Transactional
	public void persist(Vendor vendor) {
		this.vendorDao.persist(vendor);
	}

	@Override
	@Transactional
	public Vendor createVendorFromInput(VendorJson input, Boolean isAdmin) {

		log.info("Creating new vendor");

		if (!this.checkVendorName(input.getName(), null, isAdmin))
			throw new ConflictException(10001, "Vendor " + input.getName() + " already exists");

		Vendor v = new Vendor();
		v.populateVendorFromInput(input);

		this.persist(v);

		return v;
	}

	@Override
	@Transactional
	public void update(Vendor vendor) {
		this.vendorDao.update(vendor);
	}

	@Override
	@Transactional
	public Vendor updateVendorFromInput(Integer id, VendorJson input, Boolean isAdmin) {

		log.info("Updating vendor with id {}", id);

		if (!this.checkVendorName(input.getName(), id, isAdmin))
			throw new ConflictException(10001, "Vendor " + input.getName() + " already exists");

		Vendor v = this.getVendor(id, isAdmin);
		v.populateVendorFromInput(input);

		this.update(v);

		return v;
	}

	@Override
	@Transactional
	public void deleteVendorById(Integer id, Boolean isAdmin) {

		log.info("Deleting vendor with id {}", id);

		if (this.invoiceItemKwhService.checkInvoiceItemsForVendor(id))
			throw new ConflictException(10101, "Cannot delete vendor " + id + " because there are one or more kWh invoice items");

		this.vendorDao.delete(this.getVendor(id, isAdmin));
	}

	private boolean checkVendorName(String name, Integer vendorId, Boolean isAdmin) {

		for (Vendor v : this.getVendors(isAdmin))
			if (v.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
				if ((vendorId == null) || (v.getId() != vendorId))
					return false;

		return true;
	}
}
