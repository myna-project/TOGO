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
package it.mynaproject.togo.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import it.mynaproject.togo.api.model.InvoiceItemkWhJson;

@Entity
@Table(name="invoice_item_kwh")
public class InvoiceItemkWh extends BaseDomain {

	@ManyToOne
	@JoinColumn(name="vendor_id")
	private Vendor vendor;

	@ManyToOne
	@JoinColumn(name="drain_id")
	private Drain drain;

	@Column
	private Integer year;

	@Column
	private Integer month;

	@Column(name="f1_energy")
	private Float f1Energy;

	@Column(name="f2_energy")
	private Float f2Energy;

	@Column(name="f3_energy")
	private Float f3Energy;

	@Column(name="interruptibility_remuneration")
	private Float interruptibilityRemuneration;

	@Column(name="production_capacity_availability")
	private Float productionCapacityAvailability;

	@Column(name="grtn_operating_costs")
	private Float grtnOperatingCosts;

	@Column(name="procurement_dispatching_resources")
	private Float procurementDispatchingResources;

	@Column(name="reintegration_temporary_safeguard")
	private Float reintegrationTemporarySafeguard;

	@Column(name="f1_unit_safety_costs")
	private Float f1UnitSafetyCosts;

	@Column(name="f2_unit_safety_costs")
	private Float f2UnitSafetyCosts;

	@Column(name="f3_unit_safety_costs")
	private Float f3UnitSafetyCosts;

	@Column(name="transport_energy")
	private Float transportEnergy;

	@Column(name="transport_energy_equalization")
	private Float transportEnergyEqualization;

	@Column(name="system_charges_energy")
	private Float systemChargesEnergy;

	@Column(name="duty_excise_1")
	private Float dutyExcise1;

	@Column(name="duty_excise_2")
	private Float dutyExcise2;

	@Column(name="duty_excise_3")
	private Float dutyExcise3;

	@Column(name="f1_reactive_energy_33")
	private Float f1ReactiveEnergy33;

	@Column(name="f2_reactive_energy_33")
	private Float f2ReactiveEnergy33;

	@Column(name="f3_reactive_energy_33")
	private Float f3ReactiveEnergy33;

	@Column(name="f1_reactive_energy_75")
	private Float f1ReactiveEnergy75;

	@Column(name="f2_reactive_energy_75")
	private Float f2ReactiveEnergy75;

	@Column(name="f3_reactive_energy_75")
	private Float f3ReactiveEnergy75;

	@Column(name="loss_perc_rate")
	private Float lossPercRate;

	@Column(name="vat_perc_rate")
	private Float vatPercRate;

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public Drain getDrain() {
		return drain;
	}

	public void setDrain(Drain drain) {
		this.drain = drain;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Float getF1Energy() {
		return f1Energy;
	}

	public void setF1Energy(Float f1Energy) {
		this.f1Energy = f1Energy;
	}

	public Float getF2Energy() {
		return f2Energy;
	}

	public void setF2Energy(Float f2Energy) {
		this.f2Energy = f2Energy;
	}

	public Float getF3Energy() {
		return f3Energy;
	}

	public void setF3Energy(Float f3Energy) {
		this.f3Energy = f3Energy;
	}

	public Float getInterruptibilityRemuneration() {
		return interruptibilityRemuneration;
	}

	public void setInterruptibilityRemuneration(Float interruptibilityRemuneration) {
		this.interruptibilityRemuneration = interruptibilityRemuneration;
	}

	public Float getProductionCapacityAvailability() {
		return productionCapacityAvailability;
	}

	public void setProductionCapacityAvailability(Float productionCapacityAvailability) {
		this.productionCapacityAvailability = productionCapacityAvailability;
	}

	public Float getGrtnOperatingCosts() {
		return grtnOperatingCosts;
	}

	public void setGrtnOperatingCosts(Float grtnOperatingCosts) {
		this.grtnOperatingCosts = grtnOperatingCosts;
	}

	public Float getProcurementDispatchingResources() {
		return procurementDispatchingResources;
	}

	public void setProcurementDispatchingResources(Float procurementDispatchingResources) {
		this.procurementDispatchingResources = procurementDispatchingResources;
	}

	public Float getReintegrationTemporarySafeguard() {
		return reintegrationTemporarySafeguard;
	}

	public void setReintegrationTemporarySafeguard(Float reintegrationTemporarySafeguard) {
		this.reintegrationTemporarySafeguard = reintegrationTemporarySafeguard;
	}

	public Float getF1UnitSafetyCosts() {
		return f1UnitSafetyCosts;
	}

	public void setF1UnitSafetyCosts(Float f1UnitSafetyCosts) {
		this.f1UnitSafetyCosts = f1UnitSafetyCosts;
	}

	public Float getF2UnitSafetyCosts() {
		return f2UnitSafetyCosts;
	}

	public void setF2UnitSafetyCosts(Float f2UnitSafetyCosts) {
		this.f2UnitSafetyCosts = f2UnitSafetyCosts;
	}

	public Float getF3UnitSafetyCosts() {
		return f3UnitSafetyCosts;
	}

	public void setF3UnitSafetyCosts(Float f3UnitSafetyCosts) {
		this.f3UnitSafetyCosts = f3UnitSafetyCosts;
	}

	public Float getTransportEnergy() {
		return transportEnergy;
	}

	public void setTransportEnergy(Float transportEnergy) {
		this.transportEnergy = transportEnergy;
	}

	public Float getTransportEnergyEqualization() {
		return transportEnergyEqualization;
	}

	public void setTransportEnergyEqualization(Float transportEnergyEqualization) {
		this.transportEnergyEqualization = transportEnergyEqualization;
	}

	public Float getSystemChargesEnergy() {
		return systemChargesEnergy;
	}

	public void setSystemChargesEnergy(Float systemChargesEnergy) {
		this.systemChargesEnergy = systemChargesEnergy;
	}

	public Float getDutyExcise1() {
		return dutyExcise1;
	}

	public void setDutyExcise1(Float dutyExcise1) {
		this.dutyExcise1 = dutyExcise1;
	}

	public Float getDutyExcise2() {
		return dutyExcise2;
	}

	public void setDutyExcise2(Float dutyExcise2) {
		this.dutyExcise2 = dutyExcise2;
	}

	public Float getDutyExcise3() {
		return dutyExcise3;
	}

	public void setDutyExcise3(Float dutyExcise3) {
		this.dutyExcise3 = dutyExcise3;
	}

	public Float getF1ReactiveEnergy33() {
		return f1ReactiveEnergy33;
	}

	public void setF1ReactiveEnergy33(Float f1ReactiveEnergy33) {
		this.f1ReactiveEnergy33 = f1ReactiveEnergy33;
	}

	public Float getF2ReactiveEnergy33() {
		return f2ReactiveEnergy33;
	}

	public void setF2ReactiveEnergy33(Float f2ReactiveEnergy33) {
		this.f2ReactiveEnergy33 = f2ReactiveEnergy33;
	}

	public Float getF3ReactiveEnergy33() {
		return f3ReactiveEnergy33;
	}

	public void setF3ReactiveEnergy33(Float f3ReactiveEnergy33) {
		this.f3ReactiveEnergy33 = f3ReactiveEnergy33;
	}

	public Float getF1ReactiveEnergy75() {
		return f1ReactiveEnergy75;
	}

	public void setF1ReactiveEnergy75(Float f1ReactiveEnergy75) {
		this.f1ReactiveEnergy75 = f1ReactiveEnergy75;
	}

	public Float getF2ReactiveEnergy75() {
		return f2ReactiveEnergy75;
	}

	public void setF2ReactiveEnergy75(Float f2ReactiveEnergy75) {
		this.f2ReactiveEnergy75 = f2ReactiveEnergy75;
	}

	public Float getF3ReactiveEnergy75() {
		return f3ReactiveEnergy75;
	}

	public void setF3ReactiveEnergy75(Float f3ReactiveEnergy75) {
		this.f3ReactiveEnergy75 = f3ReactiveEnergy75;
	}

	public Float getLossPercRate() {
		return lossPercRate;
	}

	public void setLossPercRate(Float lossPercRate) {
		this.lossPercRate = lossPercRate;
	}

	public Float getVatPercRate() {
		return vatPercRate;
	}

	public void setVatPercRate(Float vatPercRate) {
		this.vatPercRate = vatPercRate;
	}

	public void populateInvoiceItemkWh(InvoiceItemkWhJson input, Drain drain, Vendor vendor) {

		this.setDrain(drain);
		this.setVendor(vendor);
		this.setYear(input.getYear());
		this.setMonth(input.getMonth());
		this.setF1Energy(input.getF1Energy());
		this.setF2Energy(input.getF2Energy());
		this.setF3Energy(input.getF3Energy());
		this.setInterruptibilityRemuneration(input.getInterruptibilityRemuneration());
		this.setProductionCapacityAvailability(input.getProductionCapacityAvailability());
		this.setGrtnOperatingCosts(input.getGrtnOperatingCosts());
		this.setProcurementDispatchingResources(input.getProcurementDispatchingResources());
		this.setReintegrationTemporarySafeguard(input.getReintegrationTemporarySafeguard());
		this.setF1UnitSafetyCosts(input.getF1UnitSafetyCosts());
		this.setF2UnitSafetyCosts(input.getF2UnitSafetyCosts());
		this.setF3UnitSafetyCosts(input.getF3UnitSafetyCosts());
		this.setTransportEnergy(input.getTransportEnergy());
		this.setTransportEnergyEqualization(input.getTransportEnergyEqualization());
		this.setSystemChargesEnergy(input.getSystemChargesEnergy());
		this.setDutyExcise1(input.getDutyExcise1());
		this.setDutyExcise2(input.getDutyExcise2());
		this.setDutyExcise3(input.getDutyExcise3());
		this.setF1ReactiveEnergy33(input.getF1ReactiveEnergy33());
		this.setF2ReactiveEnergy33(input.getF2ReactiveEnergy33());
		this.setF3ReactiveEnergy33(input.getF3ReactiveEnergy33());
		this.setF1ReactiveEnergy75(input.getF1ReactiveEnergy75());
		this.setF2ReactiveEnergy75(input.getF2ReactiveEnergy75());
		this.setF3ReactiveEnergy75(input.getF3ReactiveEnergy75());
		this.setLossPercRate(input.getLossPercRate());
		this.setVatPercRate(input.getVatPercRate());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InvoiceItemkWh [vendor=");
		builder.append((vendor != null) ? vendor.getId() : null);
		builder.append(", drain=");
		builder.append((drain != null) ? drain.getId() : null);
		builder.append(", year=");
		builder.append(year);
		builder.append(", month=");
		builder.append(month);
		builder.append(", f1Energy=");
		builder.append(f1Energy);
		builder.append(", f2Energy=");
		builder.append(f2Energy);
		builder.append(", f3Energy=");
		builder.append(f3Energy);
		builder.append(", interruptibilityRemuneration=");
		builder.append(interruptibilityRemuneration);
		builder.append(", productionCapacityAvailability=");
		builder.append(productionCapacityAvailability);
		builder.append(", grtnOperatingCosts=");
		builder.append(grtnOperatingCosts);
		builder.append(", procurementDispatchingResources=");
		builder.append(procurementDispatchingResources);
		builder.append(", reintegrationTemporarySafeguard=");
		builder.append(reintegrationTemporarySafeguard);
		builder.append(", f1UnitSafetyCosts=");
		builder.append(f1UnitSafetyCosts);
		builder.append(", f2UnitSafetyCosts=");
		builder.append(f2UnitSafetyCosts);
		builder.append(", f3UnitSafetyCosts=");
		builder.append(f3UnitSafetyCosts);
		builder.append(", transportEnergy=");
		builder.append(transportEnergy);
		builder.append(", transportEnergyEqualization=");
		builder.append(transportEnergyEqualization);
		builder.append(", systemChargesEnergy=");
		builder.append(systemChargesEnergy);
		builder.append(", dutyExcise1=");
		builder.append(dutyExcise1);
		builder.append(", dutyExcise2=");
		builder.append(dutyExcise2);
		builder.append(", dutyExcise3=");
		builder.append(dutyExcise3);
		builder.append(", f1ReactiveEnergy33=");
		builder.append(f1ReactiveEnergy33);
		builder.append(", f2ReactiveEnergy33=");
		builder.append(f2ReactiveEnergy33);
		builder.append(", f3ReactiveEnergy33=");
		builder.append(f3ReactiveEnergy33);
		builder.append(", f1ReactiveEnergy75=");
		builder.append(f1ReactiveEnergy75);
		builder.append(", f2ReactiveEnergy75=");
		builder.append(f2ReactiveEnergy75);
		builder.append(", f3ReactiveEnergy75=");
		builder.append(f3ReactiveEnergy75);
		builder.append(", lossPercRate=");
		builder.append(lossPercRate);
		builder.append(", vatPercRate=");
		builder.append(vatPercRate);
		builder.append("]");
		return builder.toString();
	}
}
