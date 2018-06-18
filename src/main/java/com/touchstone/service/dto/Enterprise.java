package com.touchstone.service.dto;

/**
 * A DTO
 */
public class Enterprise {

	private String $class;

	private String enterpriseName;

	private Address address;

	private String email;

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

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
		return "Enterprise [$class=" + $class + ", enterpriseName=" + enterpriseName + ", address=" + address
				+ ", email=" + email + ", userId=" + userId + ", mobile=" + mobile + ", isMobileValidated="
				+ isMobileValidated + ", isEmailValidated=" + isEmailValidated + "]";
	}

}
