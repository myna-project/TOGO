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
package it.mynaproject.togo.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import it.mynaproject.togo.api.model.RoleJson;

@Entity
@Table(name="role")
public class Role extends BaseDomain implements GrantedAuthority {

	private static final long serialVersionUID = -5704123566643000391L;

	@Column(nullable=false,unique=true)
	private String name;

	private String description;

	@ManyToMany(fetch=FetchType.LAZY,cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},mappedBy="roleList")
	private List<User> userList = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getUsers() {
		return this.userList;
	}

	public void setUsers(List<User> users) {
		this.userList = users;
	}

	@Override
	public String getAuthority() {
		return this.name;
	}

	public void populateRoleFromInput(RoleJson input) {

		this.setDescription(input.getDescription());
		this.setName(input.getName());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Role [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
}
