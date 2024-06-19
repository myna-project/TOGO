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

import it.mynaproject.togo.api.dao.OrgDao;
import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.OrgJson;
import it.mynaproject.togo.api.service.JobService;
import it.mynaproject.togo.api.service.OrgService;
import it.mynaproject.togo.api.service.UserService;

@Service
public class OrgServiceImpl implements OrgService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrgDao orgDao;

	@Autowired
	private JobService jobService;

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public Org getOrg(Integer id, Boolean isAdmin, String username) {

		log.debug("Request for org with id {}", id);

		Org o = this.orgDao.getOrg(id);
		if ((o == null) || (!isAdmin && !this.orgIsVisibleByUser(o, username)))
			throw new NotFoundException(404, "Org " + id + " not found");

		return o;
	}

	@Override
	@Transactional
	public List<Org> getOrgs() {
		return this.orgDao.getOrgs();
	}

	public Boolean orgIsVisibleByUser(Org o, String username) {
		return this.getOrgsForUser(username).contains(o);
	}

	@Override
	@Transactional
	public List<Org> getOrgsForUser(String username) {

		User u = this.userService.getUserByUsername(username);
		ArrayList<Org> orgs = new ArrayList<>();
		for (Org o : this.getOrgs()) {
			for (Job j : u.getJobs()) {
				if (o.getJobs().contains(j) && !orgs.contains(o)) {
					orgs.add(o);
					List<Org> childList = new ArrayList<Org>();
					childList = this.getAllChildren(o, childList);
					for (Org child : childList)
						if (!orgs.contains(child))
							orgs.add(child);
				}
			}
		}

		return orgs;
	}

	@Override
	@Transactional
	public void persist(Org org) {
		this.orgDao.persist(org);
		this.jobService.createDefaultJob(org);
	}

	@Override
	@Transactional
	public Org createOrgFromInput(OrgJson input, Boolean isAdmin, String username) {

		log.info("Creating new org: {}", input.toString());

		Org parent = null;
		if (input.getParentId() != null)
			parent = this.getOrg(input.getParentId(), isAdmin, username);

		if (!this.checkNameForOrg(input.getName(), input.getParentId(), null, username))
			throw new ConflictException(4001, "Org name " + input.getName() + " not available for this parent org");

		Org o = new Org();
		o.populateOrgFromInput(input, parent);

		this.persist(o);

		return o;
	}

	@Override
	@Transactional
	public void update(Org org) {
		this.orgDao.update(org);
	}

	@Override
	@Transactional
	public Org updateOrgFromInput(Integer id, OrgJson input, Boolean isAdmin, String username) {

		log.info("Updating org with id {}", id);

		Org org = this.getOrg(id, isAdmin, username);

		Org parent = null;
		if (input.getParentId() != null) {
			parent = this.getOrg(input.getParentId(), isAdmin, username);

			if (!this.cycleSafe(org, parent))
				throw new ConflictException(4002, "Parent " + input.getParentId() + " not available for this org");
		}

		if (!this.checkNameForOrg(input.getName(), input.getParentId(), org.getId(), username))
			throw new ConflictException(4001, "Org name " + input.getName() + " not available for this parent org");

		org.populateOrgFromInput(input, parent);

		this.update(org);

		return org;
	}

	@Override
	@Transactional
	public void deleteOrgById(Integer id, Boolean isAdmin, String username) {

		Org o = this.getOrg(id, isAdmin, username);
		if (o.getJobs().size() > 0)
			throw new ConflictException(4101, "Cannot delete org " + o.getName() + " because there are one or more jobs");
		if (o.getClients().size() > 0)
			throw new ConflictException(4102, "Cannot delete org " + o.getName() + " because there are one or more clients");
		if (o.getChildList().size() > 0)
			throw new ConflictException(4103, "Cannot delete org " + o.getName() + " because there are one or more orgs children");

		this.orgDao.delete(o);
	}

	private Boolean checkNameForOrg(String name, Integer parentId, Integer orgId, String username) {

		for (Org o : this.getOrgsForUser(username))
			if (o.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
				if ((parentId == null) || ((o.getParent() != null) && (o.getParent().getId() == parentId)))
					if ((orgId == null) || (o.getId() != orgId))
						return false;

		return true;
	}

	private Boolean cycleSafe(Org org, Org newParent) {

		List<Org> childList = new ArrayList<Org>();
		childList = this.getAllChildren(org, childList);

		if (childList.contains(newParent))
			return false;

		return true;
	}

	private List<Org> getAllChildren(Org org, List<Org> childList) {

		for (Org child : org.getChildList()) {
			if (!childList.contains(child))
				childList.add(child);

			childList = this.getAllChildren(child, childList);
		}

		return childList;
	}
}
