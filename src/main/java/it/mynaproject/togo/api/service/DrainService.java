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

import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.model.DrainJson;

public interface DrainService {

	public Drain getDrain(Integer id, Boolean isAdmin, String username);
	public List<Drain> getDrains();
	public List<Drain> getDrainsForUser(String username);
	public void persist(Drain drain);
	public Drain createDrainFromInput(DrainJson input, Boolean isAdmin, String username);
	public void update(Drain drain);
	public Drain updateDrainFromInput(Integer id, DrainJson input, Boolean isAdmin, String username);
	public void deleteDrainById(Integer id, Boolean isAdmin, String username);
}