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
package it.mynaproject.togo.api.dao.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.Query;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.mynaproject.togo.api.dao.MeasureDao;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.MeasureType;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.domain.impl.MeasureBitfield;
import it.mynaproject.togo.api.domain.impl.MeasureDouble;
import it.mynaproject.togo.api.domain.impl.MeasureString;
import it.mynaproject.togo.api.exception.ConflictException;
import it.mynaproject.togo.api.exception.GenericException;
import it.mynaproject.togo.api.util.Pair;

@Repository
public class MeasureDaoImpl extends BaseDaoImpl implements MeasureDao {

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void persist(Measure measure) {

		log.debug("Creating new measure: {}", measure);

		em.persist(measure);
		em.flush();
	}

	@Override
	public void persistMultiple(List<Measure> measures) {

		log.debug("Creating new list of measure of size: {}", measures.size());

		for (Measure measure : measures) {
			log.debug("Creating new measure: {}", measure);

			try {
				if (measure instanceof MeasureDouble) {
					MeasureDouble m = (MeasureDouble) measure;
					em.persist(m);
				} else if (measure instanceof MeasureBitfield) {
					MeasureBitfield m = (MeasureBitfield) measure;
					em.persist(m);
				} else if (measure instanceof MeasureString) {
					MeasureString m = (MeasureString) measure;
					em.persist(m);
				} else {
					throw new Exception("measure type not found.");
				}
			} catch (EntityExistsException ex) {
				throw new ConflictException(8001, "Measure " + measure.getDrain().getMeasureId() + " is duplicate at time " + measure.getTime().toString() + " into JSON received", ex);
			} catch (Exception e) {
				if (e.getCause() instanceof ConstraintViolationException) {
					throw new ConflictException(8002, "There is already a value for measure " + measure.getDrain().getMeasureId() + " at time " + measure.getTime().toString(), e);
				} else {
					throw new GenericException(8003, "Error occured during saving measures: " + e.getMessage(), e);
				}
			}
		}

		try {
			em.flush();
		} catch (EntityExistsException ex) {
			throw new ConflictException(8001, "There are duplicate measures in JSON received", ex);
		} catch (Exception e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				throw new ConflictException(8002, "There is already a value for a measure in the same time", e);
			} else {
				throw new GenericException(8003, "Error occured during saving measures: " + e.getMessage(), e);
			}
		}
	}

	@Override
	public void update(Measure measure) {

		log.debug("Updating measure: {}", measure);

		em.persist(em.merge(measure));
		em.flush();
	}

	@Override
	public void updateMultiple(List<Measure> measures) {

		log.debug("Updating list of measure of size: {}", measures.size());

		for (Measure measure : measures) {
			log.debug("Updating measure: {}", measure);

			try {
				if (measure instanceof MeasureDouble) {
					MeasureDouble m = (MeasureDouble) measure;
					em.persist(em.merge(m));
				} else if (measure instanceof MeasureBitfield) {
					MeasureBitfield m = (MeasureBitfield) measure;
					em.persist(em.merge(m));
				} else if (measure instanceof MeasureString) {
					MeasureString m = (MeasureString) measure;
					em.persist(em.merge(m));
				} else {
					throw new GenericException(8003, "Measure type not found");
				}
			} catch (Exception e) {
				throw new GenericException(8003, "Error occured during saving measures: " + e.getCause().getCause().getMessage(), e);
			}
		}

		try {
			em.flush();
		} catch (Exception e) {
			throw new GenericException(8003, "Error occured during saving measures: " + e.getCause().getCause().getMessage(), e);
		}
	}

	@Override
	public void delete(Measure measure) {

		log.debug("Deleting measure: {}", measure);

		em.remove(em.merge(measure));
		em.flush();
	}

	@Override
	public void deleteMultiple(List<Measure> measures) {

		log.debug("Deleting list of measure of size: {}", measures.size());

		for (Measure measure : measures)
			em.remove(em.merge(measure));

		em.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getMeasures(Drain d, MeasureType measureType, Date start, Date end) {

		String table = (measureType.equals(MeasureType.DOUBLE)) ? "MeasureDouble" : ((measureType.equals(MeasureType.BITFIELD)) ? "MeasureBitfield" : "MeasureString");

		String queryString = "FROM " + table + " WHERE drain_id = :drain_id";
		if ((start != null) && (end != null)) {
			queryString = queryString.concat(" AND time BETWEEN :start AND :end");
		}

		Query q = em.createQuery(queryString);
		q.setParameter("drain_id", d.getId());
		if ((start != null) && (end != null)) {
			q.setParameter("start", start);
			q.setParameter("end", end);
		}

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Map<String, List<Measure>> getMultipleMeasures(Map<String, List<Measure>> drainMeasures, List<String> drainIds, String minMaxValue, Double coeff, MeasureType measureType, Boolean inc, Date start, Date end, TimeAggregation timeAggregation, MeasureAggregation measureAggregation, String measurePositiveNegativeValue) {

		log.debug("Start selection of measures {} measureType: {} start: {} end: {} time aggregation: {} measureAggregation: {} measurePositiveNegativeValue: {} minMaxValue: {}", drainIds, measureType, start, end, timeAggregation, measureAggregation, measurePositiveNegativeValue, minMaxValue);

		TimeAggregation baseTimeAggregation = timeAggregation;
		if (!measurePositiveNegativeValue.isEmpty() || minMaxValue != "")
			timeAggregation = TimeAggregation.NONE;

		String whereOutliers = "";
		if (minMaxValue != "") {
			String[] outliers = minMaxValue.split("_");
			if (!outliers[0].equals("null"))
				whereOutliers += " AND value >= " + outliers[0];
			if (!outliers[1].equals("null"))
				whereOutliers += " AND value <= " + outliers[1];
		}

		String queryMeasureAggr = (measureAggregation != null) ? measureAggregation.toString().toUpperCase() : "";

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, (int) 0);

		if (measureType.equals(MeasureType.DOUBLE)) {
			String select = "drain_id, " + queryMeasureAggr + "(value)" + ((coeff != 1.00) ? " * " + coeff : "");
			String table = "measure_double";
			String whereDrain = "drain_id IN (" + String.join(",", drainIds) + ")";
			String whereTime = "(bucket >= '" + df.format(start) + "' AND bucket <= '" + df.format(end) + "')";
			String groupBy = " GROUP BY drain_id";
			String orderBy = (timeAggregation.equals(TimeAggregation.ALL)) ? "" : " ORDER BY bucket";

			switch (timeAggregation) {
			case ALL:
				whereTime = "(time >= '" + df.format(start) + "' AND time <= '" + df.format(end) + "')";
				break;
			case YEAR:
				select = "drain_id, " + queryMeasureAggr + "(" + measureAggregation.toString().toLowerCase() + ")" + ((coeff != 1.00) ? " * " + coeff : "") + ", timescaledb_experimental.time_bucket_ng('1 years', bucket)";
				table = "measure_metrics_hourly";
				groupBy = groupBy.concat(", timescaledb_experimental.time_bucket_ng('1 years', bucket)");
				orderBy = " ORDER BY timescaledb_experimental.time_bucket_ng('1 years', bucket)";
				break;
			case MONTH:
				select = "drain_id, " + queryMeasureAggr + "(" + measureAggregation.toString().toLowerCase() + ")" + ((coeff != 1.00) ? " * " + coeff : "") + ", timescaledb_experimental.time_bucket_ng('1 months', bucket)";
				table = "measure_metrics_hourly";
				groupBy = groupBy.concat(", timescaledb_experimental.time_bucket_ng('1 months', bucket)");
				orderBy = " ORDER BY timescaledb_experimental.time_bucket_ng('1 months', bucket)";
				break;
			case DAY:
				select = "drain_id, " + queryMeasureAggr + "(" + measureAggregation.toString().toLowerCase() + ")" + ((coeff != 1.00) ? " * " + coeff : "") + ", timescaledb_experimental.time_bucket_ng('1 days', bucket)";
				table = "measure_metrics_hourly";
				groupBy = groupBy.concat(", timescaledb_experimental.time_bucket_ng('1 days', bucket)");
				orderBy = " ORDER BY timescaledb_experimental.time_bucket_ng('1 days', bucket)";
				break;
			case HOUR:
				select = "drain_id, " + measureAggregation.toString().toLowerCase() + ((coeff != 1.00) ? " * " + coeff : "") + ", bucket";
				table = "measure_metrics_hourly";
				groupBy = "";
				break;
			case QHOUR:
				select = "drain_id, " + queryMeasureAggr + "(value)" + ((coeff != 1.00) ? " * " + coeff : "") + ", time_bucket('15 minutes', time)";
				whereTime = "(time >= '" + df.format(start) + "' AND time <= '" + df.format(end) + "')";
				groupBy = groupBy.concat(", time_bucket('15 minutes', time)");
				orderBy = " ORDER BY time_bucket('15 minutes', time)";
				break;
			case MINUTE:
				select = "drain_id, " +queryMeasureAggr + "(value)" + ((coeff != 1.00) ? " * " + coeff : "") + ", time_bucket('1 minutes', time)";
				whereTime = "(time >= '" + df.format(start) + "' AND time <= '" + df.format(end) + "')";
				groupBy = groupBy.concat(", time_bucket('1 minutes', time)");
				orderBy = " ORDER BY time_bucket('1 minutes', time)";
				break;
			case NONE:
				whereTime = "(time >= '" + df.format(start) + "' AND time <= '" + df.format(end) + "')" + whereOutliers;
				break;
			default:
				break;
			}

			String fromWhere = " FROM ";
			if (inc) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				cal.add(Calendar.HOUR_OF_DAY, -1);
				whereTime = "(time >= '" + df.format(start) + "' AND time <= '" + df.format(end) + "')" + whereOutliers;
				switch (timeAggregation) {
				case YEAR:
					select = "drain_id, " + queryMeasureAggr + "(value)" + ((coeff != 1.00) ? " * " + coeff : "") + ", timescaledb_experimental.time_bucket_ng('1 years', time)";
					table = "measure_double";
					groupBy = " GROUP BY drain_id, timescaledb_experimental.time_bucket_ng('1 years', time)";
					orderBy = " ORDER BY timescaledb_experimental.time_bucket_ng('1 years', time)";
					break;
				case MONTH:
					select = "drain_id, " + queryMeasureAggr + "(value)" + ((coeff != 1.00) ? " * " + coeff : "") + ", timescaledb_experimental.time_bucket_ng('1 months', time)";
					table = "measure_double";
					groupBy = " GROUP BY drain_id, timescaledb_experimental.time_bucket_ng('1 months', time)";
					orderBy = " ORDER BY timescaledb_experimental.time_bucket_ng('1 months', time)";
					break;
				case DAY:
					select = "drain_id, " + queryMeasureAggr + "(value)" + ((coeff != 1.00) ? " * " + coeff : "") + ", timescaledb_experimental.time_bucket_ng('1 days', time)";
					table = "measure_double";
					groupBy = " GROUP BY drain_id, timescaledb_experimental.time_bucket_ng('1 days', time)";
					orderBy = " ORDER BY timescaledb_experimental.time_bucket_ng('1 days', time)";
					break;
				case HOUR:
					select = "drain_id, " + queryMeasureAggr + "(value)" + ((coeff != 1.00) ? " * " + coeff : "") + ", time_bucket('1 hours', time)";
					table = "measure_double";
					groupBy = " GROUP BY drain_id, time_bucket('1 hours', time)";
					orderBy = " ORDER BY time_bucket('1 hours', time)";
				default:
					break;
				}
				fromWhere = fromWhere.concat("(SELECT drain_id, time, value - lag(value) OVER (PARTITION BY drain_id ORDER BY time) AS value FROM " + table + " WHERE " + whereDrain + " AND (time >= '" + df.format(cal.getTime()) + "' AND time <= '" + df.format(end) + "')) AS m WHERE " + whereTime);
			} else {
				fromWhere = fromWhere.concat(table + " WHERE " + whereDrain + " AND " + whereTime);
			}

			StatelessSession statelessSession = ((Session) em.getDelegate()).getSessionFactory().openStatelessSession();

			org.hibernate.query.Query query = statelessSession.createNativeQuery(timeAggregation.equals(TimeAggregation.NONE) ? "SELECT drain_id, value, time" + fromWhere + " ORDER BY time;" : "SELECT " + select + fromWhere + groupBy + orderBy);
			query.setFetchSize(10000);
			query.setReadOnly(true);

			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);

			List<Pair<Date, Date>> slots = new ArrayList<Pair<Date, Date>>();
			Map<String, DoubleSummaryStatistics[]> stats = new HashMap<String, DoubleSummaryStatistics[]>();
			if ((!measurePositiveNegativeValue.isEmpty() || !minMaxValue.isEmpty()) && (baseTimeAggregation != TimeAggregation.NONE)) {
				Calendar startCal = Calendar.getInstance();
				startCal.setTime(start);
				Calendar endCal = Calendar.getInstance();
				endCal.setTime(end);
				createSlotsStats(baseTimeAggregation, slots, start, end, startCal);
			}
			while (results.next()) {
				Object[] arrayObjects = results.get();
				if (arrayObjects[1] != null) {
					for (int i = 2; i < arrayObjects.length; i++) {
						if (arrayObjects[i] instanceof Float) {
							arrayObjects[i] = ((Float) arrayObjects[i]).intValue();
						} else if (arrayObjects[i] instanceof Double) {
							arrayObjects[i] = ((Double) arrayObjects[i]).intValue();
						}
					}
					String drainMeasuresKey = String.valueOf((Integer) arrayObjects[0]) + "_" + measureAggregation + (measurePositiveNegativeValue.isEmpty() ? "" : ("_" + measurePositiveNegativeValue)) + (minMaxValue.isEmpty() ? "" : minMaxValue);
					// Only positive/negative case
					if (!measurePositiveNegativeValue.isEmpty()) {
						if ((baseTimeAggregation != TimeAggregation.NONE)) {
							if (!stats.containsKey(drainMeasuresKey)) {
								DoubleSummaryStatistics[] newStats = new DoubleSummaryStatistics[slots.size()];
								stats.put(drainMeasuresKey, newStats);
							}
							for (Integer k = 0; k < slots.size(); k++) {
								if (stats.get(drainMeasuresKey)[k] == null) {
									DoubleSummaryStatistics newStat = new DoubleSummaryStatistics();
									stats.get(drainMeasuresKey)[k] = newStat;
								}
								Calendar time = Calendar.getInstance();
								time.setTime(new Date(((Timestamp) arrayObjects[2]).getTime()));
								if (time.getTime().equals(slots.get(k).getLeft()) || (time.getTime().after(slots.get(k).getLeft()) && time.getTime().before(slots.get(k).getRight()))) {
									if ((Double) arrayObjects[1] != null) {
										if (measurePositiveNegativeValue.equals("pos")) {
											stats.get(drainMeasuresKey)[k].accept((Double) arrayObjects[1] < 0 ? 0 : (Double) arrayObjects[1]);
										} else {
											stats.get(drainMeasuresKey)[k].accept((Double) arrayObjects[1] > 0 ? 0 : (Double) arrayObjects[1]);
										}
									}
									break;
								}
							}
						} else {
							Measure measure = new MeasureDouble();
							measure.setValue(((measurePositiveNegativeValue.equals("pos") && ((Double) arrayObjects[1] < 0)) || (measurePositiveNegativeValue.equals("neg") && ((Double) arrayObjects[1] > 0))) ? 0 : (Double) arrayObjects[1]);
							measure.setTime(timeAggregation.equals(TimeAggregation.ALL) ? start : (Timestamp) arrayObjects[2]);

							if (drainMeasures.get(drainMeasuresKey) != null) {
								drainMeasures.get(drainMeasuresKey).add(measure);
							} else {
								List<Measure> measures = new ArrayList<Measure>();
								measures.add(measure);
								drainMeasures.put(drainMeasuresKey, measures);
							}
						}
					}
					if (!minMaxValue.isEmpty()) {
						if ((baseTimeAggregation != TimeAggregation.NONE)) {
							if (!stats.containsKey(drainMeasuresKey)) {
								DoubleSummaryStatistics[] newStats = new DoubleSummaryStatistics[slots.size()];
								stats.put(drainMeasuresKey, newStats);
							}
							for (Integer k = 0; k < slots.size(); k++) {
								if (stats.get(drainMeasuresKey)[k] == null) {
									DoubleSummaryStatistics newStat = new DoubleSummaryStatistics();
									stats.get(drainMeasuresKey)[k] = newStat;
								}
								Calendar time = Calendar.getInstance();
								time.setTime(new Date(((Timestamp) arrayObjects[2]).getTime()));
								if (time.getTime().equals(slots.get(k).getLeft()) || (time.getTime().after(slots.get(k).getLeft()) && time.getTime().before(slots.get(k).getRight()))) {
									if ((Double) arrayObjects[1] != null)
										stats.get(drainMeasuresKey)[k].accept((Double) arrayObjects[1]);
									break;
								}
							}
						} else {
							Measure measure = new MeasureDouble();
							measure.setValue((Double) arrayObjects[1]);
							measure.setTime(timeAggregation.equals(TimeAggregation.ALL) ? start : (Timestamp) arrayObjects[2]);

							if (drainMeasures.get(drainMeasuresKey) != null) {
								drainMeasures.get(drainMeasuresKey).add(measure);
							} else {
								List<Measure> measures = new ArrayList<Measure>();
								measures.add(measure);
								drainMeasures.put(drainMeasuresKey, measures);
							}
						}
					}
					if (measurePositiveNegativeValue.isEmpty() && minMaxValue.isEmpty()) {
						Measure measure = new MeasureDouble();
						measure.setValue((Double) arrayObjects[1]);
						measure.setTime(timeAggregation.equals(TimeAggregation.ALL) ? start : (Timestamp) arrayObjects[2]);

						if (drainMeasures.get(drainMeasuresKey) != null) {
							drainMeasures.get(drainMeasuresKey).add(measure);
						} else {
							List<Measure> measures = new ArrayList<Measure>();
							measures.add(measure);
							drainMeasures.put(drainMeasuresKey, measures);
						}
					}
				}
			}
			results.close();
			statelessSession.close();

			if ((!measurePositiveNegativeValue.isEmpty() || !minMaxValue.isEmpty()) && (baseTimeAggregation != TimeAggregation.NONE)) {
				for (String drainKey : stats.keySet()) {
					for (Integer k = 0; k < slots.size(); k++) {
						Measure measure = new MeasureDouble();
						if ((stats.get(drainKey)[k] != null) && (stats.get(drainKey)[k].getCount() > 0)) {
							switch (measureAggregation) {
								case SUM: {
									measure.setValue(stats.get(drainKey)[k].getSum());
									measure.setTime(slots.get(k).getLeft());
									break;
								}
								case AVG: {
									measure.setValue(stats.get(drainKey)[k].getAverage());
									measure.setTime(slots.get(k).getLeft());
									break;
								}
								case MIN: {
									measure.setValue(stats.get(drainKey)[k].getMin());
									measure.setTime(slots.get(k).getLeft());
									break;
								}
								case MAX: {
									measure.setValue(stats.get(drainKey)[k].getMax());
									measure.setTime(slots.get(k).getLeft());
									break;
								}
								default:
									break;
							}
						}
						if ((measure.getValue() != null) && (measure.getTime() != null)) {
							if (drainMeasures.get(drainKey) != null) {
								drainMeasures.get(drainKey).add(measure);
							} else {
								List<Measure> measures = new ArrayList<Measure>();
								measures.add(measure);
								drainMeasures.put(drainKey, measures);
							}
						}
					}
				}
			}
		} else {
			String year = "EXTRACT(year FROM time)";
			String month = "EXTRACT(month FROM time)";
			String day = "EXTRACT(day FROM time)";
			String hour = "EXTRACT(hour FROM time)";
			String tz = "EXTRACT(timezone FROM time)";
			String queryTimeAggr = year + " AS year";
			String table = (measureType.equals(MeasureType.BITFIELD)) ? "measure_bitfield" : "measure_string";
			String whereDrain = "drain_id IN (" + String.join(",", drainIds) + ")";
			String whereTime = "(time >= '" + df.format(start) + "' AND time <= '" + df.format(end) + "')";
			String groupBy = "drain_id, " + year;
			String orderBy = year;
			switch (timeAggregation) {
			case ALL:
				queryTimeAggr = "drain_id";
				groupBy = "drain_id";
				orderBy = "drain_id";
				break;
			case YEAR:
				break;
			case MONTH:
				queryTimeAggr = queryTimeAggr.concat(", " + month + " AS month");
				groupBy = groupBy.concat(", " + month);
				orderBy = orderBy.concat(", " + month);
				break;
			case DAY:
				queryTimeAggr = queryTimeAggr.concat(", " + month + " AS month, " + day + " AS day");
				groupBy = groupBy.concat(", " + month + ", " + day);
				orderBy = orderBy.concat(", " + month + ", " + day);
				break;
			case HOUR:
				queryTimeAggr = queryTimeAggr.concat(", " + month + " AS month, " + day + " AS day, " + hour + " AS hour, " + tz + " AS tz");
				groupBy = groupBy.concat(", " + month + ", " + day + ", " + hour + ", " + tz);
				orderBy = orderBy.concat(", " + month + ", " + day + ", " + hour + ", " + tz + " DESC");
				break;
			case QHOUR:
				String qhour = "FLOOR(EXTRACT(minute FROM time)/15)";
				queryTimeAggr = queryTimeAggr.concat(", " + month + " AS month, " + day + " AS day, " + hour + " AS hour, " + tz + " AS tz, " + qhour + " AS quarter");
				groupBy = groupBy.concat(", " + month + ", " + day + ", " + hour + ", " + tz + ", " + qhour);
				orderBy = orderBy.concat(", " + month + ", " + day + ", " + hour + ", " + tz + " DESC, " + qhour);
				break;
			case MINUTE:
				String minute = "EXTRACT(minute FROM time)";
				queryTimeAggr = queryTimeAggr.concat(", " + month + " AS month, " + day + " AS day, " + hour + " AS hour, " + tz + " AS tz, " + minute + " AS minute");
				groupBy = groupBy.concat(", " + month + ", " + day + ", " + hour + ", " + tz + ", " + minute);
				orderBy = orderBy.concat(", " + month + ", " + day + ", " + hour + ", " + tz + " DESC, " + minute);
				break;
			default:
				break;
			}
			String fromWhere = " FROM " + table + " WHERE " + whereDrain + " AND " + whereTime;

			StatelessSession statelessSession = ((Session) em.getDelegate()).getSessionFactory().openStatelessSession();

			org.hibernate.query.Query query = statelessSession.createNativeQuery(timeAggregation.equals(TimeAggregation.NONE) ? "SELECT drain_id, value, time" + fromWhere + " ORDER BY time;" : "SELECT drain_id, " + queryMeasureAggr + "(value), " + queryTimeAggr + fromWhere + " GROUP BY " + groupBy + " ORDER BY " + orderBy);
			query.setFetchSize(10000);
			query.setReadOnly(true);

			ScrollableResults results = query.scroll(ScrollMode.FORWARD_ONLY);
			while (results.next()) {
				Object[] arrayObjects = results.get();
				for (int i = 2; i < arrayObjects.length; i++) {
					if (arrayObjects[i] instanceof Float) {
						arrayObjects[i] = ((Float) arrayObjects[i]).intValue();
					} else if (arrayObjects[i] instanceof Double) {
						arrayObjects[i] = ((Double) arrayObjects[i]).intValue();
					}
				}

				Measure measure = (measureType.equals(MeasureType.BITFIELD)) ? new MeasureBitfield() : new MeasureString();
				measure.setValue((String) arrayObjects[1]);

				if (timeAggregation != TimeAggregation.NONE) {
					calendar.set(Calendar.YEAR, (int) arrayObjects[2]);
				}

				switch (timeAggregation) {
				case MINUTE:
					calendar.set(Calendar.MONTH, (int) arrayObjects[3] - 1);
					calendar.set(Calendar.DAY_OF_MONTH, (int) arrayObjects[4]);
					calendar.set(Calendar.HOUR_OF_DAY, (int) arrayObjects[5]);
					calendar.set(Calendar.DST_OFFSET, (int) arrayObjects[6] * 1000);
					calendar.set(Calendar.ZONE_OFFSET, 0);
					calendar.set(Calendar.MINUTE, (int) arrayObjects[7]);
					break;
				case QHOUR:
					calendar.set(Calendar.MONTH, (int) arrayObjects[3] - 1);
					calendar.set(Calendar.DAY_OF_MONTH, (int) arrayObjects[4]);
					calendar.set(Calendar.HOUR_OF_DAY, (int) arrayObjects[5]);
					calendar.set(Calendar.DST_OFFSET, (int) arrayObjects[6] * 1000);
					calendar.set(Calendar.MINUTE, (int) 15 * (int) arrayObjects[7]);
					calendar.set(Calendar.ZONE_OFFSET, 0);
					break;
				case HOUR:
					calendar.set(Calendar.MONTH, (int) arrayObjects[3] - 1);
					calendar.set(Calendar.DAY_OF_MONTH, (int) arrayObjects[4]);
					calendar.set(Calendar.HOUR_OF_DAY, (int) arrayObjects[5]);
					calendar.set(Calendar.DST_OFFSET, (int) arrayObjects[6] * 1000);
					calendar.set(Calendar.ZONE_OFFSET, 0);
					calendar.set(Calendar.MINUTE, (int) 0);
					break;
				case DAY:
					calendar.set(Calendar.MONTH, (int) arrayObjects[3] - 1);
					calendar.set(Calendar.DAY_OF_MONTH, (int) arrayObjects[4]);
					calendar.set(Calendar.HOUR_OF_DAY, (int) 0);
					calendar.set(Calendar.MINUTE, (int) 0);
					break;
				case MONTH:
					calendar.set(Calendar.MONTH, (int) arrayObjects[3] - 1);
					calendar.set(Calendar.DAY_OF_MONTH, (int) 1);
					calendar.set(Calendar.HOUR_OF_DAY, (int) 0);
					calendar.set(Calendar.MINUTE, (int) 0);
					break;
				case YEAR:
					calendar.set(Calendar.MONTH, (int) 0);
					calendar.set(Calendar.DAY_OF_MONTH, (int) 1);
					calendar.set(Calendar.HOUR_OF_DAY, (int) 0);
					calendar.set(Calendar.MINUTE, (int) 0);
					break;
				case ALL:
					calendar.setTime(start);
					break;
				case NONE:
					calendar.setTimeInMillis(((Timestamp) arrayObjects[2]).getTime());
					break;
				default:
					break;
				}
				measure.setTime(calendar.getTime());

				if (drainMeasures.get(String.valueOf((Integer) arrayObjects[0]) + "_" + measureAggregation + (minMaxValue.isEmpty() ? "" : minMaxValue)) != null) {
					drainMeasures.get(String.valueOf((Integer) arrayObjects[0]) + "_" + measureAggregation + (minMaxValue.isEmpty() ? "" : minMaxValue)).add(measure);
				} else {
					List<Measure> measures = new ArrayList<Measure>();
					measures.add(measure);
					drainMeasures.put(String.valueOf((Integer) arrayObjects[0]) + "_" + measureAggregation + (minMaxValue.isEmpty() ? "" : minMaxValue), measures);
				}
			}
			results.close();
			statelessSession.close();
		}

		return drainMeasures;
	}

	public void createSlotsStats(TimeAggregation timeAggregation, List<Pair<Date, Date>> slots, Date start_date, Date end_date, Calendar startCal) {

		Calendar endSlotCal = Calendar.getInstance();
		endSlotCal.setTime(start_date);

		switch (timeAggregation) {
			case ALL: {
				slots.add(new Pair<Date, Date>(start_date, end_date));
			}
			case YEAR: {
				startCal.set(Calendar.MONTH, 0);
				startCal.set(Calendar.DATE, 1);
				startCal.set(Calendar.HOUR_OF_DAY, 0);
				startCal.set(Calendar.MINUTE, 0);
				startCal.set(Calendar.SECOND, 0);
				while ((startCal.getTime().before(end_date))) {
					endSlotCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
					endSlotCal.set(Calendar.MONTH, startCal.getActualMaximum(Calendar.MONTH));
					endSlotCal.set(Calendar.DATE, startCal.getActualMaximum(Calendar.DATE));
					endSlotCal.set(Calendar.HOUR_OF_DAY, startCal.getActualMaximum(Calendar.HOUR_OF_DAY));
					endSlotCal.set(Calendar.MINUTE, startCal.getActualMaximum(Calendar.MINUTE));
					endSlotCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));
					slots.add(new Pair<Date, Date>(startCal.getTime(), endSlotCal.getTime()));
					startCal = (Calendar) endSlotCal.clone();
					startCal.add(Calendar.SECOND, 1);
				}
			}
			case MONTH: {
				startCal.set(Calendar.DATE, 1);
				startCal.set(Calendar.HOUR_OF_DAY, 0);
				startCal.set(Calendar.MINUTE, 0);
				startCal.set(Calendar.SECOND, 0);
				while ((startCal.getTime().before(end_date))) {
					endSlotCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
					endSlotCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
					endSlotCal.set(Calendar.DATE, startCal.getActualMaximum(Calendar.DATE));
					endSlotCal.set(Calendar.HOUR_OF_DAY, startCal.getActualMaximum(Calendar.HOUR_OF_DAY));
					endSlotCal.set(Calendar.MINUTE, startCal.getActualMaximum(Calendar.MINUTE));
					endSlotCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));
					slots.add(new Pair<Date, Date>(startCal.getTime(), endSlotCal.getTime()));
					startCal = (Calendar) endSlotCal.clone();
					startCal.add(Calendar.SECOND, 1);
				}
			}
			case DAY: {
				startCal.set(Calendar.HOUR_OF_DAY, 0);
				startCal.set(Calendar.MINUTE, 0);
				startCal.set(Calendar.SECOND, 0);
				while ((startCal.getTime().before(end_date))) {
					endSlotCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
					endSlotCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
					endSlotCal.set(Calendar.DATE, startCal.get(Calendar.DATE));
					endSlotCal.set(Calendar.HOUR_OF_DAY, startCal.getActualMaximum(Calendar.HOUR_OF_DAY));
					endSlotCal.set(Calendar.MINUTE, startCal.getActualMaximum(Calendar.MINUTE));
					endSlotCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));
					slots.add(new Pair<Date, Date>(startCal.getTime(), endSlotCal.getTime()));
					startCal = (Calendar) endSlotCal.clone();
					startCal.add(Calendar.SECOND, 1);
				}
			}
			case HOUR: {
				startCal.set(Calendar.MINUTE, 0);
				startCal.set(Calendar.SECOND, 0);
				while ((startCal.getTime().before(end_date))) {
					endSlotCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
					endSlotCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
					endSlotCal.set(Calendar.DATE, startCal.get(Calendar.DATE));
					endSlotCal.set(Calendar.HOUR_OF_DAY, startCal.get(Calendar.HOUR_OF_DAY));
					endSlotCal.set(Calendar.MINUTE, startCal.getActualMaximum(Calendar.MINUTE));
					endSlotCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));
					slots.add(new Pair<Date, Date>(startCal.getTime(), endSlotCal.getTime()));
					startCal = (Calendar) endSlotCal.clone();
					startCal.add(Calendar.SECOND, 1);
				}
			}
			case QHOUR: {
				startCal.set(Calendar.MINUTE, (startCal.get(Calendar.MINUTE) / 15));
				startCal.set(Calendar.SECOND, 0);
				while ((startCal.getTime().before(end_date))) {
					endSlotCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
					endSlotCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
					endSlotCal.set(Calendar.DATE, startCal.get(Calendar.DATE));
					endSlotCal.set(Calendar.HOUR_OF_DAY, startCal.get(Calendar.HOUR_OF_DAY));
					endSlotCal.set(Calendar.MINUTE, (startCal.get(Calendar.MINUTE) / 15) * 15 + 14);  // Round up to the next quarter hour
					endSlotCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));
					slots.add(new Pair<Date, Date>(startCal.getTime(), endSlotCal.getTime()));
					startCal = (Calendar) endSlotCal.clone();
					startCal.add(Calendar.SECOND, 1);
				}
			}
			case MINUTE: {
				startCal.set(Calendar.SECOND, 0);
				while ((startCal.getTime().before(end_date))) {
					endSlotCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
					endSlotCal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
					endSlotCal.set(Calendar.DATE, startCal.get(Calendar.DATE));
					endSlotCal.set(Calendar.HOUR_OF_DAY, startCal.get(Calendar.HOUR_OF_DAY));
					endSlotCal.set(Calendar.MINUTE, (startCal.get(Calendar.MINUTE)));
					endSlotCal.set(Calendar.SECOND, startCal.getActualMaximum(Calendar.SECOND));
					slots.add(new Pair<Date, Date>(startCal.getTime(), endSlotCal.getTime()));
					startCal = (Calendar) endSlotCal.clone();
					startCal.add(Calendar.SECOND, 1);
				}
			}
			default:
				break;
		}
	}
}
