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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.mynaproject.togo.api.service.OrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.InvoiceItemkWhDao;
import it.mynaproject.togo.api.dao.MeasureDao;
import it.mynaproject.togo.api.dao.TimeSlotDao;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.InvoiceItemkWh;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureType;
import it.mynaproject.togo.api.domain.Vendor;
import it.mynaproject.togo.api.domain.impl.MeasureDouble;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.InvoiceItemkWhJson;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.InvoiceItemkWhService;
import it.mynaproject.togo.api.service.VendorService;
import it.mynaproject.togo.api.util.DateUtil;

@Service
public class InvoiceItemkWhServiceImpl implements InvoiceItemkWhService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private InvoiceItemkWhDao invoiceItemkWhDao;

	@Autowired
	private TimeSlotDao timeSlotDao;

	@Autowired
	private DrainService drainService;

	@Autowired
	private VendorService vendorService;

	@Autowired
	private MeasureDao measureDao;

	@Autowired
	private OrgService orgService;

	@Override
	@Transactional
	public InvoiceItemkWh getInvoiceItemkWh(Integer id, Boolean isAdmin) {

		InvoiceItemkWh i = this.invoiceItemkWhDao.getInvoiceItemkWh(id);
		if (i == null)
			throw new NotFoundException(404, "Invoice item kWh " + id + " not found");

		return i;
	}

	@Override
	@Transactional
	public List<InvoiceItemkWh> getInvoiceItemskWh(Integer drainId, Integer vendorId, Integer year, Integer month, Boolean isAdmin, String username) {

		if (drainId != null)
			this.drainService.getDrain(drainId, isAdmin, username);
		if (vendorId != null)
			this.vendorService.getVendor(vendorId, isAdmin);

		List<InvoiceItemkWh> invoiceItems = this.invoiceItemkWhDao.getInvoiceItemskWh(drainId, vendorId, year, month);
		if (isAdmin || (drainId != null))
			return invoiceItems;

		List<InvoiceItemkWh> invoiceItemsVisible = new ArrayList<>();
		List<Drain> drainList = new ArrayList<>();
		for (InvoiceItemkWh item : invoiceItems) {
			if (orgService.orgIsVisibleByUser(item.getDrain().getFeed().getClients().get(0).getOrg(), username)) {
				if (!drainList.contains(item.getDrain())) {
					drainList.add(item.getDrain());
					invoiceItemsVisible.add(item);
				}
			}
		}

		return invoiceItemsVisible;
	}

	@Override
	@Transactional
	public void persist(InvoiceItemkWh invoiceItemkWh) {

		this.removeTimeSeries(invoiceItemkWh);
		this.invoiceItemkWhDao.persist(invoiceItemkWh);
		this.createTimeSeries(invoiceItemkWh);
	}

	@Override
	@Transactional
	public InvoiceItemkWh createInvoiceItemkWhFromInput(InvoiceItemkWhJson input, Boolean isAdmin, String username) {

		log.info("Creating new invoice item kwh");

		if (this.checkInvoiceItemkWhExists(input.getVendorId(), input.getDrainId(), input.getYear(), input.getMonth()))
			throw new ConflictException(11001, "Invoice item kWh " + input.getYear() + " - " + input.getMonth() + " already exists for these vendor and drain");

		Drain d = this.drainService.getDrain(input.getDrainId(), isAdmin, username);

		Vendor v = this.vendorService.getVendor(input.getVendorId(), isAdmin);

		InvoiceItemkWh i = new InvoiceItemkWh();
		i.populateInvoiceItemkWh(input, d, v);

		this.persist(i);

		return i;
	}

	@Override
	@Transactional
	public void update(InvoiceItemkWh invoiceItemkWh) {

		this.removeTimeSeries(invoiceItemkWh);
		this.invoiceItemkWhDao.update(invoiceItemkWh);
		this.createTimeSeries(invoiceItemkWh);
	}

	@Override
	@Transactional
	public InvoiceItemkWh updateInvoiceItemkWhFromInput(Integer id, InvoiceItemkWhJson input, Boolean isAdmin, String username) {

		log.info("Updating invoice item kwh with id {}", id);

		InvoiceItemkWh i = this.getInvoiceItemkWh(id, isAdmin);

		Drain d = this.drainService.getDrain(input.getDrainId(), isAdmin, username);

		Vendor v = this.vendorService.getVendor(input.getVendorId(), isAdmin);

		i.populateInvoiceItemkWh(input, d, v);

		this.update(i);

		return i;
	}

	@Override
	@Transactional
	public void deleteInvoiceItemkWhById(Integer id, Boolean isAdmin) {

		InvoiceItemkWh i = this.getInvoiceItemkWh(id, isAdmin);

		this.removeTimeSeries(i);
		this.invoiceItemkWhDao.delete(i);
	}

	@Override
	public Boolean checkInvoiceItemsForVendor(Integer vendorId) {
		return this.invoiceItemkWhDao.checkInvoiceItemsForVendor(vendorId);
	}

	private Boolean checkInvoiceItemkWhExists(Integer vendorId, Integer drainId, Integer year, Integer month) {
		return this.invoiceItemkWhDao.checkInvoiceItemkWhExists(vendorId, drainId, year, month);
	}

	private void createTimeSeries(InvoiceItemkWh i) {

		Float f1UnitCost = (float) 0.00;
		Float f2UnitCost = (float) 0.00;
		Float f3UnitCost = (float) 0.00;

		if (i.getF1Energy() != null)
			f1UnitCost = f1UnitCost + i.getF1Energy();
		if (i.getF2Energy() != null)
			f2UnitCost = f2UnitCost + i.getF2Energy();
		if (i.getF3Energy() != null)
			f3UnitCost = f3UnitCost + i.getF3Energy();
		if (i.getInterruptibilityRemuneration() != null) {
			f1UnitCost = f1UnitCost + i.getInterruptibilityRemuneration();
			f2UnitCost = f2UnitCost + i.getInterruptibilityRemuneration();
			f3UnitCost = f3UnitCost + i.getInterruptibilityRemuneration();
		}
		if (i.getProductionCapacityAvailability() != null) {
			f1UnitCost = f1UnitCost + i.getProductionCapacityAvailability();
			f2UnitCost = f2UnitCost + i.getProductionCapacityAvailability();
			f3UnitCost = f3UnitCost + i.getProductionCapacityAvailability();
		}
		if (i.getGrtnOperatingCosts() != null) {
			f1UnitCost = f1UnitCost + i.getGrtnOperatingCosts();
			f2UnitCost = f2UnitCost + i.getGrtnOperatingCosts();
			f3UnitCost = f3UnitCost + i.getGrtnOperatingCosts();
		}
		if (i.getProcurementDispatchingResources() != null) {
			f1UnitCost = f1UnitCost + i.getProcurementDispatchingResources();
			f2UnitCost = f2UnitCost + i.getProcurementDispatchingResources();
			f3UnitCost = f3UnitCost + i.getProcurementDispatchingResources();
		}
		if (i.getReintegrationTemporarySafeguard() != null) {
			f1UnitCost = f1UnitCost + i.getReintegrationTemporarySafeguard();
			f2UnitCost = f2UnitCost + i.getReintegrationTemporarySafeguard();
			f3UnitCost = f3UnitCost + i.getReintegrationTemporarySafeguard();
		}
		if (i.getF1UnitSafetyCosts() != null)
			f1UnitCost = f1UnitCost + i.getF1UnitSafetyCosts();
		if (i.getF2UnitSafetyCosts() != null)
			f2UnitCost = f2UnitCost + i.getF2UnitSafetyCosts();
		if (i.getF3UnitSafetyCosts() != null)
			f3UnitCost = f3UnitCost + i.getF3UnitSafetyCosts();
		// apply loss perc rate on each time slot
		if (i.getLossPercRate() != null) {
			f1UnitCost = f1UnitCost + (f1UnitCost * i.getLossPercRate()/100);
			f2UnitCost = f2UnitCost + (f2UnitCost * i.getLossPercRate()/100);
			f3UnitCost = f3UnitCost + (f3UnitCost * i.getLossPercRate()/100);
		}

		if (i.getTransportEnergy() != null) {
			f1UnitCost = f1UnitCost + i.getTransportEnergy();
			f2UnitCost = f2UnitCost + i.getTransportEnergy();
			f3UnitCost = f3UnitCost + i.getTransportEnergy();
		}
		if (i.getTransportEnergyEqualization() != null) {
			f1UnitCost = f1UnitCost + i.getTransportEnergyEqualization();
			f2UnitCost = f2UnitCost + i.getTransportEnergyEqualization();
			f3UnitCost = f3UnitCost + i.getTransportEnergyEqualization();
		}
		if (i.getSystemChargesEnergy() != null) {
			f1UnitCost = f1UnitCost + i.getSystemChargesEnergy();
			f2UnitCost = f2UnitCost + i.getSystemChargesEnergy();
			f3UnitCost = f3UnitCost + i.getSystemChargesEnergy();
		}

		// apply vat perc rate on each time slot
		if (i.getVatPercRate() != null) {
			f1UnitCost = f1UnitCost + (f1UnitCost * i.getVatPercRate()/100);
			f2UnitCost = f2UnitCost + (f2UnitCost * i.getVatPercRate()/100);
			f3UnitCost = f3UnitCost + (f3UnitCost * i.getVatPercRate()/100);
		}

		// from month start date
		Calendar startCal = Calendar.getInstance();
		startCal.set(i.getYear(), i.getMonth() - 1, 1, 0, 0, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		// to month end date
		Calendar endCal = (Calendar) startCal.clone();
		endCal.set(Calendar.DATE, startCal.getActualMaximum(Calendar.DATE));
		endCal.set(Calendar.HOUR, startCal.getActualMaximum(Calendar.HOUR));
		endCal.set(Calendar.MINUTE, startCal.getActualMaximum(Calendar.MINUTE));
		endCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));

		List<Measure> measures = new ArrayList<Measure>();
		while (startCal.before(endCal)) {
			// create measure
			MeasureDouble m = new MeasureDouble();
			m.setDrain(i.getDrain());
			m.setTime(startCal.getTime());
			Integer slot = this.timeSlotDao.getTimeSlotFromTime(startCal);
			Double value = (slot.equals(1) ? f1UnitCost : (slot.equals(2) ? f2UnitCost : f3UnitCost)).doubleValue();
			m.setValue(value);
			measures.add(m);

			startCal.setTime(DateUtil.add(m.getTime(), Calendar.HOUR, 1));
		}

		this.measureDao.persistMultiple(measures);
	}

	private void removeTimeSeries(InvoiceItemkWh i) {

		// from month start date
		Calendar startCal = Calendar.getInstance();
		startCal.set(i.getYear(), i.getMonth() - 1, 1, 0, 0, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		// to month end date
		Calendar endCal = (Calendar) startCal.clone();
		endCal.set(Calendar.DATE, startCal.getActualMaximum(Calendar.DATE));
		endCal.set(Calendar.HOUR, startCal.getActualMaximum(Calendar.HOUR));
		endCal.set(Calendar.MINUTE, startCal.getActualMaximum(Calendar.MINUTE));
		endCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));

		this.measureDao.deleteMultiple(this.measureDao.getMeasures(i.getDrain(), MeasureType.DOUBLE, startCal.getTime(), endCal.getTime()));
	}
}
