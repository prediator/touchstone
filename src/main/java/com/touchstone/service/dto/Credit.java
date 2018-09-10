package com.touchstone.service.dto;

public class Credit {

	private String $class = "org.touchstone.basic.Credit";
	private String creditId;
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

	public String getCreditId() {
		return creditId;
	}

	public void setCreditId(String creditId) {
		this.creditId = creditId;
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