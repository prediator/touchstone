package com.touchstone.service.dto;

public class MiscellaneousAssetDetails {

	private String $class = "org.touchstone.basic.MiscellaneousAssetDetails";
	private String miscellaneousAssetDetailsId;
	private String path;
	private Boolean template;
	private String type;
	private String makeAndModel;
	private String cost;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getMiscellaneousAssetDetailsId() {
		return miscellaneousAssetDetailsId;
	}

	public void setMiscellaneousAssetDetailsId(String miscellaneousAssetDetailsId) {
		this.miscellaneousAssetDetailsId = miscellaneousAssetDetailsId;
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

	public String getMakeAndModel() {
		return makeAndModel;
	}

	public void setMakeAndModel(String makeAndModel) {
		this.makeAndModel = makeAndModel;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
