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
package it.mynaproject.togo.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class RoleJson {

	private Integer id;

	@NotBlank(message = "Please provide a name!")
	private String name;

	@NotBlank(message = "Please provide a description!")
	private String description;

	@JsonProperty("users_list")
	private List <String> usersList = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public List<String> getUsers() {
		return usersList;
	}

	public void setUsers(List<String> usersList) {
		this.usersList = usersList;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleJson [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", users=");
		builder.append(usersList);
		builder.append("]");
		return builder.toString();
	}
}
