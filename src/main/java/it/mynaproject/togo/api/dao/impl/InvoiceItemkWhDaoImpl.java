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
package it.mynaproject.togo.api.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.mynaproject.togo.api.dao.InvoiceItemkWhDao;
import it.mynaproject.togo.api.domain.InvoiceItemkWh;

@Repository
public class InvoiceItemkWhDaoImpl extends BaseDaoImpl implements InvoiceItemkWhDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(InvoiceItemkWh invoiceItemkWh) {

		log.info("Creating new invoice item kwh: {}", invoiceItemkWh.toString());

		em.persist(invoiceItemkWh);
		em.flush();
	}

	@Override
	public void update(InvoiceItemkWh invoiceItemkWh) {

		log.info("Updating invoice item kwh: {}", invoiceItemkWh.toString());

		em.persist(em.merge(invoiceItemkWh));
		em.flush();
	}

	@Override
	public void delete(InvoiceItemkWh invoiceItemkWh) {

		log.info("Deleting invoice item kwh: {}", invoiceItemkWh.toString());

		em.remove(em.merge(invoiceItemkWh));
		em.flush();
	}

	@Override
	public InvoiceItemkWh getInvoiceItemkWh(Integer id) {

		log.debug("Getting time slot detail with id: {}", id);

		Query q = em.createQuery("FROM InvoiceItemkWh WHERE id = :id");
		q.setParameter("id", id);

		try {
			return (InvoiceItemkWh) q.getSingleResult();
		}catch(NoResultException nre){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceItemkWh> getInvoiceItemskWh(Integer drainId, Integer vendorId, Integer year, Integer month) {

		log.debug("Getting list of invoice items kWh with drain ID: {} vendor ID: {} year: {} month: {}", drainId, vendorId, year, month);

		String hql = "FROM InvoiceItemkWh WHERE 1 = 1";

		if (drainId != null)
			hql = hql.concat(" AND drain_id = " + String.valueOf(drainId));

		if (vendorId != null)
			hql = hql.concat(" AND vendor_id = " + String.valueOf(vendorId));

		if (year != null)
			hql = hql.concat(" AND year = " + String.valueOf(year));

		if (month != null)
			hql = hql.concat(" AND month = " + String.valueOf(month));

		Query q = em.createQuery(hql);

		return q.getResultList();
	}

	@Override
	public Boolean checkInvoiceItemkWhExists(Integer vendorId, Integer drainId, Integer year, Integer month) {
		
		Query query = em.createQuery("SELECT COUNT(*) FROM InvoiceItemkWh WHERE vendor_id = :vendorId AND drain_id = :drainId AND year = :year AND month = :month");
		query.setParameter("vendorId", vendorId);
		query.setParameter("drainId", drainId);
		query.setParameter("year", year);
		query.setParameter("month", month);

		return (Integer.valueOf(((Long) query.getSingleResult()).intValue()) > 0) ? true : false;
	}

	@Override
	public Boolean checkInvoiceItemsForVendor(Integer vendorId) {

		Query query = em.createQuery("SELECT COUNT(*) FROM InvoiceItemkWh WHERE vendor_id = :vendorId");
		query.setParameter("vendorId", vendorId);

		return (Integer.valueOf(((Long) query.getSingleResult()).intValue()) > 0) ? true : false;
	}
}
