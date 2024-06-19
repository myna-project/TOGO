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

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.UserDao;
import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.domain.Role;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.UserJson;
import it.mynaproject.togo.api.mqtt.MqttStarter;
import it.mynaproject.togo.api.service.OrgService;
import it.mynaproject.togo.api.service.RoleService;
import it.mynaproject.togo.api.service.UserService;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserDao userDao;

	@Autowired
	private OrgService orgService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MqttStarter mqttStarter;

	@Override
	@Transactional
	public User getUser(Integer id, Boolean isAdmin, String username) {

		User u = this.userDao.getUser(id);
		if (u == null || (!isAdmin && !this.getVisibleUsers(username).contains(u)))
			throw new NotFoundException(404, "User " + id + " not found");

		return u;
	}

	@Override
	@Transactional
	public List<User> getUsers() {
		return this.userDao.getUsers();
	}

	@Override
	@Transactional
	public void persist(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		this.userDao.persist(user);
	}

	@Override
	@Transactional
	public User createUserFromInput(UserJson input, Boolean isAdmin, String username) {

		log.info("Creating new user: {}", input.toString().replaceFirst("password=.*,", ""));

		if (!this.checkNameForUser(input.getUsername(), null))
			throw new ConflictException(1001, "Username " + input.getUsername() + " not available");
		if (!this.checkEmailForUser(input.getEmail(), null))
			throw new ConflictException(1002, "Email address " + input.getEmail() + " already registered");

		User user = new User();

		List<Role> roles = new ArrayList<>();
		for (Integer id : input.getRoleIds()) {
			Role r = this.roleService.getRole(id);
			roles.add(r);
		}

		user.populateUserFromInput(input, roles);

		this.persist(user);

		mqttStarter.subscribeUser(user);

		return user;
	}

	@Override
	@Transactional
	public void update(User user, Boolean changePassword) {

		if (changePassword)
			user.setPassword(passwordEncoder.encode(user.getPassword()));

		this.userDao.update(user);
	}

	@Override
	@Transactional
	public User updateUserFromInput(Integer id, UserJson input, Boolean isAdmin, String username) {

		log.info("Updating user with id {} from input: {}", id, input.toString());

		User user = this.getUser(id, isAdmin, username);
		String oldUsername = user.getUsername();

		if (!this.checkNameForUser(input.getUsername(), input.getId()))
			throw new ConflictException(1001, "Username " + input.getUsername() + " not available");
		if (!this.checkEmailForUser(input.getEmail(), input.getId()))
			throw new ConflictException(1002, "Email address " + input.getEmail() + " already registered");

		boolean changePassword = false;
		if (input.getOldPassword() != null) {
			if (input.getPassword().equals(input.getOldPassword()))
				throw new ConflictException(1004, "Old and new password must be different");

			if (!this.passwordsMatch(user.getPassword(), input.getOldPassword()))
				throw new ConflictException(1005, "Old password does not match");

			changePassword = true;
		}

		List <Role> roles = new ArrayList<>();
		for (Integer idRole : input.getRoleIds()) {
			Role r = this.roleService.getRole(idRole);
			roles.add(r);
		}

		user.populateUserFromInput(input, roles);

		this.update(user, changePassword);

		try {
			mqttStarter.unsubscribeUser(oldUsername);
			mqttStarter.subscribeUser(user);
		} catch (MqttException e) {
			log.error(e.getMessage());
		}

		return user;
	}

	@Override
	@Transactional
	public void deleteUserById(Integer id, Boolean isAdmin, String username) {

		log.info("Deleting user: {}", id);

		User u = this.getUser(id, isAdmin, username);
		if (u.getRoles().size() > 0)
			throw new ConflictException(1101, "Cannot delete user with id " + id + " because is linked to roles");
		if (u.getJobs().size() > 0)
			throw new ConflictException(1102, "Cannot delete user with id " + id + " because is linked to jobs");

		try {
			mqttStarter.unsubscribeUser(u.getUsername());
		} catch (MqttException e) {
			log.error(e.getMessage());
		}

		this.userDao.delete(u);
	}

	@Override
	@Transactional
	public User getUserByUsername(String username) {
		return this.userDao.getUserByUsername(username);
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = this.userDao.getUserByUsername(username);

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), (user.getEnabled() == 1), true, true, true, user.getRoles());
	}

	@Override
	@Transactional
	public void addJobToUser(User user, Job job) {

		if (!user.getJobs().contains(job)) {
			user.addJob(job);
			this.update(user, false);
		}
	}

	@Override
	@Transactional
	public void removeJobFromUser(User user,Job job) {

		user.removeJob(job);
		this.update(user, false);
	}

	@Override
	@Transactional
	public List<User> getVisibleUsers(String username) {

		ArrayList<User> visibleUsers= new ArrayList<>();

		for (Org org : this.orgService.getOrgsForUser(username)) {
			List<Job> jobs = org.getJobs();
			for (Job job : jobs) {
				List<User> tmp = job.getUsers();
				tmp.removeAll(visibleUsers);
				visibleUsers.addAll(tmp);
			}
		}

		return visibleUsers;
	}

	private Boolean passwordsMatch(String oldPsw, String newPsw) {
		return this.passwordEncoder.matches(newPsw, oldPsw);
	}

	private Boolean checkNameForUser(String name, Integer id) {

		User u = this.userDao.getUserByUsername(name);
		if (u != null)
			if ((id == null) || (u.getId() != id))
				return false;

		return true;
	}

	private Boolean checkEmailForUser(String email, Integer id) {

		for (User u : this.getUsers())
			if (u.getEmail().trim().toLowerCase().equals(email.trim().toLowerCase()))
				if ((id == null) || (u.getId() != id))
					return false;

		return true;
	}
}
