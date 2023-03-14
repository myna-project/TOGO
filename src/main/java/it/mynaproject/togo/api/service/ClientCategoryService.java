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

import it.mynaproject.togo.api.domain.ClientCategory;
import it.mynaproject.togo.api.model.ClientCategoryJson;

public interface ClientCategoryService {

	public ClientCategory getClientCategory(Integer id);
	public List<ClientCategory> getClientCategories();
	public void persist(ClientCategory category);
	public ClientCategory createClientCategoryFromJson(ClientCategoryJson input);
	public void update(ClientCategory client);
	public ClientCategory updateClientCategoryFromJson(Integer id, ClientCategoryJson input);
	public void deleteClientCategoryById(Integer id);
}
