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
package it.mynaproject.togo.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.ClientCategoryDao;
import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.ClientCategory;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.ClientCategoryJson;
import it.mynaproject.togo.api.service.ClientCategoryService;
import it.mynaproject.togo.api.service.ClientService;

@Service
public class ClientCategoryServiceImpl implements ClientCategoryService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClientService clientService;

	@Autowired
	private ClientCategoryDao clientCategoryDao;

	@Override
	@Transactional
	public ClientCategory getClientCategory(Integer id) {

		log.debug("Request for client category with id {}", id);

		ClientCategory c = this.clientCategoryDao.getClientCategory(id);
		if (c == null)
			throw new NotFoundException(404, "Client category " + id + " not found");

		return c;
	}

	@Override
	@Transactional
	public List<ClientCategory> getClientCategories() {
		return this.clientCategoryDao.getClientCategories();
	}

	@Override
	@Transactional
	public void persist(ClientCategory category) {
		this.clientCategoryDao.persist(category);
	}

	@Override
	@Transactional
	public ClientCategory createClientCategoryFromJson(ClientCategoryJson input) {

		log.info("Creating new client category: {}", input.toString());

		if (!this.checkClientCategoryDescription(input.getDescription(), null))
			throw new ConflictException(15001, "Client category description " + input.getDescription() + " already exists");

		ClientCategory c = new ClientCategory();
		c.populateClientCategoryFromInput(input);

		this.persist(c);

		return c;
	}

	@Override
	@Transactional
	public void update(ClientCategory category) {
		this.clientCategoryDao.update(category);
	}

	@Override
	@Transactional
	public ClientCategory updateClientCategoryFromJson(Integer id, ClientCategoryJson input) {

		log.info("Updating client category with id {} input: {}", id, input.toString());

		if (!this.checkClientCategoryDescription(input.getDescription(), id))
			throw new ConflictException(15001, "Client category description " + input.getDescription() + " already exists");

		ClientCategory category = this.getClientCategory(id);
		category.populateClientCategoryFromInput(input);

		this.update(category);

		return category;
	}

	@Override
	@Transactional
	public void deleteClientCategoryById(Integer id) {

		log.info("Deleting client category: {}", id);

		ClientCategory category = this.getClientCategory(id);

		for (Client client : this.clientService.getClients())
			if ((client.getCategory() != null) && (client.getCategory().equals(category)))
				throw new ConflictException(15101, "Cannot delete category " + category.getId() + " because it is already in use");

		this.clientCategoryDao.delete(category);
	}

	private Boolean checkClientCategoryDescription(String description, Integer clientId) {

		for (ClientCategory c : this.getClientCategories())
			if (c.getDescription().trim().toLowerCase().equals(description.trim().toLowerCase()))
				if ((clientId == null) || (c.getId() != clientId))
					return false;

		return true;
	}
}
