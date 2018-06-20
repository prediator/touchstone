package com.touchstone.service.dto;

public class OtpDto {

	String number;

	String message;

	String countryCode;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "OtpDto [number=" + number + ", message=" + message + ", countryCode=" + countryCode + "]";
	}

}
