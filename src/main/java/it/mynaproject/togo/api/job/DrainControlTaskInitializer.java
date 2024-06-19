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
package it.mynaproject.togo.api.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.DrainControl;
import it.mynaproject.togo.api.domain.DrainControlDetail;
import it.mynaproject.togo.api.domain.DrainControlType;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Operation;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.domain.impl.MeasureDouble;
import it.mynaproject.togo.api.model.PairDrainMeasuresJson;
import it.mynaproject.togo.api.service.DrainControlDetailService;
import it.mynaproject.togo.api.service.DrainControlService;
import it.mynaproject.togo.api.service.DrainService;
import it.mynaproject.togo.api.service.FeedService;
import it.mynaproject.togo.api.service.FormulaService;
import it.mynaproject.togo.api.service.MeasureService;
import it.mynaproject.togo.api.util.Mail;
import it.mynaproject.togo.api.util.Pair;

@Component
public class DrainControlTaskInitializer implements InitializingBean, DisposableBean {

	@Autowired
	private DrainControlService drainControlService;

	@Autowired
	private DrainControlDetailService drainControlDetailService;

	@Autowired
	private MeasureService measureService;

	@Autowired
	private FormulaService formulaService;

	@Autowired
	private FeedService feedService;

	@Autowired
	private DrainService drainService;

	@Autowired
	private CronTaskRegistrar cronTaskRegistrar;

	public DrainControlTaskInitializer() {}

	public DrainControlRunnableTask newDrainControlRunnableTask(Integer id) {
		
		return new DrainControlRunnableTask(id);
	}

	@Override
	public void destroy() throws Exception {

		for (DrainControl control : this.drainControlService.getDrainControls(true, "")) {
			DrainControlRunnableTask task = new DrainControlRunnableTask(control.getId());
			cronTaskRegistrar.removeCronTask(task);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		for (DrainControl control : this.drainControlService.getDrainControls(true, "")) {
			DrainControlRunnableTask task = new DrainControlRunnableTask(control.getId());
			cronTaskRegistrar.addCronTask(task, control.getCronSecond() + " " + control.getCronMinute() + " " + control.getCronHour() + " " + control.getCronDayMonth() + " " + control.getCronMonth() + " " + control.getCronDayWeek());
		}
	}

	public class DrainControlRunnableTask implements Runnable {

		private Integer id;

		public DrainControlRunnableTask(Integer id) {
			this.id = id;
		}

		@Override
		public void run() {

			Calendar now = Calendar.getInstance();

			Calendar end = Calendar.getInstance();
			end.set(Calendar.SECOND, 0);
			end.set(Calendar.MILLISECOND, 0);

			Boolean oldAlert = false;
			Boolean newAlert = false;
			Boolean newRecover = false;

			DrainControl control = drainControlService.getDrainControl(id, true, "");

			List<DrainControl> measureDiffControls = new ArrayList<DrainControl>();
			Map<Integer, Feed> feeds = new HashMap<Integer, Feed>();
			Map<Org, Map<Client, List<DrainControlDetail>>> tree = new HashMap<Org, Map<Client, List<DrainControlDetail>>>();
			Map<Org, List<DrainControlDetail>> formulas = new HashMap<Org, List<DrainControlDetail>>();
			Map<Org, Integer> countDetailsForOrg = new HashMap<Org, Integer>();
			Pair<Drain, List<Measure>> lastHourMeasures = new Pair<Drain, List<Measure>>();
			Integer errorCounter = 0;
			for (DrainControlDetail detail : control.getDetails()) {
				if (detail.getActive()) {
					Calendar start = Calendar.getInstance();
					start.setTime(now.getTime());
					start.add(Calendar.MINUTE, -detail.getLastMinutes());
					detail.setStartTime(start);
					detail.setEndTime(end);
					switch (control.getType()) {
						case MISSING:
							Boolean checkLastHourMeasures = measureService.checkMeasuresForDrain(detail.getDrain(), start.getTime(), end.getTime());
							if (!checkLastHourMeasures) {
								if (detail.getError()) {
									oldAlert = true;
								} else {
									detail.setError(true);
									newAlert = true;
								}
								Drain d = drainService.getDrain(detail.getDrain().getId(), Boolean.TRUE, "");
								if (d.getControls() != null) {
									for (DrainControlDetail dcd : d.getControls()) {
										if (dcd.getDrainControl().getType().equals(DrainControlType.MEASUREDIFF) && dcd.getActive() && dcd.getError()) {
											dcd.setError(false);
											drainControlDetailService.update(dcd);
											if (!measureDiffControls.contains(dcd.getDrainControl()))
												measureDiffControls.add(dcd.getDrainControl());
											measureDiffControls.get(measureDiffControls.indexOf(dcd.getDrainControl())).setErrors(measureDiffControls.get(measureDiffControls.indexOf(dcd.getDrainControl())).getErrors() - 1);
										}
									}
								}
							} else {
								if (detail.getError()) {
									newRecover = true;
									detail.setError(false);
								}
							}
							break;

						case MEASUREDIFF:
							lastHourMeasures = measureService.getAllMeasuresFromDrainId(detail.getDrain().getId(), start.getTime(), end.getTime(), Boolean.TRUE, "");
							if (detail.getWaitingMeasures() != null) {
								if (lastHourMeasures.getRight().size() < (detail.getWaitingMeasures() * ((detail.getDelta() != null) ? detail.getDelta()/100 : 1))) {
									detail.setReceivedMeasures(lastHourMeasures.getRight().size());
									Boolean missing = false;
									Drain d = drainService.getDrain(detail.getDrain().getId(), Boolean.TRUE, "");
									if (d.getControls() != null) {
										for (DrainControlDetail dcd : d.getControls()) {
											if (dcd.getDrainControl().getType().equals(DrainControlType.MISSING) && dcd.getActive() && dcd.getError())
												missing = true;
										}
									}
									if (detail.getError()) {
										if (!missing)
											oldAlert = true;
										else
											detail.setError(false);
									} else {
										if (!missing) {
											detail.setError(true);
											newAlert = true;
										}
									}
								} else {
									if (detail.getError()) {
										detail.setError(false);
										newRecover = true;
									}
									if (lastHourMeasures.getRight().size() >= detail.getWaitingMeasures())
										detail.setWaitingMeasures(lastHourMeasures.getRight().size());
								}
							} else {
								if (lastHourMeasures.getRight().size() > 0)
									detail.setWaitingMeasures(lastHourMeasures.getRight().size());
							}
							break;

						case THRESHOLD:
							MeasureDouble lastMeasure = new MeasureDouble();
							String[] drainIds = new String[1];
							ArrayList<Operation> drainOperations = new ArrayList<Operation>();
							ArrayList<MeasureAggregation> measureAggregations = new ArrayList<MeasureAggregation>();
							if (detail.getDrain() != null) {
								drainIds[0] = "d_" + detail.getDrain().getId();
								drainOperations.add(Operation.SEMICOLON);
								measureAggregations.add(detail.getAggregation());
							} else {
								Formula f = formulaService.getFormula(detail.getFormula().getId(), Boolean.TRUE, "");
								drainIds = new String[f.getComponents().size()];
								int i = 0;
								for (FormulaComponent fc : f.getComponents()) {
									drainIds[i] = "d_" + fc.getDrain().getId();
									drainOperations.add(fc.getOperator());
									measureAggregations.add(fc.getAggregation());
									i++;
								}
							}
							List<PairDrainMeasuresJson> lastHourMeasuresJson = measureService.getMeasures(drainIds, drainOperations, measureAggregations, null, null, start.getTime(), end.getTime(), TimeAggregation.ALL, Boolean.TRUE, "");
							if (lastHourMeasuresJson.size() > 0) {
								PairDrainMeasuresJson drainMeasuresJson = lastHourMeasuresJson.get(0);
								if (drainMeasuresJson.getMeasures().size() > 0)
									lastMeasure.setValue((Double) drainMeasuresJson.getMeasures().get(0).getValue());
							}
							if (lastMeasure.getValue() != null) {
								lastMeasure.setValue(new BigDecimal(Double.toString(lastMeasure.getValue())).setScale((detail.getDrain() != null) ? ((detail.getDrain().getDecimals() != null) ? detail.getDrain().getDecimals() : 2) : 2, RoundingMode.HALF_UP).doubleValue());
								Boolean checkThreshold = (((detail.getLowThreshold() != null) && (lastMeasure.getValue() < detail.getLowThreshold())) || ((detail.getHighThreshold() != null) && (lastMeasure.getValue() > detail.getHighThreshold())));
								if (checkThreshold) {
									detail.setLastMeasure(lastMeasure);
									if (detail.getError()) {
										oldAlert = true;
									} else {
										detail.setError(true);
										newAlert = true;
									}
								} else {
									if (detail.getError()) {
										newRecover = true;
										detail.setError(false);
									}
								}
							}
							break;

						default:
							break;
					}

					detail.setLastErrorTime(null);
					if (detail.getError()) {
						errorCounter++;
						detail.setDescription((detail.getDrain() != null) ? detail.getDrain().getName() + (((detail.getDrain().getUnitOfMeasure() != null) && !detail.getDrain().getUnitOfMeasure().equals("")) ? " (" + detail.getDrain().getUnitOfMeasure() + ")" : "") : ((detail.getFormula() != null) ? detail.getFormula().getName() : ""));
						detail.setLastErrorTime(end.getTime());
						if (detail.getDrain() != null) {
							Feed f = feeds.get(detail.getDrain().getFeed().getId());
							if (f == null) {
								f = feedService.getFeed(detail.getDrain().getFeed().getId(), true, "");
								feeds.put(f.getId(), f);
							}
							Client c = null;
							for (Client client : f.getClients()) {
								if ((client.getEnergyClient() != null) && client.getEnergyClient()) {
									c = client;
									break;
								}
							}
							if (c != null) {
								Org o = c.getOrg();
								if (tree.containsKey(o)) {
									Map<Client, List<DrainControlDetail>> clientsForOrg = tree.get(o);
									if (clientsForOrg.containsKey(c)) {
										List<DrainControlDetail> detailsForClient = clientsForOrg.get(c);
										detailsForClient.add(detail);
										clientsForOrg.replace(c, detailsForClient);
										tree.replace(o, clientsForOrg);
									} else {
										List<DrainControlDetail> detailsForClient = new ArrayList<DrainControlDetail>();
										detailsForClient.add(detail);
										clientsForOrg.put(c, detailsForClient);
										tree.replace(o, clientsForOrg);
									}
								} else {
									List<DrainControlDetail> detailsForClient = new ArrayList<DrainControlDetail>();
									detailsForClient.add(detail);
									Map<Client, List<DrainControlDetail>> newClients = new HashMap<Client, List<DrainControlDetail>>();
									newClients.put(c, detailsForClient);
									tree.put(o, newClients);
								}
								Integer detailsForOrg = countDetailsForOrg.get(o);
								if (detailsForOrg != null)
									countDetailsForOrg.replace(o, detailsForOrg + 1);
								else
									countDetailsForOrg.put(o, 1);
							}
						}
						if (detail.getFormula() != null) {
							Org o = detail.getFormula().getOrg();
							if (formulas.containsKey(o)) {
								List<DrainControlDetail> detailsForOrg = formulas.get(o);
								detailsForOrg.add(detail);
								formulas.replace(o, detailsForOrg);
							} else {
								List<DrainControlDetail> detailsForOrg = new ArrayList<DrainControlDetail>();
								detailsForOrg.add(detail);
								formulas.put(o, detailsForOrg);
							}
							Integer detailsForOrg = countDetailsForOrg.get(o);
							if (detailsForOrg != null)
								countDetailsForOrg.replace(o, detailsForOrg + 1);
							else
								countDetailsForOrg.put(o, 1);
						}
					}
					drainControlDetailService.update(detail);
				}
			}

			for (DrainControl dc : measureDiffControls)
				drainControlService.update(dc);

			Calendar last = Calendar.getInstance();
			if (control.getLastMailSentTime() != null) {
				last.setTime(control.getLastMailSentTime());
				last.add(Calendar.DATE, +1);
			}
			if (newRecover || newAlert || (oldAlert && ((control.getLastMailSentTime() == null) || (end.getTime().compareTo(last.getTime()) >= 0)))) {
				StringBuilder subject = new StringBuilder();
				subject.append(((oldAlert || newAlert) ? "ALERT" : "RECOVER") + " [" + Mail.getHostname() + "]: " + control.getName());
				StringBuilder content = new StringBuilder();
				content.append("<html><style>table, th, td { border: 1px solid black; }</style><body><p>Gentile utente,</p>");
				switch (control.getType()) {
					case MISSING:
						if (oldAlert || newAlert) {
							content.append("<p>Le segnaliamo che non sono state registrate misure per " + ((errorCounter == 1) ? "la seguente grandezza" : "le seguenti " + errorCounter + " grandezze") + (((control.getErrors() != null) && (control.getErrors() > 0) && (errorCounter != control.getErrors())) ? " (nella rilevazione precedente erano " + control.getErrors() + ")" : "") + ":</p>");
						} else if (newRecover) {
							content.append("<p>Le segnaliamo che l'arrivo di misure sull'installazione " + Mail.getHostname() + " è stato ripristinato.</p>");
						}
						break;

					case MEASUREDIFF:
						if (oldAlert || newAlert) {
							content.append("<p>Le segnaliamo che è stata riscontrata una diminuzione nel numero di misure registrate per " + ((errorCounter == 1) ? "la seguente grandezza" : "le seguenti " + errorCounter + " grandezze") + (((control.getErrors() != null) && (control.getErrors() > 0) && (errorCounter != control.getErrors())) ? " (nella rilevazione precedente erano " + control.getErrors() + ")" : "") + ":</p>");
						} else if (newRecover) {
							content.append("<p>Le segnaliamo che l'arrivo di misure sull'installazione " + Mail.getHostname() + " è stato ripristinato.</p>");
						}
						break;

					case THRESHOLD:
						if (oldAlert || newAlert) {
							content.append("<p>Le segnaliamo che per " + ((errorCounter == 1) ? "la seguente grandezza" : "le seguenti " + errorCounter + " grandezze") + (((control.getErrors() != null) && (control.getErrors() > 0) && (errorCounter != control.getErrors())) ? " (nella rilevazione precedente erano " + control.getErrors() + ")" : "") + " il valore misurato è fuori dai limiti impostati:</p>");
						} else if (newRecover) {
							content.append("<p>Le segnaliamo che i valori misurati sull'installazione " + Mail.getHostname() + " sono rientrati nei limiti impostati.</p>");
						}
						break;

					default:
						break;
				}
				if (oldAlert || newAlert) {
					content.append(this.getErrorDetails(tree, formulas, countDetailsForOrg, control));
					content.append("<p>La invitiamo a contattare l'assistenza.</p>");
				}
				content.append("<p>Distinti saluti,<br/>Myna-Project.org s.r.l.</p>");
				content.append("<small>Orario di rilevamento: " + ((end.get(Calendar.DAY_OF_MONTH) < 10) ? "0" : "") + end.get(Calendar.DAY_OF_MONTH) + "/" + (((end.get(Calendar.MONTH) + 1) < 10) ? "0" : "") + (end.get(Calendar.MONTH) + 1) + "/" + end.get(Calendar.YEAR) + " alle " + ((end.get(Calendar.HOUR_OF_DAY) < 10) ? "0" : "") + end.get(Calendar.HOUR_OF_DAY) + ":" + ((end.get(Calendar.MINUTE) < 10) ? "0" : "") + end.get(Calendar.MINUTE) + "<br/>");
				content.append("Messaggio automatico. La preghiamo di non rispondere a questa mail.</small></body></html>");
				Boolean mailSent = Mail.sendMail(control.getMailReceivers(), subject.toString(), content.toString());
				if (mailSent && (oldAlert || newAlert))
					control.setLastMailSentTime(end.getTime());
			}
			control.setErrors(errorCounter);
			drainControlService.update(control);
		}

		private StringBuilder getErrorDetails(Map<Org, Map<Client, List<DrainControlDetail>>> tree, Map<Org, List<DrainControlDetail>> formulas, Map<Org, Integer> countDrainsForOrg, DrainControl control) {

			StringBuilder details = new StringBuilder();
			details.append("<table><thead><tr><th>Organizzazione</th><th>Punto di misura/Formula</th><th>Grandezza</th><th>Periodo di controllo</th>" + (control.getType().equals(DrainControlType.MEASUREDIFF) ? "<th>Misure ricevute/attese</th>" : "") + (control.getType().equals(DrainControlType.THRESHOLD) ? "<th>Aggregazione</th><th>Valore</th><th>Limiti</th>" : "") + "</tr></thead><tbody>");
			if (tree.size() > 0) {
				for (Org o : tree.keySet()) {
					details.append("<tr><td rowspan=\"" + countDrainsForOrg.get(o) + "\">" + o.getName() + "</td>");
					Boolean firstClient = true;
					for (Client c : tree.get(o).keySet()) {
						if (!firstClient)
							details.append("<tr>");
						details.append("<td rowspan=\"" + tree.get(o).get(c).size() + "\">" + c.getName() + "</td>");
						Boolean firstDrain = true;
						for (DrainControlDetail d : tree.get(o).get(c)) {
							if (!firstDrain)
								details.append("<tr>");
							details.append(this.getDetailString(d, control));
							firstDrain = false;
						}
						firstClient = false;
					}
					if (formulas.containsKey(o))
						for (DrainControlDetail d : formulas.get(o))
							details.append(this.getDetailString(d, control));
				}
			} else if (formulas.size() > 0) {
				for (Org o : formulas.keySet()) {
					details.append("<tr><td rowspan=\"" + countDrainsForOrg.get(o) + "\">" + o.getName() + "</td>");
					Boolean firstDetail = true;
					for (DrainControlDetail d : formulas.get(o)) {
						if (!firstDetail)
							details.append("<tr>");
						details.append(this.getDetailString(d, control));
						firstDetail = false;
					}
				}
			}
			details.append("</tbody></table>");

			return details;
		}

		private StringBuilder getDetailString(DrainControlDetail d, DrainControl control) {

			StringBuilder detail = new StringBuilder();

			detail.append("<td>" + d.getDescription() + "</td>");
			if (d.getDrain() == null)
				detail.append("<td></td>");
			detail.append("<td>" + ((d.getStartTime().get(Calendar.HOUR_OF_DAY) < 10) ? "0" : "") + d.getStartTime().get(Calendar.HOUR_OF_DAY) + ":" + ((d.getStartTime().get(Calendar.MINUTE) < 10) ? "0" : "") + d.getStartTime().get(Calendar.MINUTE) + " - " + ((d.getEndTime().get(Calendar.HOUR_OF_DAY) < 10) ? "0" : "") + d.getEndTime().get(Calendar.HOUR_OF_DAY) + ":" + ((d.getEndTime().get(Calendar.MINUTE) < 10) ? "0" : "") + d.getEndTime().get(Calendar.MINUTE) + "</td>");
			if (control.getType().equals(DrainControlType.MEASUREDIFF))
				detail.append("<td>" + d.getReceivedMeasures() + "/" + d.getWaitingMeasures() + "</td>");
			if (control.getType().equals(DrainControlType.THRESHOLD)) {
				detail.append("<td>" + d.getAggregation() + "</td>");
				detail.append("<td>" + d.getLastMeasure().getValue() + "</td>");
				detail.append("<td>" + (((d.getLowThreshold() != null) && (d.getHighThreshold() == null)) ? " &gt;= " + d.getLowThreshold() : "") + (((d.getLowThreshold() == null) && (d.getHighThreshold() != null)) ? " &lt;= " + d.getHighThreshold() : "") + (((d.getLowThreshold() != null) && (d.getHighThreshold() != null)) ? d.getLowThreshold() + " - " + d.getHighThreshold() : "") + "</td>");
			}
			detail.append("</tr>");

			return detail;
		}

		@Override
		public String toString() {

			StringBuilder builder = new StringBuilder();
			builder.append("MeasureControlRunnableTask [id=");
			builder.append(id);
			builder.append("]");
			return builder.toString();
		}

		@Override
		public boolean equals(Object o) {

			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			DrainControlRunnableTask that = (DrainControlRunnableTask) o;
			return id.equals(that.id);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
	}
}
