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

import it.mynaproject.togo.api.domain.DrainControl;
import it.mynaproject.togo.api.model.DrainControlJson;

public interface DrainControlService {

	public DrainControl getDrainControl(Integer id, Boolean isAdmin, String username);
	public List<DrainControl> getDrainControls(Boolean isAdmin, String username);
	public void persist(DrainControl control);
	public DrainControl createDrainControlFromInput(DrainControlJson input, Boolean isAdmin, String username);
	public void update(DrainControl control);
	public DrainControl updateDrainControlFromInput(Integer id, DrainControlJson input, Boolean isAdmin, String username);
	public void deleteDrainControlById(Integer id, Boolean isAdmin, String username);
}
