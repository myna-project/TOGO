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

import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.model.FormulaJson;

public interface FormulaService {

	public Formula getFormula(Integer formulaId, Boolean isAdmin, String username);
	public List<Formula> getAllFormulas(Boolean isAdmin, String username);
	public void persist(Formula formula);
	public Formula createFormulaFromInput(FormulaJson input, Boolean isAdmin, String username);
	public void update(Formula formula);
	public Formula updateFormulaFromInput(Integer id, FormulaJson input, Boolean isAdmin, String username);
	public void deleteFormulaById(Integer id, Boolean isAdmin, String username);
}
