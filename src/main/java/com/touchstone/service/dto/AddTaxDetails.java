package com.touchstone.service.dto;

public class AddTaxDetails {

	private String $class = "org.touchstone.basic.addTaxDetails";
	private TaxDetails taxDetails ;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public TaxDetails getTaxDetails() {
		return taxDetails;
	}

	public void setTaxDetails(TaxDetails taxDetails) {
		this.taxDetails = taxDetails;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
