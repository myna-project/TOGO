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
package it.mynaproject.togo.api.util;

public class Pair<L, R> {

	private final L left;
	private final R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public Pair() {
		this(null, null);
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		return left.hashCode() ^ right.hashCode();
	}

	public boolean isEmpty() {
		return this.left == null;
	}

	@Override
	public boolean equals(Object o) {

		if (o == null)
			return false;
		if (!(o instanceof Pair))
			return false;

		Pair<?, ?> pairo = (Pair<?, ?>) o;

		return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
	}
}
