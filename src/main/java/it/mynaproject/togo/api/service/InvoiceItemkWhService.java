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
package it.mynaproject.togo.api.service;

import java.util.List;

import it.mynaproject.togo.api.domain.InvoiceItemkWh;
import it.mynaproject.togo.api.model.InvoiceItemkWhJson;

public interface InvoiceItemkWhService {

	public InvoiceItemkWh getInvoiceItemkWh(Integer id, Boolean isAdmin);
	public List<InvoiceItemkWh> getInvoiceItemskWh(Integer drainId, Integer vendorId, Integer year, Integer month, Boolean isAdmin, String username);
	public void persist(InvoiceItemkWh invoiceItemkWh);
	public InvoiceItemkWh createInvoiceItemkWhFromInput(InvoiceItemkWhJson input, Boolean isAdmin, String username);
	public void update(InvoiceItemkWh invoiceItemkWh);
	public InvoiceItemkWh updateInvoiceItemkWhFromInput(Integer id, InvoiceItemkWhJson input, Boolean isAdmin, String username);
	public void deleteInvoiceItemkWhById(Integer id, Boolean isAdmin);
	public Boolean checkInvoiceItemsForVendor(Integer vendorId);
}
