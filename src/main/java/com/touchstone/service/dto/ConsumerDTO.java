package com.touchstone.service.dto;

/**
 * A DTO
 */
public class ConsumerDTO {

	private String $class;

	private Address address;

	private String firstName;

	private String lastName;

	private String email;

	private String gender;

	private String dob;

	private String userId;

	private String mobile;

	private Boolean isMobileValidated;

	private Boolean isEmailValidated;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Boolean getIsMobileValidated() {
		return isMobileValidated;
	}

	public void setIsMobileValidated(Boolean isMobileValidated) {
		this.isMobileValidated = isMobileValidated;
	}

	public Boolean getIsEmailValidated() {
		return isEmailValidated;
	}

	public void setIsEmailValidated(Boolean isEmailValidated) {
		this.isEmailValidated = isEmailValidated;
	}

	@Override
	public String toString() {
		return "ConsumerDTO [$class=" + $class + ", address=" + address + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", gender=" + gender + ", dob=" + dob + ", userId=" + userId
				+ ", mobile=" + mobile + ", isMobileValidated=" + isMobileValidated + ", isEmailValidated="
				+ isEmailValidated + "]";
	}

}
