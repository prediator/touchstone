
package com.touchstone.service.dto;

public class AddCreditReport {

	private String $class = "org.touchstone.basic.addCreditReport";
	private CreditReport creditReport;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public CreditReport getCreditReport() {
		return creditReport;
	}

	public void setCreditReport(CreditReport creditReport) {
		this.creditReport = creditReport;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
