package com.touchstone.service.dto;

public class InsuranceDetails_ {
	private String $class = "org.touchstone.basic.InsuranceDetails";
	private String insuranceDetailsId;
	private String path;
	private Boolean template;
	private String type;
	private String maturitydate;
	private String amount;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getInsuranceDetailsId() {
		return insuranceDetailsId;
	}

	public void setInsuranceDetailsId(String insuranceDetailsId) {
		this.insuranceDetailsId = insuranceDetailsId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getTemplate() {
		return template;
	}

	public void setTemplate(Boolean template) {
		this.template = template;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMaturitydate() {
		return maturitydate;
	}

	public void setMaturitydate(String maturitydate) {
		this.maturitydate = maturitydate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
