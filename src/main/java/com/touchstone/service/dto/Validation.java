package com.touchstone.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A DTO
 */
public class Validation {

	private String $class;

	@JsonInclude(Include.NON_NULL)
	private String consumer;

	@JsonInclude(Include.NON_NULL)
	private String enterprise;

	@JsonInclude(Include.NON_NULL)
	private Boolean isEmailValidated;

	@JsonInclude(Include.NON_NULL)
	private Boolean isMobileValidated;

	@JsonInclude(Include.NON_NULL)
	private Boolean isAddressValidated;
	
	@JsonInclude(Include.NON_NULL)
	private String validationBy;
	
	@JsonInclude(Include.NON_NULL)
	private String validationStatus;
	
	@JsonInclude(Include.NON_NULL)
	private String validationType;
	
	@JsonInclude(Include.NON_NULL)
	private String validationDate;
	
	@JsonInclude(Include.NON_NULL)
	private String validationEmail;
	
	@JsonInclude(Include.NON_NULL)
	private String validationNote;
	
	

	public String getValidationBy() {
		return validationBy;
	}

	public void setValidationBy(String validationBy) {
		this.validationBy = validationBy;
	}

	public String getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}

	public String getValidationType() {
		return validationType;
	}

	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}

	public String getValidationDate() {
		return validationDate;
	}

	public void setValidationDate(String validationDate) {
		this.validationDate = validationDate;
	}

	public String getValidationEmail() {
		return validationEmail;
	}

	public void setValidationEmail(String validationEmail) {
		this.validationEmail = validationEmail;
	}

	public String getValidationNote() {
		return validationNote;
	}

	public void setValidationNote(String validationNote) {
		this.validationNote = validationNote;
	}

	public String getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	public Boolean getIsAddressValidated() {
		return isAddressValidated;
	}

	public void setIsAddressValidated(Boolean isAddressValidated) {
		this.isAddressValidated = isAddressValidated;
	}

	public Boolean getIsMobileValidated() {
		return isMobileValidated;
	}

	public void setIsMobileValidated(Boolean isMobileValidated) {
		this.isMobileValidated = isMobileValidated;
	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public Boolean getIsEmailValidated() {
		return isEmailValidated;
	}

	public void setIsEmailValidated(Boolean isEmailValidated) {
		this.isEmailValidated = isEmailValidated;
	}

	@Override
	public String toString() {
		return "EmailValidation [$class=" + $class + ", consumer=" + consumer + ", isEmailValidated=" + isEmailValidated
				+ "]";
	}

}
