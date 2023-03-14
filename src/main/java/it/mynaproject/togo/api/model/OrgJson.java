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
public class OrgJson {

	private Integer id;

	@NotBlank(message = "Please provide a name!")
	private String name;

	@JsonProperty("parent_id")
	private Integer parentId;

	private List<OrgJson> children = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public void addChild(OrgJson o) {
		this.children.add(o);
	}

	public List<OrgJson> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrgJson [name=");
		builder.append(name);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append("]");
		return builder.toString();
	}
}
