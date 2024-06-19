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

import it.mynaproject.togo.api.domain.Vendor;

public interface VendorDao {

	public void persist(Vendor vendor);
	public void update(Vendor vendor);
	public void delete(Vendor vendor);
	public Vendor getVendor(Integer id);
	public List<Vendor> getVendors();
}
