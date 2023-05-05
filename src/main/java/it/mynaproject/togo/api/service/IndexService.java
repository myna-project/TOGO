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

import java.util.Date;
import java.util.List;

import it.mynaproject.togo.api.domain.Index;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.model.IndexJson;

public interface IndexService {

	public Index getIndex(Integer id, Date start, Date end, TimeAggregation timeAggregation, Boolean isAdmin, String username);
	public List<Index> getIndices(Boolean isAdmin, String username);
	public void persist(Index index);
	public Index createIndexFromInput(IndexJson input, Boolean isAdmin, String username);
	public void update(Index index);
	public Index updateIndexFromInput(Integer id, IndexJson input, Boolean isAdmin, String username);
	public void deleteIndexById(Integer id, Boolean isAdmin, String username);
}
