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

import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.model.UserJson;

public interface UserService {

	public User getUser(Integer id, Boolean isAdmin, String username);
	public List<User> getUsers();
	public List<User> getVisibleUsers(String username);
	public void persist(User user);
	public User createUserFromInput(UserJson input, Boolean isAdmin, String username);
	public void update(User user, Boolean changeUser);
	public User updateUserFromInput(Integer id, UserJson input, Boolean isAdmin, String username);
	public void deleteUserById(Integer id, Boolean isAdmin, String username);
	public User getUserByUsername(String username);
	public void addJobToUser(User user, Job job);
	public void removeJobFromUser(User user, Job job);
}
