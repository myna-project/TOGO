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

import it.mynaproject.togo.api.dao.DrainControlDao;
import it.mynaproject.togo.api.dao.DrainControlDetailDao;
import it.mynaproject.togo.api.domain.DrainControl;
import it.mynaproject.togo.api.domain.DrainControlDetail;
import it.mynaproject.togo.api.domain.DrainControlType;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.job.CronTaskRegistrar;
import it.mynaproject.togo.api.job.DrainControlTaskInitializer;
import it.mynaproject.togo.api.job.DrainControlTaskInitializer.DrainControlRunnableTask;
import it.mynaproject.togo.api.model.DrainControlDetailJson;
import it.mynaproject.togo.api.model.DrainControlJson;
import it.mynaproject.togo.api.service.DrainControlService;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.FormulaService;
import it.mynaproject.togo.api.service.OrgService;

@Service
public class DrainControlServiceImpl implements DrainControlService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DrainControlDao drainControlDao;

	@Autowired
	private DrainControlDetailDao drainControlDetailDao;

	@Autowired
	private OrgService orgService;

	@Autowired
	private DrainService drainService;

	@Autowired
	private FormulaService formulaService;

	@Autowired
	private CronTaskRegistrar cronTaskRegistrar;

	@Autowired
	private DrainControlTaskInitializer drainControlTaskInitializer;

	@Override
	@Transactional
	public DrainControl getDrainControl(Integer id, Boolean isAdmin, String username) {

		DrainControl control = this.drainControlDao.getDrainControl(id);
		if ((control == null) || (control.getOrg() == null) || (!isAdmin && !this.orgService.orgIsVisibleByUser(control.getOrg(), username)))
			throw new NotFoundException(404, "Drain control " + id + " not found");

		return control;
	}

	@Override
	@Transactional
	public List<DrainControl> getDrainControls(Boolean isAdmin, String username) {

		List<DrainControl> controlList = new ArrayList<DrainControl>();
		for (DrainControl control : this.drainControlDao.getDrainControls())
			if (isAdmin || ((control.getOrg() != null) && this.orgService.orgIsVisibleByUser(control.getOrg(), username)))
				controlList.add(control);

		return controlList;
	}

	@Override
	@Transactional
	public void persist(DrainControl control) {
		this.drainControlDao.persist(control);
	}

	@Override
	@Transactional
	public DrainControl createDrainControlFromInput(DrainControlJson input, Boolean isAdmin, String username) {

		log.info("Creating new drain control");

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		if (!this.checkNameForDrainControlForOrg(input.getName(), null, org, isAdmin, username))
			throw new ConflictException(14003, "Drain control name " + input.getName() + " not available for this org");

		DrainControl control = new DrainControl();
		control.populateDrainControlFromInput(input, org);

		this.persist(control);

		List<DrainControlDetail> details = new ArrayList<DrainControlDetail>();
		for (DrainControlDetailJson dj : input.getDetails()) {
			if ((control.getType().equals(DrainControlType.MISSING) || control.getType().equals(DrainControlType.MEASUREDIFF) && (dj.getDrainId() != null)) || (control.getType().equals(DrainControlType.THRESHOLD) && ((dj.getDrainId() != null) || (dj.getFormulaId() != null)))) {
				DrainControlDetail detail = new DrainControlDetail();
				detail.populateDrainControlDetailFromInput(dj, (dj.getDrainId() != null) ? this.drainService.getDrain(dj.getDrainId(), isAdmin, username) : null, (dj.getFormulaId() != null) ? this.formulaService.getFormula(dj.getFormulaId(), isAdmin, username) : null);
				detail.setDrainControl(control);
				detail.setError(false);
				this.drainControlDetailDao.persist(detail);
				details.add(detail);
			}
		}
		control.setDetails(details);

		DrainControlRunnableTask task = this.drainControlTaskInitializer.newDrainControlRunnableTask(control.getId());
		this.cronTaskRegistrar.addCronTask(task, control.getCronSecond() + " " + control.getCronMinute() + " " + control.getCronHour() + " " + control.getCronDayMonth() + " " + control.getCronMonth() + " " + control.getCronDayWeek());

		return control;
	}

	@Override
	@Transactional
	public void update(DrainControl control) {
		this.drainControlDao.update(control);
	}

	@Override
	@Transactional
	public DrainControl updateDrainControlFromInput(Integer id, DrainControlJson input, Boolean isAdmin, String username) {

		log.info("Updating drain control with id {}", id);

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);

		DrainControl control = this.getDrainControl(id, isAdmin, username);
		if (!this.checkNameForDrainControlForOrg(input.getName(), id, org, isAdmin, username))
			throw new ConflictException(14003, "Drain control name " + input.getName() + " not available for this org");

		DrainControlRunnableTask task = this.drainControlTaskInitializer.newDrainControlRunnableTask(control.getId());
		this.cronTaskRegistrar.removeCronTask(task);

		List<DrainControlDetail> originalDetails = new ArrayList<DrainControlDetail>();
		originalDetails.addAll(control.getDetails());
		List<DrainControlDetail> detailToDelete = new ArrayList<DrainControlDetail>();
		detailToDelete.addAll(control.getDetails());
		control.populateDrainControlFromInput(input, org);

		this.update(control);

		List<DrainControlDetail> detailToUpdate = new ArrayList<DrainControlDetail>();
		List<DrainControlDetail> detailToCreate = new ArrayList<DrainControlDetail>();
		for (DrainControlDetailJson dj : input.getDetails()) {
			Boolean found = false;
			for (DrainControlDetail d : originalDetails) {
				if ((d.getDrain() != null) && (dj.getDrainId() != null) && (d.getDrain().getId() == dj.getDrainId())) {
					detailToDelete.remove(d);
					d.populateDrainControlDetailFromInput(dj, this.drainService.getDrain(dj.getDrainId(), isAdmin, username), null);
					detailToUpdate.add(d);
					found = true;
				}
				if ((d.getFormula() != null) && (dj.getFormulaId() != null) && (d.getFormula().getId() == dj.getFormulaId())) {
					detailToDelete.remove(d);
					d.populateDrainControlDetailFromInput(dj, null, this.formulaService.getFormula(dj.getFormulaId(), isAdmin, username));
					detailToUpdate.add(d);
					found = true;
				}
			}
			if (!found) {
				if ((control.getType().equals(DrainControlType.MISSING) || control.getType().equals(DrainControlType.MEASUREDIFF) && (dj.getDrainId() != null)) || (control.getType().equals(DrainControlType.THRESHOLD) && ((dj.getDrainId() != null) || (dj.getFormulaId() != null)))) {
					DrainControlDetail d = new DrainControlDetail();
					d.populateDrainControlDetailFromInput(dj, (dj.getDrainId() != null) ? this.drainService.getDrain(dj.getDrainId(), isAdmin, username) : null, (dj.getFormulaId() != null) ? this.formulaService.getFormula(dj.getFormulaId(), isAdmin, username) : null);
					d.setDrainControl(control);
					d.setError(false);
					detailToCreate.add(d);
				}
			}
		}

		for (DrainControlDetail d : detailToDelete)
			this.drainControlDetailDao.delete(d);

		for (DrainControlDetail d : detailToUpdate)
			this.drainControlDetailDao.update(d);

		for (DrainControlDetail d : detailToCreate)
			this.drainControlDetailDao.persist(d);

		List<DrainControlDetail> details = new ArrayList<DrainControlDetail>();
		details.addAll(detailToUpdate);
		details.addAll(detailToCreate);
		control.setDetails(details);

		this.cronTaskRegistrar.addCronTask(task, control.getCronSecond() + " " + control.getCronMinute() + " " + control.getCronHour() + " " + control.getCronDayMonth() + " " + control.getCronMonth() + " " + control.getCronDayWeek());

		return control;
	}

	@Override
	@Transactional
	public void deleteDrainControlById(Integer id, Boolean isAdmin, String username) {

		log.info("Deleting drain control with id {}", id);

		this.drainControlDao.delete(this.getDrainControl(id, isAdmin, username));
	}

	private boolean checkNameForDrainControlForOrg(String name, Integer id, Org org, Boolean isAdmin, String username) {

		for (DrainControl c : this.getDrainControls(isAdmin, username))
			if (c.getOrg() == org)
				if (c.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
					if ((id == null) || (c.getId() != id))
						return false;

		return true;
	}
}
