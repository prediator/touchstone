
package com.touchstone.service.dto;

import java.util.List;

public class Health {

	private String $class = "org.touchstone.basic.Health";
	private String healthId;
	private String user;
	private List<HealthCare_> healthCare = null;
	private List<HealthCareReport_> healthCareReport = null;
	private List<InsuranceClaim_> insuranceClaim = null;
	private List<Ailment_> ailment = null;
	private List<Allergie_> allergie = null;

	public List<Allergie_> getAllergie() {
		return allergie;
	}

	public void setAllergie(List<Allergie_> allergie) {
		this.allergie = allergie;
	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getHealthId() {
		return healthId;
	}

	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<HealthCare_> getHealthCare() {
		return healthCare;
	}

	public void setHealthCare(List<HealthCare_> healthCare) {
		this.healthCare = healthCare;
	}

	public List<HealthCareReport_> getHealthCareReport() {
		return healthCareReport;
	}

	public void setHealthCareReport(List<HealthCareReport_> healthCareReport) {
		this.healthCareReport = healthCareReport;
	}

	public List<InsuranceClaim_> getInsuranceClaim() {
		return insuranceClaim;
	}

	public void setInsuranceClaim(List<InsuranceClaim_> insuranceClaim) {
		this.insuranceClaim = insuranceClaim;
	}

	public List<Ailment_> getAilment() {
		return ailment;
	}

	public void setAilment(List<Ailment_> ailment) {
		this.ailment = ailment;
	}

}