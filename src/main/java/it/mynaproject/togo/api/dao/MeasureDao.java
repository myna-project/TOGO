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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.MeasureType;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.util.Pair;

public interface MeasureDao {

	public void persist(Measure measure);
	public void persistMultiple(List<Measure> measures);
	public void update(Measure measure);
	public void updateMultiple(List<Measure> measures);
	public void delete(Measure measure);
	public void deleteMultiple(List<Measure> measures);
	public List<Measure> getMeasures(Drain d, MeasureType measureType, Date start, Date end);
	public Map<String, List<Measure>> getMultipleMeasures(Map<String, List<Measure>> drainMeasures, List<String> drainIds, String minMaxValue, Double coeff, MeasureType measureType, Boolean inc, Date start, Date end, TimeAggregation timeAggregation, MeasureAggregation measureAggregation, String measurePositiveNegativeValue);
	public void createSlotsStats(TimeAggregation timeAggregation, List<Pair<Date, Date>> slots, Date start_date, Date end_date, Calendar startCal);
}
