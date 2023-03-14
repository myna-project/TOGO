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
package it.mynaproject.togo.api.dao;

import java.util.List;

import it.mynaproject.togo.api.domain.InvoiceItemkWh;

public interface InvoiceItemkWhDao {

	public void persist(InvoiceItemkWh invoiceItemkWh);
	public void update(InvoiceItemkWh invoiceItemkWh);
	public void delete(InvoiceItemkWh invoiceItemkWh);
	public InvoiceItemkWh getInvoiceItemkWh(Integer id);
	public List<InvoiceItemkWh> getInvoiceItemskWh(Integer drainId, Integer vendorId, Integer year, Integer month);
	public Boolean checkInvoiceItemkWhExists(Integer vendorId, Integer drainId, Integer year, Integer month);
	public Boolean checkInvoiceItemsForVendor(Integer vendorId);
}
