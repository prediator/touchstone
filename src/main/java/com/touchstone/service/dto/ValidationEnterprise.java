package com.touchstone.service.dto;

/**
 * A DTO
 */
public class ValidationEnterprise {

	private String $class;

	private String enterprise;

	private Boolean isEmailValidated;

	public String get$class() {
		return $class;
	}

	public String getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public Boolean getIsEmailValidated() {
		return isEmailValidated;
	}

	public void setIsEmailValidated(Boolean isEmailValidated) {
		this.isEmailValidated = isEmailValidated;
	}

	@Override
	public String toString() {
		return "EmailValidation [$class=" + $class + ", enterprise=" + enterprise + ", isEmailValidated="
				+ isEmailValidated + "]";
	}

}
