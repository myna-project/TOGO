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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import it.mynaproject.togo.api.model.Constants;
import it.mynaproject.togo.api.model.FormulaElementJson;
import it.mynaproject.togo.api.model.IndexJson;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson.Value;
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
	public Index getIndex(Integer id, Date start, Date end, TimeAggregation timeAggregation, Boolean isAdmin, String username) {

		Index index = this.indexDao.getIndex(id);
		if ((index == null) || (index.getOrg() == null) || (!isAdmin && !this.orgService.orgIsVisibleByUser(index.getOrg(), username)))
			throw new NotFoundException(404, "Index " + id + " not found");

		if ((start != null) && (end != null) && (timeAggregation != null))
			index.setResult(this.getIndexResults(index, start, end, timeAggregation, isAdmin, username));

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

		Index f = this.getIndex(id, null, null, null, isAdmin, username);

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

		this.indexDao.delete(this.getIndex(id, null, null, null, isAdmin, username));
	}

	private List<IndexComponent> populateIndexComponentFromInput(Index index, IndexJson input, Boolean isAdmin, String username) {

		List<IndexComponent> comps = new ArrayList<IndexComponent>();

		Integer i = 0;
		for (FormulaElementJson fe : input.getFormulaElements()) {
			i++;

			IndexComponent newComp = new IndexComponent();
			newComp.setFormula(formulaService.getFormula(fe.getFormulaId(), isAdmin, username));
			newComp.setNSkip(fe.getNSkip());
			newComp.setSkipPeriod(fe.getSkipPeriod());
			newComp.setOperator((i == input.getOperators().size()) ? Operation.SEMICOLON : input.getOperators().get(i - 1));
			newComp.setIndex(index);

			comps.add(newComp);
		}

		return comps;
	}

	@SuppressWarnings("unchecked")
	private List<Measure> getIndexResults(Index index, Date start_time, Date end_time, TimeAggregation aggregation, Boolean isAdmin, String username) {

		if ((start_time == null) || (end_time == null))
			throw new GenericException(12007, "No times setted for index" + index.getId() + ".");
		if (aggregation == null)
			aggregation = TimeAggregation.ALL;

		ArrayList<ArrayList<Measure>> comps = new ArrayList<ArrayList<Measure>>(index.getComponents().size());
		for (IndexComponent ic : index.getComponents()) {
			ArrayList<Measure> comp = new ArrayList<Measure>();
			ArrayList<Operation> drainOperations = new ArrayList<Operation>();
			ArrayList<MeasureAggregation> measureAggregations = new ArrayList<MeasureAggregation>();
			Formula f = ic.getFormula();
			String[] drainIds = new String[f.getComponents().size()];

			int i = 0;
			for (FormulaComponent fc : f.getComponents()) {
				drainOperations.add(fc.getOperator());
				measureAggregations.add(fc.getAggregation());
				drainIds[i] = String.valueOf("d_" + fc.getDrain().getId());
				i++;
			}
			if ((ic.getNSkip() != null) && (ic.getSkipPeriod() != null)) {
				Calendar startCal = Calendar.getInstance();
				startCal.setTime(start_time);
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(end_time);

				switch (ic.getSkipPeriod()) {
				case y:
					startCal.add(Calendar.YEAR, ic.getNSkip() * -1);
					endCal.add(Calendar.YEAR, ic.getNSkip() * -1);
					break;
				case M:
					startCal.add(Calendar.MONTH, ic.getNSkip() * -1);
					endCal.add(Calendar.MONTH, ic.getNSkip() * -1);
					break;
				case d:
					startCal.add(Calendar.DAY_OF_MONTH, ic.getNSkip() * -1);
					endCal.add(Calendar.DAY_OF_MONTH, ic.getNSkip() * -1);
					break;
				case h:
					startCal.add(Calendar.HOUR, ic.getNSkip() * -1);
					endCal.add(Calendar.HOUR, ic.getNSkip() * -1);
					break;
				case m:
					startCal.add(Calendar.MINUTE, ic.getNSkip() * -1);
					endCal.add(Calendar.MINUTE, ic.getNSkip() * -1);
					break;
				default:
					break;
				}
				
				start_time = startCal.getTime();
				end_time = endCal.getTime();
			}

			List<PairDrainMeasuresJson> measuresJson = this.measureService.getMeasures(drainIds, drainOperations, measureAggregations, null, null, start_time, end_time, aggregation, isAdmin, username);
			if (measuresJson.size() > 1)
				throw new GenericException(12007, "Not unique result for a formula element. Please control sintax and time constraints of formula " + ic.getFormula().getId() + " (" + ic.getFormula().getName() + ")");

			for (PairDrainMeasuresJson mj : measuresJson) {
				for (Value value : mj.getMeasures()) {
					try {
						Calendar cal = Calendar.getInstance();
						cal.setTime(new SimpleDateFormat(Constants.DATIME_FORMAT).parse(value.getTime()));
						if ((ic.getNSkip() != null) && (ic.getSkipPeriod() != null)) {
							switch (ic.getSkipPeriod()) {
							case y:
								cal.add(Calendar.YEAR, ic.getNSkip() * 1);
								break;
							case M:
								cal.add(Calendar.MONTH, ic.getNSkip() * 1);
								break;
							case d:
								cal.add(Calendar.DAY_OF_MONTH, ic.getNSkip() * 1);
								break;
							case h:
								cal.add(Calendar.HOUR, ic.getNSkip() * 1);
								break;
							case m:
								cal.add(Calendar.MINUTE, ic.getNSkip() * 1);
								break;
							default:
								break;
							}

						}
						MeasureDouble m = new MeasureDouble();
						m.setTime(cal.getTime());
						m.setValue((Double) value.getValue());
						comp.add(m);
					} catch (ParseException e) {
						throw new GenericException(500, "Cannot transform time " + value.getTime());
					}
				}
			}
			comps.add(comp);
		}

		if (comps.size() > 1)
			for (int i = 0; i < (comps.size() - 1); i++)
				this.executeOperation(index.getComponents().get(i).getOperator(), comps, i);
		
		if (index.getCoefficient() != null)
			for (Measure m : comps.get(comps.size() - 1))
				m.setValue(((Double) m.getValue()) * index.getCoefficient().doubleValue());

		return comps.get(comps.size() - 1);
	}

	@SuppressWarnings("unchecked")
	private void executeOperation(Operation op, ArrayList<ArrayList<Measure>> comps, int i) {

		List<Measure> list = new ArrayList<Measure>();
		for (Measure m2 : comps.get(i + 1)) {
			for (int j = 0; j < comps.get(i).size();) {
				Measure m1 = comps.get(i).get(j);
				if (m2.getTime().equals(m1.getTime())) {
					Double v1 = (m1.getValue() != null) ? Double.valueOf(m1.getValue().toString()) : 0;
					Double v2 = (m2.getValue() != null) ? Double.valueOf(m2.getValue().toString()) : 0;
					switch (op) {
					case TIMES:
						m2.setValue(v1 * v2);
						break;
					case DIVISION:
						m2.setValue((v2.equals(0.00)) ? 0.00 : v1 / v2);
						break;
					case PERCENTAGE:
						m2.setValue((v2.equals(0.00)) ? 0.00 : v1 / v2 * 100);
						break;
					default:
						throw new GenericException(12002, "Unrecognized operator in formula. Please use: *, / or %.");
					}
					comps.get(i).remove(j);
					list.add(m2);
					break;
				} else if (m2.getTime().compareTo(m1.getTime()) < 0) {
					break;
				} else {
					comps.get(i).remove(j);
				}
			}
		}
		comps.get(i + 1).clear();
		comps.get(i + 1).addAll(list);
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
