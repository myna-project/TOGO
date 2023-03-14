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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.IndexDao;
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.domain.Index;
import it.mynaproject.togo.api.domain.IndexComponent;
import it.mynaproject.togo.api.domain.IndexGroup;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Operation;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.domain.impl.MeasureDouble;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.GenericException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.FormulaElementJson;
import it.mynaproject.togo.api.model.IndexJson;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson;
import it.mynaproject.togo.api.service.FormulaService;
import it.mynaproject.togo.api.service.IndexGroupService;
import it.mynaproject.togo.api.service.IndexService;
import it.mynaproject.togo.api.service.MeasureService;
import it.mynaproject.togo.api.service.OrgService;

@Service
public class IndexServiceImpl implements IndexService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IndexDao indexDao;

	@Autowired
	private OrgService orgService;

	@Autowired
	private IndexGroupService indexGroupService;

	@Autowired
	private MeasureService measureService;

	@Autowired
	private FormulaService formulaService;

	@Override
	@Transactional
	public Index getIndex(Integer id, Boolean calculate, Date start, Date end, Boolean isAdmin, String username) {

		Index index = this.indexDao.getIndex(id);
		if ((index == null) || (index.getOrg() == null) || (!isAdmin && !this.orgService.orgIsVisibleByUser(index.getOrg(), username)))
			throw new NotFoundException(404, "Index " + id + " not found");

		if (calculate) {
			List<Measure> results = new ArrayList<Measure>();

			MeasureDouble m = new MeasureDouble();
			m.setTime(start);
			m.setValue(this.getIndexResult(index, start, end, isAdmin, username));
			results.add(m);

			index.setResult(results);
		}

		return index;
	}

	@Override
	@Transactional
	public List<Index> getIndices(Boolean isAdmin, String username) {

		List<Index> indexList = new ArrayList<Index>();
		for (Index index : this.indexDao.getIndices())
			if (isAdmin || ((index.getOrg() != null) && this.orgService.orgIsVisibleByUser(index.getOrg(), username)))
				indexList.add(index);

		return indexList;
	}

	@Override
	@Transactional
	public void persist(Index index) {
		this.indexDao.persist(index);
	}

	@Override
	@Transactional
	public Index createIndexFromInput(IndexJson input, Boolean isAdmin, String username) {

		log.info("Creating new index");

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);
		IndexGroup group = (input.getGroup() != null) ? this.indexGroupService.getIndexGroup(input.getGroup().getId(), isAdmin, username) : null;

		if (!this.checkNameForIndexForOrg(input.getName(), null, org, isAdmin, username))
			throw new ConflictException(12003, "Index name " + input.getName() + " not available for this org");

		Index index = new Index();
		index.populateIndexFromInput(input, org, group);
		index.setComponents(this.populateIndexComponentFromInput(index, input, isAdmin, username));

		this.persist(index);

		return index;
	}

	@Override
	@Transactional
	public void update(Index index) {
		this.indexDao.update(index);
	}

	@Override
	@Transactional
	public Index updateIndexFromInput(Integer id, IndexJson input, Boolean isAdmin, String username) {

		log.info("Updating index with id {}", id);

		Org org = this.orgService.getOrg(input.getOrgId(), isAdmin, username);
		IndexGroup group = (input.getGroup() != null) ? this.indexGroupService.getIndexGroup(input.getGroup().getId(), isAdmin, username) : null;

		Index f = this.getIndex(id, false, null, null, isAdmin, username);

		if (!this.checkNameForIndexForOrg(input.getName(), id, org, isAdmin, username))
			throw new ConflictException(12003, "Index name " + input.getName() + " not available for this org");

		f.getComponents().clear();

		f.populateIndexFromInput(input, org, group);
		f.getComponents().addAll(this.populateIndexComponentFromInput(f, input, isAdmin, username));

		this.update(f);

		return f;
	}

	@Override
	@Transactional
	public void deleteIndexById(Integer id, Boolean isAdmin, String username) {

		log.info("Deleting index with id {}", id);

		this.indexDao.delete(this.getIndex(id, false, null, null, isAdmin, username));
	}

	@Override
	@Transactional
	public Index calculateLastIndex(Integer id, TimeAggregation timeAggregation, Integer nTimes, Boolean isAdmin, String username) {

		if (!timeAggregation.equals(TimeAggregation.YEAR) && !timeAggregation.equals(TimeAggregation.MONTH) && !timeAggregation.equals(TimeAggregation.DAY) && !timeAggregation.equals(TimeAggregation.HOUR) && !timeAggregation.equals(TimeAggregation.QHOUR) && !timeAggregation.equals(TimeAggregation.MINUTE))
			throw new GenericException(12008, "Time aggregation not available for index calculation, choose between YEAR, MONTH, DAY, QHOUR, HOUR or MINUTE");

		Index index = this.getIndex(id, false, null, null, isAdmin, username);

		Calendar end_time = Calendar.getInstance();
		Calendar start_time = Calendar.getInstance();
		start_time.set(Calendar.SECOND, 0);
		start_time.set(Calendar.MILLISECOND, 0);
		switch (timeAggregation) {
		case YEAR:
			start_time.set(Calendar.MINUTE, 0);
			start_time.set(Calendar.HOUR_OF_DAY, 0);
			start_time.set(Calendar.DAY_OF_MONTH, 1);
			start_time.set(Calendar.MONTH, 0);
			break;
		case MONTH:
			start_time.set(Calendar.MINUTE, 0);
			start_time.set(Calendar.HOUR_OF_DAY, 0);
			start_time.set(Calendar.DAY_OF_MONTH, 1);
			break;
		case DAY:
			start_time.set(Calendar.MINUTE, 0);
			start_time.set(Calendar.HOUR_OF_DAY, 0);
			break;
		case HOUR:
			start_time.set(Calendar.MINUTE, 0);
			break;
		case QHOUR:
			start_time.set(Calendar.MINUTE, (start_time.get(Calendar.MINUTE) % 15) * 15);
			break;
		default:
			break;
		}

		List<Measure> results = new ArrayList<Measure>();
		for (int i = nTimes; i > 0; i--) {
			MeasureDouble m = new MeasureDouble();
			m.setTime(start_time.getTime());
			m.setValue(this.getIndexResult(index, start_time.getTime(), end_time.getTime(), isAdmin, username));
			results.add(m);

			end_time = (Calendar) start_time.clone();
			end_time.add(Calendar.SECOND, -1);
			switch (timeAggregation) {
			case YEAR:
				start_time.add(Calendar.YEAR, -1);
				break;
			case MONTH:
				start_time.add(Calendar.MONTH, -1);
				break;
			case DAY:
				start_time.add(Calendar.DAY_OF_MONTH, -1);
				break;
			case HOUR:
				start_time.add(Calendar.HOUR, -1);
				break;
			case QHOUR:
				start_time.add(Calendar.MINUTE, -15);
				break;
			case MINUTE:
				start_time.add(Calendar.MINUTE, -1);
				break;
			default:
				break;
			}			
		}

		index.setResult(results);

		return index;
	}

	private List<IndexComponent> populateIndexComponentFromInput(Index index, IndexJson input, Boolean isAdmin, String username) {

		List<IndexComponent> comps = new ArrayList<IndexComponent>();
		// Single component case
		if (input.getFormulaElements().size() == 1) {
			IndexComponent newComp = this.setComponentTime(input.getFormulaElements().get(0));
			newComp.setFormula(formulaService.getFormula(input.getFormulaElements().get(0).getFormulaId(), isAdmin, username));
			newComp.setOperator(Operation.SEMICOLON);
			newComp.setIndex(index);

			comps.add(newComp);
		} else {
			Integer i = 0;

			for (FormulaElementJson fe : input.getFormulaElements()) {
				IndexComponent newComp = this.setComponentTime(fe);
				newComp.setFormula(formulaService.getFormula(input.getFormulaElements().get(i).getFormulaId(), isAdmin, username));
				newComp.setOperator(input.getOperators().get(i));
				newComp.setIndex(index);

				comps.add(newComp);

				i++;

				if (i == (input.getOperators().size() - 1)) {
					// last component
					IndexComponent lastComp = this.setComponentTime(input.getFormulaElements().get(i));
					lastComp.setFormula(formulaService.getFormula(input.getFormulaElements().get(i).getFormulaId(), isAdmin, username));
					lastComp.setOperator(Operation.SEMICOLON);
					lastComp.setIndex(index);

					comps.add(lastComp);

					break;
				}
			}
		}

		return comps;
	}

	private IndexComponent setComponentTime(FormulaElementJson fe) {

		IndexComponent c = new IndexComponent();

		if ((fe.getStartDate() != null) && (fe.getEndDate() != null)) {
			c.setStartTime(fe.getStartDate());
			c.setEndTime(fe.getEndDate());
		} else if ((fe.getRelativeTime() != null) && (fe.getRelativePeriod() != null)) {
			c.setRelativeTime(fe.getRelativeTime());
			c.setRelativePeriod(fe.getRelativePeriod());
		} else {
			throw new GenericException(12004, "Missing time constraints for the requested index");
		}

		return c;
	}

	private Double getIndexResult(Index index, Date start, Date end, Boolean isAdmin, String username) {

		List<IndexComponent> ics = index.getComponents();

		Date start_time = start;
		Date end_time = end;
		if (start == null || end == null) {
			List<Date> timesList = this.getIndexTimes(ics.get(0));
			start_time = timesList.get(0);
			end_time = timesList.get(1);
		}

		if ((start_time == null) || (end_time == null))
			throw new GenericException(12007, "No times setted for index" + index.getId() + ".");

		// Create a partial result with the first element
		Double result = this.getIndexComponentResult(ics.get(0), start_time, end_time, isAdmin, username);

		if (ics.size() > 1) {
			for (int i = 0; i < ics.size()-1; i++) {
				if (start == null || end == null) {
					List<Date> timesList = this.getIndexTimes(ics.get(i + 1));
					start_time = timesList.get(0);
					end_time = timesList.get(1);
				}
				Double res = this.getIndexComponentResult(ics.get(i + 1), start_time, end_time, isAdmin, username);
				if (res != null) {
					Operation operator = ics.get(i).getOperator();
					switch (operator) {
					case TIMES:
						result *= res;
						break;
					case DIVISION:
						if (!res.equals(0.00))
							result = result/res;
						else
							result = 0.00;
						break;
					case PERCENTAGE:
						if (!res.equals(0.00))
							result = result/res * 100;
						else
							result = 0.00;
						break;
					default:
						throw new GenericException(12002, "Unrecognized operator in formula. Please use: *, / or %.");
					}
				}
			}
		}

		return result;
	}

	private Double getIndexComponentResult(IndexComponent ic, Date start, Date end, Boolean isAdmin, String username) {

		ArrayList<Operation> drainOperations = new ArrayList<Operation>();
		ArrayList<MeasureAggregation> measureAggregations = new ArrayList<MeasureAggregation>();
		Formula f = ic.getFormula();
		String[] drainIds = new String[f.getComponents().size()];
		TimeAggregation aggregation = TimeAggregation.ALL;

		int i = 0;
		for (FormulaComponent fc : f.getComponents()) {
			drainOperations.add(fc.getOperator());
			measureAggregations.add(fc.getAggregation());
			drainIds[i] = String.valueOf(fc.getDrain().getId());
			i++;
		}

		List<PairDrainMeasuresJson> measureJson = this.measureService.getMeasures(drainIds, drainOperations, measureAggregations, start, end, aggregation, isAdmin, username);
		if ((measureJson.get(0).getMeasures().size() > 1) || (measureJson.size() > 1)) {
			throw new GenericException(12007, "Not unique result for a formula element. Please control sintax and time constraints of formula " + ic.getFormula().getId() + " (" + ic.getFormula().getName() + ")");
		} else if (measureJson.isEmpty() || measureJson.get(0).getMeasures().isEmpty()) {
			return (Double) 0.00;
		} else {
			return (Double) measureJson.get(0).getMeasures().get(0).getValue();
		}
	}

	private List<Date> getIndexTimes(IndexComponent ic) {

		Date start_time = null;
		Date end_time = null;

		if ((ic.getStartTime() != null) && (ic.getEndTime() != null)) {
			start_time = ic.getStartTime();
			end_time = ic.getEndTime();
		} else if ((ic.getRelativePeriod() != null) && (ic.getRelativeTime() != null)) {
			end_time = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(end_time);

			switch (ic.getRelativePeriod()) {
			case m:
				cal.add(Calendar.MINUTE, -ic.getRelativeTime());
				start_time = cal.getTime();
				break;
			case h:
				cal.add(Calendar.HOUR_OF_DAY, -ic.getRelativeTime());
				start_time = cal.getTime();
				break;
			case d:
				cal.add(Calendar.DATE, -ic.getRelativeTime());
				start_time = cal.getTime();
				break;
			case M:
				cal.add(Calendar.MONTH, -ic.getRelativeTime());
				start_time = cal.getTime();
				break;
			case y:
				cal.add(Calendar.YEAR, -ic.getRelativeTime());
				start_time = cal.getTime();
				break;
			default:
				throw new GenericException(12005, "Unexpected relative time format in Index.");
			}
		} else {
			throw new GenericException(12006, "Unexpected time format in Index.");
		}

		List<Date> timesList = new ArrayList<Date>();
		timesList.add(start_time);
		timesList.add(end_time);

		return timesList;
	}

	private boolean checkNameForIndexForOrg(String name, Integer indexId, Org org, Boolean isAdmin, String username) {

		for (Index f : this.getIndices(isAdmin, username))
			if (f.getOrg() == org)
				if (f.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
					if ((indexId == null) || (f.getId() != indexId))
						return false;

		return true;
	}
}
