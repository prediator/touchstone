
package com.touchstone.service.dto;

public class HealthCareReport {

	private String $class = "org.touchstone.basic.addHealthCareReport";
	private HealthCareReport_ healthCareReport;
	private String health;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public HealthCareReport_ getHealthCareReport() {
		return healthCareReport;
	}

	public void setHealthCareReport(HealthCareReport_ healthCareReport) {
		this.healthCareReport = healthCareReport;
	}

	public String getHealth() {
		return health;
	}

	public void setHealth(String health) {
		this.health = health;
	}

}
