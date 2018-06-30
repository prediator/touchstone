package com.touchstone.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A DTO
 */
public class Update {

	private String $class;

	@JsonInclude(Include.NON_NULL)
	private String consumer;

	@JsonInclude(Include.NON_NULL)
	private String enterprise;

	@JsonInclude(Include.NON_NULL)
	private String mobile;

	@JsonInclude(Include.NON_NULL)
	private String email;

	@JsonInclude(Include.NON_NULL)
	private Address newaddress;

	@JsonInclude(Include.NON_NULL)
	private String firstName;

	@JsonInclude(Include.NON_NULL)
	private String enterpriseName;

	@JsonInclude(Include.NON_NULL)
	private String lastName;

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public String getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getNewaddress() {
		return newaddress;
	}

	public void setNewaddress(Address newaddress) {
		this.newaddress = newaddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Update [$class=" + $class + ", consumer=" + consumer + ", enterprise=" + enterprise + ", mobile="
				+ mobile + ", email=" + email + ", newaddress=" + newaddress + ", firstName=" + firstName
				+ ", lastName=" + lastName + "]";
	}

}
