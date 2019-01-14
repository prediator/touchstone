package com.touchstone.service.dto;

public class AddInsuranceDetails {

	private String $class = "org.touchstone.basic.addInsuranceDetails";
	private InsuranceDetails_ insuranceDetails;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public InsuranceDetails_ getInsuranceDetails() {
		return insuranceDetails;
	}

	public void setInsuranceDetails(InsuranceDetails_ insuranceDetails) {
		this.insuranceDetails = insuranceDetails;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
