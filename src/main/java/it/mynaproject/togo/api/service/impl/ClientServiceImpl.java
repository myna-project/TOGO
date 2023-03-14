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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.ClientDao;
import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.ClientType;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.ClientJson;
import it.mynaproject.togo.api.service.ClientCategoryService;
import it.mynaproject.togo.api.service.ClientService;
import it.mynaproject.togo.api.service.FeedService;
import it.mynaproject.togo.api.service.OrgService;

@Service
public class ClientServiceImpl implements ClientService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private ClientCategoryService clientCategoryService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private FeedService feedService;

	@Override
	@Transactional
	public Client getClient(Integer id, Boolean isAdmin, String username) {

		log.debug("Request for client with id {}", id);

		Client c = this.clientDao.getClient(id);
		if ((c != null) && (isAdmin || this.orgService.orgIsVisibleByUser(c.getOrg(), username))) {
			return c;
		} else {
			throw new NotFoundException(404, "Client " + id + " not found");
		}
	}

	@Override
	@Transactional
	public List<Client> getClients() {
		return this.clientDao.getClients();
	}

	@Override
	@Transactional
	public List<Client> getClientsForUser(String username) {

		List<Client> clients = new ArrayList<>();
		for (Client c : this.clientDao.getClients()) {
			if ((c != null) && (((username != null) && this.orgService.orgIsVisibleByUser(c.getOrg(), username)) || (username == null)))
				clients.add(c);
		}

		return clients;
	}

	@Override
	@Transactional
	public void persist(Client client) {
		this.clientDao.persist(client);
	}

	@Override
	@Transactional
	public Client createClientFromJson(ClientJson input, Boolean isAdmin, String username) {

		log.info("Creating new client: {}", input.toString());

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		if (!this.checkClientNameForOrg(input.getName(), org, null))
			throw new ConflictException(5001, "Client name " + input.getName() + " not available for this org");

		Client controller = null;
		if (input.getControllerId() != null)
			controller = this.getClient(input.getControllerId(), isAdmin, username);

		Client parent = null;
		if (input.getParentId() != null)
			parent = this.getClient(input.getParentId(), isAdmin, username);

		List<Client> children = new ArrayList<>();
		for (Integer cid : input.getChildIds())
			children.add(this.getClient(cid, isAdmin, username));

		List<Client> controlledList = new ArrayList<>();
		for (Integer cid : input.getControlledIds())
			controlledList.add(this.getClient(cid, isAdmin, username));

		List<Feed> feeds = new ArrayList<>();
		for (Integer fid : input.getFeedIds())
			feeds.add(feedService.getFeed(fid, isAdmin, username));

		Client c = new Client();
		c.populateClientFromInput(input, ((input.getCategoryId() != null) ? this.clientCategoryService.getClientCategory(input.getCategoryId()) : null), org, controller, parent, children, controlledList, feeds, true);

		this.persist(c);

		return c;
	}

	@Override
	@Transactional
	public void update(Client client, String username) {

		Client old = this.getClient(client.getId(), true, username);

		for (Client c : old.getChildList()) {
			if (!client.getChildList().contains(c)) {
				c.setParent(null);
				this.update(c, username);
			}
		}

		for (Client c : old.getControlledList()) {
			if (!client.getControlledList().contains(c)) {
				c.setController(null);
				this.update(c, username);
			}
		}

		for (Feed f : old.getFeeds()) {
			if (!client.getFeeds().contains(f)) {
				f.removeClient(client);
				this.feedService.update(f);
			}
		}

		this.clientDao.update(client);
	}

	@Override
	@Transactional
	public Client updateClientFromJson(Integer id, ClientJson input, Boolean isAdmin, String username) {

		log.info("Updating client with id {} input: {}", id, input.toString());

		Client client = this.getClient(id, isAdmin, username);

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		if (!this.checkClientNameForOrg(input.getName(), org, id))
			throw new ConflictException(5001, "Client name " + input.getName() + " not available for this org");

		Client parent = null;
		if (input.getParentId() != null) {
			parent = this.getClient(input.getParentId(), isAdmin, username);
			if (!this.cycleSafe(client, parent))
				throw new ConflictException(5002, "Parent" + input.getParentId() + " not available for this client");
		}

		Client controller = null;
		if (input.getControllerId() != null) {
			controller = this.getClient(input.getControllerId(), isAdmin, username);
			if (!this.cycleSafe(client, controller))
				throw new ConflictException(5003, "Controller" + input.getControllerId() + " not available for this client");
		}

		List<Client> childList = new ArrayList<Client>();
		for (Integer cid : input.getChildIds()) {
			if (cid == client.getId())
				throw new ConflictException(5002, "Parent" + input.getControllerId() + " not available for this client");

			childList.add(this.getClient(cid, isAdmin, username));
		}

		List<Client> controlledList = new ArrayList<Client>();
		for (Integer cid : input.getControlledIds()) {
			if (cid == client.getId())
				throw new ConflictException(5003, "Controller" + input.getControllerId() + " not available for this client");

			controlledList.add(this.getClient(cid, isAdmin, username));
		}

		List<Feed> feeds = new ArrayList<Feed>();
		for (Integer fid : input.getFeedIds())
			feeds.add(feedService.getFeed(fid, isAdmin, username));

		client.populateClientFromInput(input, ((input.getCategoryId() != null) ? this.clientCategoryService.getClientCategory(input.getCategoryId()) : null), org, controller, parent, childList, controlledList, feeds, client.getType().equals(ClientType.WOLF_MANAGED) ? false : true);

		this.update(client, username);

		return client;
	}

	@Override
	@Transactional
	public void deleteClientById(Integer clientId, Boolean isAdmin, String username) {

		log.info("Deleting client: {}", clientId);

		Client client = this.getClient(clientId, isAdmin, username);

		if (client.getType().equals(ClientType.WOLF_MANAGED))
			throw new ConflictException(5101, "Cannot delete client " + client.getName() + " because it is managed by Wolf");

		if (client.getFeeds().size() > 0)
			throw new ConflictException(5102, "Cannot delete client " + client.getName() + " because there are one or more feeds");

		if (client.getChildList().size() > 0)
			throw new ConflictException(5103, "Cannot delete client " + client.getName() + " because there are one or more client children");

		if (client.getControlledList().size() > 0)
			throw new ConflictException(5104, "Cannot delete client " + client.getName() + " because there are one or more controlled client");

		this.clientDao.delete(client);
	}

	@Transactional
	@Override
	public List<Integer> getClientIdByDeviceId(String deviceId) {
		return this.clientDao.getClientIdByDeviceId(deviceId);
	}

	@Transactional
	@Override
	public Client getClientFromDeviceId(Client controller, String deviceId) {

		if ((controller.getDeviceId() != null) && controller.getDeviceId().equals(deviceId))
			return controller;

		for (Client p : controller.getControlledList()) {
			if ((p.getDeviceId() != null) && p.getDeviceId().equals(deviceId)) {
				return p;
			} else {
				Client temp = getClientFromDeviceId(p, deviceId);
				if (temp != null)
					return temp;
			}
		}

		return null;
	}

	@Override
	public List<Client> getAllControlledClients(Client controller) {

		List<Client> controlledList = new ArrayList<Client>();

		return this.getAllControlledClients(controller, controlledList);
	}

	@Override
	public Boolean inactiveClientsFromController(Client controller, String username) {

		Boolean active = false;
		Boolean lastControlled = true;
		for (Client controlled : controller.getControlledList()) {
			lastControlled = false;
			Boolean controlled_active = inactiveClientsFromController(controlled, username);
			if (controlled_active && !active)
				active = true;
		}

		if (lastControlled) {
			active = controller.getActive();
		} else {
			controller.setActive(active);
			this.update(controller, username);
		}

		return active;
	}

	private Boolean checkClientNameForOrg(String name, Org org, Integer clientId) {

		for (Client c : org.getClients())
			if (c.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
				if ((clientId == null) || (c.getId() != clientId))
					return false;

		return true;
	}

	private Boolean cycleSafe(Client client, Client newParent) {

		List<Client> childList = new ArrayList<Client>();
		childList = this.getAllChildren(client, childList);

		if (childList.contains(newParent))
			return false;

		return true;
	}

	private List<Client> getAllChildren(Client client, List<Client> childList) {

		for (Client child : client.getChildList()) {
			if (!childList.contains(child))
				childList.add(child);

			childList = this.getAllChildren(child, childList);
		}

		return childList;
	}

	private List<Client> getAllControlledClients(Client client, List<Client> controlledList) {

		for (Client controlled : client.getControlledList()) {
			if (!controlledList.contains(controlled))
				controlledList.add(controlled);

			controlledList = this.getAllChildren(controlled, controlledList);
		}

		return controlledList;
	}
}
