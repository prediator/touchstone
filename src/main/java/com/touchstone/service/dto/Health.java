
package com.touchstone.service.dto;

import java.util.List;

public class Health {

	private String $class = "org.touchstone.basic.health";
	private String healthId;
	private String user;
	private List<Object> healthCare = null;
	private List<Object> healthCareReport = null;
	private List<Object> insuranceClaims = null;
	private List<Object> ailment = null;

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

	public List<Object> getHealthCare() {
		return healthCare;
	}

	public void setHealthCare(List<Object> healthCare) {
		this.healthCare = healthCare;
	}

	public List<Object> getHealthCareReport() {
		return healthCareReport;
	}

	public void setHealthCareReport(List<Object> healthCareReport) {
		this.healthCareReport = healthCareReport;
	}

	public List<Object> getInsuranceClaims() {
		return insuranceClaims;
	}

	public void setInsuranceClaims(List<Object> insuranceClaims) {
		this.insuranceClaims = insuranceClaims;
	}

	public List<Object> getAilment() {
		return ailment;
	}

	public void setAilment(List<Object> ailment) {
		this.ailment = ailment;
	}

}