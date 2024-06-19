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

import it.mynaproject.togo.api.dao.FeedDao;
import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.FeedJson;
import it.mynaproject.togo.api.service.ClientService;
import it.mynaproject.togo.api.service.FeedService;
import it.mynaproject.togo.api.service.OrgService;

@Service
public class FeedServiceImpl implements FeedService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FeedDao feedDao;

	@Autowired
	private ClientService clientService;

	@Autowired
	private OrgService orgService;

	@Override
	@Transactional
	public Feed getFeed(Integer id, Boolean isAdmin, String username) {

		Feed f = this.feedDao.getFeed(id);
		if ((f == null) || (!isAdmin && !this.orgService.orgIsVisibleByUser(f.getClients().get(0).getOrg(), username)))
			throw new NotFoundException(404, "Feed " + id + " not found");

		return f;
	}

	@Override
	@Transactional
	public List<Feed> getFeedsForUser(String username) {

		List<Feed> feeds = new ArrayList<>();
		for (Client c : this.clientService.getClientsForUser(username)) {
			List<Feed> tmp = c.getFeeds();
			tmp.removeAll(feeds);
			feeds.addAll(tmp);
		}

		return feeds;
	}

	@Override
	@Transactional
	public void persist(Feed feed) {
		this.feedDao.persist(feed);
	}

	@Override
	@Transactional
	public Feed createFeedFromInput(FeedJson input, Boolean isAdmin, String username) {

		log.info("Creating new feed: {}", input.toString());

		List<Client> clients = new ArrayList<>();
		for (Integer cId : input.getClientIds()) {
			Client c = this.clientService.getClient(cId, isAdmin, username);
			if (!this.checkFeedDescrForClient(input.getDescription(), c, null))
				throw new ConflictException(6001, "Feed description " + input.getDescription() + " not available for this client");
			clients.add(c);
		}

		Feed feed = new Feed();
		feed.populateFeedFromInput(input, clients);

		this.persist(feed);

		return feed;
	}

	@Override
	@Transactional
	public void update(Feed feed) {
		this.feedDao.update(feed);
	}

	@Override
	@Transactional
	public Feed updateFeedFromInput(Integer id, FeedJson input, Boolean isAdmin, String username) {

		log.info("Update feed with id {}", id);

		Feed f = this.getFeed(id, isAdmin, username);

		List<Client> clients = new ArrayList<>();
		for (Integer clientId : input.getClientIds()) {
			Client c = clientService.getClient(clientId, isAdmin, username);
			if (!this.checkFeedDescrForClient(input.getDescription(), c, input.getId()))
				throw new ConflictException(6001, "Feed description " + input.getDescription() + " not available for this client");
			clients.add(c);
		}

		f.populateFeedFromInput(input, clients);

		this.update(f);

		return f;
	}

	@Override
	@Transactional
	public void deleteFeedById(Integer id, Boolean isAdmin, String username) {

		Feed f = this.getFeed(id, isAdmin, username);

		if (f.getDrains().size() > 0)
			throw new ConflictException(6101, "Cannot delete feed " + id + " because there are one or more drains");

		this.feedDao.delete(f);
	}

	private Boolean checkFeedDescrForClient(String description, Client client, Integer feedId) {

		for (Feed f : client.getFeeds())
			if (f.getDescription().trim().toLowerCase().equals(description.trim().toLowerCase()))
				if ((feedId == null) || (f.getId() != feedId))
					return false;

		return true;
	}
}
