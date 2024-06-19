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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.mynaproject.togo.api.domain.IndexRelativePeriod;

import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormulaElementJson {

	@NotNull(message = "Please provide a formula id!")
	@JsonProperty("formula_id")
	Integer formulaId;

	@JsonProperty("formula_name")
	String formulaName;

	@JsonProperty("n_skip")
	Integer nSkip;

	@JsonProperty("skip_period")
	IndexRelativePeriod skipPeriod;

	public Integer getFormulaId() {
		return formulaId;
	}

	public void setFormulaId(Integer formulaId) {
		this.formulaId = formulaId;
	}

	public String getFormulaName() {
		return formulaName;
	}

	public void setFormulaName(String formulaName) {
		this.formulaName = formulaName;
	}

	public Integer getNSkip() {
		return nSkip;
	}

	public void setNSkip(Integer nSkip) {
		this.nSkip = nSkip;
	}

	public IndexRelativePeriod getSkipPeriod() {
		return skipPeriod;
	}

	public void setSkipPeriod(IndexRelativePeriod skipPeriod) {
		this.skipPeriod = skipPeriod;
	}
}
