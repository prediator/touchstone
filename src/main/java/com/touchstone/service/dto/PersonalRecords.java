package com.touchstone.service.dto;

import java.util.List;

public class PersonalRecords {

	private String $class;
	private String personalId;
	private String user;
	private List<Documents> documents = null;
	private List<TaxDetails> taxDetails = null;
	private List<CreditReport> creditReport = null;
	private List<BankDetails> bankDetails = null;
	private List<PropertyDetails> propertyDetails = null;
	private List<AddIous> ious = null;
	private List<AwardsRecognitions> awardsRecognitions = null;
	private List<InsuranceDetails_> insuranceDetails = null;
	private List<MiscellaneousAssetDetails> miscellaneousAssetDetails = null;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getPersonalId() {
		return personalId;
	}

	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Documents> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Documents> documents) {
		this.documents = documents;
	}

	public List<TaxDetails> getTaxDetails() {
		return taxDetails;
	}

	public void setTaxDetails(List<TaxDetails> taxDetails) {
		this.taxDetails = taxDetails;
	}

	public List<CreditReport> getCreditReport() {
		return creditReport;
	}

	public void setCreditReport(List<CreditReport> creditReport) {
		this.creditReport = creditReport;
	}

	public List<BankDetails> getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(List<BankDetails> bankDetails) {
		this.bankDetails = bankDetails;
	}

	public List<PropertyDetails> getPropertyDetails() {
		return propertyDetails;
	}

	public void setPropertyDetails(List<PropertyDetails> propertyDetails) {
		this.propertyDetails = propertyDetails;
	}

	public List<AddIous> getIous() {
		return ious;
	}

	public void setIous(List<AddIous> ious) {
		this.ious = ious;
	}

	public List<AwardsRecognitions> getAwardsRecognitions() {
		return awardsRecognitions;
	}

	public void setAwardsRecognitions(List<AwardsRecognitions> awardsRecognitions) {
		this.awardsRecognitions = awardsRecognitions;
	}

	public List<InsuranceDetails_> getInsuranceDetails() {
		return insuranceDetails;
	}

	public void setInsuranceDetails(List<InsuranceDetails_> insuranceDetails) {
		this.insuranceDetails = insuranceDetails;
	}

	public List<MiscellaneousAssetDetails> getMiscellaneousAssetDetails() {
		return miscellaneousAssetDetails;
	}

	public void setMiscellaneousAssetDetails(List<MiscellaneousAssetDetails> miscellaneousAssetDetails) {
		this.miscellaneousAssetDetails = miscellaneousAssetDetails;
	}

}