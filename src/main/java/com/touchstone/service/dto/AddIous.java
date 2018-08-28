
package com.touchstone.service.dto;

public class AddIous {

	private String $class = "org.touchstone.basic.addIous";
	private Ious ious;
	private String personalRecords;
	private Validation validation;

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public Ious getIous() {
		return ious;
	}

	public void setIous(Ious ious) {
		this.ious = ious;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
