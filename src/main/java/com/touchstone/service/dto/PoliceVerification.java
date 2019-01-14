package com.touchstone.service.dto;

public class PoliceVerification {

	private String $class;
	private String policeVerificationId;
	private String recordNumber;
	private String dateOfReport;
	private String documentReference;
	private String id;
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

	public String getPoliceVerificationId() {
		return policeVerificationId;
	}

	public void setPoliceVerificationId(String policeVerificationId) {
		this.policeVerificationId = policeVerificationId;
	}

	public String getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(String recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getDateOfReport() {
		return dateOfReport;
	}

	public void setDateOfReport(String dateOfReport) {
		this.dateOfReport = dateOfReport;
	}

	public String getDocumentReference() {
		return documentReference;
	}

	public void setDocumentReference(String documentReference) {
		this.documentReference = documentReference;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}