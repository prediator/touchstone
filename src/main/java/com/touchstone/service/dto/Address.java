package com.touchstone.service.dto;

/**
 * A DTO
 */
public class Address {

	private String $class;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private long zipcode;
	private String country;
	private Boolean isAddressValidated;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public long getZipcode() {
		return zipcode;
	}

	public void setZipcode(long zipcode) {
		this.zipcode = zipcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Boolean getIsAddressValidated() {
		return isAddressValidated;
	}

	public void setIsAddressValidated(Boolean isAddressValidated) {
		this.isAddressValidated = isAddressValidated;
	}

	@Override
	public String toString() {
		return "Address [$class=" + $class + ", address1=" + address1 + ", address2=" + address2 + ", city=" + city
				+ ", state=" + state + ", zipcode=" + zipcode + ", country=" + country + ", isAddressValidated="
				+ isAddressValidated + "]";
	}

}
