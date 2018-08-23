package com.touchstone.service.dto;

public class AddAwardsRecognitions {

	private String $class = "org.touchstone.basic.addAwardsRecognitions";
	private AwardsRecognitions awardsRecognitions;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public AwardsRecognitions getAwardsRecognitions() {
		return awardsRecognitions;
	}

	public void setAwardsRecognitions(AwardsRecognitions awardsRecognitions) {
		this.awardsRecognitions = awardsRecognitions;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
