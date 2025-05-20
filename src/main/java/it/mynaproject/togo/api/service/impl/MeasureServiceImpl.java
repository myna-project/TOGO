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
import java.util.Collections;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.MeasureDao;
import it.mynaproject.togo.api.dao.impl.InvoiceItemkWhDaoImpl;
import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.domain.InvoiceItemkWh;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.MeasureType;
import it.mynaproject.togo.api.domain.Operation;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.domain.impl.MeasureBitfield;
import it.mynaproject.togo.api.domain.impl.MeasureDouble;
import it.mynaproject.togo.api.domain.impl.MeasureString;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.GenericException;
import it.mynaproject.togo.api.exception.NotFoundException;
import it.mynaproject.togo.api.model.Constants;
import it.mynaproject.togo.api.model.CsvMeasureJson;
import it.mynaproject.togo.api.model.CsvMeasuresJson;
import it.mynaproject.togo.api.model.JsonUtil;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson.Value;
import it.mynaproject.togo.api.service.ClientService;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.FeedService;
import it.mynaproject.togo.api.service.MeasureService;
import it.mynaproject.togo.api.service.OrgService;
import it.mynaproject.togo.api.service.FormulaService;
import it.mynaproject.togo.api.util.DateUtil;
import it.mynaproject.togo.api.util.Pair;

@Service
public class MeasureServiceImpl implements MeasureService {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MeasureDao measureDao;

	@Autowired
	private DrainService drainService;

	@Autowired
	private FormulaService formulaService;

	@Autowired
	private FeedService feedService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private InvoiceItemkWhDaoImpl invoiceItemkWhDaoImpl;

	@Override
	@Transactional
	public Pair<Drain, List<Measure>> getAllMeasuresFromDrainId(Integer drainId, Date start, Date end, Boolean isAdmin, String username) {

		Drain d = this.drainService.getDrain(drainId, isAdmin, username);

		return new Pair<Drain, List<Measure>>(d, measureDao.getMeasures(d, this.getMeasureTypeFromDrain(d), start, end));
	}

	@Override
	@Transactional
	public void persist(Measure measure) {
		this.measureDao.persist(measure);
	}

	@Override
	@Transactional
	public void persistMultiple(List<Measure> measures) {
		this.measureDao.persistMultiple(measures);
	}

	@Override
	@Transactional
	public void createMeasuresFromJson(CsvMeasuresJson csvMeasuresJson, Boolean isAdmin, String username) {

		log.debug("Creating new measures");

		Map<String, Client> checkedClients = new HashMap<String, Client>();
		Map<Integer, Map<String, Drain>> checkedDrains = new HashMap<Integer, Map<String, Drain>>();

		this.persistMultiple(generateMeasuresFromJson(csvMeasuresJson, isAdmin, checkedClients, checkedDrains, username));
	}

	@Override
	@Transactional
	public void createMeasuresFromJsonList(List<CsvMeasuresJson> csvMeasuresJsonList, Boolean isAdmin, String username) {

		log.debug("Creating new measures from list");

		Map<String, Client> checkedClients = new HashMap<String, Client>();
		Map<Integer, Map<String, Drain>> checkedDrains = new HashMap<Integer, Map<String, Drain>>();

		List<Measure> measures = new ArrayList<Measure>();
		for (CsvMeasuresJson csvMeasuresJson : csvMeasuresJsonList) {
			measures.addAll(generateMeasuresFromJson(csvMeasuresJson, isAdmin, checkedClients, checkedDrains, username));
		}

		this.persistMultiple(measures);
	}

	@Override
	@Transactional
	public void update(Measure measure) {
		this.measureDao.update(measure);
	}

	@Override
	@Transactional
	public void updateMultiple(List<Measure> measures) {
		this.measureDao.updateMultiple(measures);
	}

	@Override
	@Transactional
	public void updateMeasuresFromJson(CsvMeasuresJson csvMeasuresJson, Boolean isAdmin, String username) {

		log.debug("Updating measures");

		Map<String, Client> checkedClients = new HashMap<String, Client>();
		Map<Integer, Map<String, Drain>> checkedDrains = new HashMap<Integer, Map<String, Drain>>();

		Map<String, List<Measure>> measures = retrieveMeasuresFromJson(csvMeasuresJson, isAdmin, checkedClients, checkedDrains, username);

		this.updateMultiple(measures.get("update"));
		this.persistMultiple(measures.get("create"));
	}

	@Override
	@Transactional
	public void updateMeasuresFromJsonList(List<CsvMeasuresJson> csvMeasuresJsonList, Boolean isAdmin, String username) {

		log.debug("Updating measures from list");

		Map<String, Client> checkedClients = new HashMap<String, Client>();
		Map<Integer, Map<String, Drain>> checkedDrains = new HashMap<Integer, Map<String, Drain>>();

		List<Measure> measureToUpdate = new ArrayList<Measure>();
		List<Measure> measureToCreate = new ArrayList<Measure>();
		for (CsvMeasuresJson csvMeasuresJson : csvMeasuresJsonList) {
			Map<String, List<Measure>> measures = retrieveMeasuresFromJson(csvMeasuresJson, isAdmin, checkedClients, checkedDrains, username);
			measureToUpdate.addAll(measures.get("update"));
			measureToCreate.addAll(measures.get("create"));
		}

		this.updateMultiple(measureToUpdate);
		this.persistMultiple(measureToCreate);
	}

	@Override
	@Transactional
	public void delete(Measure measure) {
		this.measureDao.delete(measure);
	}

	@Override
	@Transactional
	public void deleteMeasures(Integer clientId, String deviceId, Date start, Date end, Boolean isAdmin, String username) {

		Client c = this.retrieveClientFromDeviceId(clientId, deviceId, isAdmin, username);

		List<Measure> measures = new ArrayList<Measure>();
		for (Feed f : c.getFeeds()) {
			for (Drain d : f.getDrains()) {
				measures.addAll(this.measureDao.getMeasures(d, this.getMeasureTypeFromDrain(d), start, end));
			}
		}

		this.measureDao.deleteMultiple(measures);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PairDrainMeasuresJson> getMeasures(String[] drainIds, ArrayList<Operation> drainOperations, ArrayList<MeasureAggregation> measureAggregations, ArrayList<String> positiveNegativeValues, ArrayList<Boolean> excludeOutliers, Date start, Date end, TimeAggregation timeAggregation, boolean forceDiff, boolean isAdmin, String username) {

		Date end_date = DateUtil.extractEndDate(end);
		Date start_date = DateUtil.extractStartDate(start, end_date, Constants.MEASURE_HISTORY_TIME_WINDOW);

		Map<String, List<Measure>> drainMeasures = new HashMap<String, List<Measure>>();
		List<String> allDrainIds = new ArrayList<String>();
		List<Operation> allDrainOperations = new ArrayList<Operation>();
		List<MeasureAggregation> allMeasureAggregations = new ArrayList<MeasureAggregation>();
		List<String> allPositiveNegativeValues = new ArrayList<String>();
		List<Boolean> allExcludeOutliers = new ArrayList<Boolean>();

		if ((positiveNegativeValues == null) || (positiveNegativeValues.size() == 0))
			positiveNegativeValues = new ArrayList<String>();

		if ((excludeOutliers == null) || (excludeOutliers.size() == 0))
			excludeOutliers = new ArrayList<Boolean>();

		Integer k = 0;
		for (String drainId : drainIds) {
			if (drainId.substring(0, 1).equals("f")) {
				Formula f = this.formulaService.getFormula(Integer.parseInt(drainId.substring(2, drainId.length())), isAdmin, username);
				List<FormulaComponent> formulaComponents = f.getComponents();
				for (FormulaComponent fc : formulaComponents) {
					allDrainIds.add(String.valueOf(fc.getDrain().getId()));
					allDrainOperations.add(fc.getOperator());
					allMeasureAggregations.add(fc.getAggregation());
					allPositiveNegativeValues.add((fc.getPositiveNegativeValue() == null) ? "" : fc.getPositiveNegativeValue());
					allExcludeOutliers.add(fc.getExcludeOutliers());
				}
			} else if (drainId.substring(0, 1).equals("d")) {
				allDrainIds.add(String.valueOf(Integer.parseInt(drainId.substring(2, drainId.length()))));
				allDrainOperations.add(drainOperations.get(k));
				allMeasureAggregations.add(measureAggregations.get(k));
				allPositiveNegativeValues.add((positiveNegativeValues.size() == 0) ? "" : ((positiveNegativeValues.get(k) == null) ? "" : positiveNegativeValues.get(k)));
				allExcludeOutliers.add((excludeOutliers.size() == 0) ? false : excludeOutliers.get(k) == null ? false : excludeOutliers.get(k));
				k++;
			}
		}

		Map<MeasureType, Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String,List<String>>>>>>> queries = this.groupQueries(drainMeasures, allDrainIds.toArray(new String[0]), allMeasureAggregations, allPositiveNegativeValues, allExcludeOutliers, false, isAdmin, username);

		this.executeGroupedQueries(drainMeasures, queries, start_date, end_date, timeAggregation, forceDiff);

		List<PairDrainMeasuresJson> jsonArray = new ArrayList<PairDrainMeasuresJson>();
		List<PairDrainMeasuresJson> jsonFinalArray = new ArrayList<PairDrainMeasuresJson>();
		Integer i = 0;
		for (String drainId : drainIds) {
			if (drainId.substring(0, 1).equals("f")) {
				List<PairDrainMeasuresJson> formulaJsonArray = new ArrayList<PairDrainMeasuresJson>();
				ArrayList<Operation> formulaDrainOperations = new ArrayList<Operation>();
				List<Operation> semicolonFormulaOperation = new ArrayList<>();

				Formula formula = this.formulaService.getFormula(Integer.parseInt(drainId.substring(2, drainId.length())), isAdmin, username);
				for (int j = 0; j < formula.getComponents().size(); j++) {
					Drain d = formula.getComponents().get(j).getDrain();
					Operation o = formula.getComponents().get(j).getOperator();
					formulaDrainOperations.add(o);
					if (o.equals(Operation.SEMICOLON))
						semicolonFormulaOperation.add(o);
					Pair<Drain, List<Measure>> measures = new Pair(d, drainMeasures.get(String.valueOf(d.getId()) + "_" + allMeasureAggregations.get(i + j) + (allPositiveNegativeValues.get(i + j).isEmpty() ? "" : ("_" + allPositiveNegativeValues.get(i + j))) + (allExcludeOutliers.get(i) ? d.getMinValue() + "_" + d.getMaxValue() : "")));
					formulaJsonArray.add(JsonUtil.pairDrainMeasureToPairDrainMeasureJson(measures, timeAggregation, start_date, end_date));
				}
				calculateOperations(formulaDrainOperations, isAdmin, username, formulaJsonArray, jsonArray);
				if (semicolonFormulaOperation.size() > 1)
					drainOperations.addAll(drainOperations.size(), semicolonFormulaOperation);
				i = i + formula.getComponents().size() - 1;
			}
			if (drainId.substring(0, 1).equals("d")) {
				Drain d = this.drainService.getDrain(Integer.parseInt(drainId.substring(2, drainId.length())), isAdmin, username);

				Pair<Drain, List<Measure>> measures = new Pair(d, drainMeasures.get(String.valueOf(d.getId()) + "_" + allMeasureAggregations.get(i) + (allPositiveNegativeValues.get(i).isEmpty() ? "" : ("_" + allPositiveNegativeValues.get(i))) + (allExcludeOutliers.get(i) ? d.getMinValue() + "_" + d.getMaxValue() : "")));

				jsonArray.add(JsonUtil.pairDrainMeasureToPairDrainMeasureJson(measures, timeAggregation, start_date, end_date));
			}
			i++;
		}

		calculateOperations(drainOperations, isAdmin, username, jsonArray, jsonFinalArray);

		return jsonFinalArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PairDrainMeasuresJson> getCosts(Integer drainCostId, String[] drainIds, ArrayList<Operation> drainOperations, MeasureAggregation measureAggregation, ArrayList<String> positiveNegativeValues, ArrayList<Boolean> excludeOutliers, Date start, Date end, TimeAggregation timeAggregation, boolean isAdmin, String username) {

		List<String> allDrainIds = new ArrayList<String>();
		List<Operation> allDrainOperations = new ArrayList<Operation>();
		List<MeasureAggregation> allMeasureAggregations = new ArrayList<MeasureAggregation>();
		List<String> allPositiveNegativeValues = new ArrayList<String>();
		List<Boolean> allExcludeOutliers = new ArrayList<Boolean>();

		Integer drainCount = 0;
		for (String drainId : drainIds) {
			if (drainId.substring(0, 1).equals("f")) {
				Formula f = this.formulaService.getFormula(Integer.parseInt(drainId.substring(2, drainId.length())), isAdmin, username);
				List<FormulaComponent> formulaComponents = f.getComponents();
				for (FormulaComponent fc : formulaComponents) {
					allDrainIds.add(String.valueOf(fc.getDrain().getId()));
					allDrainOperations.add(fc.getOperator());
					allMeasureAggregations.add(measureAggregation);
					allPositiveNegativeValues.add(fc.getPositiveNegativeValue() == null ? "" : fc.getPositiveNegativeValue());
					allExcludeOutliers.add(fc.getExcludeOutliers());
				}
			} else if (drainId.substring(0, 1).equals("d")) {
				allDrainIds.add(String.valueOf(Integer.parseInt(drainId.substring(2, drainId.length()))));
				allDrainOperations.add(drainOperations.get(drainCount));
				allMeasureAggregations.add(measureAggregation);
				allPositiveNegativeValues.add((positiveNegativeValues.size() == 0) ? "" : ((positiveNegativeValues.get(drainCount) == null) ? "" : positiveNegativeValues.get(drainCount)));
				allExcludeOutliers.add(excludeOutliers.get(drainCount));
				drainCount++;
			}
		}
		Date end_date = DateUtil.extractEndDate(end);
		Date start_date = DateUtil.extractStartDate(start, end_date, Constants.MEASURE_HISTORY_TIME_WINDOW);

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start_date);

		Calendar startCost = Calendar.getInstance();
		startCost.setTime(start_date);
		startCost.set(Calendar.MINUTE, startCal.getActualMinimum(Calendar.MINUTE));

		Calendar startMonthCal = Calendar.getInstance();
		startMonthCal.setTime(start_date);
		startMonthCal.set(Calendar.DATE, startCal.getActualMinimum(Calendar.DATE));
		startMonthCal.set(Calendar.HOUR_OF_DAY, startCal.getActualMinimum(Calendar.HOUR_OF_DAY));
		startMonthCal.set(Calendar.MINUTE, startCal.getActualMinimum(Calendar.MINUTE));
		startMonthCal.set(Calendar.SECOND, startCal.getActualMinimum(Calendar.SECOND));

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end_date);

		List<Pair<Date, Date>> slots = new ArrayList<Pair<Date, Date>>();
		if (!timeAggregation.equals(TimeAggregation.QHOUR))
			this.measureDao.createSlotsStats(timeAggregation, slots, start_date, end_date, startCal);
		List<DoubleSummaryStatistics> stats = new ArrayList<DoubleSummaryStatistics>();
		for (int k = 0; k < slots.size(); k++)
			stats.add(new DoubleSummaryStatistics());

		Drain dCost = this.drainService.getDrain(drainCostId, isAdmin, username);
		Map<String, List<Measure>> costs = new HashMap<String, List<Measure>>();
		List<String> costsDrainIds = new ArrayList<String>();
		costsDrainIds.add(String.valueOf(drainCostId));
		costs = this.measureDao.getMultipleMeasures(costs, costsDrainIds, "", Double.valueOf("1.00"), this.getMeasureTypeFromDrain(dCost), Boolean.FALSE, startCost.getTime(), end_date, TimeAggregation.HOUR, MeasureAggregation.AVG, "");
		HashMap<String, Double> costsMap = new HashMap<String, Double>();
		for (String costDrain : costs.keySet()) {
			for (Measure m : costs.get(costDrain)) {
				if (m.getTime().compareTo(start_date) >= 0)
					costsMap.put(DateUtil.formatDateString(m.getTime(), Constants.DATIME_FORMAT), Double.valueOf(m.getValue().toString()));
				if (timeAggregation.equals(TimeAggregation.QHOUR)) {
					Calendar qhour = Calendar.getInstance();
					qhour.setTime(m.getTime());
					for (Integer i = 0; i <= 2; i++) {
						qhour.add(Calendar.MINUTE, 15);
						if (qhour.getTime().compareTo(start_date) >= 0)
							costsMap.put(DateUtil.formatDateString(qhour.getTime(), Constants.DATIME_FORMAT), Double.valueOf(m.getValue().toString()));
					}
				}
			}
		}

		Map<String, List<Measure>> drainMeasures = new HashMap<String, List<Measure>>();
		Map<MeasureType, Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>>> queries = this.groupQueries(drainMeasures, allDrainIds.toArray(new String[0]), new ArrayList<MeasureAggregation>(Collections.nCopies(allDrainIds.toArray(new String[0]).length, MeasureAggregation.SUM)), allPositiveNegativeValues, allExcludeOutliers, true, isAdmin, username);

		this.executeGroupedQueries(drainMeasures, queries, startMonthCal.getTime(), end_date, timeAggregation.equals(TimeAggregation.QHOUR) ? TimeAggregation.QHOUR : TimeAggregation.HOUR, false);

		List<PairDrainMeasuresJson> jsonArray = new ArrayList<PairDrainMeasuresJson>();
		List<PairDrainMeasuresJson> jsonFinalArray = new ArrayList<PairDrainMeasuresJson>();

		Integer i = 0;
		for (String drainSId : drainIds) {
			if (drainSId.substring(0, 1).equals("f")) {
				List<PairDrainMeasuresJson> formulaJsonArray = new ArrayList<PairDrainMeasuresJson>();
				ArrayList<Operation> formulaDrainOperations = new ArrayList<Operation>();
				List<Operation> semicolonFormulaOperation = new ArrayList<>();

				Formula formula = this.formulaService.getFormula(Integer.parseInt(drainSId.substring(2, drainSId.length())), isAdmin, username);
				for (int j = 0; j < formula.getComponents().size(); j++) {
					Drain d = formula.getComponents().get(j).getDrain();
					Operation o = formula.getComponents().get(j).getOperator();
					formulaDrainOperations.add(o);
					if (o.equals(Operation.SEMICOLON))
						semicolonFormulaOperation.add(o);
					Pair<Drain, List<Measure>> measures = new Pair(d, drainMeasures.get(String.valueOf(d.getId()) + "_" + MeasureAggregation.SUM + (allPositiveNegativeValues.get(i).isEmpty() ? "" : ("_" + allPositiveNegativeValues.get(i))) + (allExcludeOutliers.get(i) ? d.getMinValue() + "_" + d.getMaxValue() : "")));
					formulaJsonArray.add(JsonUtil.pairDrainMeasureToPairDrainMeasureJson(measures, timeAggregation.equals(TimeAggregation.QHOUR) ? TimeAggregation.QHOUR : TimeAggregation.HOUR, start_date, end_date));
				}
				calculateOperations(formulaDrainOperations, isAdmin, username, formulaJsonArray, jsonArray);
				if (semicolonFormulaOperation.size() > 1)
					drainOperations.addAll(drainOperations.size() , semicolonFormulaOperation);
				i = i + formula.getComponents().size() - 1;
			}
			if (drainSId.substring(0, 1).equals("d")) {
				Drain d = this.drainService.getDrain(Integer.parseInt(drainSId.substring(2, drainSId.length())), isAdmin, username);

				Pair<Drain, List<Measure>> measures = new Pair(d, drainMeasures.get(String.valueOf(d.getId()) + "_" + MeasureAggregation.SUM + (allPositiveNegativeValues.get(i).isEmpty() ? "" : ("_" + allPositiveNegativeValues.get(i))) + (allExcludeOutliers.get(i) ? d.getMinValue() + "_" + d.getMaxValue() : "")));

				jsonArray.add(JsonUtil.pairDrainMeasureToPairDrainMeasureJson(measures, timeAggregation.equals(TimeAggregation.QHOUR) ? TimeAggregation.QHOUR : TimeAggregation.HOUR, start_date, end_date));
			}
			i++;
		}

		int count = 0;
		int fCount = 1;
		boolean unitMatch = true; // if in a formula there are two different units or more (e.g W and A) is set to false
		List<Operation> multiplied = new ArrayList<Operation>();
		List<PairDrainMeasuresJson> multipliedDrains = new ArrayList<PairDrainMeasuresJson>();
		Operation op;

		// Multiplication and division are executed in a dedicated cycle before other operations
		for (int j = 0; j < drainOperations.size() - 1; j++) {
			op = drainOperations.get(j);
			if (op.equals(Operation.TIMES) || op.equals(Operation.DIVISION)) {
				if (unitMatch && !jsonArray.get(count).getUnit().equals(jsonArray.get(count + 1).getUnit()))
					unitMatch = false;

				this.executeTimesDivisionOperations(op, jsonArray, count);
				multiplied.add(drainOperations.get(count));
				multipliedDrains.add(jsonArray.get(count));
			}
			count++;
		}

		drainOperations.removeAll(multiplied);
		jsonArray.removeAll(multipliedDrains);
		count = 0;
		for (int j = 0; j < drainOperations.size(); j++) {
			op = drainOperations.get(j);
			if (op.equals(Operation.SEMICOLON)) {
				PairDrainMeasuresJson newDrainList = jsonArray.get(count);

				newDrainList.setUnit("€");

				if (newDrainList.isFormula()) {
					newDrainList.setDrainName("Formula_" + fCount);
					fCount++;
				} else {
					Drain d = drainService.getDrain(jsonArray.get(count).getDrainId(), isAdmin, username);
					Feed f = feedService.getFeed(d.getFeed().getId(), isAdmin, username);
					for (Client c : f.getClients()) {
						if ((c.getEnergyClient() != null) && c.getEnergyClient()) {
							newDrainList.setDrainName(c.getName() + " - " +jsonArray.get(count).getDrainName());
							newDrainList.setDecimals(dCost.getDecimals());
							break;
						}
					}
				}
				newDrainList.setUnit("€");

				Integer month = null;
				Integer year = null;
				Double cumulMonth = (Double) 0.00;
				Double vat_perc_rate = (Double) 0.00;
				Double duty_excise_1 = (Double) 0.00;
				Double duty_excise_2 = (Double) 0.00;
				List<Value> valueToDelete = new ArrayList<Value>();
				for (Value value : newDrainList.getMeasures()) {
					try {
						Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(value.getTime());
						Calendar time = Calendar.getInstance();
						time.setTime(d);
						if (value.getValue() != null) {
							if ((year != null) && (month != null) && (time.get(Calendar.YEAR) == year) && (time.get(Calendar.MONTH) == month)) {
								cumulMonth += (Double) value.getValue();
							} else {
								year = time.get(Calendar.YEAR);
								month = time.get(Calendar.MONTH);
								cumulMonth = (Double) value.getValue();
								vat_perc_rate = (Double) 0.00;
								duty_excise_1 = (Double) 0.00;
								duty_excise_2 = (Double) 0.00;
								List<InvoiceItemkWh> itemskWh = invoiceItemkWhDaoImpl.getInvoiceItemskWh(drainCostId, null, year, month + 1);
								if (itemskWh.size() > 0) {
									if (itemskWh.get(0).getDutyExcise1() != null)
										duty_excise_1 = itemskWh.get(0).getDutyExcise1().doubleValue();
									if (itemskWh.get(0).getDutyExcise2() != null)
										duty_excise_2 = itemskWh.get(0).getDutyExcise2().doubleValue();
									if (itemskWh.get(0).getVatPercRate() != null)
										vat_perc_rate = itemskWh.get(0).getVatPercRate().doubleValue()/100;
								}
							}
						}
						Double cost = costsMap.get(value.getTime());
						if ((value.getValue() != null) && (cost != null)) {
							cost += ((cumulMonth < 200000) ? duty_excise_1 : duty_excise_2) * (1 + vat_perc_rate);
							if (timeAggregation.equals(TimeAggregation.QHOUR)) {
								value.setValue((Double) value.getValue() * cost);
							} else {
								for (Integer k = 0; k < slots.size(); k++) {
									if (time.getTime().equals(slots.get(k).getLeft()) || (time.getTime().after(slots.get(k).getLeft()) && time.getTime().before(slots.get(k).getRight()))) {
										stats.get(k).accept((Double) value.getValue() * cost);
										break;
									}
								}
							}
						} else {
							valueToDelete.add(value);
						}
					} catch (ParseException e) {
						throw new GenericException(500, "Cannot transform time " + value.getTime());
					}
				}
				newDrainList.getMeasures().removeAll(valueToDelete);

				if (!timeAggregation.equals(TimeAggregation.QHOUR)) {
					newDrainList.setMeasures(new ArrayList<Value>());
					for (Integer k = 0; k < slots.size(); k++) {
						if (stats.get(k).getCount() > 0) {
							switch (measureAggregation) {
							case SUM: {
								newDrainList.addValue(stats.get(k).getSum(), slots.get(k).getLeft());
								break;
							}
							case AVG: {
								newDrainList.addValue(stats.get(k).getAverage(), slots.get(k).getLeft());
								break;
							}
							case MIN: {
								newDrainList.addValue(stats.get(k).getMin(), slots.get(k).getLeft());
								break;
							}
							case MAX: {
								newDrainList.addValue(stats.get(k).getMax(), slots.get(k).getLeft());
								break;
							}
							default:
								break;
							}
						}
						stats.set(k, new DoubleSummaryStatistics());
					}
				}

				jsonFinalArray.add(newDrainList);
			} else {
				if (unitMatch && !jsonArray.get(count).getUnit().equals(jsonArray.get(count + 1).getUnit()))
					unitMatch = false;

				this.executeSumSubctrationOperation(op, jsonArray, count);
			}

			count++;
		}

		return jsonFinalArray;
	}

	@Override
	public Boolean checkMeasuresForDrain(Drain drain, Date start, Date end) {
		return (this.measureDao.getMeasures(drain, this.getMeasureTypeFromDrain(drain), start, end).size() > 0) ? true : false;
	}

	private List<Measure> generateMeasuresFromJson(CsvMeasuresJson csvMeasuresJson, Boolean isAdmin, Map<String, Client> checkedClients, Map<Integer, Map<String, Drain>> checkedDrains, String username) {

		Integer clientId = csvMeasuresJson.getClientId();
		String deviceId = csvMeasuresJson.getDeviceId();

		Client c = null;
		if (checkedClients.containsKey(deviceId)) {
			c = checkedClients.get(deviceId);
		} else {
			c = this.retrieveClientFromDeviceId(clientId, deviceId, isAdmin, username);

			checkedClients.put(deviceId, c);
		}

		List<Measure> measures = new ArrayList<Measure>();
		Map<String, Drain> checkedDrainsForClient = checkedDrains.getOrDefault(c.getId(), new HashMap<String, Drain>());
		for (CsvMeasureJson measure : csvMeasuresJson.getMeasures()) {
			Drain drain = null;
			if (checkedDrainsForClient.containsKey(measure.getMeasureId())) {
				drain = checkedDrainsForClient.get(measure.getMeasureId());
			} else {
				for(Feed f: c.getFeeds()) {
					for (Drain d : f.getDrains()) {
						if ((d.getMeasureId() != null) && d.getMeasureId().equals(measure.getMeasureId())) {
							if (drain == null) {
								drain = d;
								checkedDrainsForClient.put(measure.getMeasureId(), d);
								checkedDrains.put(c.getId(), checkedDrainsForClient);
							} else {
								throw new ConflictException(8005, "Measure " + measure.getMeasureId() + " is present in more than one drain for device setted in JSON");
							}
						}
					}
				}

				if (drain == null)
					throw new NotFoundException(404, "Drain not found with measureId = " + measure.getMeasureId() + " for client with deviceId " + deviceId);
			}

			MeasureType mt = this.getMeasureTypeFromDrain(drain);
			if (mt.equals(MeasureType.DOUBLE)) {
				MeasureDouble m = new MeasureDouble();
				m.setDrain(drain);
				m.setTime(csvMeasuresJson.getAt());
				if (drain.getMeasureType().equals("c")) {
					if (measure.getValue().toString().equals("true")) {
						m.setValue(Double.parseDouble("1.0"));
					} else {
						m.setValue(Double.parseDouble("0.0"));
					}
				} else {
					try {
						m.setValue(Double.parseDouble(measure.getValue().toString()));
					} catch (NumberFormatException e) {
						log.info("Value {} cannot parse to double", measure.getValue().toString());
						continue;
					}
				}
				measures.add(m);
			} else if (mt.equals(MeasureType.BITFIELD)) {
				MeasureBitfield m = new MeasureBitfield();
				m.setDrain(drain);
				m.setTime(csvMeasuresJson.getAt());
				m.setValue(measure.getValue());
				measures.add(m);
			} else {
				MeasureString m = new MeasureString();
				m.setDrain(drain);
				m.setTime(csvMeasuresJson.getAt());
				m.setValue(measure.getValue());
				measures.add(m);
			}
		}

		return measures;
	}

	private Map<String, List<Measure>> retrieveMeasuresFromJson(CsvMeasuresJson csvMeasuresJson, Boolean isAdmin, Map<String, Client> checkedClients, Map<Integer, Map<String, Drain>> checkedDrains, String username) {

		Integer clientId = csvMeasuresJson.getClientId();
		String deviceId = csvMeasuresJson.getDeviceId();

		Client c = null;
		if (checkedClients.containsKey(deviceId)) {
			c = checkedClients.get(deviceId);
		} else {
			c = this.retrieveClientFromDeviceId(clientId, deviceId, isAdmin, username);

			checkedClients.put(deviceId, c);
		}

		List<Measure> measureToUpdate = new ArrayList<Measure>();
		List<Measure> measureToCreate = new ArrayList<Measure>();

		Map<String, Drain> checkedDrainsForClient = checkedDrains.getOrDefault(c.getId(), new HashMap<String, Drain>());
		for (CsvMeasureJson measure : csvMeasuresJson.getMeasures()) {
			Drain drain = null;
			if (checkedDrainsForClient.containsKey(measure.getMeasureId())) {
				drain = checkedDrainsForClient.get(measure.getMeasureId());
			} else {
				for(Feed f: c.getFeeds()) {
					for (Drain d : f.getDrains()) {
						if ((d.getMeasureId() != null) && d.getMeasureId().equals(measure.getMeasureId())) {
							if (drain == null) {
								drain = d;
								checkedDrainsForClient.put(measure.getMeasureId(), d);
								checkedDrains.put(c.getId(), checkedDrainsForClient);
							} else {
								throw new ConflictException(8005, "Measure " + measure.getMeasureId() + " is present in more than one drain for device setted in JSON");
							}
						}
					}
				}

				if (drain == null)
					throw new NotFoundException(404, "Drain not found with measureId = " + measure.getMeasureId() + " for client with deviceId " + deviceId);
			}

			MeasureType mt = this.getMeasureTypeFromDrain(drain);
			if (mt.equals(MeasureType.DOUBLE)) {
				MeasureDouble m = new MeasureDouble();
				m.setDrain(drain);
				m.setTime(csvMeasuresJson.getAt());
				if (drain.getMeasureType().equals("c")) {
					if (measure.getValue().toString().equals("true")) {
						m.setValue(Double.parseDouble("1.0"));
					} else {
						m.setValue(Double.parseDouble("0.0"));
					}
				} else {
					m.setValue(Double.parseDouble(measure.getValue().toString()));
				}

				List<Measure> meas = this.measureDao.getMeasures(drain, mt, m.getTime(), m.getTime());
				if (meas.size() > 0) {
					MeasureDouble updatedMeasure = (MeasureDouble) meas.get(0);
					updatedMeasure.setValue(m.getValue());
					measureToUpdate.add(updatedMeasure);
				} else {
					measureToCreate.add(m);
				}
			} else if (mt.equals(MeasureType.BITFIELD)) {
				MeasureBitfield m = new MeasureBitfield();
				m.setDrain(drain);
				m.setTime(csvMeasuresJson.getAt());
				m.setValue(measure.getValue());

				List<Measure> meas = this.measureDao.getMeasures(drain, mt, m.getTime(), m.getTime());
				if (meas.size() > 0) {
					MeasureBitfield updatedMeasure = (MeasureBitfield) meas.get(0);
					updatedMeasure.setValue(m.getValue());
					measureToUpdate.add(updatedMeasure);
				} else {
					measureToCreate.add(m);
				}
			} else {
				MeasureString m = new MeasureString();
				m.setDrain(drain);
				m.setTime(csvMeasuresJson.getAt());
				m.setValue(measure.getValue());

				List<Measure> meas = this.measureDao.getMeasures(drain, mt, m.getTime(), m.getTime());
				if (meas.size() > 0) {
					MeasureString updatedMeasure = (MeasureString) meas.get(0);
					updatedMeasure.setValue(m.getValue());
					measureToUpdate.add(updatedMeasure);
				} else {
					measureToCreate.add(m);
				}
			}
		}

		Map<String, List<Measure>> measures = new HashMap<String, List<Measure>>();
		measures.put("update", measureToUpdate);
		measures.put("create", measureToCreate);

		return measures;
	}

	private Client retrieveClientFromDeviceId(Integer clientId, String deviceId, Boolean isAdmin, String username) {
		// If client == -1 search for deviceId
		if (clientId.equals(-1)) {
			List<Integer> clientIds = clientService.getClientIdByDeviceId(deviceId);
			if (clientIds == null || clientIds.size() == 0) {
				throw new NotFoundException(404, "Client with deviceId = " + deviceId + " not found");
			} else if (clientIds.size() > 1) {
				if (isAdmin) {
					throw new ConflictException(8004, "Found multiple client with deviceId " + deviceId);
				} else {
					Integer clientIdFound = null;
					for (Integer cid : clientIds) {
						Client cl = clientService.getClient(cid, true, username);
						if (this.orgService.orgIsVisibleByUser(cl.getOrg(), username)) {
							if (clientIdFound != null)
								throw new ConflictException(8004, "Found multiple client with deviceId " + deviceId);
							else
								clientIdFound = cid;
						}
					}
					if (clientIdFound == null)
						throw new NotFoundException(404, "Client with deviceId = " + deviceId + " not found");
					else
						clientId = clientIdFound;
				}
			} else {
				clientId = clientIds.get(0);
			}
		}

		Client parent = clientService.getClient(clientId, isAdmin, username);
		Client c = clientService.getClientFromDeviceId(parent, deviceId);

		if (c == null)
			throw new NotFoundException(404, "Client with deviceId = " + deviceId + " not found");

		return c;
	}

	private MeasureType getMeasureTypeFromDrain(Drain drain) {

		if (drain.getMeasureType().equalsIgnoreCase("b") || drain.getMeasureType().equals("c") || drain.getMeasureType().equals("d") || drain.getMeasureType().equals("f") || drain.getMeasureType().equalsIgnoreCase("h") || drain.getMeasureType().equalsIgnoreCase("i") || drain.getMeasureType().equalsIgnoreCase("q") || (drain.getMeasureType() == null)) {
			return MeasureType.DOUBLE;
		} else if (drain.getMeasureType().equals("C")) {
			return MeasureType.BITFIELD;
		} else {
			return MeasureType.STRING;
		}
	}

	private Map<MeasureType, Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>>> groupQueries(Map<String, List<Measure>> drainMeasures, String[] drainIds, List<MeasureAggregation> measureAggregations, List<String> positiveNegativeValues, List<Boolean> excludeOutlierValues, boolean costs, boolean isAdmin, String username) {

		Map<MeasureType, Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>>> queries = new HashMap<MeasureType, Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>>>();
		Integer i = 0;
		for (String drainId : drainIds) {
			Drain d = this.drainService.getDrain(Integer.parseInt(drainId), isAdmin, username);
			String type = ((d.getType() != null) && (!d.getType().equals(""))) ? d.getType() : "diff";
			Double coeff = !costs ? 1.00 : (d.getUnitOfMeasure().toLowerCase().equals("wh") ? 0.001 : (d.getUnitOfMeasure().toLowerCase().equals("mwh") ? 1000.00 : 1.00));

			List<String> newDrainIds = new ArrayList<String>();
			Map<String, List<String>> newMinMaxValues = new HashMap<String, List<String>>();
			Map<String, Map<String, List<String>>> newPositiveNegativeValues = new HashMap<String, Map<String, List<String>>>();
			Map<Double, Map<String, Map<String, List<String>>>> newCoeff = new HashMap<Double, Map<String, Map<String, List<String>>>>();
			Map<String, Map<Double, Map<String, Map<String, List<String>>>>> newType = new HashMap<String, Map<Double, Map<String, Map<String, List<String>>>>>();
			Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>> newAggr = new HashMap<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>>();
			if (!drainMeasures.containsKey(String.valueOf(d.getId()) + "_" + measureAggregations.get(i) + (!positiveNegativeValues.get(i).isEmpty() ? ("_" + positiveNegativeValues.get(i)) :  "") + (excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : ""))) {
				drainMeasures.put(String.valueOf(d.getId()) + "_" + measureAggregations.get(i) + (!positiveNegativeValues.get(i).isEmpty() ?  ("_" + positiveNegativeValues.get(i)) :  "") + (excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : ""), new ArrayList<Measure>());
				if (queries.containsKey(this.getMeasureTypeFromDrain(d))) {
					if (queries.get(this.getMeasureTypeFromDrain(d)).containsKey(measureAggregations.get(i))) {
						if (queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).containsKey(type)) {
							if (queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).get(type).containsKey(coeff)) {
								if (queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).get(type).get(coeff).containsKey(positiveNegativeValues.get(i))) {
									if (queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).get(type).get(coeff).get(positiveNegativeValues.get(i)).containsKey(excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : ""))
										queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).get(type).get(coeff).get(positiveNegativeValues.get(i)).get(excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : "").add(drainId);
									else {
										newDrainIds.add(drainId);
										queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).get(type).get(coeff).get(positiveNegativeValues.get(i)).put(excludeOutlierValues.get(i) ? d.getMinValue() + "_" + d.getMaxValue() : "", newDrainIds);
									}
								} else {
									newDrainIds.add(drainId);
									newMinMaxValues.put(excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : "", newDrainIds);
									queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).get(type).get(coeff).put(positiveNegativeValues.get(i), newMinMaxValues);
								}
							} else {
								newDrainIds.add(drainId);
								newMinMaxValues.put(excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : "", newDrainIds);
								newPositiveNegativeValues.put(positiveNegativeValues.get(i), newMinMaxValues);
								queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).get(type).put(coeff, newPositiveNegativeValues);
							}
						} else {
							newDrainIds.add(drainId);
							newMinMaxValues.put(excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : "", newDrainIds);
							newPositiveNegativeValues.put(positiveNegativeValues.get(i), newMinMaxValues);
							newCoeff.put(coeff, newPositiveNegativeValues);
							queries.get(this.getMeasureTypeFromDrain(d)).get(measureAggregations.get(i)).put(type, newCoeff);
						}
					} else {
						newDrainIds.add(drainId);
						newMinMaxValues.put(excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : "", newDrainIds);
						newPositiveNegativeValues.put(positiveNegativeValues.get(i), newMinMaxValues);
						newCoeff.put(coeff, newPositiveNegativeValues);
						newType.put(type, newCoeff);
						queries.get(this.getMeasureTypeFromDrain(d)).put(measureAggregations.get(i), newType);
					}
				} else {
					newDrainIds.add(drainId);
					newMinMaxValues.put(excludeOutlierValues.get(i) ? "" + d.getMinValue() + "_" + d.getMaxValue() : "", newDrainIds);
					newPositiveNegativeValues.put(positiveNegativeValues.get(i), newMinMaxValues);
					newCoeff.put(coeff, newPositiveNegativeValues);
					newType.put(type, newCoeff);
					newAggr.put(measureAggregations.get(i), newType);
					queries.put(this.getMeasureTypeFromDrain(d), newAggr);
				}
			}
			i++;
		}
		
		return queries;
	}

	private void executeGroupedQueries(Map<String, List<Measure>> drainMeasures, Map<MeasureType, Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>>> queries, Date start_date, Date end_date, TimeAggregation timeAggr, Boolean forceDiff) {

		for (MeasureType measureType : queries.keySet()) {
			Map<MeasureAggregation, Map<String, Map<Double, Map<String, Map<String, List<String>>>>>> aggregations = queries.get(measureType);
			for (MeasureAggregation aggr : aggregations.keySet()) {
				Map<String, Map<Double, Map<String, Map<String, List<String>>>>> types = aggregations.get(aggr);
				for (String type : types.keySet()) {
					Map<Double, Map<String, Map<String, List<String>>>> coeffs = types.get(type);
					for (Double coeff : coeffs.keySet()) {
						Map<String, Map<String, List<String>>> positiveNegativeValues = coeffs.get(coeff);
						for (String positiveNegativeValue : positiveNegativeValues.keySet()) {
							Map<String, List<String>> minMaxValues = positiveNegativeValues.get(positiveNegativeValue);
							for (String minMaxValue : minMaxValues.keySet())
								drainMeasures = this.measureDao.getMultipleMeasures(drainMeasures, minMaxValues.get(minMaxValue),minMaxValue, coeff, measureType, forceDiff || type.equals("inc"), start_date, end_date, timeAggr, aggr, positiveNegativeValue);
						}
					}
				}
			}
		}
	}

	private void calculateOperations(ArrayList<Operation> drainOperations, boolean isAdmin, String username, List<PairDrainMeasuresJson> jsonArray, List<PairDrainMeasuresJson> jsonFinalArray) {

		int count = 0;
		int fCount = 1;
		boolean unitMatch = true; // if in a formula there are two different units or more (e.g W and A) is set to false
		List<Operation> multiplied = new ArrayList<Operation>();
		List<PairDrainMeasuresJson> multipliedDrains = new ArrayList<PairDrainMeasuresJson>();
		Operation op;

		// Multiplication and division are executed in a dedicated cycle before other operations
		for (int j = 0; j < drainOperations.size() - 1; j++) {
			op = drainOperations.get(j);
			if (op.equals(Operation.TIMES) || op.equals(Operation.DIVISION)) {
				if (unitMatch && !jsonArray.get(count).getUnit().equals(jsonArray.get(count + 1).getUnit()))
					unitMatch = false;

				this.executeTimesDivisionOperations(op, jsonArray, count);
				multiplied.add(drainOperations.get(count));
				multipliedDrains.add(jsonArray.get(count));
			}
			count++;
		}

		drainOperations.removeAll(multiplied);
		jsonArray.removeAll(multipliedDrains);
		count = 0;
		for (int j = 0; j < drainOperations.size(); j++) {
			op = drainOperations.get(j);
			if (op.equals(Operation.SEMICOLON)) {
				PairDrainMeasuresJson newDrainList = jsonArray.get(count);

				if (newDrainList.isFormula()) {
					newDrainList.setDrainName("Formula_" + fCount);
					fCount++;

					if (!unitMatch) {
						newDrainList.setUnit("?");
						unitMatch = true;
					}
				} else {
					Drain d = drainService.getDrain(jsonArray.get(count).getDrainId(), isAdmin, username);
					Feed f = feedService.getFeed(d.getFeed().getId(), isAdmin, username);
					for (Client c : f.getClients()) {
						if ((c.getEnergyClient() != null) && c.getEnergyClient()) {
							newDrainList.setDrainName(c.getName() + " - " + jsonArray.get(count).getDrainName());
							newDrainList.setDecimals(d.getDecimals());
							break;
						}
					}
				}

				jsonFinalArray.add(newDrainList);
			} else {
				if (unitMatch && !jsonArray.get(count).getUnit().equals(jsonArray.get(count + 1).getUnit()))
					unitMatch = false;

				this.executeSumSubctrationOperation(op, jsonArray, count);
			}
			count++;
		}
	}

	@SuppressWarnings("unchecked")
	private void executeSumSubctrationOperation(Operation op, List<PairDrainMeasuresJson> jsonArray, int count) {

		List<Value> list = new ArrayList<Value>();
		if (jsonArray.get(count + 1).getMeasures().size() > 0) {
			for (int k = 0; k < jsonArray.get(count + 1).getMeasures().size(); k++) {
				Value m2 = jsonArray.get(count + 1).getMeasures().get(k);
				if (jsonArray.get(count).getMeasures().size() > 0) {
					for (int index = 0; index < jsonArray.get(count).getMeasures().size();) {
						Value m1 = jsonArray.get(count).getMeasures().get(index);
						if (m2.getTime().equals(m1.getTime())) {
							Double v1 = (m1.getValue() != null) ? Double.valueOf(m1.getValue().toString()) : 0;
							Double v2 = (m2.getValue() != null) ? Double.valueOf(m2.getValue().toString()) : 0;
							if (op.equals(Operation.PLUS))
								m2.setValue(v1 + v2);
							else
								m2.setValue(v1 - v2);

							list.add(m2);
							jsonArray.get(count).getMeasures().remove(index);
							break;
						} else if (m2.getTime().compareTo(m1.getTime()) < 0) {
							if (k == jsonArray.get(count + 1).getMeasures().size() - 1) {
								list.add(m1);
								jsonArray.get(count).getMeasures().remove(index);
								if (index == jsonArray.get(count).getMeasures().size())
									break;
							} else {
								list.add(m2);
								break;
							}
						} else {
							list.add(m1);
							jsonArray.get(count).getMeasures().remove(index);
						}
					}
				} else {
					list.add(m2);
				}
			}
			if (jsonArray.get(count).getMeasures().size() > 0)
				list.addAll(jsonArray.get(count).getMeasures());
		} else {
			for (Value m2 : jsonArray.get(count).getMeasures())
				list.add(m2);
		}

		jsonArray.get(count + 1).setMeasures(list);
		jsonArray.get(count + 1).setFormula(true);
	}
	
	@SuppressWarnings("unchecked")
	private void executeTimesDivisionOperations(Operation op, List<PairDrainMeasuresJson> jsonArray, int count) {

		List<Value> list = new ArrayList<Value>();
		for (Value m2 : jsonArray.get(count + 1).getMeasures()) {
			for (int index = 0; index < jsonArray.get(count).getMeasures().size();) {
				Value m1 = jsonArray.get(count).getMeasures().get(index);
				if (m2.getTime().equals(m1.getTime())) {
					Double v1 = (m1.getValue() != null) ? Double.valueOf(m1.getValue().toString()) : 0;
					Double v2 = (m2.getValue() != null) ? Double.valueOf(m2.getValue().toString()) : 0;
					m2.setValue(op.equals(Operation.TIMES) ? v1 * v2 : ((v2 == 0) ? 0.00 : v1 / v2));
					jsonArray.get(count).getMeasures().remove(index);
					list.add(m2);
					break;
				} else if (m2.getTime().compareTo(m1.getTime()) < 0) {
					break;
				} else {
					jsonArray.get(count).getMeasures().remove(index);
				}
			}
		}

		jsonArray.get(count + 1).setMeasures(list);
		jsonArray.get(count + 1).setFormula(true);
	}
}
