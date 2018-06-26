package com.touchstone.service.dto;

public class OtpDto {

	String number;

	String countryCode;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "OtpDto [number=" + number + ", countryCode=" + countryCode + "]";
	}

}
