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

import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.ClientCategoryJson;

@Entity
@Table(name="client_category")
public class ClientCategory extends BaseDomain {

	@Column(nullable=false)
	private String description;

	@Column
	private byte[] image;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public void populateClientCategoryFromInput(ClientCategoryJson input) {

		this.setDescription(input.getDescription());
		if (input.getImage() != null)
			this.setImage(Base64.getDecoder().decode(input.getImage()));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientCategory [description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
}
