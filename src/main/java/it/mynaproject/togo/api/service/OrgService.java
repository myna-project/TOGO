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

import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.model.OrgJson;

public interface OrgService {

	public Org getOrg(Integer id, Boolean isAdmin, String username);
	public List<Org> getOrgs();
	public Boolean orgIsVisibleByUser(Org o, String username);
	public List<Org> getOrgsForUser(String username);
	public void persist(Org org);
	public Org createOrgFromInput(OrgJson input, Boolean isAdmin, String username);
	public void update(Org org);
	public Org updateOrgFromInput(Integer id, OrgJson input, Boolean isAdmin, String username);
	public void deleteOrgById(Integer id, Boolean isAdmin, String username);
}
