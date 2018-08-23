package com.touchstone.service.dto;

public class TaxDetails {

	private String $class = "org.touchstone.basic.TaxDetails";
	private String taxDetailsId;
	private String path;
	private String taxPaid;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getTaxDetailsId() {
		return taxDetailsId;
	}

	public void setTaxDetailsId(String taxDetailsId) {
		this.taxDetailsId = taxDetailsId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTaxPaid() {
		return taxPaid;
	}

	public void setTaxPaid(String taxPaid) {
		this.taxPaid = taxPaid;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
