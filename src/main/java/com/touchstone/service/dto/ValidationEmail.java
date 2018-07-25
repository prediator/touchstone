package com.touchstone.service.dto;

public class ValidationEmail {

	private String validationBy;
	private String email;
	private String description;
	private String type;
	private String slno;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSlno() {
		return slno;
	}

	public void setSlno(String slno) {
		this.slno = slno;
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

	@Override
	public String toString() {
		return "ValidationEmail [validationBy=" + validationBy + ", email=" + email + ", description=" + description
				+ ", type=" + type + ", slno=" + slno + "]";
	}

}
