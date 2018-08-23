
package com.touchstone.service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Personal {

	private String $class ="org.touchstone.basic.PersonalRecords";
	private String personalId;
	private String user;
	
	@JsonInclude(Include.NON_NULL)
	private List<String> documents;
	@JsonInclude(Include.NON_NULL)
	private List<String> taxDetails;
	@JsonInclude(Include.NON_NULL)
	private List<String> creditReport;
	@JsonInclude(Include.NON_NULL)
	private List<String> bankDetails;
	@JsonInclude(Include.NON_NULL)
	private List<String> propertyDetails;
	@JsonInclude(Include.NON_NULL)
	private List<String> ious;
	@JsonInclude(Include.NON_NULL)
	private List<String> awardsRecognitions;
	@JsonInclude(Include.NON_NULL)
	private List<String> insuranceDetails;
	@JsonInclude(Include.NON_NULL)
	private List<String> miscellaneousAssetDetails;

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

	public List<String> getDocuments() {
		return documents;
	}

	public void setDocuments(List<String> documents) {
		this.documents = documents;
	}

	public List<String> getTaxDetails() {
		return taxDetails;
	}

	public void setTaxDetails(List<String> taxDetails) {
		this.taxDetails = taxDetails;
	}

	public List<String> getCreditReport() {
		return creditReport;
	}

	public void setCreditReport(List<String> creditReport) {
		this.creditReport = creditReport;
	}

	public List<String> getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(List<String> bankDetails) {
		this.bankDetails = bankDetails;
	}

	public List<String> getPropertyDetails() {
		return propertyDetails;
	}

	public void setPropertyDetails(List<String> propertyDetails) {
		this.propertyDetails = propertyDetails;
	}

	public List<String> getIous() {
		return ious;
	}

	public void setIous(List<String> ious) {
		this.ious = ious;
	}

	public List<String> getAwardsRecognitions() {
		return awardsRecognitions;
	}

	public void setAwardsRecognitions(List<String> awardsRecognitions) {
		this.awardsRecognitions = awardsRecognitions;
	}

	public List<String> getInsuranceDetails() {
		return insuranceDetails;
	}

	public void setInsuranceDetails(List<String> insuranceDetails) {
		this.insuranceDetails = insuranceDetails;
	}

	public List<String> getMiscellaneousAssetDetails() {
		return miscellaneousAssetDetails;
	}

	public void setMiscellaneousAssetDetails(List<String> miscellaneousAssetDetails) {
		this.miscellaneousAssetDetails = miscellaneousAssetDetails;
	}

}