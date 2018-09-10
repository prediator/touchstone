package com.touchstone.service.dto;

public class CreditDTO {

	private String $class;
	private Credit credit;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public Credit getCredit() {
		return credit;
	}

	public void setCredit(Credit credit) {
		this.credit = credit;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}