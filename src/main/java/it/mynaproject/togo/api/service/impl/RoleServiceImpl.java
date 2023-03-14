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

import it.mynaproject.togo.api.dao.RoleDao;
import it.mynaproject.togo.api.domain.Role;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.RoleJson;
import it.mynaproject.togo.api.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoleDao roleDao;

	@Override
	@Transactional
	public Role getRole(Integer id) {

		Role r = this.roleDao.getRole(id);
		if (r == null)
			throw new NotFoundException(404, "Role " + id + " not found");

		return r;
	}

	@Override
	@Transactional
	public List<Role> getRoles() {
		return this.roleDao.getRoles();
	}

	@Override
	@Transactional
	public void persist(Role role) {
		this.roleDao.persist(role);
	}

	@Override
	@Transactional
	public Role createRoleFromJson(RoleJson input) {

		log.info("Creating new role: {}", input.toString());

		if (!this.checkNameForRole(input.getName(), null))
			throw new ConflictException(2001, "Role " + input.getName() + " already exists");

		Role role = new Role();
		role.populateRoleFromInput(input);

		this.persist(role);

		return role;
	}

	@Override
	@Transactional
	public void update(Role role) {
		this.roleDao.update(role);
	}

	@Override
	@Transactional
	public Role updateRoleFromJson(Integer id, RoleJson input) {

		log.info("Updating role with id: {}", id);

		if (!this.checkNameForRole(input.getName(), id))
			throw new ConflictException(2001, "Role " + input.getName() + " already exists");

		Role role = this.getRole(id);
		role.populateRoleFromInput(input);

		this.update(role);

		return role;
	}

	@Override
	@Transactional
	public void deleteRoleById(Integer id) {

		log.info("Deleting role: {}", id);

		Role r = this.getRole(id);

		if (r.getUsers().size() > 0)
			throw new ConflictException(2101, "Cannot delete role " + r.getName() + " because it is assigned to one or more users");

		this.roleDao.delete(r);
	}

	private Boolean checkNameForRole(String name, Integer id) {

		for (Role r : this.getRoles())
			if (r.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
				if ((id == null) || (r.getId() != id))
					return false;

		return true;
	}
}
