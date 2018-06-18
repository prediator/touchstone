package com.touchstone.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A DTO
 */
public class ValidationConsumer {

	private String $class;

	private String consumer;

	@JsonInclude(Include.NON_NULL)
	private Boolean isEmailValidated;

	@JsonInclude(Include.NON_NULL)
	private Boolean isMobileValidated;

	@JsonInclude(Include.NON_NULL)
	private Boolean isAddressValidated;

	public Boolean getIsAddressValidated() {
		return isAddressValidated;
	}

	public void setIsAddressValidated(Boolean isAddressValidated) {
		this.isAddressValidated = isAddressValidated;
	}

	public Boolean getIsMobileValidated() {
		return isMobileValidated;
	}

	public void setIsMobileValidated(Boolean isMobileValidated) {
		this.isMobileValidated = isMobileValidated;
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

	public Boolean getIsEmailValidated() {
		return isEmailValidated;
	}

	public void setIsEmailValidated(Boolean isEmailValidated) {
		this.isEmailValidated = isEmailValidated;
	}

	@Override
	public String toString() {
		return "EmailValidation [$class=" + $class + ", consumer=" + consumer + ", isEmailValidated=" + isEmailValidated
				+ "]";
	}

}
