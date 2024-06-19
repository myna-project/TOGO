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

import it.mynaproject.togo.api.dao.VendorDao;
import it.mynaproject.togo.api.domain.Vendor;

@Repository
public class VendorDaoImpl extends BaseDaoImpl implements VendorDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Vendor vendor) {

		log.info("Creating new vendor: {}", vendor.toString());

		em.persist(vendor);
		em.flush();
	}

	@Override
	public void update(Vendor vendor) {

		log.info("Updating vendor: {}", vendor.toString());

		em.persist(em.merge(vendor));
		em.flush();
	}

	@Override
	public void delete(Vendor vendor) {

		log.info("Deleting vendor: {}", vendor.toString());

		em.remove(em.merge(vendor));
		em.flush();
	}

	@Override
	public Vendor getVendor(Integer id) {

		log.debug("Getting vendor with id: {}", id);

		Query q = em.createQuery("FROM Vendor WHERE id=:id");
		q.setParameter("id", id);

		try {
			return (Vendor) q.getSingleResult();
		}catch(NoResultException nre){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Vendor> getVendors() {

		log.debug("Getting list of vendors");

		Query q = em.createQuery("FROM Vendor");

		return q.getResultList();
	}
}
