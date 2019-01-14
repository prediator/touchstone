package com.touchstone.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * tax
 */

@Document(collection = "personal")
public class Personal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String license;

	private String aadhar;

	private String taxno;

	private String userId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getTaxno() {
		return taxno;
	}

	public void setTaxno(String taxno) {
		this.taxno = taxno;
	}

	@Override
	public String toString() {
		return "Personal [license=" + license + ", aadhar=" + aadhar + ", taxno=" + taxno + "]";
	}

}
