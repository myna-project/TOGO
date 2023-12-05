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

import it.mynaproject.togo.api.domain.Client;
import it.mynaproject.togo.api.domain.ClientCategory;
import it.mynaproject.togo.api.domain.Dashboard;
import it.mynaproject.togo.api.domain.DashboardsUsers;
import it.mynaproject.togo.api.domain.DashboardWidget;
import it.mynaproject.togo.api.domain.DashboardWidgetDetail;
import it.mynaproject.togo.api.domain.Drain;
import it.mynaproject.togo.api.domain.DrainControl;
import it.mynaproject.togo.api.domain.DrainControlDetail;
import it.mynaproject.togo.api.domain.Feed;
import it.mynaproject.togo.api.domain.Formula;
import it.mynaproject.togo.api.domain.FormulaComponent;
import it.mynaproject.togo.api.domain.Index;
import it.mynaproject.togo.api.domain.IndexComponent;
import it.mynaproject.togo.api.domain.IndexGroup;
import it.mynaproject.togo.api.domain.InvoiceItemkWh;
import it.mynaproject.togo.api.domain.Job;
import it.mynaproject.togo.api.domain.Measure;
import it.mynaproject.togo.api.domain.MeasureAggregation;
import it.mynaproject.togo.api.domain.Operation;
import it.mynaproject.togo.api.domain.Org;
import it.mynaproject.togo.api.domain.Role;
import it.mynaproject.togo.api.domain.TimeAggregation;
import it.mynaproject.togo.api.domain.TimeSlot;
import it.mynaproject.togo.api.domain.TimeSlotDetail;
import it.mynaproject.togo.api.domain.User;
import it.mynaproject.togo.api.domain.Vendor;
import it.mynaproject.togo.api.util.Pair;

import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.util.TreeMap;
import java.util.Date;

public class JsonUtil {

	static public List<OrgJson> orgToOrgJsonHierarchically(final List<Org> list) {

		List<OrgJson> roots = new ArrayList<>();

		TreeMap<Integer, OrgJson> tmp = new TreeMap<>();

		for (Org org : list) {
			OrgJson oj = orgToOrgJson(org);
			tmp.put(org.getId(), oj);
		}

		for (Org org : list) {
			OrgJson oj = tmp.get(org.getId());

			if (org.getParent() != null) {
				if (!list.contains(org.getParent())) {
					if (!roots.contains(oj))
						roots.add(oj);
				} else {
					tmp.get(org.getParent().getId()).addChild(oj);
				}
			} else {
				if (!roots.contains(oj))
					roots.add(oj);
			}
		}

		return roots;
	}

	static public OrgJson orgToOrgJson(final Org org) {

		final OrgJson oj = new OrgJson();
		oj.setId(org.getId());
		oj.setName(org.getName());
		oj.setParentId((org.getParent() != null) ? org.getParent().getId() : null);

		return oj;
	}

	public static JobJson jobToJobJson(final Job j) {

		final JobJson jj = new JobJson();
		jj.setId(j.getId());
		jj.setName(j.getName());
		jj.setDescription(j.getDescription());
		jj.setOrgId((j.getOrg() != null) ? j.getOrg().getId() : null);

		return jj;
	}

	public static ClientCategoryJson clientCategoryToClientCategoryJson(final ClientCategory c) {

		final ClientCategoryJson cj = new ClientCategoryJson();
		cj.setId(c.getId());
		cj.setDescription(c.getDescription());
		if (c.getImage() != null)
			cj.setImage(Base64.getEncoder().encodeToString(c.getImage()));

		return cj;
	}

	public static ClientJson clientToClientJson(final Client c, Boolean returnImage) {

		final ClientJson clientJson = new ClientJson();
		clientJson.setId(c.getId());
		clientJson.setName(c.getName());
		if (c.getCategory() != null)
			clientJson.setCategoryId(c.getCategory().getId());
		clientJson.setComputerClient(c.getComputerClient());
		clientJson.setEnergyClient(c.getEnergyClient());
		if (c.getParent() != null)
			clientJson.setParentId(c.getParent().getId());
		if (c.getController() != null)
			clientJson.setControllerId(c.getController().getId());
		clientJson.setOrgId((c.getOrg() != null) ? c.getOrg().getId() : null);
		clientJson.setType(c.getType());
		if (c.getDeviceId() != null)
			clientJson.setDeviceId(c.getDeviceId());
		if (c.getPluginId() != null)
			clientJson.setPluginId(c.getPluginId());
		if (returnImage && (c.getImage() != null))
			clientJson.setImage(Base64.getEncoder().encodeToString(c.getImage()));
		clientJson.setActive(c.getActive());
		clientJson.setWarning(false);
		clientJson.setAlarm(false);
		clientJson.setAlert(false);

		List<Integer> childIds = new ArrayList<>();
		for (Client child : c.getChildList())
			childIds.add(child.getId());
		clientJson.setChildIds(childIds);

		List<Integer> controlledIds = new ArrayList<>();
		for (Client controlled : c.getControlledList())
			controlledIds.add(controlled.getId());
		clientJson.setControlledIds(controlledIds);

		List<Integer> feedIds = new ArrayList<>();
		List<Integer> defaultDrainIds = new ArrayList<>();
		for (Feed f : c.getFeeds()) {
			feedIds.add(f.getId());

			for (Drain d : f.getDrains()) {
				if (d.getClientDefaultDrain())
					defaultDrainIds.add(d.getId());

				for (DrainControlDetail cd : d.getControls()) {
					if (cd.getActive() && cd.getError() && (cd.getDrainControl() != null)) {
						switch (cd.getDrainControl().getType()) {
							case MISSING:
								clientJson.setAlarm(true);
								break;

							case MEASUREDIFF:
								clientJson.setWarning(true);
								break;

							case THRESHOLD:
								clientJson.setAlert(true);
								break;

							default:
								break;
						}
					}
				}
			}
		}
		clientJson.setFeedIds(feedIds);
		clientJson.setDefaultDrainIds(defaultDrainIds);

		List<Integer> formulaIds = new ArrayList<>();
		for (Formula formula : c.getFormulas())
			formulaIds.add(formula.getId());
		clientJson.setFormulaIds(formulaIds);

		return clientJson;
	}

	public static UserJson userToUserJson(final User u) {

		final UserJson uj = new UserJson();
		uj.setId(u.getId());
		uj.setUsername(u.getUsername());
		uj.setName(u.getName());
		uj.setSurname(u.getSurname());
		uj.setStyle(u.getStyle());
		uj.setLang(u.getLang());
		uj.setEnabled(u.getEnabled() == 1);
		uj.setEmail(u.getEmail());
		uj.setDefaultStart(u.getDefaultStart());
		uj.setDefaultEnd(u.getDefaultEnd());
		uj.setDrainTreeDepth(u.getDrainTreeDepth());
		if (u.getAvatar() != null)
			uj.setAvatar(Base64.getEncoder().encodeToString(u.getAvatar()));
		List<Integer> rolesId = new ArrayList<>();
		for (Role r : u.getRoles())
			rolesId.add(r.getId());
		uj.setRoleIds(rolesId);
		List<Integer> jobsId = new ArrayList<>();
		for (Job j : u.getJobs())
			jobsId.add(j.getId());
		uj.setJobIds(jobsId);
		List<Integer> dashboardsId = new ArrayList<>();
		for (DashboardsUsers du: u.getDashboardsUsers()) {
			dashboardsId.add(du.getDashboard().getId());
			if (du.getDefaultDashboard())
				uj.setDefaultDashboardId(du.getDashboard().getId());
		}
		uj.setDashboardIds(dashboardsId);
		return uj;
	}

	public static RoleJson roleToRoleJson(final Role r) {

		final RoleJson rj = new RoleJson();
		rj.setId(r.getId());
		rj.setName(r.getName());
		rj.setDescription(r.getDescription());

		List<String> sr = new ArrayList<>();
		for (User u : r.getUsers())
			sr.add(u.getUsername());
		rj.setUsers(sr);

		return rj;
	}

	public static FeedJson feedToFeedJson(Feed f) {

		final FeedJson fj = new FeedJson();
		fj.setId(f.getId());
		fj.setDescription(f.getDescription());
		for (Client c : f.getClients())
			fj.addClientId(c.getId());

		return fj;
	}

	public static DrainJson drainToDrainJson(Drain d) {

		final DrainJson drainJson = new DrainJson();
		drainJson.setId(d.getId());
		drainJson.setFeedId((d.getFeed() != null) ? d.getFeed().getId() : null);
		drainJson.setName(d.getName());
		drainJson.setMeasureId(d.getMeasureId());
		drainJson.setUnitOfMeasure(d.getUnitOfMeasure());
		drainJson.setType(d.getType());
		drainJson.setMeasureType(d.getMeasureType());
		drainJson.setDecimals(d.getDecimals());
		drainJson.setClientDefaultDrain(d.getClientDefaultDrain());
		drainJson.setPositiveNegativeValue(d.getPositiveNegativeValue());
		drainJson.setBaseDrainId((d.getBaseDrain() != null) ? d.getBaseDrain().getId() : null);
		drainJson.setDiffDrainId((d.getDiffDrain() != null) ? d.getDiffDrain().getId() : null);
		drainJson.setCoefficient(d.getCoefficient());
		List<DrainControlDetailJson> controlDetailsJson = new ArrayList<DrainControlDetailJson>();
		for (DrainControlDetail detail : d.getControls())
			controlDetailsJson.add(drainControlDetailToDrainControlDetailJson(detail));
		drainJson.setControls(controlDetailsJson);

		return drainJson;
	}

	public static MeasureJson measureToMeasureJson(Measure m) {

		final MeasureJson mj = new MeasureJson();
		mj.setAt(m.getTime());
		mj.setDrainId((m.getDrain() != null) ? m.getDrain().getId() : null);
		mj.setValue((m.getValue() != null) ? m.getValue().toString() : null);

		return mj;
	}

	public static PairDrainMeasuresJson pairDrainMeasureToPairDrainMeasureJson(Pair<Drain, List<Measure>> measureList, TimeAggregation timeAggregation, Date start, Date end) {

		final PairDrainMeasuresJson pdmj = new PairDrainMeasuresJson(timeAggregation, start, end);
		pdmj.setDrainId(measureList.getLeft().getId());
		pdmj.setDrainName(measureList.getLeft().getName());
		pdmj.setUnit(measureList.getLeft().getUnitOfMeasure());
		pdmj.setType(measureList.getLeft().getType());
		pdmj.setMeasureType(measureList.getLeft().getMeasureType());
		if (measureList.getRight() != null)
			for (Measure m : measureList.getRight())
				pdmj.addValue(m.getValue(), m.getTime());

		return pdmj;
	}

	public static FormulaJson formulaToFormulaJson(Formula f) {

		final FormulaJson fj = new FormulaJson();
		fj.setId(f.getId());
		fj.setName(f.getName());
		fj.setOrgId((f.getOrg() != null) ? f.getOrg().getId() : null);
		fj.setClientId((f.getClient() != null) ? f.getClient().getId() : null);
		List<Integer> comps = new ArrayList<Integer>();
		List<MeasureAggregation> aggrs = new ArrayList<MeasureAggregation>();
		List<Operation> opers = new ArrayList<Operation>();
		List<String> legends = new ArrayList<String>();
		List<String> positiveNegativeValues = new ArrayList<String>();
		for (FormulaComponent fc : f.getComponents()) {
			comps.add(fc.getDrain().getId());
			opers.add(fc.getOperator());
			aggrs.add(fc.getAggregation());
			legends.add(fc.getLegend());
			positiveNegativeValues.add(fc.getPositiveNegativeValue());
		}
		fj.setComponents(comps);
		fj.setPositiveNegativeValues(positiveNegativeValues);
		fj.setAggregations(aggrs);
		fj.setOperators(opers);
		fj.setLegends(legends);

		return fj;
	}

	public static VendorJson vendorToVendorJson(Vendor v) {

		final VendorJson vj = new VendorJson();
		vj.setId(v.getId());
		vj.setName(v.getName());

		return vj;
	}

	public static TimeSlotJson timeSlotToTimeSlotJson(TimeSlot t) {

		final TimeSlotJson tsj = new TimeSlotJson();
		tsj.setId(t.getId());
		tsj.setName(t.getName());

		List<TimeSlotDetailJson> details = new ArrayList<TimeSlotDetailJson>();
		for (TimeSlotDetail td : t.getDetails()) {
			TimeSlotDetailJson tdJson = new TimeSlotDetailJson();
			tdJson.setId(td.getId());
			tdJson.setDayOfWeek(td.getDayOfWeek());
			tdJson.setStartTime((td.getStartTime() != null) ? td.getStartTime().toString() : null);
			tdJson.setEndTime((td.getEndTime() != null) ? td.getEndTime().toString() : null);
			details.add(tdJson);
		}
		tsj.setDetails(details);

		return tsj;
	}

	public static TimeSlotDetailJson timeSlotDetailToTimeSlotDetailJson(TimeSlotDetail td) {

		final TimeSlotDetailJson tsdj = new TimeSlotDetailJson();
		tsdj.setId(td.getId());
		tsdj.setTimeSlotId((td.getTimeSlot() != null) ? td.getTimeSlot().getId() : null);
		tsdj.setDayOfWeek(td.getDayOfWeek());
		tsdj.setStartTime((td.getStartTime() != null) ? td.getStartTime().toString() : null);
		tsdj.setEndTime((td.getEndTime() != null) ? td.getEndTime().toString() : null);

		return tsdj;
	}

	public static InvoiceItemkWhJson invoiceItemkWhToinvoiceItemkWhJson(InvoiceItemkWh i) {

		final InvoiceItemkWhJson iikj = new InvoiceItemkWhJson();
		iikj.setId(i.getId());
		iikj.setDrainId((i.getDrain() != null) ? i.getDrain().getId() : null);
		iikj.setVendorId((i.getVendor() != null) ? i.getVendor().getId() : null);
		iikj.setYear(i.getYear());
		iikj.setMonth(i.getMonth());
		iikj.setF1Energy(i.getF1Energy());
		iikj.setF2Energy(i.getF2Energy());
		iikj.setF3Energy(i.getF3Energy());
		iikj.setInterruptibilityRemuneration(i.getInterruptibilityRemuneration());
		iikj.setProductionCapacityAvailability(i.getProductionCapacityAvailability());
		iikj.setGrtnOperatingCosts(i.getGrtnOperatingCosts());
		iikj.setProcurementDispatchingResources(i.getProcurementDispatchingResources());
		iikj.setReintegrationTemporarySafeguard(i.getReintegrationTemporarySafeguard());
		iikj.setF1UnitSafetyCosts(i.getF1UnitSafetyCosts());
		iikj.setF2UnitSafetyCosts(i.getF2UnitSafetyCosts());
		iikj.setF3UnitSafetyCosts(i.getF3UnitSafetyCosts());
		iikj.setTransportEnergy(i.getTransportEnergy());
		iikj.setTransportEnergyEqualization(i.getTransportEnergyEqualization());
		iikj.setSystemChargesEnergy(i.getSystemChargesEnergy());
		iikj.setDutyExcise1(i.getDutyExcise1());
		iikj.setDutyExcise2(i.getDutyExcise2());
		iikj.setDutyExcise3(i.getDutyExcise3());
		iikj.setF1ReactiveEnergy33(i.getF1ReactiveEnergy33());
		iikj.setF2ReactiveEnergy33(i.getF2ReactiveEnergy33());
		iikj.setF3ReactiveEnergy33(i.getF3ReactiveEnergy33());
		iikj.setF1ReactiveEnergy75(i.getF1ReactiveEnergy75());
		iikj.setF2ReactiveEnergy75(i.getF2ReactiveEnergy75());
		iikj.setF3ReactiveEnergy75(i.getF3ReactiveEnergy75());
		iikj.setLossPercRate(i.getLossPercRate());
		iikj.setVatPercRate(i.getVatPercRate());

		return iikj;
	}

	public static IndexJson indexToIndexJson(Index index) {

		final IndexJson indexJson = new IndexJson();
		ArrayList<FormulaElementJson> feList = new ArrayList<FormulaElementJson>();
		List<Operation> opList = new ArrayList<Operation>();

		indexJson.setId(index.getId());
		indexJson.setName(index.getName());
		indexJson.setOrgId(index.getOrg().getId());
		if (index.getGroup() != null)
			indexJson.setGroup(indexGroupToIndexGroupJson(index.getGroup()));
		indexJson.setCoefficient(index.getCoefficient());
		indexJson.setUnitOfMeasure(index.getUnitOfMeasure());
		indexJson.setDecimals(index.getDecimals());
		indexJson.setMinValue(index.getMinValue());
		indexJson.setMaxValue(index.getMaxValue());
		indexJson.setWarningValue(index.getWarningValue());
		indexJson.setAlarmValue(index.getAlarmValue());
		List<MeasureJson> results = new ArrayList<MeasureJson>();
		if (index.getResult() != null) 
			for (Measure m : index.getResult())
				results.add(measureToMeasureJson(m));
		indexJson.setResult(results);
		for (IndexComponent ic : index.getComponents()) {
			FormulaElementJson fe = new FormulaElementJson();
			fe.setFormulaId(ic.getFormula().getId());
			fe.setFormulaName(ic.getFormula().getName());
			fe.setNSkip(ic.getNSkip());
			fe.setSkipPeriod(ic.getSkipPeriod());
			feList.add(fe);
			opList.add(ic.getOperator());
		}
		indexJson.setFormulaElements(feList);
		indexJson.setOperators(opList);

		return indexJson;
	}

	public static IndexGroupJson indexGroupToIndexGroupJson(IndexGroup group) {

		final IndexGroupJson indexGroupJson = new IndexGroupJson();
		indexGroupJson.setId(group.getId());
		indexGroupJson.setName(group.getName());
		if (group.getOrg() != null)
			indexGroupJson.setOrgId(group.getOrg().getId());

		return indexGroupJson;
	}

	public static DrainControlJson drainControlToDrainControlJson(DrainControl control) {

		final DrainControlJson controlJson = new DrainControlJson();
		controlJson.setId(control.getId());
		controlJson.setName(control.getName());
		if (control.getOrg() != null)
			controlJson.setOrgId(control.getOrg().getId());
		controlJson.setType(control.getType());
		controlJson.setCronSecond(control.getCronSecond());
		controlJson.setCronMinute(control.getCronMinute());
		controlJson.setCronHour(control.getCronHour());
		controlJson.setCronDayMonth(control.getCronDayMonth());
		controlJson.setCronDayWeek(control.getCronDayWeek());
		controlJson.setCronMonth(control.getCronMonth());
		controlJson.setMailReceivers(control.getMailReceivers());
		controlJson.setErrors(control.getErrors());
		controlJson.setLastMailSentTime(control.getLastMailSentTime());
		List<DrainControlDetailJson> controlDetailsJson = new ArrayList<DrainControlDetailJson>();
		for (DrainControlDetail detail : control.getDetails())
			controlDetailsJson.add(drainControlDetailToDrainControlDetailJson(detail));
		controlJson.setDetails(controlDetailsJson);

		return controlJson;
	}

	public static DrainControlDetailJson drainControlDetailToDrainControlDetailJson(DrainControlDetail detail) {

		final DrainControlDetailJson detailJson = new DrainControlDetailJson();
		if (detail.getDrainControl() != null)
			detailJson.setType(detail.getDrainControl().getType());
		if (detail.getDrain() != null)
			detailJson.setDrainId(detail.getDrain().getId());
		if (detail.getFormula() != null)
			detailJson.setFormulaId(detail.getFormula().getId());
		detailJson.setLastMinutes(detail.getLastMinutes());
		detailJson.setAggregation(detail.getAggregation());
		detailJson.setLowThreshold(detail.getLowThreshold());
		detailJson.setHighThreshold(detail.getHighThreshold());
		detailJson.setDelta(detail.getDelta());
		detailJson.setActive(detail.getActive());
		detailJson.setWaitingMeasures(detail.getWaitingMeasures());
		detailJson.setError(detail.getError());
		detailJson.setLastErrorTime(detail.getLastErrorTime());

		return detailJson;
	}

	public static DashboardJson dashboardToDashboardJson(Dashboard d) {

		final DashboardJson dj = new DashboardJson();
		dj.setId(d.getId());
		dj.setName(d.getName());
		dj.setOrgId((d.getOrg() != null) ? d.getOrg().getId() : null);
		List<Integer> userIds = new ArrayList<>();
		for (DashboardsUsers du: d.getDashboardsUsers())
			userIds.add(du.getUser().getId());
		dj.setUserIds(userIds);

		return dj;
	}

	public static DashboardWidgetJson dashboardWidgetToDashboardWidgetJson(DashboardWidget widget) {

		final DashboardWidgetJson widgetJson = new DashboardWidgetJson();
		widgetJson.setId(widget.getId());
		widgetJson.setnCols(widget.getnCols());
		widgetJson.setnRows(widget.getnRows());
		widgetJson.setxPos(widget.getxPos());
		widgetJson.setyPos(widget.getyPos());
		widgetJson.setWidgetType(widget.getWidgetType());
		widgetJson.setCostsDrainId((widget.getCostsDrain() != null) ? widget.getCostsDrain().getId() : null);
		widgetJson.setCostsAggregation(widget.getCostsAggregation());
		widgetJson.setIntervalSeconds(widget.getIntervalSeconds());
		widgetJson.setTitle(widget.getTitle());
		widgetJson.setBackgroundColor(widget.getBackgroundColor());
		widgetJson.setNumberPeriods(widget.getNumberPeriods());
		widgetJson.setPeriod(widget.getPeriod());
		widgetJson.setStartTime(widget.getStartTime());
		widgetJson.setEndTime(widget.getEndTime());
		widgetJson.setLegend(widget.getLegend());
		widgetJson.setLegendPosition(widget.getLegendPosition());
		widgetJson.setLegendLayout(widget.getLegendLayout());
		widgetJson.setNavigator(widget.getNavigator());
		widgetJson.setTimeAggregation(widget.getTimeAggregation());
		widgetJson.setMinValue(widget.getMinValue());
		widgetJson.setMaxValue(widget.getMaxValue());
		widgetJson.setWarningValue(widget.getWarningValue());
		widgetJson.setAlarmValue(widget.getAlarmValue());
		widgetJson.setColor1(widget.getColor1());
		widgetJson.setColor2(widget.getColor2());
		widgetJson.setColor3(widget.getColor3());
		List<DashboardWidgetDetailJson> widgetDetailsJson = new ArrayList<DashboardWidgetDetailJson>();
		for (DashboardWidgetDetail detail : widget.getDetails())
			widgetDetailsJson.add(dashboardWidgetDetailToDashboardWidgetDetailJson(detail));
		widgetJson.setDetails(widgetDetailsJson);

		return widgetJson;
	}

	public static DashboardWidgetDetailJson dashboardWidgetDetailToDashboardWidgetDetailJson(DashboardWidgetDetail detail) {

		final DashboardWidgetDetailJson detailJson = new DashboardWidgetDetailJson();
		if (detail.getIndex() != null)
			detailJson.setIndexId(detail.getIndex().getId());
		if (detail.getDrain() != null)
			detailJson.setDrainId(detail.getDrain().getId());
		if (detail.getFormula() != null)
			detailJson.setFormulaId(detail.getFormula().getId());
		if (detail.getDrainControl() != null)
			detailJson.setDrainControlId(detail.getDrainControl().getId());
		detailJson.setAggregation(detail.getAggregation());
		detailJson.setOperator(detail.getOperator());
		detailJson.setPositiveNegativeValue((detail.getPositiveNegativeValue() != null) ? detail.getPositiveNegativeValue() : "");

		return detailJson;
	}
}
