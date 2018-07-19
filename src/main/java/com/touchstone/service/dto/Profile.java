package com.touchstone.service.dto;

/**
 * A DTO
 */
public class Profile {

	private String $class = "org.touchstone.basic.Profile";
	private String profileId;
	private String user;
	private String yearsOfExperience;

	// private String[] education;
	// private String[] certification;
	// private String[] skills;
	// private String[] experience;
	// private String[] projects;
	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getYearsOfExperience() {
		return yearsOfExperience;
	}

	public void setYearsOfExperience(String yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}

	@Override
	public String toString() {
		return "Profile [$class=" + $class + ", profileId=" + profileId + ", user=" + user + ", yearsOfExperience="
				+ yearsOfExperience + "]";
	}

}
