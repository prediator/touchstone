package com.touchstone.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class HealthCare {

	private String $class = "org.touchstone.basic.HealthCare";
	private String healthCareId;
	private String bloodGroup;
	private String height;
	private String weight;
	private String sugar;
	private String healthReportTypeName;
	private String bloodPressure;
	private String dateOfReport;
	private Boolean needCheckUpReminder;
	private List<String> reportReference = null;

	private Validation validation;

	public String getHealthReportTypeName() {
		return healthReportTypeName;
	}

	public void setHealthReportTypeName(String healthReportTypeName) {
		this.healthReportTypeName = healthReportTypeName;
	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getHealthCareId() {
		return healthCareId;
	}

	public void setHealthCareId(String healthCareId) {
		this.healthCareId = healthCareId;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getSugar() {
		return sugar;
	}

	public void setSugar(String sugar) {
		this.sugar = sugar;
	}

	public String getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public String getDateOfReport() {
		return dateOfReport;
	}

	public void setDateOfReport(String dateOfReport) {
		this.dateOfReport = dateOfReport;
	}

	public Boolean getNeedCheckUpReminder() {
		return needCheckUpReminder;
	}

	public void setNeedCheckUpReminder(Boolean needCheckUpReminder) {
		this.needCheckUpReminder = needCheckUpReminder;
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
