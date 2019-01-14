package com.touchstone.service.dto;

public class PoliceVerificationDTO {

	private String $class;
	private PoliceVerification policeVerification;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public PoliceVerification getPoliceVerification() {
		return policeVerification;
	}

	public void setPoliceVerification(PoliceVerification policeVerification) {
		this.policeVerification = policeVerification;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}