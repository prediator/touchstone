package com.touchstone.service.dto;

/**
 * A DTO
 */
public class EducationDTO {

	private String $class;
	
	private Education education;

	private String id;

	private String profile;

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
	}

	public Education getEducation() {
		return education;
	}

	public void setEducation(Education education) {
		this.education = education;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	@Override
	public String toString() {
		return "Certificate [$class=" + $class + ", education=" + education + ", id=" + id + ", profile="
				+ profile + "]";
	}
}
