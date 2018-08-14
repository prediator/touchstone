package com.touchstone.service.dto;

import java.util.List;

public class InsuranceClaim_ {

	private String $class = "org.touchstone.basic.InsuranceClaim";
	private String insuranceClaimId;
	private Boolean currentlyCovered;
	private String companyName;
	private Boolean hospitalisedInLast;
	private Boolean previouslyCovered;
	private String previouslyCoveredInsuranceName;
	private String diagnosis;
	private String dateOfInsurance;
	private String policyNo;
	private String dateOfAdmition;
	private List<String> claimreports = null;
	private List<String> ailment = null;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getInsuranceClaimId() {
		return insuranceClaimId;
	}

	public void setInsuranceClaimId(String insuranceClaimId) {
		this.insuranceClaimId = insuranceClaimId;
	}

	public Boolean getCurrentlyCovered() {
		return currentlyCovered;
	}

	public void setCurrentlyCovered(Boolean currentlyCovered) {
		this.currentlyCovered = currentlyCovered;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Boolean getHospitalisedInLast() {
		return hospitalisedInLast;
	}

	public void setHospitalisedInLast(Boolean hospitalisedInLast) {
		this.hospitalisedInLast = hospitalisedInLast;
	}

	public Boolean getPreviouslyCovered() {
		return previouslyCovered;
	}

	public void setPreviouslyCovered(Boolean previouslyCovered) {
		this.previouslyCovered = previouslyCovered;
	}

	public String getPreviouslyCoveredInsuranceName() {
		return previouslyCoveredInsuranceName;
	}

	public void setPreviouslyCoveredInsuranceName(String previouslyCoveredInsuranceName) {
		this.previouslyCoveredInsuranceName = previouslyCoveredInsuranceName;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getDateOfInsurance() {
		return dateOfInsurance;
	}

	public void setDateOfInsurance(String dateOfInsurance) {
		this.dateOfInsurance = dateOfInsurance;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getDateOfAdmition() {
		return dateOfAdmition;
	}

	public void setDateOfAdmition(String dateOfAdmition) {
		this.dateOfAdmition = dateOfAdmition;
	}

	public List<String> getClaimreports() {
		return claimreports;
	}

	public void setClaimreports(List<String> claimreports) {
		this.claimreports = claimreports;
	}

	public List<String> getAilment() {
		return ailment;
	}

	public void setAilment(List<String> ailment) {
		this.ailment = ailment;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
