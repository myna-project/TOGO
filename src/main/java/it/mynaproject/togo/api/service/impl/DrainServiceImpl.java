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

import it.mynaproject.togo.api.dao.DrainDao;
import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.ClientType;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.DrainJson;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.FeedService;
import it.mynaproject.togo.api.service.MeasureService;
import it.mynaproject.togo.api.service.OrgService;

@Service
public class DrainServiceImpl implements DrainService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DrainDao drainDao;

	@Autowired
	private FeedService feedService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private MeasureService measureService;

	@Override
	@Transactional
	public Drain getDrain(Integer id, Boolean isAdmin, String username) {

		Drain d = this.drainDao.getDrain(id);
		if ((d == null) || (!isAdmin && !this.orgService.orgIsVisibleByUser(d.getFeed().getClients().get(0).getOrg(), username)))
			throw new NotFoundException(404, "Drain " + id + " not found");

		return d;
	}

	@Override
	@Transactional
	public List<Drain> getDrains() {
		return this.drainDao.getDrains();
	}

	@Override
	@Transactional
	public List<Drain> getDrainsForUser(String username) {

		List<Drain> drains = new ArrayList<>();
		for (Feed f : this.feedService.getFeedsForUser(username)){
			List<Drain> tmp = f.getDrains();
			tmp.removeAll(drains);
			drains.addAll(tmp);
		}

		return drains;
	}

	@Override
	@Transactional
	public void persist(Drain drain) {
		this.drainDao.persist(drain);
	}

	@Override
	@Transactional
	public Drain createDrainFromInput(DrainJson input, Boolean isAdmin, String username) {

		log.info("Creating new drain: {}", input.toString());

		Feed f = this.feedService.getFeed(input.getFeedId(), isAdmin, username);

		if (!this.checkDrainNameForFeed(input.getName(), f, null))
			throw new ConflictException(7001, "Drain name " + input.getName() + " not available for this feed");

		Drain baseDrain = (input.getBaseDrainId() != null) ? this.getDrain(input.getBaseDrainId(), isAdmin, username) : null;
		Drain diffDrain = (input.getDiffDrainId() != null) ? this.getDrain(input.getDiffDrainId(), isAdmin, username) : null;

		Drain drain = new Drain();
		drain.populateDrainFromInput(input, baseDrain,diffDrain, f, true);

		this.persist(drain);

		return drain;
	}

	@Override
	@Transactional
	public void update(Drain drain) {
		this.drainDao.update(drain);
	}

	@Override
	@Transactional
	public Drain updateDrainFromInput(Integer id, DrainJson input, Boolean isAdmin, String username) {

		log.info("Update drain {} from input: {}", id, input.toString());

		Drain d = this.getDrain(id, isAdmin, username);

		Feed f = this.feedService.getFeed(input.getFeedId(), isAdmin, username);

		if (!this.checkDrainNameForFeed(input.getName(), f, id))
			throw new ConflictException(7001, "Drain name " + input.getName() + " not available for this feed");

		Drain baseDrain = (input.getBaseDrainId() != null) ? this.getDrain(input.getBaseDrainId(), isAdmin, username) : null;
		Drain diffDrain = (input.getDiffDrainId() != null) ? this.getDrain(input.getDiffDrainId(), isAdmin, username) : null;

		Boolean editable = true;
		for (Client c : f.getClients()) {
			if (c.getComputerClient() && c.getType().equals(ClientType.WOLF_MANAGED)) {
				editable = false;
				break;
			}
		}

		d.populateDrainFromInput(input, baseDrain, diffDrain, f, editable);

		this.update(d);

		return d;
	}

	@Override
	@Transactional
	public void deleteDrainById(Integer id, Boolean isAdmin, String username) {

		Drain d = this.getDrain(id, isAdmin, username);

		if (this.measureService.checkMeasuresForDrain(d, null, null))
			throw new ConflictException(7101, "Cannot delete drain " + id + " because there are one or more measures");

		for (Client c : d.getFeed().getClients())
			if (c.getType().equals(ClientType.WOLF_MANAGED))
				throw new ConflictException(7102, "Cannot delete drain " + id + " because it is managed by Wolf");

		this.drainDao.delete(d);
	}

	@Transactional
	public Integer getDrainsCount() {
		return this.drainDao.getDrains().size();
	}

	private Boolean checkDrainNameForFeed(String name, Feed f, Integer drainId) {

		for (Drain d : f.getDrains())
			if (d.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
				if ((drainId == null) || (d.getId() != drainId))
					return false;

		return true;
	}
}
