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
package it.mynaproject.togo.api.dao;

import it.mynaproject.togo.api.domain.FormulaComponent;

public interface FormulaComponentDao {

	public void persist(FormulaComponent formulaComponent);
	public void update(FormulaComponent formulaComponent);
	public void delete(FormulaComponent formulaComponent);
	public FormulaComponent getFormulaComponentById(Integer id);
}
