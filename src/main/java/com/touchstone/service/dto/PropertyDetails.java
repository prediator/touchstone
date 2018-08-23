package com.touchstone.service.dto;

public class PropertyDetails {

	private String $class = "org.touchstone.basic.PropertyDetails";
	private String propertyDetailsId;
	private String path;
	private String type;
	private String address;
	private String area;
	private Validation validation;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getPropertyDetailsId() {
		return propertyDetailsId;
	}

	public void setPropertyDetailsId(String propertyDetailsId) {
		this.propertyDetailsId = propertyDetailsId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Validation getValidation() {
		return validation;
	}

	public void setValidation(Validation validation) {
		this.validation = validation;
	}

}
