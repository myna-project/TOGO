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
package it.mynaproject.togo.api.exception;

public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 3221366047232938662L;

	private Integer code;

	public ForbiddenException(String msg) {
		super(msg);
	}

	public ForbiddenException(Integer code, String msg) {
		super(msg);
		this.setCode(code);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
