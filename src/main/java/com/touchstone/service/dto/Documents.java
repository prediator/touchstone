package com.touchstone.service.dto;

public class Documents {

	private String $class = "org.touchstone.basic.Documents";
	private String documentsId;
	private String aadhar = "";
	private String license = "";
	private String income = "";
	private Validation validation;

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getDocumentsId() {
		return documentsId;
	}

	public void setDocumentsId(String documentsId) {
		this.documentsId = documentsId;
	}

}