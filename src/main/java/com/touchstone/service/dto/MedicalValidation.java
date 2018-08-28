package com.touchstone.service.dto;

public class MedicalValidation {

	private String validationBy;
	private String email;
	private String description;
	private String type;
	private String slno;
	private String url;
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getValidationBy() {
		return validationBy;
	}

	public void setValidationBy(String validationBy) {
		this.validationBy = validationBy;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSlno() {
		return slno;
	}

	@Override
	public String toString() {
		return "MedicalValidation [validationBy=" + validationBy + ", email=" + email + ", description=" + description
				+ ", type=" + type + ", slno=" + slno + ", url=" + url + "]";
	}

	public void setSlno(String slno) {
		this.slno = slno;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
