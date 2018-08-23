package com.touchstone.service.dto;

public class AddBankDetails {

	private String $class = "org.touchstone.basic.addBankDetails";
	private BankDetails bankDetails;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public BankDetails getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(BankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
