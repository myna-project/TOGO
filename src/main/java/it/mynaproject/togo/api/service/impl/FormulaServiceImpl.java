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
package it.mynaproject.togo.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.FormulaDao;
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.domain.Operation;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.FormulaJson;
import it.mynaproject.togo.api.service.ClientService;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.FormulaService;
import it.mynaproject.togo.api.service.OrgService;

@Service
public class FormulaServiceImpl implements FormulaService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrgService orgService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private FormulaDao formulaDao;

	@Autowired
	private DrainService drainService;

	@Override
	@Transactional
	public Formula getFormula(Integer id, Boolean isAdmin, String username) {

		Formula f = this.formulaDao.getFormula(id);

		if ((f == null) || (f.getOrg() == null) || (!isAdmin && !this.orgService.orgIsVisibleByUser(f.getOrg(), username)))
			throw new NotFoundException(404, "Formula " + id + " not found");

		return f;
	}

	@Override
	@Transactional
	public List<Formula> getAllFormulas(Boolean isAdmin, String username) {

		List<Formula> fList = new ArrayList<Formula>();

		for (Formula f : this.formulaDao.getAllFormulas())
			if (isAdmin || ((f.getOrg() != null) && this.orgService.orgIsVisibleByUser(f.getOrg(), username)))
				fList.add(f);

		return fList;
	}

	@Override
	@Transactional
	public void persist(Formula formula) {
		this.formulaDao.persist(formula);
	}

	@Override
	@Transactional
	public Formula createFormulaFromInput(FormulaJson input, Boolean isAdmin, String username) {

		log.info("Creating new formula");

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		if (!this.checkNameForFormulaForOrg(input.getName(), null, org, isAdmin, username))
			throw new ConflictException(9001, "Formula name " + input.getName() + " not available for this org");

		Formula f = new Formula();
		f.populateFormulaFromInput(input, org, (input.getClientId() != null) ? this.clientService.getClient(input.getClientId(), isAdmin, username) : null);
		f.setComponents(populateFormulaComponentFromInput(f, input, isAdmin, username));

		this.persist(f);

		return f;
	}

	@Override
	@Transactional
	public void update(Formula formula) {
		this.formulaDao.update(formula);
	}

	@Override
	@Transactional
	public Formula updateFormulaFromInput(Integer id, FormulaJson input, Boolean isAdmin, String username) {

		log.info("Updating formula with id {}", id);

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		Formula f = this.getFormula(id, isAdmin, username);

		if (!this.checkNameForFormulaForOrg(input.getName(), id, org, isAdmin, username))
			throw new ConflictException(9001, "Formula name " + input.getName() + " not available for this org");

		f.getComponents().clear();

		f.populateFormulaFromInput(input, org, (input.getClientId() != null) ? this.clientService.getClient(input.getClientId(), isAdmin, username) : null);
		f.getComponents().addAll(populateFormulaComponentFromInput(f, input, isAdmin, username));

		this.update(f);

		return f;
	}

	@Override
	@Transactional
	public void deleteFormulaById(Integer id, Boolean isAdmin, String username) {

		log.info("Deleting formula with id {}", id);

		Formula f = this.getFormula(id, isAdmin, username);

		this.formulaDao.delete(f);
	}

	private List<FormulaComponent> populateFormulaComponentFromInput(Formula f, FormulaJson input, Boolean isAdmin, String username) {

		List<FormulaComponent> comps = new ArrayList<FormulaComponent>();

		Integer i = 0;
		for (Integer drainId : input.getComponents()) {
			i++;

			FormulaComponent newComp = new FormulaComponent();
			newComp.setDrain(drainService.getDrain(drainId, isAdmin, username));
			newComp.setFormula(f);
			newComp.setPositiveNegativeValue(input.getPositiveNegativeValues().get(i - 1));
			newComp.setExcludeOutliers(input.getExcludeOutliers().get(i - 1));
			newComp.setAggregation(input.getAggregations().get(i - 1));
			newComp.setOperator((i == input.getComponents().size()) ? Operation.SEMICOLON : input.getOperators().get(i - 1));
			newComp.setLegend(input.getLegends().get(i -1));

			comps.add(newComp);
		}

		return comps;
	}

	private boolean checkNameForFormulaForOrg(String name, Integer formulaId, Org org, Boolean isAdmin, String username) {

		for (Formula f : this.getAllFormulas(isAdmin, username))
			if (f.getOrg() == org)
				if (f.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
					if ((formulaId == null) || (f.getId() != formulaId))
						return false;

		return true;
	}
}
