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

import it.mynaproject.togo.api.dao.IndexGroupDao;
import it.mynaproject.togo.api.domain.IndexGroup;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.IndexGroupJson;
import it.mynaproject.togo.api.service.IndexGroupService;
import it.mynaproject.togo.api.service.OrgService;

@Service
public class IndexGroupServiceImpl implements IndexGroupService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IndexGroupDao indexGroupDao;

	@Autowired
	private OrgService orgService;

	@Override
	@Transactional
	public IndexGroup getIndexGroup(Integer id, Boolean isAdmin, String username) {

		IndexGroup group = this.indexGroupDao.getIndexGroup(id);
		if ((group == null) || (group.getOrg() == null) || (!isAdmin && !this.orgService.orgIsVisibleByUser(group.getOrg(), username)))
			throw new NotFoundException(404, "Index group " + id + " not found");

		return group;
	}

	@Override
	@Transactional
	public List<IndexGroup> getIndexGroups(Boolean isAdmin, String username) {

		List<IndexGroup> groupList = new ArrayList<IndexGroup>();
		for (IndexGroup group : this.indexGroupDao.getIndexGroups())
			if (isAdmin || ((group.getOrg() != null) && this.orgService.orgIsVisibleByUser(group.getOrg(), username)))
				groupList.add(group);

		return groupList;
	}

	@Override
	@Transactional
	public void persist(IndexGroup group) {
		this.indexGroupDao.persist(group);
	}

	@Override
	@Transactional
	public IndexGroup createIndexGroupFromInput(IndexGroupJson input, Boolean isAdmin, String username) {

		log.info("Creating new index group");

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		if (!this.checkNameForIndexGroupForOrg(input.getName(), null, org, isAdmin, username))
			throw new ConflictException(13003, "Index group name " + input.getName() + " not available for this org");

		IndexGroup group = new IndexGroup();
		group.populateIndexGroupFromInput(input, org);

		this.persist(group);

		return group;
	}

	@Override
	@Transactional
	public void update(IndexGroup group) {
		this.indexGroupDao.update(group);
	}

	@Override
	@Transactional
	public IndexGroup updateIndexGroupFromInput(Integer id, IndexGroupJson input, Boolean isAdmin, String username) {

		log.info("Updating index group with id {}", id);

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		IndexGroup g = this.getIndexGroup(id, isAdmin, username);
		if (!this.checkNameForIndexGroupForOrg(input.getName(), id, org, isAdmin, username))
			throw new ConflictException(13003, "Index group name " + input.getName() + " not available for this org");

		g.populateIndexGroupFromInput(input, org);

		this.update(g);

		return g;
	}

	@Override
	@Transactional
	public void deleteIndexGroupById(Integer id, Boolean isAdmin, String username) {

		log.info("Deleting index group with id {}", id);

		this.indexGroupDao.delete(this.getIndexGroup(id, isAdmin, username));
	}

	private boolean checkNameForIndexGroupForOrg(String name, Integer id, Org org, Boolean isAdmin, String username) {

		for (IndexGroup g : this.getIndexGroups(isAdmin, username))
			if (g.getOrg() == org)
				if (g.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
					if ((id == null) || (g.getId() != id))
						return false;

		return true;
	}
}
