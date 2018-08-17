package com.touchstone.service.dto;

import java.util.List;

public class HealthCareReport_ {

	private String $class = "org.touchstone.basic.HealthCareReport";
	private String healthCareReportId;
	private String name;
	private String date;
	private List<String> reportReference = null;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getHealthCareReportId() {
		return healthCareReportId;
	}

	public void setHealthCareReportId(String healthCareReportId) {
		this.healthCareReportId = healthCareReportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<String> getReportReference() {
		return reportReference;
	}

	public void setReportReference(List<String> reportReference) {
		this.reportReference = reportReference;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
