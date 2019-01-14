package com.touchstone.service.dto;

/**
 * A DTO
 */
public class SkillDTO {

	private String $class="";
	
	private Skills skills;

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

	public Skills getSkills() {
		return skills;
	}

	public void setSkills(Skills skills) {
		this.skills = skills;
	}

	@Override
	public String toString() {
		return "SkillDTO [$class=" + $class + ", id=" + id + ", profile=" + profile + "]";
	}
}
