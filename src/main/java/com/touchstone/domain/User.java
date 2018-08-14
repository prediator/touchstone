package com.touchstone.domain;

import com.touchstone.config.Constants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.time.Instant;

/**
 * A user.
 */

@org.springframework.data.mongodb.core.mapping.Document(collection = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String userId;

	@NotNull
	@Pattern(regexp = Constants.LOGIN_REGEX)
	@Size(min = 1, max = 50)
	@Indexed
	@Field("userId")
	private String login;

	@Size(max = 50)
	@Field("userType")
	private String userType;

	@Size(max = 50)
	@Field("firstName")
	private String firstName;

	@Size(max = 50)
	@Field("lastName")
	private String lastName;

	@Size(max = 50)
	@Field("enterpriseName")
	private String enterpriseName;

	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	private String password;

	@Email
	@Size(min = 5, max = 100)
	@Indexed
	@Field("emailId")
	private String email;

	private boolean activated = false;

	@Size(min = 2, max = 6)
	@Field("lang_key")
	private String langKey;

	@Size(max = 256)
	@Field("image_url")
	private String imageUrl;

	@Size(max = 20)
	@Field("activation_key")
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@Field("reset_key")
	@JsonIgnore
	private String resetKey;

	@Field("reset_date")
	private Instant resetDate = null;

	@Field("profile_id")
	private String profileId;

	@Field("health_id")
	private String healthId;

	@JsonIgnore
	private Set<Authority> authorities = new HashSet<>();

	public String getHealthId() {
		return healthId;
	}

	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getLogin() {
		return login;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	// Lowercase the login before saving it in database
	public void setLogin(String login) {
		this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public Instant getResetDate() {
		return resetDate;
	}

	public void setResetDate(Instant resetDate) {
		this.resetDate = resetDate;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		User user = (User) o;
		return !(user.getUserId() == null || getUserId() == null) && Objects.equals(getUserId(), user.getUserId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getUserId());
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", login=" + login + ", userType=" + userType + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", enterpriseName=" + enterpriseName + ", password=" + password
				+ ", email=" + email + ", activated=" + activated + ", langKey=" + langKey + ", imageUrl=" + imageUrl
				+ ", activationKey=" + activationKey + ", resetKey=" + resetKey + ", resetDate=" + resetDate
				+ ", profileId=" + profileId + ", authorities=" + authorities + "]";
	}
}
