package com.touchstone.domain;

import java.io.Serializable;

/**
 * inquiry
 */

@org.springframework.data.mongodb.core.mapping.Document(collection = "inquiry")
public class Inquiry implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;

	private String name;

	private String phone;

	private String message;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Inquiry [email=" + email + ", name=" + name + ", phone=" + phone + ", message=" + message + "]";
	}

}
