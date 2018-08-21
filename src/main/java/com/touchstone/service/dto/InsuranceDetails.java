
package com.touchstone.service.dto;

public class InsuranceDetails {

	private String $class;
	private String coveredByOtherMediclaim = "";
	private String hospitalizedInLast4Year = "";
	private String previouslyCoveredMediclaim = "";
	private String ailmentIdReference = "";
	private DateOfCommencementOfFirstInsurance dateOfCommencementOfFirstInsurance;
	private String currentlyCoveredMediclaimcompanyName = "";
	private String policyNumber = "";
	private DateOfAdmission dateOfAdmission;
	private String previouslyCoveredMediclaimcompanyName = "";
	private String diagnosis = "";

	public class DateOfAdmission {

		private Integer year;
		private Integer month;
		private Integer day;

		public Integer getYear() {
			return year;
		}

		public void setYear(Integer year) {
			this.year = year;
		}

		public Integer getMonth() {
			return month;
		}

		public void setMonth(Integer month) {
			this.month = month;
		}

		public Integer getDay() {
			return day;
		}

		public void setDay(Integer day) {
			this.day = day;
		}

	}

	public class DateOfCommencementOfFirstInsurance {

		private Integer year;
		private Integer month;
		private Integer day;

		public Integer getYear() {
			return year;
		}

		public void setYear(Integer year) {
			this.year = year;
		}

		public Integer getMonth() {
			return month;
		}

		public void setMonth(Integer month) {
			this.month = month;
		}

		public Integer getDay() {
			return day;
		}

		public void setDay(Integer day) {
			this.day = day;
		}

	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getCoveredByOtherMediclaim() {
		return coveredByOtherMediclaim;
	}

	public void setCoveredByOtherMediclaim(String coveredByOtherMediclaim) {
		this.coveredByOtherMediclaim = coveredByOtherMediclaim;
	}

	public String getHospitalizedInLast4Year() {
		return hospitalizedInLast4Year;
	}

	public void setHospitalizedInLast4Year(String hospitalizedInLast4Year) {
		this.hospitalizedInLast4Year = hospitalizedInLast4Year;
	}

	public String getPreviouslyCoveredMediclaim() {
		return previouslyCoveredMediclaim;
	}

	public void setPreviouslyCoveredMediclaim(String previouslyCoveredMediclaim) {
		this.previouslyCoveredMediclaim = previouslyCoveredMediclaim;
	}

	public String getAilmentIdReference() {
		return ailmentIdReference;
	}

	public void setAilmentIdReference(String ailmentIdReference) {
		this.ailmentIdReference = ailmentIdReference;
	}

	public DateOfCommencementOfFirstInsurance getDateOfCommencementOfFirstInsurance() {
		return dateOfCommencementOfFirstInsurance;
	}

	public void setDateOfCommencementOfFirstInsurance(
			DateOfCommencementOfFirstInsurance dateOfCommencementOfFirstInsurance) {
		this.dateOfCommencementOfFirstInsurance = dateOfCommencementOfFirstInsurance;
	}

	public String getCurrentlyCoveredMediclaimcompanyName() {
		return currentlyCoveredMediclaimcompanyName;
	}

	public void setCurrentlyCoveredMediclaimcompanyName(String currentlyCoveredMediclaimcompanyName) {
		this.currentlyCoveredMediclaimcompanyName = currentlyCoveredMediclaimcompanyName;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public DateOfAdmission getDateOfAdmission() {
		return dateOfAdmission;
	}

	public void setDateOfAdmission(DateOfAdmission dateOfAdmission) {
		this.dateOfAdmission = dateOfAdmission;
	}

	public String getPreviouslyCoveredMediclaimcompanyName() {
		return previouslyCoveredMediclaimcompanyName;
	}

	public void setPreviouslyCoveredMediclaimcompanyName(String previouslyCoveredMediclaimcompanyName) {
		this.previouslyCoveredMediclaimcompanyName = previouslyCoveredMediclaimcompanyName;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

}