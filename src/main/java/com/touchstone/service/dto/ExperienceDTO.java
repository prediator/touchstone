package com.touchstone.service.dto;

/**
 * A DTO
 */
public class ExperienceDTO {

	private String $class="";
	
	private Experience experience;

	private String id;

	private String profile="";

	public String get$class() {
		return $class;
	}

	public void set$class(String $class) {
		this.$class = $class;
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

	public Experience getExperience() {
		return experience;
	}

	public void setExperience(Experience experience) {
		this.experience = experience;
	}

	@Override
	public String toString() {
		return "ExperienceDTO [$class=" + $class + ", experience=" + experience + ", id=" + id + ", profile=" + profile
				+ "]";
	}
}
