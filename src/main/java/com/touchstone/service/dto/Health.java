
package com.touchstone.service.dto;

import java.util.List;

public class Health {

	private String $class = "org.touchstone.basic.Health";
	private String healthId;
	private String user;
	private List<String> healthCare = null;
	private List<String> healthCareReport = null;
	private List<String> insuranceClaims = null;
	private List<String> ailment = null;

	public String get$class() {
		return $class;
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

	public List<String> getHealthCare() {
		return healthCare;
	}

	public void setHealthCare(List<String> healthCare) {
		this.healthCare = healthCare;
	}

	public List<String> getHealthCareReport() {
		return healthCareReport;
	}

	public void setHealthCareReport(List<String> healthCareReport) {
		this.healthCareReport = healthCareReport;
	}

	public List<String> getInsuranceClaims() {
		return insuranceClaims;
	}

	public void setInsuranceClaims(List<String> insuranceClaims) {
		this.insuranceClaims = insuranceClaims;
	}

	public List<String> getAilment() {
		return ailment;
	}

	public void setAilment(List<String> ailment) {
		this.ailment = ailment;
	}

}