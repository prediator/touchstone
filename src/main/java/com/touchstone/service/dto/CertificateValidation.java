package com.touchstone.service.dto;

/**
 * Validation
 */
public class CertificateValidation {

	private String $class;

	private String validationBy= "";

	private String validationStatus= "";

	private String validationType= "";

	private String validationDate= "";

	private String validationEmail= "";

	private String validationNote= "";

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

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

	@Override
	public String toString() {
		return "CertificateValidation [$class=" + $class + ", validationBy=" + validationBy + ", validationStatus="
				+ validationStatus + ", validationType=" + validationType + ", validationDate=" + validationDate
				+ ", validationEmail=" + validationEmail + ", validationNote=" + validationNote + "]";
	}
}
