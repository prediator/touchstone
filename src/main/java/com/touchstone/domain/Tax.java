package com.touchstone.domain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * tax
 */

@Document(collection = "tax")
public class Tax implements Serializable {

	private static final long serialVersionUID = 1L;

	private String path;

	private String tax_paid;
	
	private String accountYear;

	private String userId;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTax_paid() {
		return tax_paid;
	}

	public void setTax_paid(String tax_paid) {
		this.tax_paid = tax_paid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccountYear() {
		return accountYear;
	}

	public void setAccountYear(String accountYear) {
		this.accountYear = accountYear;
	}

	@Override
	public String toString() {
		return "Tax [path=" + path + ", tax_paid=" + tax_paid + ", accountYear=" + accountYear + ", userId=" + userId
				+ "]";
	}

	 
}
