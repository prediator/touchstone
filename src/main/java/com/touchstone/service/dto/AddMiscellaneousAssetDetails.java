
package com.touchstone.service.dto;

public class AddMiscellaneousAssetDetails {

	private String $class = "org.touchstone.basic.addMiscellaneousAssetDetails";
	private MiscellaneousAssetDetails miscellaneousAssetDetails;
	private String personalRecords;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public MiscellaneousAssetDetails getMiscellaneousAssetDetails() {
		return miscellaneousAssetDetails;
	}

	public void setMiscellaneousAssetDetails(MiscellaneousAssetDetails miscellaneousAssetDetails) {
		this.miscellaneousAssetDetails = miscellaneousAssetDetails;
	}

	public String getPersonalRecords() {
		return personalRecords;
	}

	public void setPersonalRecords(String personalRecords) {
		this.personalRecords = personalRecords;
	}

}
