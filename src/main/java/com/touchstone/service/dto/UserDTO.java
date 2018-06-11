package com.touchstone.service.dto;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.touchstone.domain.Authority;
import com.touchstone.domain.Gender;
import com.touchstone.domain.User;
import com.touchstone.domain.UserType;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

	private String id;

	private String userId = "tdy";

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@Email
	@Size(min = 5, max = 100)
	private String emailId;

	private String address1;

	private String address2;

	private String city;

	private String country;

	private Date dob;

	private String enterpriseName;

	private Gender gender;

	private String langKey;

	private String mobile;

	private String state;

	private UserType userType;

	private String zipcode;
	private boolean activated = false;

	private Set<String> authorities;

	public UserDTO() {
		// Empty constructor needed for Jackson.
	}

	public UserDTO(User user) {
		this.id = user.getUserId();
		this.userId = user.getLogin();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.emailId = user.getEmail();
		this.activated = user.getActivated();
		this.authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet());
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
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

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public Set<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<String> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", emailId=" + emailId + ", address1=" + address1 + ", address2=" + address2 + ", city=" + city
				+ ", country=" + country + ", dob=" + dob + ", enterpriseName=" + enterpriseName + ", gender=" + gender
				+ ", langKey=" + langKey + ", mobile=" + mobile + ", state=" + state + ", userType=" + userType
				+ ", zipcode=" + zipcode + ", activated=" + activated + ", authorities=" + authorities + "]";
	}
}
