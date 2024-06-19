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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Operation;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.model.CsvMeasuresJson;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson;
import it.mynaproject.togo.api.util.Pair;

public interface MeasureService {

	public Pair<Drain, List<Measure>> getAllMeasuresFromDrainId(Integer drainId, Date start, Date end, Boolean isAdmin, String username);
	public void persist(Measure measure);
	public void persistMultiple(List<Measure> measures);
	public void createMeasuresFromJson(CsvMeasuresJson csvMeasuresJson, Boolean isAdmin, String username);
	public void createMeasuresFromJsonList(List<CsvMeasuresJson> csvMeasuresJsonList, Boolean isAdmin, String username);
	public void update(Measure measure);
	public void updateMultiple(List<Measure> measures);
	public void updateMeasuresFromJson(CsvMeasuresJson csvMeasuresJson, Boolean isAdmin, String username);
	public void updateMeasuresFromJsonList(List<CsvMeasuresJson> csvMeasuresJsonList, Boolean isAdmin, String username);
	public void delete(Measure measure);
	public void deleteMeasures(Integer clientId, String deviceId, Date start, Date end, Boolean isAdmin, String username);
	public List<PairDrainMeasuresJson> getMeasures(String[] drainIds, ArrayList<Operation> drainOperations, ArrayList<MeasureAggregation> measureAggregations, ArrayList<String> positiveNegativeValues, ArrayList<Boolean> excludeOutliers, Date start, Date end, TimeAggregation timeAggregation, boolean isAdmin, String username);
	public List<PairDrainMeasuresJson> getCosts(Integer drainCostId, String[] drainIds, ArrayList<Operation> drainOperations, MeasureAggregation measureAggregation, ArrayList<String> positiveNegativeValues, ArrayList<Boolean> excludeOutliers, Date start, Date end, TimeAggregation timeAggregation, boolean isAdmin, String username);
	public Boolean checkMeasuresForDrain(Drain drain, Date start, Date end);
}
