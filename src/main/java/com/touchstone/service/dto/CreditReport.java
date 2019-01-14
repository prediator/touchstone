package com.touchstone.service.dto;

public class CreditReport {

	private String $class = "org.touchstone.basic.CreditReport";
	private String creditReportId;
	private String path;
	private String reportdate;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getCreditReportId() {
		return creditReportId;
	}

	public void setCreditReportId(String creditReportId) {
		this.creditReportId = creditReportId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getReportdate() {
		return reportdate;
	}

	public void setReportdate(String reportdate) {
		this.reportdate = reportdate;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
